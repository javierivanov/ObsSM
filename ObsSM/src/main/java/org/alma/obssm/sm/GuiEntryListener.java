/**
 * *****************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) AUI - Associated Universities Inc., 2016
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 ******************************************************************************
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
 * Gui Implementation of EntryListener.
 * 
 * @author Javier Fuentes
 * @version 0.3
 * 
 * @see EntryListener
 */
public class GuiEntryListener extends EntryListener {

    private Manager m;
    private String dataList = "";

    public GuiEntryListener(Manager m) {
        this.m = m;
    }

    @Override
    public void onEntry(TransitionTarget state) {
        final String k = parent.getKeyName();
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
