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
import org.jgrapht.graph.Vertex;

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
    
    public GraphMaker(SCXML sm) {
        this.sm = sm;
    }
    
    public List< DirectedMultigraph<Vertex, Edge> > getGraph() {
        List< DirectedMultigraph<Vertex, Edge> > l = new ArrayList<>();
        for (String s: getRootStatesList()) {
            l.add(makeGraph(s));
        }
        return l;
    }
    
    private DirectedMultigraph<Vertex, Edge> makeGraph(String parentId) {
        DirectedMultigraph<Vertex, Edge> g = new DirectedMultigraph<>(Edge.class);
        LinkedList<Vertex> visitados = new LinkedList<>();
        LinkedList<Vertex> porVisitar = new LinkedList<>();
        
        porVisitar.add(new Vertex(parentId));
        
        while (!porVisitar.isEmpty()) {
            Vertex pv = porVisitar.removeLast();
            if (!visitados.contains(pv)) visitados.add(pv);
            List<Transition> tlist = getTransitionsListFromState(pv.getState());
            for (Transition t: tlist) {
                if (!porVisitar.contains(new Vertex(t.getNext())) && !visitados.contains(new Vertex(t.getNext())))
                    porVisitar.add(new Vertex(t.getNext()));
            }
        }
        
        for (Vertex s: visitados) {
            g.addVertex(s);
        }
        
        for (Vertex s: visitados) {
            for (Transition t: getTransitionsListFromState(s.getState())) {
                g.addEdge(s, new Vertex(t.getNext()), new Edge(s, new Vertex(t.getNext()), t.getEvent()));
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
