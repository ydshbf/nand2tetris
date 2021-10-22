/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package HackGUI;

import Hack.Controller.Breakpoint;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * This class represents the gui of a breakpoint panel.
 */
public class BreakpointWindow extends JFrame implements MouseListener, BreakpointChangedListener {

    // The table of breakpoints.
    private JTable breakpointTable;

    // The vector of breakpoints.
    private Vector<Breakpoint> breakpoints;

    // The model of this table.
    private BreakpointTableModel model;

    // A vector containing the listeners to this object.
    private Vector<BreakpointsChangedListener> listeners;

    // The layout of this component.
    private FlowLayout flowLayout = new FlowLayout();

    // The add remove and ok buttons.
    private JButton addButton = new JButton();
    private JButton removeButton = new JButton();
    private JButton okButton = new JButton();

    // Creating icons.
    private ImageIcon addIcon = new ImageIcon(Utilities.imagesDir + "smallplus.gif");
    private ImageIcon removeIcon = new ImageIcon(Utilities.imagesDir + "smallminus.gif");
    private ImageIcon okIcon = new ImageIcon(Utilities.imagesDir + "ok2.gif");

    // Creating the window which allows adding and editing a given breakpoint.
    private BreakpointVariablesWindow variables = new BreakpointVariablesWindow();

    // The selected row in the breakpoint table.
    private int selectedRowIndex = -1;

    /**
     * Constructs a new BreakpointWindow.
     */
    public BreakpointWindow() {
        super("断点面板");
        breakpoints = new Vector<Breakpoint>();
        model = new BreakpointTableModel();
        breakpointTable = new JTable(model);
        ColoredTableCellRenderer coloredRenderer = new ColoredTableCellRenderer();
        breakpointTable.setDefaultRenderer(breakpointTable.getColumnClass(0), coloredRenderer);
        listeners = new Vector<BreakpointsChangedListener>();
        setResizable(false);

        jbInit();
    }

    /**
     * Sets the breakpoints list with the given one.
     */
    public void setBreakpoints (Collection<Breakpoint> breakpoints) {
        this.breakpoints = new Vector<Breakpoint>(breakpoints);
        breakpointTable.revalidate();
    }

    /**
     * Sets the list of recognized variables with the given one.
     */
    public void setVariables (String[] newVars) {
        variables.setVariables(newVars);
    }

    /**
    * Registers the given BreakpointChangedListener as a listener to this component.
    */
    public void addBreakpointListener (BreakpointsChangedListener listener) {
        listeners.addElement(listener);
    }

    /**
     * Notifies all the BreakpointChangedListeners on actions taken in it, by creating a
     * BreakpointChangedEvent and sending it using the breakpointChanged method to all
     * of the listeners.
     */
    public void notifyListeners () {
        BreakpointsChangedEvent event = new BreakpointsChangedEvent(this,breakpoints);
        for (BreakpointsChangedListener listener : listeners)
            listener.breakpointsChanged(event);
    }

    /**
     * Returns the breakpoints table.
     */
    public JTable getTable() {
        return breakpointTable;
    }

    /**
     * Called when there was a change in one of the breakpoints.
     * The event contains the changed breakpoint.
     */
    public void breakpointChanged(BreakpointChangedEvent event) {
        Breakpoint p = event.getBreakpoint();
        if(selectedRowIndex == -1)
            breakpoints.addElement(p);
        else
            breakpoints.setElementAt(p,selectedRowIndex);
        breakpointTable.revalidate();
        repaint();
        notifyListeners();
    }

    /**
     * Implementing the action of double-clicking the mouse on the table.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int row = breakpointTable.getSelectedRow();
            selectedRowIndex = row;
            variables.setBreakpointName(breakpoints.elementAt(row).getVarName());
            variables.setBreakpointValue(breakpoints.elementAt(row).getValue());
            //variables.setVisible(true);
            variables.showWindow();
        }

    }

    /**
     * Empty implementation.
     */
    public void mouseExited (MouseEvent e) {}

    /**
     * Empty implementation.
     */
    public void mouseEntered (MouseEvent e) {}

    /**
     * Empty implementation.
     */
    public void mouseReleased (MouseEvent e) {}

    /**
     * Empty implementation.
     */
    public void mousePressed (MouseEvent e) {}

    // Initializes this window.
    private void jbInit() {
        variables.addListener(this);
        this.getContentPane().setLayout(flowLayout);
        breakpointTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        breakpointTable.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(breakpointTable);
        scrollPane.setPreferredSize(new Dimension(190, 330));
        addButton.setPreferredSize(new Dimension(35, 25));
        addButton.setToolTipText("设断点");
        addButton.setIcon(addIcon);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButton_actionPerformed();
            }
        });
        removeButton.setPreferredSize(new Dimension(35, 25));
        removeButton.setToolTipText("删断点");
        removeButton.setIcon(removeIcon);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeButton_actionPerformed();
            }
        });
        okButton.setPreferredSize(new Dimension(35, 25));
        okButton.setToolTipText("好");
        okButton.setIcon(okIcon);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed();
            }
        });
        this.getContentPane().add(scrollPane, null);
        this.getContentPane().add(addButton, null);
        this.getContentPane().add(removeButton, null);
        this.getContentPane().add(okButton, null);
        setSize(210,410);
        setLocation(250,250);
    }

    /**
     * Implementing the action of pressing the add button.
     */
    public void addButton_actionPerformed() {
        breakpointTable.clearSelection();
        selectedRowIndex = -1;
        variables.setNameCombo(0);
        variables.setBreakpointName("");
        variables.setBreakpointValue("");
        variables.showWindow();
    }

    /**
     * Implementing the action of pressing the remove button.
     */
    public void removeButton_actionPerformed() {
        int selectedRow = breakpointTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < breakpointTable.getRowCount()) {
            model.removeRow(breakpointTable.getSelectedRow());
            breakpointTable.revalidate();
            notifyListeners();
        }
    }

    /**
     * Implementing the action of pressing the ok button.
     */
    public void okButton_actionPerformed() {
        setVisible(false);
    }

    // An inner class representing the model of the breakpoint table.
    class BreakpointTableModel extends AbstractTableModel {
        String[] columnNames = {"变量名", "值"};

        /**
         * Returns the number of columns.
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Returns the number of rows.
         */
        public int getRowCount() {
            return breakpoints.size();
        }

        /**
         * Returns the names of the columns.
         */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         * Returns the value at a specific row and column.
         */
        public Object getValueAt(int row, int col) {
            Breakpoint breakpoint = breakpoints.elementAt(row);

            if(col==0)
                return breakpoint.getVarName();
            else
                return breakpoint.getValue();
        }

        /**
         * Removes a row from this table.
         */
        public void removeRow(int index) {
            if(breakpoints.size()>0)
                breakpoints.removeElementAt(index);
        }

        /**
         * Returns true of this table cells are editable, false - otherwise.
         */
        public boolean isCellEditable(int row, int col){
            return false;
        }
    }

    // An inner class which implements the cell renderer of the breakpoint table, giving
    // the feature of coloring the background of a specific cell.
    class ColoredTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent
            (JTable table, Object value, boolean selected, boolean focused, int row, int column)
        {
            setEnabled(table == null || table.isEnabled());
            setBackground(breakpoints.elementAt(row).isReached() ? Color.red : null);
            setHorizontalAlignment(SwingConstants.CENTER);
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            return this;
        }
    }
}
