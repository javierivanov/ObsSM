package org.alma.obssm;


import org.alma.obssm.sm.StateMachine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alma.obssm.net.ServerLineReader;
import org.alma.obssm.parser.Parser;
import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;



public class Run {
    public static void main(String args[]) {
        Run run = new Run();
    }
    
    public Run()
    {
        try {
            ServerLineReader slr = new ServerLineReader(8888);
            System.out.println("Server on the line!");
            StateMachine sm = new StateMachine("/Users/javier/Desktop/ObsSM.xml");
            System.out.println("SCXML parsed");
            Parser p = new Parser("/Users/javier/Desktop/ObsSM.json");
            System.out.println("JSON transitions subjects parsed");
            while(true)
            {
            	System.out.println("Loop started and waiting for logs on port" + slr.getServerSocket().getLocalPort());
                String line = slr.waitForLine();
                sm.fireEvent(p.getParseAction(line, sm.getTransitionsStringList()));
                if (line.equals("EOF")) 
                {
                    slr.killserver();
                    break;
                }
            }
            
            System.out.println("Loop ended");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModelException | SAXException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
