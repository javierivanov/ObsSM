package org.alma.obssm.parser;

import java.util.List;

public class SubjectTransition {
    public List<String> and_list;
    public List<String> or_list;
    public List<String> search_list;

    @Override
    public String toString() {
        return "SubjectTransition{" + "and_list=" + and_list + ", or_list=" + or_list + ", search_list=" + search_list + '}';
    }
    
}
