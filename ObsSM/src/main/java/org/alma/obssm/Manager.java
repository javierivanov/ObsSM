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


package org.alma.obssm;

import org.alma.obssm.gui.ObsSMPanel;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.StateMachineManager;

/**
 * This class manages all components, with the purpose of interconnecting 
 * objects between them.
 * 
 * TO DO: Allow to launch a console mode operation.
 * 
 * @author Javier Fuentes
 * @version 0.3
 * @see StateMachineManager
 * @see LineReader
 * @see ObsSMPanel
 * @see Parser
 *  
 */


public class Manager {
    public StateMachineManager smm;
    public LineReader lr;
    public ObsSMPanel osmPanel;
    public Parser parser;
    public Thread mainThread;
    
    /**
     * This constructor launches the Panel for visual operation.
     */
    public Manager() {
        
        osmPanel = new ObsSMPanel(this);
    }
}
