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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.alma.obssm.ui.ObsSMPanel;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.StateMachineManager;

/**
 * This class manages all components, with the purpose of interconnecting
 * objects between them.
 *
 * TO DO: Allow to launch a console mode operation.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.4
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
    
    public String default_query_base = "";
    public String ELKUrl = "http://elk-master.osf.alma.cl:9200";

    public static final boolean SIMUL = false;
    /**
     * This constructor launches the Panel for visual operation.
     */
    public Manager() {

        osmPanel = new ObsSMPanel(this);
    }

    /**
     *
     * Returns a temporal file for a internal resource.
     *
     * @param file
     * @return temporal file.
     * @throws IOException
     */
    public File getResourceFiles(String file) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File f = File.createTempFile("temp-file", ".temp");

        try (FileWriter fw = new FileWriter(f)) {
            String tempLine;
            
            while (true) {
                tempLine = br.readLine();
                if (tempLine == null) {
                    break;
                }
                fw.append(tempLine);
            }
        }

        return f;
    }
    
    /**
     * 
     * Return a String with the contents of the resource file.
     * 
     * @param file
     * @return
     * @throws IOException 
     */

    public String getResourceString(String file) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String tempLine;
        StringBuilder sb = new StringBuilder();
        while (true) {
            tempLine = br.readLine();
            if (tempLine == null) {
                break;
            }
            sb.append(tempLine);
        }
        return sb.toString();
    }

}
