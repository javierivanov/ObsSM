package org.alma.obssm.gui;

/**
 *
 * @author javier
 */
public class Edge {

    public String name;
    public Node from;
    public Node to;
    public int percent;

    public Edge(String name, Node from, Node to, int percent) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "Edge{" + "name=" + name + ", from=" + from + ", to=" + to + ", percent=" + percent + '}';
    }

}
