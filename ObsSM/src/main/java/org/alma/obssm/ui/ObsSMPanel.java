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

package org.alma.obssm.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import org.alma.obssm.Manager;
import org.alma.obssm.net.ElasticSearchImpl;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.GuiEntryListener;
import org.alma.obssm.sm.StateMachineManager;
import org.apache.commons.scxml.model.ModelException;
import org.xml.sax.SAXException;

/**
 *
 * Main graphical interface.
 * 
 * @version 0.4
 * @author Javier Fuentes Muñoz j.fuentes.m@icloud.com
 */
public class ObsSMPanel extends JFrame {

    private final Manager m;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem saveItem;
    private JMenuItem quitItem;
    private JMenu editMenu;
    private JMenuItem setConfItem;
    private JMenuItem startItem;
    private JMenuItem stopItem;
    private JPanel searchPanel;
    private JTextField dfrom;
    private JTextField dto;
    private JLabel queryLabel;
    private JTextField query;
    private JButton searchButton;

    private JScrollPane scrollTablePane;
    public DefaultTableModel tablemodel;
    public JTable table;

    JLabel statusLabel;
    private ObsSMPanelConf confPanel;

    private boolean dataSaved;
    
    private Thread mainThread;

    public ObsSMPanel(Manager m) {
        super("ObsSM2 Panel");
        this.m = m;
        initialize();
        initializeListeners();
        confPanel = new ObsSMPanelConf(m);
        dataSaved = true;

        //Default files
        try {
            //Right usage of a internal JAR files.
            m.parser = new Parser(m.getResourceFiles("log_translate.json").getAbsolutePath());
            m.smm = new StateMachineManager(m.getResourceFiles("model.xml").getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (m.parser == null || m.smm == null)
                 JOptionPane.showMessageDialog(m.osmPanel, "SM SCXML Model or JSON Log translator files are Missing!");
        }
       
    }

    
    
    /**
     *
     * Sets of all visual objects
     */
    private void initialize() {
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        setMenuBar();

        String[] columnNames = {"TimeStamp",
            "Array",
            "Event",
            "State From",
            "State To"};

        searchPanel = new JPanel(new FlowLayout());

        dfrom = new JTextField("2016-04-26T19:41:02.351", 13);
        dto = new JTextField("2016-04-26T19:50:00.000", 13);
        queryLabel = new JLabel("Query: ");
        query = new JTextField("Array: Array032", 13);
        searchButton = new JButton("GO!");

        searchPanel.add(dfrom);
        searchPanel.add(dto);
        searchPanel.add(queryLabel);
        searchPanel.add(query);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        tablemodel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tablemodel);
        table.setFillsViewportHeight(true);
        scrollTablePane = new JScrollPane(table);
        add(scrollTablePane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setSize(getWidth(), 10);
        statusLabel = new JLabel("Status fuck yeah!");
        statusPanel.add(statusLabel);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     *
     * Sets of all menubar objects
     */
    private void setMenuBar() {
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        saveItem = new JMenuItem("Save");
        quitItem = new JMenuItem("Quit");
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);

        editMenu = new JMenu("Edit");
        setConfItem = new JMenuItem("Configuration");
        startItem = new JMenuItem("Start search");
        stopItem = new JMenuItem("Stop");
        editMenu.add(setConfItem);
        editMenu.add(startItem);
        editMenu.add(stopItem);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Listeners for all interaction objects
     */
    private void initializeListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startThreadSearch();
            }
        });

        startItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startThreadSearch();
            }
        });

        setConfItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confPanel.setVisible(true);
            }
        });

        stopItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopThreadSearch();
            }
        });

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataSaved = false;
                saveData();
            }
        });

        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appClosing();
            }
        });

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                appClosing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

    }

    /**
     *
     * Prevents unexpected window closing without saving.
     */
    public void appClosing() {
        if (!dataSaved) {
            int res = JOptionPane.showConfirmDialog(m.osmPanel, "Closing de application without saving data?");
            if (JOptionPane.OK_OPTION == res) {
                System.exit(0);
            }
            if (JOptionPane.NO_OPTION == res) {
                saveData();
            }
        } else {
            dispose();
            System.exit(0);
        }
    }

    
    
    /**
     * 
     * Stops the current execution!
     */
    public void stopThreadSearch() {
        
        try {
            m.lr.endCommunication();
            m.lr.interrupt();
            mainThread.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     *
     * Starts the main thread and search data.
     */
    public void startThreadSearch() {

        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m.lr = new ElasticSearchImpl(dfrom.getText(), dto.getText(), query.getText());
                    //Checking the SM Manager and JSON Log parser
                    if (m.smm == null || m.parser == null) {
                        JOptionPane.showMessageDialog(m.osmPanel, "SM SCXML Model or JSON Log translator Missing!");
                        return;
                    }

                    statusLabel.setText("Searching data...");
                    //Starting up the communications!
                    //in the case of ElasticSearch input it is retrieving data from ElastickSearch.
                    m.lr.startCommunication();
                    statusLabel.setText("Ready!");
                    dataSaved = false;
                    while (m.lr.isCommunicationActive()) {
                        statusLabel.setText("Listening data...");
                        String line = m.lr.waitForLine();
                        System.out.println(line);
                        if (line != null) {
                            if ("EOF".equals(line)) {
                                break;
                            }
                            String event;
                            String keyName;

                            if (!m.smm.isSMIdleAvailable()) {
                                m.smm.addNewStateMachine(new GuiEntryListener(m));
                            }

                            event = m.parser.getParseAction(line, m.smm.getAllPossiblesTransitions());
                            keyName = m.parser.getKeyName(line, event);
                            m.parser.saveExtraData(line, keyName, event);

                            m.smm.findAndTriggerAction(event, keyName);
                        }
                    }
                    statusLabel.setText("Loop ended: data in table!");
                } catch (HeadlessException | IOException | ParseException | InterruptedException | ModelException | SAXException ex) {
                    Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
                    statusLabel.setText("Something wrong, check the logs!");
                }
            }
        });

        mainThread.start();
    }

    public void saveData() {
        if (dataSaved) {
            return;
        }
        JFileChooser f = new JFileChooser();
        
        if (f.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fg = f.getSelectedFile();
            Logger.getLogger(ObsSMPanel.class.getName()).log(Level.INFO, "File is going to be written");
            FileWriter fw = null;
            try {
                fg = new File(fg.toString() + ".csv");
                fg.createNewFile();
                fw = new FileWriter(fg);
                for (int row = 0; row < tablemodel.getRowCount(); row++) {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < tablemodel.getColumnCount(); col++) {
                        sb.append(tablemodel.getValueAt(row, col)).append(";");
                    }
                    sb.append("\n");
                    fw.append(sb);
                }
            } catch (IOException ex) {
                Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                    dataSaved = true;
                } catch (IOException ex) {
                    Logger.getLogger(ObsSMPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
