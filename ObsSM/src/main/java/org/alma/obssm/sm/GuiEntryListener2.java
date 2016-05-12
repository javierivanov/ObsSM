/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.sm;

import org.alma.obssm.Manager;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * @author javier
 */
public class GuiEntryListener2 extends EntryListener {

    public GuiEntryListener2(Manager m) {
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
        m.osmPanel2.tablemodel.addRow(new String[]{timeStamp, array, transition.getEvent(), from.getId(), to.getId()});
    }

}
