package org.alma.obssm.net;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javier
 */
public class SimulationImpl implements LineReader{

    private String[] fifo;
    private int i=0;

    /**
     * This is a custom LineReader. It reads data from a local file.
     * @param example
     */
    public SimulationImpl(String example) {
        fifo = example.split("&");
        for (int a=0; a < fifo.length; a++) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulationImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            fifo[a] = ts.toString().replace(" ","T") + " Array000" + fifo[a];
        }
    }
    
    
    
    
    @Override
    public String waitForLine() throws IOException, InterruptedException {
        if (i==fifo.length) return "EOF";
        return fifo[i++];
    }

    @Override
    public void endCommunication() throws IOException {
        
    }

    @Override
    public void interrupt() {
        
    }

    @Override
    public void startCommunication() throws IOException, ParseException {
        
    }

    @Override
    public boolean isCommunicationActive() {
        return i!=fifo.length;
    }
    
}
