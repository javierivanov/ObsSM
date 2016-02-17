package org.alma.obssm.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        for (Iterator i = se.iterator(); i.hasNext();)
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
        SubjectTransition st = this.subjects.get(transition);
        if (st == null)
        {
            throw new NullPointerException("The transition do not exists on the HashMap");
        }
        return st;
    }
    
    
    public List<String> getListSearchPattern(String line, String transition) throws NullPointerException
    {
        SubjectTransition st = getSubjectTransition(transition);
        List<String> list =   new ArrayList<>();
        
        st.search_list.stream().map((pattern) -> Pattern.compile(pattern)).map((p) -> p.matcher(line)).map((m) -> {
            String output = "";
            if (m.find())
            {
                output = m.group();
            }
            return output;
        }).forEach((output) -> {
            list.add(output);
        });
        
        return list;
    }
    
}
