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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.alma.obssm.Manager;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * Default implementation to use with the main GUI.
 * 
 * @version 0.4
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
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
        //System.out.println("\tEVENT: " + transition.getEvent() + " TO: " + to.getId());
        Color color = null;
        
        String eventtype = m.parser.getConstraints().get(transition.getEvent()).eventType;
        
        switch(eventtype) {
            case "initial": color = Color.yellow;
            break;
            case "final": color = Color.yellow;
            break;
            case "exception": color = Color.red;
            break;
        }
        long delta = getTimeMillis(timeStamp);
        
        for (int row = m.osmPanel.tablemodel.getRowCount()-1; row>=0; row--) {
            if (m.osmPanel.tablemodel.getValueAt(row, 2).equals(to.getId())) {
                delta -= getTimeMillis((String)m.osmPanel.tablemodel.getValueAt(row, 0));
                break;
            }
        }
        
        if (delta == getTimeMillis(timeStamp)) delta = 0L;
        
        delta/=1000;
        /*
        DirectedMultigraph<Vertex, Edge> graph = parent.getGraph(from);
        
        Vertex origin = graph.getEdgeSource(new Edge(new Vertex(from.getId()), new Vertex(to.getId()), transition.getEvent()));
        Vertex dest = graph.getEdgeTarget(new Edge(new Vertex(from.getId()), new Vertex(to.getId()), transition.getEvent()));
        
        Long targetTimeStamp = getTimeMillis(timeStamp);
        dest.setPayload(targetTimeStamp);
        if (origin.getPayload() == null) origin.setPayload(targetTimeStamp);
        Long sourceTimeStamp = (Long) origin.getPayload();
        delta = targetTimeStamp - sourceTimeStamp;
        
        if (sourceTimeStamp != 0) delta = targetTimeStamp - sourceTimeStamp;
        */
        m.osmPanel.tablemodel.addRow(new String[]{timeStamp, array, from.getId(), transition.getEvent(), to.getId(), String.valueOf(delta)}, color);
        int maximun = m.osmPanel.scrollTablePane.getVerticalScrollBar().getMaximum();
        m.osmPanel.scrollTablePane.getVerticalScrollBar().setValue(maximun);
    }

    private long getTimeMillis(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date d;
        try {
            d = sdf.parse(timestamp.replace("T", " "));
        } catch (ParseException ex) {
            return (Long) 0L;
        }
        return (Long) d.getTime();
    }
    
    @Override
    public void initialize() {
        
    }

}
