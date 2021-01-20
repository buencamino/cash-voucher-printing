import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_employee extends JPanel {
    JLabel lbl_employeename, lbl_rate, lbl_fixedrate, lbl_total, lbl_title;
    JRadioButton radio_fixedrate, radio_dailyrate;
    JTextField text_employeename, text_rate, text_total;
    JTable tbl_employee;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    DefaultTableModel tablemodel;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ResultSet rset;
    Vector<String> headers = new Vector<String>();
    JButton btn_add, btn_modify, btn_delete, btn_clear;
    String buff_empid;

    public panel_employee() throws Exception {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        HandleItemButton itemcontrol = new HandleItemButton();

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Employee #");
        headers.add("Employee Name");
        headers.add("Rate");
        headers.add("Daily Rate");
        headers.add("Fixed Rate");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_employee = new JTable(tablemodel);
        tbl_employee.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_employee);
        sp.setPreferredSize(new Dimension(700,300));

        listselectionmodel = tbl_employee.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_employee.setSelectionModel(listselectionmodel);

        lbl_employeename = new JLabel("Employee Name : ");
        lbl_rate = new JLabel("Rate : ");
        lbl_fixedrate = new JLabel("Fixed Rate?");
        lbl_total = new JLabel("Monthly Total : ");
        lbl_title = new JLabel("Employee Details");

        radio_fixedrate = new JRadioButton("Yes");
        radio_dailyrate = new JRadioButton("No");
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(radio_fixedrate);
        bgroup.add(radio_dailyrate);
        radio_fixedrate.addItemListener(itemcontrol);
        radio_dailyrate.addItemListener(itemcontrol);

        text_employeename = new JTextField(25);
        text_rate = new JTextField(10);
        text_total = new JTextField(10);

        lbl_total.setEnabled(false);
        text_total.setEnabled(false);

        btn_add = new JButton("Add");
        btn_modify = new JButton("Modify");
        btn_delete = new JButton("Delete");
        btn_clear = new JButton("Clear");

        btn_add.setPreferredSize(new Dimension(150,25));
        btn_modify.setPreferredSize(new Dimension(85,25));
        btn_delete.setPreferredSize(new Dimension(85,25));

        btn_modify.setEnabled(false);
        btn_delete.setEnabled(false);

        btn_add.addActionListener(control);
        btn_modify.addActionListener(control);
        btn_delete.addActionListener(control);
        btn_clear.addActionListener(control);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_employeename, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_employeename, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_rate, c);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_rate, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_fixedrate, c);

        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        add(radio_fixedrate, c);

        c.gridx = 2;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        add(radio_dailyrate, c);

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_total, c);

        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_total, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 6;
        c.anchor = GridBagConstraints.LINE_START;
        add(sp, c);

        //7th row
        c.gridx = 3;
        c.gridy = 6;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_add, c);

        c.gridx = 4;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_modify, c);

        c.gridx = 5;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_delete, c);
    }

    class HandleItemButton implements ItemListener
    {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getSource();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (source == radio_fixedrate)
                {
                    lbl_total.setEnabled(true);
                    text_total.setEnabled(true);
                }
                else if (source == radio_dailyrate)
                {
                    lbl_total.setEnabled(false);
                    text_total.setEnabled(false);
                }
            }
            else if (e.getStateChange() == ItemEvent.DESELECTED) {
                // Your deselected code here.
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("id_emp"));
            record.add(rset1.getString("employeename"));
            record.add(rset1.getString("rate"));
            String type = null;

            if (rset1.getString("dailyrate").equals("0"))
                type = "No";
            else
                type = "Yes";

            record.add(type);
            record.add(rset1.getString("fixedrate"));

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class selectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if(lsm.isSelectionEmpty())
            {
                System.out.println("nothing selected");
            }
            else
            {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                Object x, y, z, w, o;

                x = tbl_employee.getValueAt(maxIndex, 0);
                y = tbl_employee.getValueAt(maxIndex, 1);
                z = tbl_employee.getValueAt(maxIndex, 2);
                w = tbl_employee.getValueAt(maxIndex, 3);
                o = tbl_employee.getValueAt(maxIndex, 4);

                text_employeename.setText(y.toString());
                text_rate.setText(z.toString());

                if (w.toString().equals("Yes"))
                {
                    text_total.setText("");
                    radio_dailyrate.setSelected(true);
                }
                else if (w.toString().equals("No"))
                {
                    radio_fixedrate.setSelected(true);
                    text_total.setText(o.toString());
                }

                buff_empid = x.toString();

                btn_delete.setEnabled(true);
                btn_modify.setEnabled(true);
            }
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {

                if (!(text_employeename.getText().equals("") || text_rate.getText().equals("")))
                {
                    dbconnect conn = new dbconnect();

                    int i = 0;

                    ResultSet rsetcheck;
                    try {
                        rsetcheck = conn.checkDuplicateemployee(text_employeename.getText());
                        while (rsetcheck.next())
                            i++;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    if (i == 0)
                    {
                        try {
                            int daily = 0;

                            if (radio_fixedrate.isSelected())
                                daily = 0;
                            else if (radio_dailyrate.isSelected())
                                daily = 1;

                            conn.addEmployee(text_employeename.getText(), text_rate.getText(), daily, text_total.getText());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        text_employeename.setText("");
                        text_rate.setText("");
                        text_total.setText("");

                        dbconnect conn2 = new dbconnect();

                        try
                        {
                            rset = conn2.getlist();
                            refreshTable(rset);
                            conn2.close();
                        }
                        catch (Exception j)
                        {
                            System.out.println(j.getMessage());
                        }
                    }
                    else if (i > 0)
                    {
                        JOptionPane.showMessageDialog(this, "Employee already exists in database.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Missing fields not yet entered.", "Incomplete", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (source == btn_delete)
            {
                dbconnect conn3 = new dbconnect();

                try {
                    conn3.deleteEmployee(buff_empid);
                    conn3.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try
                {
                    rset = conn2.getlist();
                    refreshTable(rset);
                    conn2.close();
                }
                catch (Exception j)
                {
                    System.out.println(j.getMessage());
                }

                text_employeename.setText("");
                text_rate.setText("");
                text_total.setText("");
                radio_dailyrate.setSelected(true);
                buff_empid = "";

                btn_delete.setEnabled(false);
                btn_modify.setEnabled(false);
            }

            if (source == btn_clear)
            {
                text_employeename.setText("");
                text_rate.setText("");
                text_total.setText("");
                radio_dailyrate.setSelected(true);
                buff_empid = "";
                btn_modify.setEnabled(false);
                btn_delete.setEnabled(false);
            }

            if (source == btn_modify)
            {
                if (!(text_employeename.getText().equals("") || text_rate.getText().equals(""))) {
                    dbconnect conn3 = new dbconnect();

                    try {
                        int daily = 0;

                        if (radio_dailyrate.isSelected())
                            daily = 1;
                        else if (radio_fixedrate.isSelected())
                            daily = 0;

                        conn3.modifyEmployee(buff_empid, text_employeename.getText(), text_rate.getText(), daily, text_total.getText());
                        conn3.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    dbconnect conn2 = new dbconnect();

                    try
                    {
                        rset = conn2.getlist();
                        refreshTable(rset);
                        conn2.close();
                    }
                    catch (Exception j)
                    {
                        System.out.println(j.getMessage());
                    }

                    text_employeename.setText("");
                    text_rate.setText("");
                    text_total.setText("");
                    radio_dailyrate.setSelected(true);
                    buff_empid = "";

                    btn_delete.setEnabled(false);
                    btn_modify.setEnabled(false);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Missing fields not yet entered.", "Incomplete", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
