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
package org.alma.obssm.ui;

import org.alma.obssm.Manager;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.sm.EntryListener;

/**
 *
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 * @version 0.4
 */
public interface UICoreActions {
    /**
     * Returns a LineReader Object which allow to Core to Read data from any source.
     * @return it returns a LineReader Object
     */
    LineReader initialize();
    /**
     * Get a reference to Manager Instance.
     * @return Manager Object.
     */
    Manager getManager();
    /**
     * Actions to take if the files are not found.
     */
    void filesNotFound();
    /**
     * Actions to take before to start the communications using LineReader.
     * For example, using the GUI, change the status bar info.
     */
    void beforeStartCommunications();
    /**
     * Actions to take before to start the loop of retrieving data.
     */
    void afterStartCommunications();
    /**
     * Actions to take on each line.
     * @param line of log to process.
     */
    void actionsPerLine(String line);
    /**
     * Returns a new Instance of an EntryListener.
     * @return EntryListener.
     */
    EntryListener getEntryListener();
    /**
     * Actions to take when the loop ends.
     */
    void loopEnd();
    /**
     * Actions to take if some exception is thrown
     * @param ex 
     */
    void exceptions(Exception ex);
    /**
     * Last actions to take, this is the end of the execution.
     */
    void cleanUp();
}
