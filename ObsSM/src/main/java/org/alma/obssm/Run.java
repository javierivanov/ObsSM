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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.MissingFormatArgumentException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alma.obssm.net.LineReader;
import org.alma.obssm.net.LineReaderImpl2;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.StateMachineManager;
import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;

/**
 * Main class, initialize the State Machines and Parsers and runs the interpreter only.
 *
 * @version 0.2
 * @author Javier Fuentes
 *
 */
public class Run {

	/*
	 * Some filter options. If the array is empty, it will show everything.
	 */
	public static final String KEYNAME_FILTER[] = {};
	public static final boolean SHOW_TIMESTAMP = false;
	
	/**
	 * Main function. Initialize the Run class..
	 * @param args
	 * @throws FileNotFoundException
	 */
    public static void main(String args[]) throws FileNotFoundException
    {
    	if (args.length == 0)
    	{
    		throw new MissingFormatArgumentException("The model path is required");
    	}
    	

    	File f = new File(args[0]);

    	if (!f.isDirectory())
    	{
    		throw new FileNotFoundException("The model and transitions folder has not been found");
    	}
    	
    	if (!new File(args[0]+"/model.xml").exists() || !new File(args[0]+"/transitions.json").exists())
    	{
    		throw new FileNotFoundException("File models (model.xml and transitions.json) have not been found on path: " + f.getAbsolutePath());
    	}

    	int port = 8888;

    	if (args.length == 2)
    	{
    		port = Integer.parseInt(args[1]);
    	}


        @SuppressWarnings("unused")
		Run run = new Run(f.getPath(), port);
    }

    /**
     * Constructor of the class, who runs the interpreter
     */  
    public Run(String filepathname, int port)
    {
        try {
        	/**
        	 * Setting up server and models files.
        	 */
            LineReader lr = new LineReaderImpl2(port);
            System.out.println(new Timestamp(System.currentTimeMillis()) +" SM Server on the line!");
            Parser p = new Parser(filepathname + "/transitions.json");
            
            StateMachineManager smm = new StateMachineManager(filepathname + "/model.xml");
            /**
             * First state machine. It will wait for a keyName
             */
			smm.addNewStateMachine();

            System.out.println(new Timestamp(System.currentTimeMillis()) +" Loop started and waiting for logs on port: " + port);
            while (true)
            {
            	/*
            	 * Init connection
            	 */
            	if(!lr.isCommunicationActive()) lr.startCommunication();
            	/*
            	 * Reads a line
            	 */
            	String line = lr.waitForLine();
            	if (line == null) continue;
            	if (line.equals("EOF")) break;
            	/*
            	 * Parse the read line.
            	 */
            	String parsedAction = p.getParseAction(line, smm.getAllPossiblesTransitions());
    			String keyName = p.getKeyName(line, parsedAction);
    			smm.findAndTriggerAction(parsedAction, keyName);
            }
            
            lr.endCommunication();
            System.out.println(new Timestamp(System.currentTimeMillis()) +" Loop ended");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModelException | SAXException ex) {
        	Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

}
