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

import org.alma.obssm.ui.ObsSMPanel;
import org.alma.obssm.ui.UICoreActions;
import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;


import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class Execute the procedure to search and parse data.
 * 
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 * @version 1.0
 * @see UICoreActions
 * 
 */
public class Core {

    /**
     * Initialize the data retrieving process. Use a given data LineReader.
     * 
     * @param actions 
     */
    public static void startSearch(UICoreActions actions) {
        actions.initialize();
        try {
            actions.getManager().lr = actions.initialize();
            //Checking the SM Manager and JSON Log parser
            if (actions.getManager().smm == null || actions.getManager().parser == null) {
                actions.filesNotFound();
            }

            actions.beforeStartCommunications();
            //Starting up the communications!
            //in the case of ElasticSearch input it is retrieving data from ElastickSearch.
            actions.getManager().lr.startCommunication();
            actions.afterStartCommunications();
            while (actions.getManager().lr.isCommunicationActive()) {
                String line = actions.getManager().lr.waitForLine();
                actions.actionsPerLine(line);
                if (line != null) {
                    if ("EOF".equals(line)) {
                        break;
                    }
                    String event;
                    String keyName;

                    if (!actions.getManager().smm.isSMIdleAvailable()) {
                        actions.getManager().smm.addNewStateMachine(actions.getEntryListener());
                    }

                    event = actions.getManager().parser.getParseAction(line, actions.getManager().smm.getAllPossiblesTransitions());
                    keyName = actions.getManager().parser.getKeyName(line, event);
                    actions.getManager().parser.saveExtraData(line, keyName, event);
                    actions.getManager().smm.findAndTriggerAction(event, keyName);
                }
            }
            actions.loopEnd();
        } catch (IOException | ParseException | ModelException | SAXException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
            actions.exceptions(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.INFO, null, ex);
            actions.exceptions(ex);
        } finally {
            actions.getManager().lr.interrupt();
            actions.cleanUp();
        }
    }
}
