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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;

/**
 * This class manages State Machines which are currently active.
 * 
 * @author Javier Fuentes
 * @version 0.2
 * @since 0.2
 */


public class StateMachineManager {
	private List<StateMachine> stateMachines;
	private String xmlpath;
	
	public StateMachineManager(String xmlpath) {
		this.xmlpath = xmlpath;
		this.stateMachines = new ArrayList<>();
	}
	
	/**
	 * Creates a new State Machine
	 * @throws IOException
	 * @throws ModelException
	 * @throws SAXException
	 */
	public void addNewStateMachine() throws IOException, ModelException, SAXException
	{
		this.stateMachines.add(new StateMachine(this.xmlpath));
	}
	
	/**
	 * Get State Machine list
	 * @return StateMachine List
	 */
	public List<StateMachine> getStateMachines()
	{
		return this.stateMachines;
	}
	
	public List<String> getAllPossiblesTransitions()
	{
		/**
		 * Listing all possibles transitions.
		 */
		List<String> out = new ArrayList<>();
		for (Iterator<StateMachine> iter = this.stateMachines.iterator(); iter.hasNext();)
		{
			StateMachine aux = iter.next();
			out.addAll(aux.getTransitionsStringList());
		}
		/**
		 * Removing duplicates
		 */
		Set<String> hs = new HashSet<>();
		hs.addAll(out);
		out.clear();
		out.addAll(hs);
		return out;
	}
	
	
	/**
	 * Search for the specific keyName SM and fire the event, if the keyName does not exists it will assign a new one.
	 * @param transition
	 * @param keyName
	 * @throws ModelException
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void findAndTriggerAction(String transition, String keyName) throws ModelException, IOException, SAXException
	{
		if (transition == null) return;
		StateMachine newOne = null;
		for  (Iterator<StateMachine> iter = this.stateMachines.iterator(); iter.hasNext();)
		{
			StateMachine aux = iter.next();
			if (aux.getKeyName() == null) newOne = aux;
			else if (aux.getKeyName().equals(keyName))
			{
				aux.fireEvent(transition);
				/**
				 * If it finds the keyName, raise the event and the method has to stop.
				 */
				return;
			}
		}
		newOne.setKeyName(keyName);
		newOne.fireEvent(transition);
		addNewStateMachine();
	}
}
