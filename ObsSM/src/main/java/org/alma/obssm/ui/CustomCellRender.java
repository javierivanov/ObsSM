/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alma.obssm.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author 0xffa
 */
public class CustomCellRender extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            CustomTableModel ctm = (CustomTableModel) table.getModel();
            
            label.setBackground(ctm.getRowColor(row));
            
            return label;
        }
    }
