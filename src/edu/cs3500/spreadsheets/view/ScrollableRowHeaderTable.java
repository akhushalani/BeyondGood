package edu.cs3500.spreadsheets.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ScrollableRowHeaderTable implements ChangeListener, PropertyChangeListener {
  private RowHeaderTable table;
  private RowHeaderTable rowHeader;
  private JScrollPane scrollPane;

  public ScrollableRowHeaderTable(JScrollPane scrollPane) {
    this.scrollPane = scrollPane;

    table = (RowHeaderTable) scrollPane.getViewport().getView();
    table.addPropertyChangeListener(this);

    RowHeaderTableModel headerModel = new RowHeaderTableModel(table.getModel());
    rowHeader = new RowHeaderTable(headerModel, true);
    rowHeader.setModel(headerModel);
    rowHeader.setSelectionModel(table.getSelectionModel());
    rowHeader.setFocusable(false);

    rowHeader.setPreferredScrollableViewportSize(rowHeader.getPreferredSize());
    this.scrollPane.setRowHeaderView(rowHeader);
    this.scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, rowHeader.getTableHeader());

    this.scrollPane.getRowHeader().addChangeListener(this);
  }

  public JTable getRowHeader() {
    return rowHeader;
  }

  public JTable getTable() {
    return table;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JViewport viewport = (JViewport) e.getSource();
    scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("selectionModel")) {
      rowHeader.setSelectionModel(table.getSelectionModel());
    }

    if (evt.getPropertyName().equals("model")) {
      rowHeader.setModel(table.getModel());
    }
  }
}
