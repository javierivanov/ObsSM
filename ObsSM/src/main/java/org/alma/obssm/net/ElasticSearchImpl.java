/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javier
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

    public ElasticSearchImpl(String timeStampStart, String timeStampEnd, String query) {
        this.timeStampStart = timeStampStart;
        this.timeStampEnd = timeStampEnd;
        this.query = query;
        this.fifoList = new LinkedList<>();
    }

    private void getData() throws IOException, MalformedURLException, ParseException {

        //Reusable vars
        DataInputStream a;
        Reader r;
        
        
        //Getting indices
        a = sendAndGetData("http://elk-master.osf.alma.cl:9200/aos-*/_field_stats?level=indices",
                "{\"fields\":[\"@timestamp\"],\"index_constraints\":{\"@timestamp\":{\"max_value\":{\"gte\":"+toEpoch(timeStampStart)+",\"format\":\"epoch_millis\"},\"min_value\":{\"lte\":"+toEpoch(timeStampEnd)+",\"format\":\"epoch_millis\"}}}}",
                "POST");
        r = new InputStreamReader(a);
        Set<String> iresponse = getIndices(r);

        for (String s : iresponse) {
            Logger.getLogger(ElasticSearchImpl.class.getName()).log(Level.INFO,"gotten index: "+s);
            //Getting hits for each index " + s + "
            a = sendAndGetData("http://elk-master.osf.alma.cl:9200/" + s + "/_search", "{\"query\":{\"filtered\":{\"query\":{\"query_string\":{\"analyze_wildcard\":true,\"query\":\"" + query + "\"}},\"filter\":{\"bool\":{\"must\":[{\"query\":{\"match\":{\"Process\":{\"query\":\"CONTROL/ACC/javaContainer\",\"type\":\"phrase\"}}}},{\"query\":{\"match\":{\"Host\":{\"query\":\"gas01\",\"type\":\"phrase\"}}}},{\"range\":{\"@timestamp\":{\"gte\":"+toEpoch(timeStampStart)+",\"lte\":"+toEpoch(timeStampEnd)+",\"format\":\"epoch_millis\"}}}],\"must_not\":[]}}}},\"size\":1000,\"sort\":[{\"@timestamp\":{\"order\":\"asc\",\"unmapped_type\":\"boolean\"}}],\"fields\":[\"*\",\"_source\"],\"script_fields\":{},\"fielddata_fields\":[\"TimeStamp\",\"@timestamp\"]}", "POST");
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

    private String toEpoch(String timeStamp) throws ParseException {
        if (timeStamp.contains("T")) {
            timeStamp = timeStamp.replace("T", " ");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = df.parse(timeStamp);
        long epoch = date.getTime();
        return String.valueOf(epoch);
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
    public void startCommunication() throws IOException {
        try {
            getData();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ElasticSearchImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ElasticSearchImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isCommunicationActive() {
        return !fifoList.isEmpty();
    }

}
