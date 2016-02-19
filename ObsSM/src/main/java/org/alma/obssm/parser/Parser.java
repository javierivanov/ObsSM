/*******************************************************************************
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
 * 
 * @autor Javier Fuentes j.fuentes.m(at)icloud.com
 * @version 0.1
 * 
 *******************************************************************************/

package org.alma.obssm.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 *
 * @author javier
 */
public class Parser {
    protected Map<String, SubjectTransition> subjects;
    
    public Parser(String url) throws FileNotFoundException
    {
        JsonReader reader = new JsonReader(new FileReader(url));
        JsonElement element = new JsonParser().parse(reader);
        Gson gson = new GsonBuilder().create();
        this.subjects = new HashMap<>();
        
        Set<Entry<String, JsonElement>> se = element.getAsJsonObject().entrySet();
        for (Iterator<Entry<String, JsonElement>> i = se.iterator(); i.hasNext();)
        {
            Entry<String, JsonElement> e = (Entry<String, JsonElement>) i.next();
            SubjectTransition t = gson.fromJson(e.getValue(), SubjectTransition.class);
            this.subjects.put(e.getKey(), t);
        }
    }
    
    private boolean parseLine(String line, String transition) throws NullPointerException
    {
        
        SubjectTransition st = getSubjectTransition(transition);
        
        if (!st.and_list.stream().noneMatch((aux) -> (!line.contains(aux)))) {
            return false;
        }
        
        if (st.or_list.stream().anyMatch((aux) -> (line.contains(aux)))) {
            return true;
        }
        
        return st.or_list.isEmpty();
    }
    
    public String getParseAction(String line, List<String> transitionList)
    {
        for (String t: transitionList)
        {
            if (parseLine(line, t)) return t;
        }
        
        return null;
    }
    
    private SubjectTransition getSubjectTransition(String transition) throws NullPointerException
    {
    	if (transition == null) return null;
        SubjectTransition st = this.subjects.get(transition);
        if (st == null)
        {
            throw new NullPointerException("The transition do not exists on the HashMap");
        }
        return st;
    }
    
    
    public List<String> getListSearchPattern(String line, String transition) throws NullPointerException
    {
    	System.out.println(transition);
        SubjectTransition st = getSubjectTransition(transition);
        List<String> list =   new ArrayList<>();

        st.search_list.stream().map((pattern) -> Pattern.compile(pattern)).map((p) -> p.matcher(line)).map((m) -> {
            String output = "";
            if (m.find())
            {
                output = m.group();
                System.out.println(output);
            }
            return output;
        }).forEach((output) -> {
            list.add(output);
        });
        
        return list;
    }
    
}
