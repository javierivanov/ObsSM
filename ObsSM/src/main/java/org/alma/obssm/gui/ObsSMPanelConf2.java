/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.alma.obssm.Manager;

/**
 *
 * @author javier
 */
public class ObsSMPanelConf2 extends JFrame {

    final private Manager m;

    public ObsSMPanelConf2(Manager m) {
        super("Configuration Pane");
        this.m = m;
        initialize();
    }

    private void initialize() {
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(300, 300);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        mainPanel.setLayout(new FlowLayout());

        JLabel ScxmlLabel = new JLabel("SCXML Model File:");
        JLabel JsonLabel = new JLabel("JSON Log Translate File:");
        JLabel inputLabel = new JLabel("   Input data format   ");
        JLabel outputLabel = new JLabel("   Output data format   ");

        JTextField scxmlField = new JTextField("custom", 15);
        JTextField jsonField = new JTextField("custom", 15);

        String inputOptions[] = {"ElasticSearch ", "XML Logs Server"};
        String outputOptions[] = {"Table model", "Optionals PLugins"};
        JComboBox<String> inputList = new JComboBox<>(inputOptions);
        JComboBox<String> outputList = new JComboBox<>(outputOptions);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        mainPanel.add(ScxmlLabel);
        mainPanel.add(scxmlField);
        mainPanel.add(JsonLabel);
        mainPanel.add(jsonField);
        mainPanel.add(inputLabel);
        mainPanel.add(inputList);
        mainPanel.add(outputLabel);
        mainPanel.add(outputList);

        mainPanel.add(saveButton);
        mainPanel.add(cancelButton);
        setMaximumSize(new Dimension(300, 300));
        setMinimumSize(new Dimension(250, 250));
        add(mainPanel, BorderLayout.CENTER);
    }

}
