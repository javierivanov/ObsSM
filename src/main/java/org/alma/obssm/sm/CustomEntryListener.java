package org.alma.obssm.sm;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

public class CustomEntryListener implements SCXMLListener {

    @Override
    public void onEntry(TransitionTarget state) {
        System.out.println("STATE: " + state.getId());
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