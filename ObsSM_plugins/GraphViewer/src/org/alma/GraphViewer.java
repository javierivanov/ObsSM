package org.alma;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.alma.graph.Edge;
import org.alma.graph.Vertex;
import org.alma.obssm.Manager;
import org.alma.obssm.sm.EntryListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.jgrapht.graph.DirectedMultigraph;

/**
 *
 * @author javier
 */
public class GraphViewer extends EntryListener {

    public static Viewer view = null;
    public static boolean first = true;

    public GraphViewer(Manager m) {
        super(m);
        if (view == null) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
            view = new Viewer();
        }
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition trnstn, String array, String timestamp, String logline) {
        view.getStatus().setText(logline);
        view.getLastArray().setText(array);
        view.getLastEvent().setText(trnstn.getEvent());
        view.getLastFrom().setText(from.getId());
        view.getLastTo().setText(to.getId());
        String s = to.getId();
        for (DirectedMultigraph<Vertex, Edge> g : stateMachine.getGraphs()) {
            String first = g.vertexSet().iterator().next().getState();
            for (Vertex v : g.vertexSet()) {
                if (v.getState().equals(s)) {
                    view.updateActualState(array, first, s);
                    return;
                }
            }
        }
    }

    @Override
    public void onEntry(TransitionTarget tt) {
        
    }

    @Override
    public void onExit(TransitionTarget tt) {

    }

    @Override
    public void initialize() {
        for (DirectedMultigraph<Vertex, Edge> g : stateMachine.getGraphs()) {
            String graphName = g.vertexSet().iterator().next().getState();
            view.addGraph(graphName, stateMachine.getKeyName(), g);
        }
    }
}
