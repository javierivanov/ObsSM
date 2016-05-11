/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.gui;

import javax.swing.DefaultComboBoxModel;
import org.alma.obssm.Manager;

/**
 *
 * @author javier
 */
public class GuiListeningState extends GuiStatesBase{
    
    public GuiListeningState(Manager m) {
        super(m);
    }

    @Override
    public void stopListening() {
        m.osmPanel.jsonFileButton.setEnabled(true);
        m.osmPanel.xmlFileButton.setEnabled(true);
        m.osmPanel.startButton.setEnabled(true);
        m.osmPanel.stopButton.setEnabled(false);
        m.osmPanel.restartButton.setEnabled(false);
        m.osmPanel.statusLabel.setText("Stoped");
    }

    @Override
    public void reset() {
        m.osmPanel.arrayComboBox.setModel(new DefaultComboBoxModel<String>());
        m.osmPanel.arrayProgressBar.setValue(0);
        m.osmPanel.arrayProgressBar.setString("");
        m.osmPanel.arrayTextArea.setText("");
        m.osmPanel.statusLabel.setText("Restarted");
        m.osmPanel.jsonFileButton.setEnabled(true);
        m.osmPanel.xmlFileButton.setEnabled(true);
        m.osmPanel.startButton.setEnabled(true);
        m.osmPanel.stopButton.setEnabled(false);
        m.osmPanel.restartButton.setEnabled(false);
    }

    @Override
    public void startListening() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
