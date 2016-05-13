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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.alma.obssm.Manager;

/**
 *
 * Configuration graphical interface.
 * 
 * @version 0.4
 * @author Javier Fuentes j.fuentes.m@icloud.com
 */
public class ObsSMPanelConf extends JFrame {

    final private Manager m;

    public ObsSMPanelConf(Manager m) {
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
