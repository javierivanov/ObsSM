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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Socket connection layer. It will requires more attention later.
 *
 * @deprecated 
 * TO DO: Create a more standard communication layer.
 * 
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.1
 *
 * @see LineReader
 * @see LineReaderImpl2
 */
public class ServerLineReader implements LineReader {

    private ServerSocket serverSocket;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ServerLineReader(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public String waitForLine() throws IOException {
        Socket client = this.serverSocket.accept();
        Scanner s = new Scanner(client.getInputStream());
        String out = s.nextLine();
        s.close();
        client.close();
        return out;
    }

    @Override
    public void endCommunication() throws IOException {
        this.serverSocket.close();
    }

    public boolean isCommunicationActive() {
        return true;
    }

    @Override
    public void startCommunication() throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void interrupt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
