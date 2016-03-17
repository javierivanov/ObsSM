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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Line reader implementation
 *
 * @author Javier Fuentes
 * @version 0.2
 *
 */
public class LineReaderImpl2 implements LineReader {

    private Socket socket;
    private ServerSocket server;
    private Scanner scanner;
    private boolean active;

    public LineReaderImpl2(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.active = false;
    }

    @Override
    public String waitForLine() throws IOException, InterruptedException {
        if (!this.scanner.hasNextLine()) {
            active = false;
            return null;
        };
        String line = this.scanner.nextLine();
        return line;
    }

    @Override
    public void endCommunication() throws IOException {
        if (this.socket == null) {
            return;
        }
        this.socket.close();
    }

    @Override
    public void startCommunication() throws IOException {
        this.endCommunication();
        this.socket = this.server.accept();
        this.scanner = new Scanner(new BufferedInputStream(this.socket.getInputStream()));
        this.active = true;
    }

    @Override
    public boolean isCommunicationActive() {
        return active;

    }

    @Override
    public void interrupt() {
        try {
            this.server.close();
        } catch (Exception e) {
        }
    }

}
