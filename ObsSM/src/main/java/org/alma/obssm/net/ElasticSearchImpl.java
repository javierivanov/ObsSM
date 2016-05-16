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
import java.util.Set;
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
    class IndicesResponse {

        HashMap<String, Object> indices;
    }

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

    private LinkedList<String> fifoList;
    private String timeStampStart;
    private String timeStampEnd;
    private String query;
    private String query_base;
    private String ELKUrl;

    public ElasticSearchImpl(String timeStampStart, String timeStampEnd, String query, String query_base, String ELKUrl) {
        this.timeStampStart = timeStampStart;
        this.timeStampEnd = timeStampEnd;
        this.query = query;
        this.query_base = query_base;
        this.ELKUrl = ELKUrl;
        this.fifoList = new LinkedList<>();
    }

    public void setELKUrl(String ELKUrl) {
        this.ELKUrl = ELKUrl;
    }

    public String getELKUrl() {
        return ELKUrl;
    }

    public void setQuery_base(String query_base) {
        this.query_base = query_base;
    }

    public String getQuery_base() {
        return query_base;
    }

    public void getData() throws IOException, MalformedURLException, ParseException {

        //Reusable vars
        DataInputStream a;
        Reader r;

        //Getting indices
        a = sendAndGetData(ELKUrl + "/aos-*/_field_stats?level=indices",
                "{\"fields\":[\"@timestamp\"],\"index_constraints\":{\"@timestamp\":{\"max_value\":{\"gte\":\"" + (timeStampStart) + "\",\"format\":\"strict_date_hour_minute_second_millis\"},\"min_value\":{\"lte\":\"" + (timeStampEnd) + "\",\"format\":\"strict_date_hour_minute_second_millis\"}}}}",
                "POST");
        r = new InputStreamReader(a);
        Set<String> iresponse = getIndices(r);

        for (String s : iresponse) {
            Logger.getLogger(ElasticSearchImpl.class.getName()).log(Level.INFO, "gotten index: {0}", s);
            //Getting hits for each index " + s + "

            query_base = query_base.replace("$Q", query);
            query_base = query_base.replace("$T1", timeStampStart);
            query_base = query_base.replace("$T2", timeStampEnd);
            a = sendAndGetData(ELKUrl + "/" + s + "/_search", query_base, "POST");
            r = new InputStreamReader(a);
            List<Hit> response = getHits(r);

            for (Hit h : response) {
                //Creating a log line for each  result.
                StringBuilder temp = new StringBuilder();
                temp.append(h._source.get("TimeStamp")).append(" ");
                temp.append(h._source.get("SourceObject")).append(" ");
                temp.append(h._source.get("File")).append(" ");
                temp.append(h._source.get("Routine")).append(" ");
                temp.append(h._source.get("text")).append(" ");

                fifoList.add(temp.toString());
            }
        }

    }

    private DataInputStream sendAndGetData(String url, String postData, String method) throws MalformedURLException, IOException, ParseException {
        URL _url = new URL(url);
        HttpURLConnection con = (HttpURLConnection) _url.openConnection();
        //  CURLOPT_POST
        con.setRequestMethod(method);
        con.setRequestProperty("Content-length", String.valueOf(postData.length()));

        con.setDoOutput(true);
        con.setDoInput(true);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        output.writeBytes(postData);
        output.close();

        // read the response
        return new DataInputStream(con.getInputStream());
    }

    private Set<String> getIndices(Reader results) {
        Gson gson = new GsonBuilder().create();
        IndicesResponse response = gson.fromJson(results, IndicesResponse.class);
        return response.indices.keySet();
    }

    private List<Hit> getHits(Reader results) {
        Gson gson = new GsonBuilder().create();
        HitsResponse response = gson.fromJson(results, HitsResponse.class);
        return response.hits.hits;
    }

    @Override
    public String waitForLine() throws IOException, InterruptedException {
        return fifoList.removeFirst();
    }

    @Override
    public void endCommunication() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void interrupt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startCommunication() throws IOException, MalformedURLException, ParseException {
        getData();
    }

    @Override
    public boolean isCommunicationActive() {
        return !fifoList.isEmpty();
    }

}
