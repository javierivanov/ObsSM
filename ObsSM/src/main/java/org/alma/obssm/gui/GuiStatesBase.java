/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.gui;

import org.alma.obssm.Manager;

/**
 *
 * @author javier
 */
public abstract class GuiStatesBase {
    protected Manager m;
    public GuiStatesBase(Manager m) {
        this.m = m;
    }
    
    public abstract void startListening();
    
    public abstract void stopListening();
    
    public abstract void reset();
    
}
