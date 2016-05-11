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

package org.alma.obssm.gui;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.alma.obssm.Manager;
import org.alma.obssm.net.LineReader;
import org.alma.obssm.net.LineReaderImpl2;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.EntryListener;
import org.alma.obssm.sm.GuiEntryListener;
import org.alma.obssm.sm.StateMachine;
import org.alma.obssm.sm.StateMachineManager;
import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;

/**
 *
 * @author Javier Fuentes j.fuentes.m@icloud.com
 * @version 0.3
 */
public class ObsSMPanel extends javax.swing.JFrame {

    /**
     * Creates new a form Panel.
     */
    private final Manager m;

    private String xmlFileName, jsonFileName;
    
    private GuiStatesBase LISTENING_STATE, STOPED_STATE;
    
    private GuiStatesBase STATE;
    
    public ObsSMPanel(Manager m) {
        this.m = m;
        LISTENING_STATE = new GuiListeningState(m);
        STOPED_STATE = new GuiStopedState(m);
        
        STATE = STOPED_STATE;
        componentsSetup();
        /**
         * Runs a thread checking for active Arrays.
         */
        checkActiveArrays();
    }
    
    
    /**
     * Setup for customs components.
     */
    private void componentsSetup() {
        try {
            /**
             * Standard look and feel.
             */
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        this.setLocationRelativeTo(null);
        stopButton.setEnabled(false);
        portSpinner.setValue(8888);
        
        /**
         * Remove thousand separator.
         */
        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
        statusLabel.setText("Waiting for Start!");
        restartButton.setEnabled(false);
        /**
         * Default files.
         */
        //if (JOptionPane.showConfirmDialog(this, "Search for files in ../models/ ?") == JOptionPane.OK_OPTION) {
            try {
                m.smm = new StateMachineManager("../models/model_interferometry.xml");
                xmlFileLabel.setText("model_interferometry.xml");
                xmlFileName = "../models/model_interferometry.xml";
                xmlFileLabel.setForeground(Color.green);
                m.parser = new Parser("../models/log_translate_interferometry.json");
                jsonFileLabel.setText("log_translate_interferometry.json");
                jsonFileName = "../models/log_translate_interferometry.json";
                jsonFileLabel.setForeground(Color.green);
            } catch (Exception e) {
            }
        //}
    }
    
    /**
     * This method start a thread to check periodically is there a 
     * new active Array, it must be launched once.
     */
    private void checkActiveArrays() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    DefaultListModel dlm = new DefaultListModel();
                    if (m.smm != null) {
                        for (StateMachine m: m.smm.getStateMachines()) {
                            if (m.getKeyName() != null) {
                                dlm.addElement(m.getKeyName() + " -> " + m.getCurrentStateId());
                            }
                        }
                    }

                    arraysActiveList.setModel(dlm);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jsonFileLabel = new javax.swing.JLabel();
        xmlFileLabel = new javax.swing.JLabel();
        jsonFileButton = new javax.swing.JButton();
        xmlFileButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        startButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        portSpinner = new javax.swing.JSpinner();
        stopButton = new javax.swing.JButton();
        arrayComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        arrayTextArea = new javax.swing.JTextArea();
        arrayProgressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        restartButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        arraysActiveList = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel1.setText("Select the states and model files");

        jsonFileLabel.setText("JSON File");

        xmlFileLabel.setText("XML File");

        jsonFileButton.setText("Select JSON File");
        jsonFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonFileButtonActionPerformed(evt);
            }
        });

        xmlFileButton.setText("Select XML File");
        xmlFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xmlFileButtonActionPerformed(evt);
            }
        });

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Port:");

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        arrayComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>());

        arrayTextArea.setColumns(20);
        arrayTextArea.setRows(5);
        jScrollPane1.setViewportView(arrayTextArea);

        jLabel2.setText("Select Array");

        statusLabel.setText("jLabel3");

        restartButton.setText("Clean");
        restartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartButtonActionPerformed(evt);
            }
        });

        arraysActiveList.setModel(new javax.swing.DefaultListModel<String>());
        jScrollPane2.setViewportView(arraysActiveList);

        jLabel3.setText("Active Arrays");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jsonFileLabel)
                                .addGap(18, 18, 18)
                                .addComponent(jsonFileButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(xmlFileLabel)
                                .addGap(18, 18, 18)
                                .addComponent(xmlFileButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(portSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(arrayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(arrayProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startButton)
                                .addGap(18, 18, 18)
                                .addComponent(stopButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(restartButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(0, 0, Short.MAX_VALUE)))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jsonFileLabel)
                    .addComponent(xmlFileLabel)
                    .addComponent(jsonFileButton)
                    .addComponent(xmlFileButton))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(portSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stopButton)
                    .addComponent(jLabel4)
                    .addComponent(statusLabel)
                    .addComponent(restartButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(arrayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(arrayProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void xmlFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xmlFileButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
        fc.setFileFilter(filter);
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                m.smm = new StateMachineManager(fc.getSelectedFile().getCanonicalPath());
                xmlFileLabel.setText(fc.getSelectedFile().getName());
            } catch (IOException ex) {
                Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_xmlFileButtonActionPerformed

    private void jsonFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonFileButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        fc.setFileFilter(filter);
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                m.parser = new Parser(fc.getSelectedFile().getCanonicalPath());
                jsonFileLabel.setText(fc.getSelectedFile().getName());
            } catch (IOException ex) {
                Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jsonFileButtonActionPerformed

    /**
     * This method is executed when start Button is clicked. 
     * It defines basics steps in order to run the server and read the logs lines.
     * 
     * In addition when the @StateMachineManager is instantiated, the listener used is 
     * @GuiEntryListener, this listener could be modified and inherited by other Listener.
     * 
     * @param evt NULL
     * 
     * @see StateMachineManager
     * @see LineReaderImpl2
     * @see LineReader
     * @see EntryListener
     * @see GuiEntryListener
     */
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        if (m.smm == null && m.parser == null) {
            JOptionPane.showMessageDialog(this, "You have to select json and xml files first");
            return;
        }
        
        STATE.startListening();
        STATE = LISTENING_STATE;
        
        m.mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m.lr = new LineReaderImpl2((Integer) (portSpinner.getValue()));
                    statusLabel.setText("Waiting a connection");
                    m.lr.startCommunication();
                    statusLabel.setText("Connected");
                    while (m.lr.isCommunicationActive()) {
                        statusLabel.setText("Listening data");
                        String line = m.lr.waitForLine();
                        if (line != null) {
                            if ("EOF".equals(line)) {
                                stopButtonActionPerformed(null);
                                statusLabel.setText("EOF reached");
                                break;
                            }
                            String event;
                            String keyName;

                            if (!m.smm.isSMIdleAvailable()) {
                                m.smm.addNewStateMachine(new GuiEntryListener(m));
                            }

                            event = m.parser.getParseAction(line, m.smm.getAllPossiblesTransitions());
                            keyName = m.parser.getKeyName(line, event);

                            m.smm.findAndTriggerAction(event, keyName);

                        }

                    }
                } catch (IOException | InterruptedException | ModelException | SAXException ex) {
                    Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
                    statusLabel.setText("Something goes wrong: " + ex.getMessage());
                }
            }
        });
        m.mainThread.start();
    }//GEN-LAST:event_startButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        // TODO add your handling code here:
        STATE.stopListening();
        STATE = STOPED_STATE;
        m.lr.interrupt();
        try {
            m.lr.endCommunication();
        } catch (IOException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        m.mainThread.interrupt();

    }//GEN-LAST:event_stopButtonActionPerformed

    private void restartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartButtonActionPerformed
        // TODO add your handling code here:
        STATE.reset();
        STATE = STOPED_STATE;
        
        m.lr.interrupt();
        try {
            m.lr.endCommunication();
        } catch (IOException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        m.mainThread.interrupt();
        
        m.smm = new StateMachineManager(xmlFileName);
        try {
            m.parser = new Parser(jsonFileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        statusLabel.setText("Restarted");
    }//GEN-LAST:event_restartButtonActionPerformed

    public JComboBox<String> getArrayComboBox() {
        return arrayComboBox;
    }

    public JProgressBar getArrayProgressBar() {
        return arrayProgressBar;
    }

    public JTextArea getArrayTextArea() {
        return arrayTextArea;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox<String> arrayComboBox;
    public javax.swing.JProgressBar arrayProgressBar;
    public javax.swing.JTextArea arrayTextArea;
    public javax.swing.JList<String> arraysActiveList;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JSeparator jSeparator1;
    public javax.swing.JSeparator jSeparator2;
    public javax.swing.JButton jsonFileButton;
    public javax.swing.JLabel jsonFileLabel;
    public javax.swing.JSpinner portSpinner;
    public javax.swing.JButton restartButton;
    public javax.swing.JButton startButton;
    public javax.swing.JLabel statusLabel;
    public javax.swing.JButton stopButton;
    public javax.swing.JButton xmlFileButton;
    public javax.swing.JLabel xmlFileLabel;
    // End of variables declaration//GEN-END:variables
}
