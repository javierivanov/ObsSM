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

import org.alma.obssm.Manager;
import org.alma.obssm.parser.Parser;
import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 * This abstract class allows to inherit it, to create your owns actions on States changes.
 * Moreover its possible to call the parent of the state machine to obtain the keyName and others components.
 * 
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 * @version 1.0
 */
public abstract class EntryListener implements SCXMLListener {

    /**
     * Parent state machine execution.
     */
    protected StateMachine stateMachine = null;

    /**
     * Global Manager instance.
     */
    protected final Manager manager;

    /**
     *
     * @param manager
     */
    public EntryListener(Manager manager) {
        this.manager = manager;
    }
    
    /**
     * This method will be executed after the parent State machine receive a keyName.
     */
    public abstract void initialize();
    
    
    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        onTransition(from, to, transition, Parser.savedArray, Parser.savedTimeStamp, Parser.savedLogLine);
    }
    
    /**
     * this methods allow to execute what you need, on every transition.
     * 
     * @param from
     * @param to
     * @param transition
     * @param array
     * @param timeStamp
     * @param logLine
     */
    public abstract void onTransition(TransitionTarget from, TransitionTarget to, Transition transition, String array, String timeStamp, String logLine);
    
    /**
     * Set the StateMachine
     * @param parent
     */
    public void setParent(StateMachine parent) {
        this.stateMachine = parent;
    }

    /**
     * Returns the StateMachine
     * @return
     */
    public StateMachine getParent() {
        return this.stateMachine;
    }
}
