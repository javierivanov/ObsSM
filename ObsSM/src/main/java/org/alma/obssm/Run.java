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
package org.alma.obssm;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class, initialize the State Machines and Parsers and runs the
 * interpreter only.
 *
 * TO DO: Upgrade to allow to run with the Manager class.
 *
 * @version 0.4
 * @author Javier Fuentes j.fuentes.m@icloud.com
 *
 * @see Manager
 */
public class Run {
    /*
     * Some filter options. If the array is empty, it will show everything.
     */
    public static String KEYNAME_FILTER[] = {};
    public static boolean SHOW_TIMESTAMP = false;
    public static boolean SIMUL = false;
    /**
     * Main function. Initialize the Run class..
     *
     * @param args
     */
    public static void main(String args[]) {
        if (args.length == 1)
            if (args[0].equals("SIMUL")) SIMUL = true;
        Manager m = new Manager();
        m.osmPanel.setVisible(true);
        Logger.getLogger(Run.class.getName()).log(Level.INFO,"Manager Running");
    }

}
