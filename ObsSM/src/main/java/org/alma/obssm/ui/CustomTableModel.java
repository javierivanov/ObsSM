package org.alma.obssm.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class CustomTableModel extends DefaultTableModel {

    private final List<Color> colorRow = new ArrayList<>();
    
    public CustomTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public void addRow(Object[] rowData, Color color) {
        super.addRow(rowData);
        colorRow.add(color);
    }

    @Override
    public void addRow(Object[] rowData) {
        addRow(rowData, null);
    }

    public Color getRowColor(int row) {
        return colorRow.get(row);
    }
}
