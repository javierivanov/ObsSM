/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.gui;

/**
 *
 * @author javier
 */
public class Node {

    public String name;
    public int x, y;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{" + "name=" + name + ", x=" + x + ", y=" + y + '}';
    }

}
