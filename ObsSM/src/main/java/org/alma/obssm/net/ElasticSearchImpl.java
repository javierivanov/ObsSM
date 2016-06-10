/**
 * *****************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) AUI - Associated Universities Inc., 2016
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 ******************************************************************************
 */
package org.alma.obssm.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.alma.obssm.Run;

/**
 *
 * Data retriever implementation, it use ElasticSearch. Don't require server
 * socket listener.
 *
 * @version 1.0
 * @author Javier Fuentes Muñoz j.fuentes.m@icloud.com
 */
public class ElasticSearchImpl implements LineReader {

    /**
     * ***************INTERNAL GSON CLASSES******************
     */
    class Hit {

        HashMap<String, Object> _source;

        public String getSource(String[] params) {
            StringBuilder out = new StringBuilder();
            if (params.length != 0) {
                out.append("{");
                for (String p : params) {
                    out.append(p).append("= ");
                    out.append((String) _source.get(p));
                    out.append(", ");
                }
                out.append("}");
            } else {
                out.append(_source.toString());
            }
            return out.toString().replaceAll("\n", "\t");
        }
    }

    class HitsList {

        List<Hit> hits;
    }

    class HitsResponse {

        HitsList hits;
    }
    /**
     * ***************INTERNAL GSON CLASSES******************
     */

    private final LinkedList<String> fifoList;
    private final String timeStampEnd;
    private final String query;
    private final String query_base;
    private final String ESUrl;
    private String timeStampStart;
    private boolean active = true;
    private String[] specialOutput = null;
    private Thread thread;

    /**
     * This is the LineReader implementation for ElasticSearch
     *
     * It requires:
     * <ul>
     * <li>time stamp to start, and end.</li>
     * <li>
     * A query and query base: the query base is a filter provided to create a
     * default search. It is a JSON document it must include three variables
     * inside ($T1, $T2, $Q) as you can see in the query_base.json file. The
     * query is a simple query form based on Lucene. Example: "Array: Array001
     * AND Array: Array002"
     * </li>
     * <li>
     * ESUrl: ElasticSearch Url.
     * </li>
     * </ul>
     *
     * @param timeStampStart
     * @param timeStampEnd
     * @param query
     * @param query_base
     * @param ESUrl
     */
    public ElasticSearchImpl(String timeStampStart, String timeStampEnd,
            String query, String query_base, String ESUrl) {
        this.timeStampStart = timeStampStart;
        this.timeStampEnd = timeStampEnd;
        this.query = query;
        this.query_base = query_base;
        this.ESUrl = ESUrl;
        this.fifoList = new LinkedList<>();
    }

    /**
     * This methods connect to ES, collect data and put them into a FIFO list.
     * It runs a thread. The FIFO list locks the execution waiting for logs.
     *
     * @throws IOException
     * @throws ParseException
     */
    public void getData() throws IOException, ParseException {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream a;
                Reader r;
                active = true;
                //DefaultTime.
                String auxTimeStampEnd = timeStampEnd;
                if (Run.VERBOSE) {
                    Logger.getLogger(ElasticSearchImpl.class.getName()).
                            log(Level.INFO, "Elastic Search start");
                }
                while (active) {
                    try {
                        //Setting query
                        //The end time change each iteration when the end is now.
                        if (timeStampEnd.equals("now")) {
                            auxTimeStampEnd = getActualUTCTime();
                        }

                        String query_base_aux = query_base.replace("$Q", query);
                        query_base_aux = query_base_aux.replace("$T1", timeStampStart);
                        query_base_aux = query_base_aux.replace("$T2", auxTimeStampEnd);
                        //Sending query
                        a = sendAndGetData(ESUrl + "/aos-*/_search", query_base_aux, "POST");

                        //System.out.println(timeStampStart + "->" + auxTimeStampEnd);
                        //Reading response
                        r = new InputStreamReader(a);
                        List<Hit> response = getHits(r);

                        if (Run.VERBOSE) {
                            Logger.getLogger(ElasticSearchImpl.class.getName())
                                    .log(Level.INFO, "List response from ElasticSearch: {0}", response.size());
                        }

                        if (response.isEmpty() && !timeStampEnd.equals("now")) {
                            break;
                        }

                        //5 seconds delay with a small response.
                        if (response.size() <= 2) {
                            Thread.sleep(5000);
                        }

                        String lastTimeStampStart = null;
                        for (Hit h : response) {
                            //Creating a log line for each  result.
                            StringBuilder temp = new StringBuilder();
                            temp.append(h._source.get("TimeStamp")).append(" ");
                            temp.append(h._source.get("SourceObject")).append(" ");
                            temp.append(h._source.get("File")).append(" ");
                            temp.append(h._source.get("Routine")).append(" ");
                            String aux = (String) h._source.get("text");
                            temp.append(aux.replace("\n", ""));

                            synchronized (fifoList) {
                                if (specialOutput != null) {
                                    fifoList.add(h.getSource(specialOutput));
                                } else {
                                    fifoList.add(temp.toString());
                                }
                                fifoList.notify();
                                if (Run.VERBOSE) {
                                    Logger.getLogger(ElasticSearchImpl.class.getName()).
                                            log(Level.INFO, "Added new log to fifoList");
                                }
                            }

                            lastTimeStampStart = (String) h._source.get("TimeStamp");
                        }

                        //Without responses actions.
                        if (lastTimeStampStart == null && !timeStampEnd.equals("now")) {
                            break;
                        } else if (lastTimeStampStart == null) {
                            lastTimeStampStart = auxTimeStampEnd;
                        }

                        if (timeStampStart.equals(lastTimeStampStart)
                                && !timeStampEnd.equals("now")) {
                            break;
                        }
                        //Updating the low time limit for a new search.
                        timeStampStart = lastTimeStampStart;

                    } catch (IOException | ParseException | InterruptedException ex) {
                        Logger.getLogger(ElasticSearchImpl.class.getName()).
                                log(Level.SEVERE, "Elastic Search fail response", ex);
                        active = false;
                    }
                }
                active = false;
                synchronized (fifoList) {
                    fifoList.add("EOF");
                    fifoList.notify();
                }
                if (Run.VERBOSE) {
                    Logger.getLogger(ElasticSearchImpl.class.getName()).
                            log(Level.INFO, "Elastic Search stop");
                }
            }
        });
        thread.start();
    }

    private DataInputStream sendAndGetData(String url, String postData, String method)
            throws IOException, ParseException {
        try {
            URL _url = new URL(url);
            HttpURLConnection con = (HttpURLConnection) _url.openConnection();
            //  CURLOPT_POST
            con.setRequestMethod(method);
            con.setRequestProperty("Content-length", String.valueOf(postData.length()));
            con.setDoOutput(true);
            con.setDoInput(true);

            try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) {
                output.writeBytes(postData);
            }
            // read the response

            return new DataInputStream(con.getInputStream());
        } catch (IOException e) {
            throw new IOException("Bad query syntax, Elastic Search return status 400");
        }
    }

    /**
     * Change the output vars
     *
     * @param specialOutput
     */
    public void setSpecialOutput(String[] specialOutput) {
        this.specialOutput = specialOutput;
    }

    /**
     * Provide actual timeStamp yyyy-MM-dd'T'HH:mm:ss.SSS The time depends on
     * the time of the execution machine.
     *
     * @return timeStamp
     */
    public String getActualUTCTime() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        simpleDateFormat.setTimeZone(timeZone);

        return simpleDateFormat.format(calendar.getTime()).replace(" ", "T");
    }

    private List<Hit> getHits(Reader results) {
        Gson gson = new GsonBuilder().create();
        HitsResponse response = gson.fromJson(results, HitsResponse.class);
        return response.hits.hits;
    }

    @Override
    public String waitForLine() throws IOException, InterruptedException {
        synchronized (fifoList) {
            if (fifoList.isEmpty()) {
                fifoList.wait();
            }
            if (Run.VERBOSE) {
                Logger.getLogger(ElasticSearchImpl.class.getName()).
                        log(Level.INFO, "Log line readed from Fifo list");
            }
            return fifoList.removeFirst();
        }
    }

    @Override
    public void endCommunication() throws IOException {
        interrupt();
    }

    @Override
    public void interrupt() {
        active = false;
        thread.interrupt();
    }

    @Override
    public void startCommunication() throws IOException, ParseException {
        getData();
    }

    @Override
    public boolean isCommunicationActive() {
        if (timeStampEnd.equals("now")) {
            return active;
        }
        return active || !fifoList.isEmpty();
    }

}
