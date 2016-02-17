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
 * 
 * @autor Javier Fuentes j.fuentes.m(at)icloud.com
 * @version 0.1
 * 
 *******************************************************************************/

package org.alma.obssm.sm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class StateMachine {
    
    
    protected SCXMLExecutor engine;
    protected SCXML stateMachine;
    
    
    public StateMachine(String xmlFile) throws IOException, ModelException, SAXException
    {
        
        this.stateMachine = SCXMLParser.parse(new File(xmlFile).toURI().toURL(),
                new SimpleErrorHandler());
        
        
        this.engine = new SCXMLExecutor(new JexlEvaluator(), new SimpleDispatcher(),
                new SimpleErrorReporter());
        this.engine.setStateMachine(this.stateMachine);
        this.engine.setSuperStep(true);
        this.engine.setRootContext(new JexlContext());
        this.engine.addListener(this.stateMachine, new CustomEntryListener());
        
        engine.go();
    }
    
    
    
    public boolean fireEvent(final String event) throws ModelException {
        if (event == null) return engine.getCurrentStatus().isFinal();
        
        TriggerEvent[] evts = {new TriggerEvent(event,
                TriggerEvent.SIGNAL_EVENT, null)};
        engine.triggerEvents(evts);
        
        return engine.getCurrentStatus().isFinal();
    }
    
    public String getCurrentStateId() {
        Set<?> states = engine.getCurrentStatus().getStates();
        State state = (State) states.iterator().next();
        return state.getId();
    }
    
    public List<String> getTransitionsStringList()
    {
        List<Transition> transitions = getCurrentState().getTransitionsList();
        List<String> list = new ArrayList<>();
     
        transitions.stream().forEach((t) -> {
            list.add(t.getEvent());
        });
        return list;
    }
    
    public List<Transition> getTransitionsList()
    {
        return getCurrentState().getTransitionsList();
    }
    
    public State getCurrentState() {
        Set<?> states = engine.getCurrentStatus().getStates();
        return ( (State) states.iterator().next());
    }
    

}