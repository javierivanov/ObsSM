
package org.alma.obssm.net;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerLineReader {
    private ServerSocket serverSocket;




    public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public ServerLineReader(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public String waitForLine() throws IOException
    {
        Socket client = this.serverSocket.accept();
        Scanner s = new Scanner(client.getInputStream());
        String out = s.nextLine();
        s.close();
        client.close();
        return out;
    }


    public void killserver() throws IOException
    {
        this.serverSocket.close();
    }
}
