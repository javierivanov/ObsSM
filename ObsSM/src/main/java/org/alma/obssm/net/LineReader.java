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
package org.alma.obssm.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * This interface provides a basic communication layer.
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.4
 *
 */
public interface LineReader {

    /**
     * This method has to lock the execution until a line is delivered.
     * 
     * @return A line read from the sender
     * @throws IOException
     * @throws InterruptedException 
     */
    public String waitForLine() throws IOException, InterruptedException;

    
    /**
     * This method has to end the communications.
     * 
     * @throws IOException 
     */
    public void endCommunication() throws IOException;

    /**
     * This method has to interrupt the communication and unlock the waitForLine method.
     */
    public void interrupt();
    
    /**
     * This method has to initialize the communications.
     * 
     * @throws IOException 
     * @throws java.net.MalformedURLException 
     * @throws java.text.ParseException 
     */
    public void startCommunication() throws IOException, MalformedURLException, ParseException;

    
    /**
     * This method has to check the status of the communication.
     * @return true if is connected, false otherwise.
     */
    public boolean isCommunicationActive();
}
