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
package org.alma.obssm.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import org.alma.obssm.Manager;
import org.alma.obssm.parser.Parser;
import org.alma.obssm.sm.StateMachineManager;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 *
 * Configuration graphical interface.
 *
 * @version 0.4
 * @author Javier Fuentes Munoz j.fuentes.m@icloud.com
 */
public class ObsSMPanelConf extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final Manager m;

    private JLabel show_label;
    private JLabel scxml_label;
    private JLabel json_label;
    private JLabel elkurl_label;
    private JTextField scxml_file;
    private JTextField json_file;
    private JTextField elk_url;
    private JButton scxml_button;
    private JButton json_button;
    private JLabel query_label;
    private JTextPane query;
    private JScrollPane query_scroll;

    private JButton save_button, cancel_button;

    public ObsSMPanelConf(Manager m) {
        super("Configuration Panel");
        this.m = m;
        initialize();
        initializeListeners();
    }

    private void initialize() {
        setAlwaysOnTop(true);
        JPanel panelPrincipal = new JPanel();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        GridBagConstraints gbc = new GridBagConstraints();
        panelPrincipal.setLayout(new GridBagLayout());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.weightx = 1;

        gbc.gridwidth = 3;
        gbc.gridheight = 1;

        show_label = new JLabel("Configuration Panel:");
        show_label.setFont(new Font(show_label.getFont().getName(), Font.BOLD, show_label.getFont().getSize() + 6));
        panelPrincipal.add(show_label, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BASELINE;
        scxml_label = new JLabel("SCXML Model file:");
        panelPrincipal.add(scxml_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        scxml_file = new JTextField("model.xml", 20);
        panelPrincipal.add(scxml_file, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 2;
        gbc.gridy = 1;
        scxml_button = new JButton("Select file");
        panelPrincipal.add(scxml_button, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        json_label = new JLabel("JSON log translate file: ");
        panelPrincipal.add(json_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        json_file = new JTextField("log_translate.json", 20);
        panelPrincipal.add(json_file, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 2;
        gbc.gridy = 2;
        json_button = new JButton("Select file");
        panelPrincipal.add(json_button, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 3;
        elkurl_label = new JLabel("ElasticSearch URL:");
        panelPrincipal.add(elkurl_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        elk_url = new JTextField(m.ELKUrl, 20);
        panelPrincipal.add(elk_url, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        query_label = new JLabel("<html>Elastic search query:<br><br>"
                + "<i>You must replace this variables</i><br>"
                + "<b>$T1</b>: <i>TimeStamp start</i><br>"
                + "<b>$T2</b>: <i>TimeStamp stop</i><br>"
                + "<b>$Q</b>: <i>Query</i></html>");
        panelPrincipal.add(query_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        query = new JTextPane();
        query_scroll = new JScrollPane(query);
        query.setContentType("text/html");
        panelPrincipal.add(query_scroll, gbc);

        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.CENTER;

        gbc.gridx = 1;
        gbc.gridy = 8;
        cancel_button = new JButton("Cancel");
        panelPrincipal.add(cancel_button, gbc);

        gbc.gridx = 2;
        gbc.gridy = 8;
        save_button = new JButton("Save");
        panelPrincipal.add(save_button, gbc);
        add((panelPrincipal));
        pack();
        setSize((int)(getSize().width*1.5), (int)(getSize().height*1.5));
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            query.setText(toHTML(m.default_query_base));
            elk_url.setText(m.ELKUrl);
        }
        super.setVisible(b); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String toHTML(String input) {
        input = Jsoup.parse(input).text();
        StringBuilder htmlOutput = new StringBuilder();
        htmlOutput.append("<html>");
        htmlOutput.append(
                input.replace("\n", "<br>")
                .replace("$Q", "<b>$Q</b>")
                .replace("$T1", "<b>$T1</b>")
                .replace("$T2", "<b>$T2</b>")
        );
        htmlOutput.append("</html>");
        return htmlOutput.toString();
    }
    
    private void toBold() {
        String in2 = Jsoup.clean(query.getText(), new Whitelist().addTags("br"));
        in2 = toHTML(in2);
        query.setText(in2);
    }
    
    private void initializeListeners() {
        
        scxml_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSCXMLFile();
            }
        });
        
        json_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openJSONFile();
            }
        });
        
        save_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        
        cancel_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        
        query.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                toBold();
            }

            @Override
            public void focusLost(FocusEvent e) {
                toBold();
            }
        });
    }

    private void save() {
        try {
            String text = Jsoup.parse(query.getText()).text();
            m.default_query_base = text;
            m.ELKUrl = elk_url.getText();
            m.smm = new StateMachineManager(scxml_file.getText());
            m.parser = new Parser(json_file.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
        setVisible(false);
    }

    public void cancel() {
        this.setVisible(false);
    }
    
    private void openSCXMLFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            scxml_file.setText(f.getAbsolutePath());
        }
    }
    
    private void openJSONFile(){
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
                json_file.setText(f.getAbsolutePath());
        }
    }
}
