package org.alma;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.JApplet;
import org.alma.graph.Edge;
import org.alma.graph.Vertex;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedMultigraph;


public final class JGraphApplet extends JApplet {

    private static final long serialVersionUID = 2202072534703043194L;

    private String last;
    private JGraphXAdapter<Vertex, Edge> jgxAdapter;

    public JGraphApplet(DirectedMultigraph<Vertex, Edge> g, Dimension s) {
        init(g,s);
        
    }

    public void init(DirectedMultigraph<Vertex, Edge> g, Dimension s) {
        // create a JGraphT graph
        jgxAdapter = new JGraphXAdapter<>(g);
        HashMap<mxICell, Vertex> v = jgxAdapter.getCellToVertexMap();
        HashMap<mxICell, Edge> e = jgxAdapter.getCellToEdgeMap();
        for (mxICell c: v.keySet()) {
            if (v.get(c).getState().contains("Idle")
                    || v.get(c).getState().contains("idle") ){
                c.setStyle("ROUNDED;strokeColor=red;fillColor=yellow");
                last = v.get(c).getState();
            }
        }
        
        
        //romoving transitions labels!
        for (Edge c: e.values()) {
            c.setTransition("");
        }
        
        jgxAdapter.refresh();
        

        getContentPane().add(execLayout(s));
        
        jgxAdapter.setVertexLabelsMovable(false);
        jgxAdapter.setEdgeLabelsMovable(false);
        jgxAdapter.setCellsBendable(false);
        jgxAdapter.setCellsDeletable(false);
        jgxAdapter.setCellsLocked(true);
        
        setVisible(true);
    }
    
    
    public mxGraphComponent execLayout(Dimension s) {
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        graphComponent.setSize(s);
        mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter, false);
        layout.setEdgeRouting(true);
        
        layout.setMoveTree(true);
        layout.execute(jgxAdapter.getDefaultParent());
        return graphComponent;
    }
    
    

    public void setState(String s) {
        HashMap<mxICell, Vertex> m = jgxAdapter.getCellToVertexMap();
        
        for (mxICell c: m.keySet()) {
            if (m.get(c).getState().equals(s)){
                c.setStyle("ROUNDED;strokeColor=red;fillColor=yellow");
            }
            if (m.get(c).getState().equals(last)){
                c.setStyle("ROUNDED;strokeColor=red;fillColor=lightsalmon");
            }
        }
        last = s;
        jgxAdapter.refresh();
    }
    
    
    public JGraphXAdapter<Vertex, Edge> getJgxAdapter() {
        return jgxAdapter;
    }


}
