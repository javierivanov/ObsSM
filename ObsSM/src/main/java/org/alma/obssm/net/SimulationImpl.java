/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javier
 */
public class SimulationImpl implements LineReader{

    private LinkedList<String> fifo;
    public SimulationImpl(String example) {
        fifo = new LinkedList<>();
        try {
            Scanner s = new Scanner(new File(example));
            while (s.hasNext()) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                fifo.add(ts.toString() + " Array000 " + s.nextLine());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimulationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    @Override
    public String waitForLine() throws IOException, InterruptedException {
        return fifo.removeFirst();
    }

    @Override
    public void endCommunication() throws IOException {
        
    }

    @Override
    public void interrupt() {
        
    }

    @Override
    public void startCommunication() throws IOException, MalformedURLException, ParseException {
        
    }

    @Override
    public boolean isCommunicationActive() {
        return !fifo.isEmpty();
    }
    
}
