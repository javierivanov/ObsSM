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
package org.alma.obssm.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * This class parse the log with the transitions constrains on the JSON file.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.4
 */
public class Parser {

    public Map<String, TransitionConstraints> constraints;
    
    
    /**
     * Vars required for the EntryListener to obtain extra data.
     */
    public static String savedTimeStamp;
    public static String savedArray;
    public static String savedEvent;
    public static String savedLogLine;
    
    /**
     * Constructor, initialize the JSON constraints file.
     *
     * @param url json file
     * @throws FileNotFoundException
     */
    public Parser(String url) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(url));
        JsonElement element = new JsonParser().parse(reader);
        Gson gson = new GsonBuilder().create();

        this.constraints = new HashMap<>();

        JsonArray arr = element.getAsJsonArray();

        for (int i = 0; i < arr.size(); i++) {
            TransitionConstraints t = gson.fromJson(arr.get(i), TransitionConstraints.class);
            this.constraints.put(t.eventName, t);
        }
    }

    public Map<String, TransitionConstraints> getConstraints() {
        return constraints;
    }

    /**
     * Read a log line and a transition, search for coincidences with the
     * transitions constrains.
     *
     * @param line
     * @param transition
     * @return true when the log line corresponds to transition.
     * @throws NullPointerException
     */
    private boolean parseLine(String line, String transition) throws NullPointerException {

        TransitionConstraints st = getTransitionConstraints(transition);
        
        if (st.search_list.size() > 0) {
            
            for (String s: st.search_list) {
                if (Pattern.compile(s).matcher(line).find()) return true;
            }
            return false;
        }
        
        for (String s: st.and_list) {
            if (!line.contains(s)) return false;
        }
        
        if (st.or_list.isEmpty()) return true;
        
        for (String s: st.or_list) {
            if (line.contains(s)) return true;
        }
        
        return false;
        
    }

    /**
     * Compares all the transitions possibilities with the log line.
     *
     * @param line
     * @param transitionList
     * @return the corresponding transition, and null when there is no
     * transition.
     */
    public String getParseAction(String line, List<String> transitionList) {
        for (String t : transitionList) {
            if (parseLine(line, t)) {
                return t;
            }
        }

        return null;
    }

    /**
     * Returns transitions constrains.
     *
     * @param transition
     * @return a transitionconstraint
     * @throws NullPointerException
     */
    private TransitionConstraints getTransitionConstraints(String transition) throws NullPointerException {
        if (transition == null) {
            return null;
        }
        TransitionConstraints st = this.constraints.get(transition);
        if (st == null) {
            throw new NullPointerException("The transition("+transition+") do not exists on the HashMap");
        }
        return st;
    }

    /**
     * Returns the keyName found on the line.
     *
     * @param line
     * @param transition
     * @return the keyName or null if not exists.
     */
    public String getKeyName(String line, String transition) {
        if (transition == null) {
            return null;
        }
        String keyName = null;
        TransitionConstraints tc = getTransitionConstraints(transition);
        Pattern pattern = Pattern.compile(tc.keyName);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            keyName = m.group();
        }
        return keyName;
    }
    
    public void saveExtraData(String line, String keyName, String event) {
        Parser.savedArray = keyName;
        Parser.savedEvent = event;
        Parser.savedLogLine = line;
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3})");
        Matcher m = pattern.matcher(line);
        Parser.savedTimeStamp = "";
        if (m.find()) Parser.savedTimeStamp = m.group();
    }
    
}
