package edu.cs3500.spreadsheets.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.Worksheet;

/**
 * A visual view of a worksheet model.
 */
public class WorksheetVisualView implements WorksheetView {
  private Worksheet model;

  /**
   * Public constructor for the WorksheetVisualView class.
   *
   * @param model the model being represented by the view
   */
  public WorksheetVisualView(Worksheet model) {
    this.model = model;
  }

  @Override
  public void renderView() {
    JFrame frame = new JFrame();

    InfiniteScrollingTableModel tableModel = new InfiniteScrollingTableModel(model);

    RowHeaderTable table = new RowHeaderTable(tableModel, false);
    JScrollPane scrollPane = new JScrollPane(table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    scrollPane.getHorizontalScrollBar().addAdjustmentListener(e -> {
      if (e.getValueIsAdjusting()
              && e.getValue() == scrollPane.getHorizontalScrollBar().getMaximum()
              - scrollPane.getHorizontalScrollBar().getVisibleAmount()) {
        tableModel.fireScrollRight();
      }
    });

    scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
      if (e.getValue() == scrollPane.getVerticalScrollBar().getMaximum()
              - scrollPane.getVerticalScrollBar().getVisibleAmount()) {
        tableModel.fireScrollDown();
      }
    });

    frame.addMouseWheelListener(e -> {
      int notches = e.getWheelRotation();
      if (notches > 0 && !e.isShiftDown()) {
        tableModel.fireScrollDown();
      } else if (notches > 0) {
        tableModel.fireScrollRight();
      }
    });

    table.setColumnSelectionAllowed(true);
    table.setRowSelectionAllowed(true);

    table.setIntercellSpacing(new Dimension(-1, -1));

    table.setBorder(BorderFactory.createEmptyBorder());
    table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

    table.setGridColor(Color.lightGray);
    table.setSelectionBackground(new Color(179, 204,
            245));

    DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
    headerRenderer.setHorizontalAlignment(JLabel.CENTER);
    table.getTableHeader().setDefaultRenderer(new HeaderRenderer(headerRenderer));

    TableCellRenderer cellRenderer = table.getDefaultRenderer(Object.class);
    table.setDefaultRenderer(Object.class, new RowHeaderTableCellRenderer(cellRenderer));

    ScrollableRowHeaderTable scrollableTable = new ScrollableRowHeaderTable(scrollPane);

    scrollableTable.getRowHeader().getTableHeader()
            .setDefaultRenderer(new HeaderRenderer(headerRenderer));
    scrollableTable.getRowHeader().setGridColor(Color.lightGray);
    scrollableTable.getRowHeader().setIntercellSpacing(new Dimension(-1, -1));

    ((InfiniteScrollingTableModel) scrollableTable.getTable().getModel())
            .adjustToFrame(table.getColumnModel().getColumn(1).getWidth(),
            table.getRowHeight(), 1000, 1000);
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        tableModel.adjustToFrame(table.getColumnModel().getColumn(1).getWidth(),
                table.getRowHeight(), frame.getWidth(), frame.getHeight());
        for (Coord c : ((InfiniteScrollingTableModel) scrollableTable.getTable().getModel())
                .getSelected()) {
          scrollableTable.getTable().changeSelection(c.row - 1, c.col - 1, true, false);
        }
      }
    });

    ListSelectionListener selectionListener = new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int[] selectedRow = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRow.length == 1 && selectedColumns.length == 1) {
          Coord c = new Coord(selectedColumns[0] + 1, selectedRow[0] + 1);
          ((InfiniteScrollingTableModel) scrollableTable.getTable().getModel()).addSelected(c);
        } else {
          ((InfiniteScrollingTableModel) scrollableTable.getTable().getModel()).clearSelected();
        }
      }
    };

    scrollableTable.getTable().getColumnModel().getSelectionModel()
            .addListSelectionListener(selectionListener);
    ListSelectionModel cellSelectionModel = scrollableTable.getTable().getSelectionModel();
    cellSelectionModel.addListSelectionListener(selectionListener);

    frame.add(scrollPane);
    frame.setSize(1300, 600);

    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  @Override
  public Appendable getAppendable() {
    return new StringBuilder();
  }
}