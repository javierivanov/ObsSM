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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * Data retriever implementation, it use ElasticSearch. Don't require server
 * socket listener.
 *
 * @version 0.4
 * @author Javier Fuentes Muñoz j.fuentes.m@icloud.com
 */
public class ElasticSearchImpl implements LineReader {

    /**
     * ***************GSON CLASSES******************
     */
    class Hit {

        HashMap<String, Object> _source;
    }

    class HitsList {

        List<Hit> hits;
    }

    class HitsResponse {

        HitsList hits;
    }
    /**
     * ***************GSON CLASSES******************
     */

    private final LinkedList<String> fifoList;
    private final String timeStampEnd;
    private final String query;
    private final String query_base;
    private final String ELKUrl;
    private String timeStampStart;
    private boolean active = true;

    private Thread thread;

    public ElasticSearchImpl(String timeStampStart, String timeStampEnd, String query, String query_base, String ELKUrl) {
        this.timeStampStart = timeStampStart;
        this.timeStampEnd = timeStampEnd;
        this.query = query;
        this.query_base = query_base;
        this.ELKUrl = ELKUrl;
        this.fifoList = new LinkedList<>();
    }

    public void getData() throws IOException, MalformedURLException, ParseException {

        //Reusable vars
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream a;
                Reader r;
                while (active) {
                    try {
                        //Getting hits for each index " + s + "
                        String query_base_aux = query_base.replace("$Q", query);
                        query_base_aux = query_base_aux.replace("$T1", timeStampStart);
                        query_base_aux = query_base_aux.replace("$T2", timeStampEnd);
                        a = sendAndGetData(ELKUrl + "/aos-*/_search", query_base_aux, "POST");
                        r = new InputStreamReader(a);
                        List<Hit> response = getHits(r);
                        if (response.isEmpty()) {
                            break;
                        }
                        String lastTimeStampStart = null;
                        for (Hit h : response) {
                            //Creating a log line for each  result.
                            StringBuilder temp = new StringBuilder();
                            temp.append(h._source.get("TimeStamp")).append(" ");
                            temp.append(h._source.get("SourceObject")).append(" ");
                            temp.append(h._source.get("File")).append(" ");
                            temp.append(h._source.get("Routine")).append(" ");
                            temp.append(h._source.get("text")).append(" ");
                            synchronized (fifoList) {
                                fifoList.add(temp.toString());
                            }

                            lastTimeStampStart = (String) h._source.get("TimeStamp");
                        }
                        synchronized (fifoList) {
                            fifoList.notify();
                        }
                        if (lastTimeStampStart == null) {
                            break;
                        }
                        if (timeStampStart.equals(lastTimeStampStart)) break;
                        timeStampStart = lastTimeStampStart;
                        System.out.println(lastTimeStampStart);
                    } catch (IOException | ParseException ex) {
                        Logger.getLogger(ElasticSearchImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread.start();
    }

    private DataInputStream sendAndGetData(String url, String postData, String method) throws MalformedURLException, IOException, ParseException {
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
            return fifoList.removeFirst();
        }
    }

    @Override
    public void endCommunication() throws IOException {
        active = false;
        thread.interrupt();
    }

    @Override
    public void interrupt() {
        active = false;
        thread.interrupt();
    }

    @Override
    public void startCommunication() throws IOException, MalformedURLException, ParseException {
        getData();
    }

    @Override
    public boolean isCommunicationActive() {
        return thread.isAlive();
    }

}
