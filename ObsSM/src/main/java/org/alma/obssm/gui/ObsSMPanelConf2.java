/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.gui;


import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import org.alma.obssm.Manager;

/**
 *
 * @author javier
 */
public class ObsSMPanelConf2 extends JFrame{
    final private Manager m;
    public ObsSMPanelConf2(Manager m){
        super("Configuration Pane");
        this.m = m;
        initialize();
    }
    
    private void initialize() {
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(200, 200);
        
        setLayout(new FlowLayout());
        
        
        JLabel ScxmlLabel = new JLabel("SCXML Model File: ");
        JLabel JsonLabel = new JLabel("JSON Log Translate File: ");
        JLabel inputLabel = new JLabel("JSON Log Translate File: ");
        JLabel outputLabel = new JLabel("JSON Log Translate File: ");
        
        
        JTextField scxmlField = new JTextField("custom", 15);
        JTextField jsonField = new JTextField("custom", 15);
        
        
        String inputOptions[] = {"ino","dos"};
        String outputOptions[] = {"ino","dos"};
        JComboBox<String>  inputList = new JComboBox<>(inputOptions);
        JComboBox<String>  outputList = new JComboBox<>(outputOptions);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        add(ScxmlLabel);
        add(scxmlField);
        add(JsonLabel);
        add(jsonField);
        add(inputLabel);
        add(inputList);
        add(outputLabel);
        add(outputList);
        
        add(saveButton);
        add(cancelButton);
                setVisible(true);
    }
    
}
