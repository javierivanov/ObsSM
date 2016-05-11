package org.alma.obssm.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import org.alma.obssm.Manager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author javier
 */
public class ObsSMPanel2 extends JFrame {

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
    private DefaultTableModel tablemodel;
    private JTable table;

    public ObsSMPanel2(Manager m) {
        super("ObsSM2 Panel");
        this.m = m;
        initialize();
    }

    private void initialize() {
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        new ObsSMPanelConf2(m);

        setMenuBar();

        String[] columnNames = {"TimeStamp",
            "Array",
            "Event",
            "State From",
            "State To"};

        searchPanel = new JPanel(new FlowLayout());

        dfrom = new JTextField("TimeStamp from", 13);
        dto = new JTextField("TimeStamp to", 13);
        queryLabel = new JLabel("Query: ");
        query = new JTextField("*", 13);
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
        JLabel statusLabel = new JLabel("Status fuck yeah!");
        statusPanel.add(statusLabel);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        add(statusPanel, BorderLayout.SOUTH);
    }

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

}
