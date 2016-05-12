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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;

/**
 * This class manages State Machines which are currently active.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.3
 */
public class StateMachineManager {

    public final static int ACTION_TRIGGERED = 1;
    public final static int ACTION_NOT_FOUND = 2;
    public final static int NEW_SM_REQUIRED = 3;

    private final List<StateMachine> stateMachines;
    private final String xmlpath;

    public StateMachineManager(String xmlpath) {
        this.xmlpath = xmlpath;
        this.stateMachines = new ArrayList<>();
    }

    /**
     * Creates a new State Machine
     *
     * @throws IOException
     * @throws ModelException
     * @throws SAXException
     */
    public void addNewStateMachine() throws IOException, ModelException, SAXException {
        this.stateMachines.add(new StateMachine(this.xmlpath));
    }

    public void addNewStateMachine(EntryListener listener) throws IOException, ModelException, SAXException {
        this.stateMachines.add(new StateMachine(this.xmlpath, listener));
    }

    public boolean isSMIdleAvailable() {
        for (StateMachine s: this.stateMachines) {
            if (s.getKeyName() == null) return true;
        }
        return false;
    }

    /**
     * Get State Machine list
     *
     * @return StateMachine List
     */
    public List<StateMachine> getStateMachines() {
        return this.stateMachines;
    }

    public List<String> getAllPossiblesTransitions() {
        /**
         * Listing all possibles transitions.
         */
        List<String> out = new ArrayList<>();
        for (StateMachine aux : this.stateMachines) {
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
     * Search for the specific keyName SM and fire the event, if the keyName
     * does not exists it will assign a new one.
     *
     * @param transition
     * @param keyName
     * @return ACTION_TRIGGERED, ACTION_NOT_FOUND, NEW_SM_REQUIRED
     * @throws ModelException
     * @throws SAXException
     * @throws IOException
     */
    @Deprecated
    public int findAndTriggerActionOld(String transition, String keyName) throws ModelException, IOException, SAXException {
        if (transition == null) {
            return ACTION_NOT_FOUND;
        }
        
        StateMachine newOne = null;
        
        for (StateMachine aux : this.stateMachines) {
            if (aux.getKeyName() == null) {
                newOne = aux;
            } else if (aux.getKeyName().equals(keyName)) {
                if (aux.fireEvent(transition)) {
                    /**
                     * For final states, the SM will be removed from the list.
                     */
                    this.stateMachines.remove(aux);
                }
                /**
                 * If it finds the keyName, raise the event and the method has
                 * to stop.
                 */
                return ACTION_TRIGGERED;
            }
        }
        if (newOne == null) throw new NullPointerException("Null state machine");
        
        newOne.setKeyName(keyName);
        if (newOne.fireEvent(transition)) {
            /**
             * For final states (on this case, could be a error final state),
             * the SM will be removed from the list.
             */
            this.stateMachines.remove(newOne);
        }
        /**
         * New SM, waiting for a new instance.
         */
        return NEW_SM_REQUIRED;
    }
    
    
    /**
     * 
     * Finds a transition on the state machines which has the specific keyNamem
     * if the state machine does not exists, it will return a new SM is required.
     * 
     * 
     * @param transition
     * @param keyName
     * @return ACTION_TRIGGERED, NEW_SM_REQUIRED, ACTION_NOT_FOUND
     * @throws ModelException
     * @throws IOException
     * @throws SAXException 
     */
    public int findAndTriggerAction(String transition, String keyName) throws ModelException, IOException, SAXException {
        if (transition == null) {
            return ACTION_NOT_FOUND;
        }
        
        
        for (StateMachine aux : this.stateMachines) {
            if (aux.getKeyName() != null) {
                if (aux.getKeyName().equals(keyName)) {
                    if (aux.fireEvent(transition)) {
                        this.stateMachines.remove(aux);
                    }
                    return ACTION_TRIGGERED;
                }
            }
        }

        StateMachine m = null;
        for (StateMachine s: this.stateMachines) {
            if (s.getKeyName() == null) {
                m = s;
            }
        }
        
        if (m == null) return ACTION_NOT_FOUND;
        
        for (String s: m.getTransitionsStringList()) {
            if (s.equals(transition)) {
                m.setKeyName(keyName);
                if (m.fireEvent(transition)) {
                    this.stateMachines.remove(m);
                }
                return NEW_SM_REQUIRED;
            }
        }
        
        return ACTION_NOT_FOUND;
    }

}
