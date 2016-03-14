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

import java.sql.Timestamp;

import org.alma.obssm.Run;
import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;


/**
 * This class implements a SCXMLListener who allows to trigger a method on every transition.
 * This class also is able to do whatever thing, e.g. connect to a database or connect to a socket
 * and transmit the transitions and states.
 * @author Javier Fuentes
 * @version 0.2
 *
 */
public class CustomEntryListener implements SCXMLListener {
	
	private StateMachine parent = null;
	
	public CustomEntryListener(StateMachine parent) {
		this.parent = parent;
	}
	
    @Override
    public void onEntry(TransitionTarget state) {
    	Timestamp ts = new Timestamp(System.currentTimeMillis());
    	if (Run.KEYNAME_FILTER.length > 0)
    	{
    		boolean show = false;
    		for (String a: Run.KEYNAME_FILTER)
    		{
    			if (a.equals(parent.getKeyName())) show = true;
    		}
    		if (!show) return;
    	}
    	String t="";
    	
    	if (Run.SHOW_TIMESTAMP) t = ts.toString();
    	/*
    	 * Don not showing idles states!
    	 */
    	if (state.getId().equals("idle")) return;
        System.out.println(t + " ON_ENTRY_STATE, KN: " + parent.getKeyName() + ", STATE: " + state.getId());
    }

    @Override
    public void onExit(TransitionTarget state) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        //sthrow new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}