/*******************************************************************************
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
 *******************************************************************************/

package org.alma.obssm.sm;

import java.awt.Color;
import org.alma.obssm.Manager;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * Default implementation to use with the main GUI.
 * 
 * @version 0.4
 * @author Javier Fuentes j.fuentes.m@icloud.com
 */
public class GuiEntryListener extends EntryListener {

    public GuiEntryListener(Manager m) {
        super(m);
    }

    @Override
    public void onEntry(TransitionTarget state) {
    }

    @Override
    public void onExit(TransitionTarget state) {
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition, String array, String timeStamp, String logline) {
        System.out.println("\tEVENT: " + transition.getEvent() + " TO: " + to.getId());
        m.osmPanel.tablemodel.addRow(new String[]{timeStamp, array, from.getId(), transition.getEvent(), to.getId()}, Color.yellow);
        int maximun = m.osmPanel.scrollTablePane.getVerticalScrollBar().getMaximum();
        m.osmPanel.scrollTablePane.getVerticalScrollBar().setValue(maximun);
    }

}
