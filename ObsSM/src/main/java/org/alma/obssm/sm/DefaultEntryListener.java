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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.alma.obssm.Manager;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 * This class implements a SCXMLListener who allows to trigger a method on every
 * transition. This class also is able to do whatever thing, e.g. connect to a
 * database or connect to a socket and transmit the transitions and states.
 *
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 * @version 1.0
 *
 */
public class DefaultEntryListener extends EntryListener {

    class InnerTransition {
        String stateFrom;
        String timeStamp;

        public InnerTransition(String stateFrom, String timeStamp) {
            this.stateFrom = stateFrom;
            this.timeStamp = timeStamp;
        }
        
    }
    
    private ArrayList<InnerTransition> pastStates;
    
    public DefaultEntryListener(Manager manager) {
        super(manager);
    }

    @Override
    public void onEntry(TransitionTarget state) {
    }

    @Override
    public void onExit(TransitionTarget state) {
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition, String array, String timeStamp, String logline) {
        
        pastStates.add(new InnerTransition(from.getId(), timeStamp));
        
        long delta = getTimeMillis(timeStamp);
        for (int row=pastStates.size()-1; row >= 0; row--) {
            if (pastStates.get(row).stateFrom.equals(to.getId())) {
                delta -= getTimeMillis(pastStates.get(row).timeStamp);
            }
        }
        
        if (delta == getTimeMillis(timeStamp)) delta = 0L;
        
        delta/=1000;
        
        
        System.out.println("ARRAY: "+array+" EVENT: " + transition.getEvent() + " TO: " + to.getId() + " FROM: " + from.getId() + " TS: " + timeStamp + " DTIME: " + delta);
        System.out.println("\tLOGLINE: " + logline);
    }

    @Override
    public void initialize() {
        pastStates = new ArrayList<>();
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

}
