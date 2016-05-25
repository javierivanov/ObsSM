package org.alma.obssm.sm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.Transition;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.Edge;

import org.xml.sax.SAXException;


public class GraphMaker {
    private SCXML sm;

    public GraphMaker(String xmlPath) {
        try {
            StateMachine aux = new StateMachine(xmlPath);
            sm = aux.getStateMachineModel();
        } catch (IOException | ModelException | SAXException ex) {
            Logger.getLogger(GraphMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List< DirectedMultigraph<String, Edge> > getGraph() {
        List< DirectedMultigraph<String, Edge> > l = new ArrayList<>();
        for (String s: getRootStatesList()) {
            l.add(makeGraph(s));
        }
        return l;
    }
    
    private DirectedMultigraph<String, Edge> makeGraph(String parentId) {
        DirectedMultigraph<String, Edge> g = new DirectedMultigraph<>(Edge.class);
        LinkedList<String> visitados = new LinkedList<>();
        LinkedList<String> porVisitar = new LinkedList<>();
        
        porVisitar.add(parentId);
        
        while (!porVisitar.isEmpty()) {
            String pv = porVisitar.removeLast();
            if (!visitados.contains(pv)) visitados.add(pv);
            List<Transition> tlist = getTransitionsListFromState(pv);
            for (Transition t: tlist) {
                if (!porVisitar.contains(t.getNext()) && !visitados.contains(t.getNext()))
                    porVisitar.add(t.getNext());
            }
        }
        
        for (String s: visitados) {
            g.addVertex(s);
        }
        
        for (String s: visitados) {
            for (Transition t: getTransitionsListFromState(s)) {
                g.addEdge(s, t.getNext(), new Edge(s, t.getNext(), t.getEvent()));
            }
            
        }
        return g;
    }
    
    private List<String> getRootStatesList() {
        List<String> l = new ArrayList<>();
        for (Object o: sm.getTargets().values()) {
            if (o instanceof State) {
                State state = (State) o;
                if (state.getChildren().isEmpty() && isRootState(state.getId()))
                    l.add(state.getId());
            }
        }
        return l;
    }
    
    private boolean isRootState(String id) {
        for (Object o: sm.getTargets().values()) {
            if (o instanceof State) {
                State state = (State)o;
                List<Transition> tlist = getTransitionsListFromState(state.getId());
                for (Transition t: tlist) {
                    if (t.getNext().equals(id)) return false;
                }
            }
        }
        
        return true;
    }
    
    private List<Transition> getTransitionsListFromState(String id) {
        List<Transition> tlist = new LinkedList<>();
        for (Object o: sm.getTargets().values()) {
            if (o instanceof State) {
                State state = (State) o;
                if (state.getId().equals(id)) return (List<Transition>)state.getTransitionsList();
            }
        }        
        return tlist;
    }
}
