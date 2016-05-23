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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.SimpleDispatcher;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.env.SimpleErrorReporter;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.Transition;
import org.xml.sax.SAXException;

/**
 * This class define a State Machine(SM) executor, who decides the legality of
 * the transitions and change its own state when its correct.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.4
 *
 */
public class StateMachine {

    private SCXMLExecutor engine;
    private SCXML stateMachine;
    private String keyName;
    
    /**
     * Useful var to remember all the actions executed before.
     */
    public List<String> historyEvents;
    
    /**
     * Constructor of the class, initialize the engine and the model and runs
     * the engine also uses a Listener who can triggers events on states
     * changes.
     *
     * @param listener
     * @see CustomEntryListener
     *
     * @param xmlFile Origin SM file with the SCXML model.
     * @throws IOException
     * @throws ModelException
     * @throws SAXException
     */
    public StateMachine(String xmlFile, EntryListener listener) throws IOException, ModelException, SAXException {
        initialize(xmlFile, listener);
    }

    public StateMachine(String xmlFile) throws IOException, ModelException, SAXException {
        initialize(xmlFile, new CustomEntryListener(null));
    }

    private void initialize(String xmlFile, EntryListener listener) throws IOException, ModelException, SAXException {
        this.stateMachine = SCXMLParser.parse(new File(xmlFile).toURI().toURL(),
                new SimpleErrorHandler());

        this.engine = new SCXMLExecutor(new JexlEvaluator(), new SimpleDispatcher(),
                new SimpleErrorReporter());
        this.engine.setStateMachine(this.stateMachine);
        this.engine.setSuperStep(true);
        this.engine.setRootContext(new JexlContext());
        this.engine.addListener(this.stateMachine, listener);
        listener.setParent(this);
        this.historyEvents = new LinkedList<>();
        this.engine.go();
    }
    
    public SCXML getStateMachineModel() {
        return engine.getStateMachine();
    }

    /**
     * Fires the transition in the model. If the event is null do nothing.
     *
     * @param event
     * @return true if the models has reached the end and false in other case.
     * @throws ModelException
     */
    public boolean fireEvent(final String event) throws ModelException {
        if (event == null) {
            return engine.getCurrentStatus().isFinal();
        }
        engine.triggerEvent(new TriggerEvent(event,
            TriggerEvent.SIGNAL_EVENT, null));
        //To remember...
        this.historyEvents.add(event);
        return engine.getCurrentStatus().isFinal();
    }

    /**
     * Returns a list with all possible transitions.
     *
     * @return List with all String names of the possible transitions.
     */
    public List<String> getTransitionsStringList() {
        Set<State> states = engine.getCurrentStatus().getStates();
        List<Transition> transitions = new ArrayList<>();
        for (State s: states) {
            for (Object t: s.getTransitionsList()) {
                if (!transitions.contains((Transition) t)) {
                    transitions.add((Transition) t);
                }
            }
        }
        List<String> list = new ArrayList<>();

        for (Transition t: transitions) {
            list.add(t.getEvent());
        }
        
        return list;
    }


    /**
     * Returns the keyName
     *
     * @return String
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Set the keyName
     *
     * @param keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public String toString() {
        return "StateMachine [engine=" + engine + ", stateMachine=" + stateMachine + ", keyName=" + keyName + "]";
    }

}
