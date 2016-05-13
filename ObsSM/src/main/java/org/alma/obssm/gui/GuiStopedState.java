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
public class GuiStopedState extends GuiStatesBase{
    
    public GuiStopedState(Manager m) {
        super(m);
    }

    @Override
    public void startListening() {
        m.osmPanel.jsonFileButton.setEnabled(false);
        m.osmPanel.xmlFileButton.setEnabled(false);
        m.osmPanel.startButton.setEnabled(false);
        m.osmPanel.stopButton.setEnabled(true);
        m.osmPanel.restartButton.setEnabled(true);
    }

    @Override
    public void stopListening() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
