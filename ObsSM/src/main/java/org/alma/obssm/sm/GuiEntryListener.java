/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.sm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import org.alma.obssm.Manager;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * @author javier
 */
public class GuiEntryListener extends EntryListener {

    private Manager m;
    private String dataList = "";

    public GuiEntryListener(Manager m) {
        this.m = m;
    }

    @Override
    public void onEntry(TransitionTarget state) {
        String k = parent.getKeyName();
        if (k == null) {
            return;
        }

        if (m.osmPanel.getArrayComboBox().getActionListeners().length == 0) {
            this.m.osmPanel.getArrayComboBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (m.osmPanel.getArrayComboBox().getSelectedItem().equals(k)) {
                        m.osmPanel.getArrayTextArea().setText(dataList);
                    }
                }
            });
        }

        DefaultComboBoxModel<String> dlm = (DefaultComboBoxModel<String>) m.osmPanel.getArrayComboBox().getModel();
        boolean t = false;
        int e = -1;
        for (int i = 0; i < dlm.getSize(); i++) {
            if (dlm.getElementAt(i).equals(k)) {
                t = true;
            }
        }

        if (!t) {
            dlm.addElement(k);
            m.osmPanel.getArrayComboBox().setSelectedItem(k);
        }

        dataList += state.getId() + "\n";
        
        if (m.osmPanel.getArrayComboBox().getSelectedItem().equals(k)) m.osmPanel.getArrayTextArea().setText(dataList);

    }

    @Override
    public void onExit(TransitionTarget state) {

    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        
    }

}
