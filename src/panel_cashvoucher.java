import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class panel_cashvoucher extends JPanel {
    ResultSet rset;
    UtilDateModel model;
    JDatePanelImpl datePanel;
    JDatePickerImpl datePicker;
    JLabel lbl_error1, lbl_date1, lbl_week;
    JButton btn_generate;
    JPanel pnl_north, pnl_center, pnl_south;
    int i;
    panel_user[] employeepanel;
    JButton[] btn_absent;
    boolean g;
    int batch, x;
    String startdate = null, enddate = null;
    Vector<Vector<Object>> listabsent;
    String[][] list_absent;

    public panel_cashvoucher(int batchid) throws Exception {
        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();

        batch = batchid;

        setLayout(new BorderLayout());

        dbconnect conn = new dbconnect();

        rset = conn.getlist();

        i = 0;

        while(rset.next()){
            i++;
        }

        pnl_north = new JPanel();
        pnl_center = new JPanel();
        pnl_north.setLayout(new GridBagLayout());
        pnl_center.setLayout(new GridLayout(i, 0, 0, 5));
        pnl_south = new JPanel();
        pnl_south.setLayout(new GridBagLayout());

        btn_generate = new JButton("Generate Vouchers");
        btn_generate.setPreferredSize(new Dimension(350, 25));
        btn_generate.addActionListener(control);
        lbl_error1 = new JLabel("Click here when finished completing details.");
        lbl_error1.setForeground(Color.BLACK);
        lbl_date1 = new JLabel("Please choose week/date :");
        lbl_week = new JLabel("For week of ....................... ");
        lbl_week.setForeground(Color.blue);

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        model = new UtilDateModel();
        datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) datePicker.getModel().getValue());
                //Calendar cal = (Calendar) datePicker.getModel().getValue();
                int dayofWeek = cal.get(Calendar.DAY_OF_WEEK);

                if (dayofWeek == 1)
                    dayofWeek = 8;

                cal.add(Calendar.DATE, -1 * (dayofWeek - Calendar.MONDAY));
                Date fdow = cal.getTime();

                cal.add(Calendar.DATE, 5);
                Date ldow = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

                startdate = formatter.format(fdow);
                enddate = formatter.format(ldow);

                DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                Date startdate1 = null, enddate1 = null;
                try {
                    startdate1 = format1.parse(startdate);
                    enddate1 = format1.parse(enddate);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                String end = new SimpleDateFormat("MMM. dd, yyyy").format(enddate1);
                String start = new SimpleDateFormat("MMM. dd, yyyy").format(startdate1);

                lbl_week.setText("For week of " + start + " to " + end);
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_north.add(lbl_date1, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_north.add(datePicker, c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        pnl_north.add(lbl_week, c);

        employeepanel = new panel_user[i];
        btn_absent = new JButton[i];
        list_absent = new String[i][6];

        if (batch == 0)
        {
            for (int x = 0; x < i; x++)
            {
                pnl_center.add(employeepanel[x] = new panel_user());

                int y = x;
                employeepanel[x].combo_list.addActionListener (new ActionListener () {
                    public void actionPerformed(ActionEvent e) {
                        g = false;

                        for (int p = 0; p < i; p++)
                        {
                            if ((p != y) && (employeepanel[p].combo_list.getSelectedItem() != null) && (employeepanel[p].combo_list.getSelectedItem().equals(employeepanel[y].combo_list.getSelectedItem())))
                            {
                                g = true;
                                break;
                            }
                            else if ((p != y) && (employeepanel[p].combo_list.getSelectedItem() != null))
                            {
                                g = false;
                            }
                        }

                        if (g)
                        {
                            employeepanel[y].lbl_error.setForeground(Color.RED);
                            employeepanel[y].lbl_error.setText("Duplicate Entry!");
                        }
                        else if (g == false)
                        {
                            employeepanel[y].lbl_error.setForeground(Color.BLACK);
                            employeepanel[y].lbl_error.setText("Status OK");
                        }
                    }
                });

                int finalX = x;
                employeepanel[x].text_days.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!((c >= '1') && (c <= '6') ||
                                (c == KeyEvent.VK_BACK_SPACE) ||
                                (c == KeyEvent.VK_DELETE)) || employeepanel[y].text_days.getText().length() >= 1) {
                            getToolkit().beep();
                            e.consume();
                        }
                        else if (!((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                        {
                            int present, absentdays;

                            present = Integer.valueOf(String.valueOf(c));

                            absentdays = 6 - present;

                            if (absentdays > 0)
                            {
                                GridBagConstraints g = new GridBagConstraints();

                                g.gridy = 0;
                                g.gridx = 4;

                                if (btn_absent[y] == null)
                                {
                                    btn_absent[y] = new JButton("Choose Absent Days");
                                    employeepanel[y].add(btn_absent[y], g);
                                }
                                else if (btn_absent[y].getParent() == null) {
                                    employeepanel[y].add(btn_absent[y], g);
                                }

                                employeepanel[y].repaint();
                                employeepanel[y].revalidate();

                                listabsent = new Vector<Vector<Object>>();
                                list_absent[y][0] = "record 1" + "employee " + y;
                                list_absent[y][1] = "record 2" + "employee " + y;

                                System.out.println(list_absent[y][0] + list_absent[y][1]);
                            }
                            else if (absentdays == 0)
                            {
                                if (!(btn_absent[y] == null))
                                    employeepanel[y].remove(btn_absent[y]);

                                employeepanel[y].repaint();
                                employeepanel[y].revalidate();
                            }

                            System.out.println(String.valueOf(c) + " " + c + " " + present + " " + absentdays);
                        }
                    }
                });
            }
        }
        else if (batch > 0)
        {
            while (x < i)
            {
                pnl_center.add(employeepanel[x] = new panel_user());

                int y = x;
                employeepanel[x].combo_list.addActionListener (new ActionListener () {
                    public void actionPerformed(ActionEvent e) {
                        g = false;

                        for (int p = 0; p < i; p++)
                        {
                            if ((p != y) && (employeepanel[p].combo_list.getSelectedItem() != null) && (employeepanel[p].combo_list.getSelectedItem().equals(employeepanel[y].combo_list.getSelectedItem())))
                            {
                                g = true;
                                break;
                            }
                            else if ((p != y) && (employeepanel[p].combo_list.getSelectedItem() != null))
                            {
                                g = false;
                            }
                        }

                        if (g)
                        {
                            employeepanel[y].lbl_error.setForeground(Color.RED);
                            employeepanel[y].lbl_error.setText("Duplicate Entry!");
                        }
                        else if (g == false)
                        {
                            employeepanel[y].lbl_error.setForeground(Color.BLACK);
                            employeepanel[y].lbl_error.setText("Status OK");
                        }
                    }
                });

                employeepanel[x].text_days.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!((c >= '1') && (c <= '6') ||
                                (c == KeyEvent.VK_BACK_SPACE) ||
                                (c == KeyEvent.VK_DELETE)) || employeepanel[y].text_days.getText().length() >= 1) {
                            getToolkit().beep();
                            e.consume();
                        }
                    }
                });

                x++;
            }
        }

        if (batch > 0)
        {
            dbconnect conn4 = new dbconnect();
            ResultSet rset4;

            rset4 = conn4.getBatchdetails(batch);

            String empname = "";
            String days1 = null;
            int j = 0;

            rset4.first();

            String start2 = null, end2 = null;

            do
            {
                empname = rset4.getString("employeename");
                days1 = rset4.getString("days");
                String start1 = rset4.getString("startdate");
                String end1 = rset4.getString("enddate");

                DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                Date enddate3 = format1.parse(end1);
                Date startdate3 = format1.parse(start1);

                enddate = new SimpleDateFormat("MM/dd/yyyy").format(enddate3);
                startdate = new SimpleDateFormat("MMM/dd/yyyy").format(startdate3);

                end2 = new SimpleDateFormat("MMM. dd, yyyy").format(enddate3);
                start2 = new SimpleDateFormat("MMM. dd, yyyy").format(startdate3);

                for (int l = 0; l < i; l++)
                {
                    employeepanel[j].combo_list.setSelectedIndex(l);
                    if (empname.equals(employeepanel[j].combo_list.getSelectedItem().toString()))
                    {
                        employeepanel[j].text_days.setText(days1);
                        break;
                    }
                }
                j++;
            }while (rset4.next());

            lbl_week.setText("For week of " + start2 + " to " + end2);
        }


        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        pnl_south.add(btn_generate, c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 0);
        pnl_south.add(lbl_error1, c);

        add(pnl_north, BorderLayout.NORTH);
        add(pnl_center, BorderLayout.CENTER);
        add(pnl_south, BorderLayout.SOUTH);
    }

    public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_generate)
            {
                String employeename = "", rate = "", dailyrate = "", days = "";
                double total = 0;
                boolean t = false;

                for (int p = 0; p < i; p++)
                {
                    if ((employeepanel[p].combo_list.getSelectedItem() == null))
                    {
                        t = true;
                        break;
                    }
                }

                if (enddate != null && startdate != null && !g && !t)
                {
                    dbconnect conn2 = new dbconnect();
                    ResultSet rset2;
                    int latestbatch = 0, newbatchid = 0, z = 0;

                    try {
                        rset2 = conn2.getBatchid();

                        if (!rset2.isBeforeFirst() ) {
                            z = 0;
                        }
                        else
                        {
                            z = 1;
                        }

                        if (z == 0)
                        {
                            newbatchid = 1;
                        }
                        else if (z > 0 && batch == 0)
                        {
                            rset2.first();

                            do{
                                latestbatch = rset2.getInt("id_batch");
                            }while (rset.next());
                            newbatchid = latestbatch + 1;
                        }
                        else if (z > 0 && batch > 0)
                        {
                            newbatchid = batch;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    for (int w = 0; w < i; w++)
                    {
                        employeename = employeepanel[w].combo_list.getSelectedItem().toString();
                        days = employeepanel[w].text_days.getText();

                        try {
                            rset.first();

                            do{
                                if (employeename.equals(rset.getString("employeename")))
                                {
                                    rate = rset.getString("rate");
                                    dailyrate = rset.getString("dailyrate");
                                    if (dailyrate.equals("1"))
                                        total = Double.parseDouble(rate) * Double.parseDouble(employeepanel[w].text_days.getText());
                                    else
                                    {
                                        if (days.equals("6"))
                                            total = Double.parseDouble(rset.getString("fixedrate"));
                                        else if (Integer.valueOf(days) < 6)
                                        {
                                            Double h = 6 - Double.valueOf(days);
                                            h = h * Double.valueOf(rate);
                                            total = Double.parseDouble(rset.getString("fixedrate")) - h;
                                        }
                                    }
                                }
                            }while (rset.next());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        if (batch == 0)
                        {
                            try {
                                conn2.addVoucher(employeename, rate, total, dailyrate, startdate, enddate, days, newbatchid);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        else if (batch > 0)
                        {
                            try {
                                conn2.updateVoucher(employeename, days, batch, startdate, enddate);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        //System.out.println(employeename + " " + startdate + " " + enddate + " " + rate + " " + dailyrate + " " +total);
                    }

                    batch = newbatchid;

                    dbconnect conn3 = new dbconnect();

                    ResultSet rset1;

                    try {
                        rset1 = conn3.getBatchid();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    removeAll();
                    repaint();
                    revalidate();

                    try {
                        add(new panel_viewvoucher(batch), BorderLayout.CENTER);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    repaint();
                    revalidate();
                }
                else if (enddate == null || startdate == null)
                {
                    lbl_error1.setForeground(Color.RED);
                    lbl_error1.setText("ERROR: No date chosen! Please choose a date above.");
                }
                else if (g)
                {
                    lbl_error1.setForeground(Color.RED);
                    lbl_error1.setText("ERROR: Please check if there are duplicate employee names.");
                }
                else if (t)
                {
                    lbl_error1.setForeground(Color.RED);
                    lbl_error1.setText("ERROR: There are missing employees not yet entered.");
                }
            }
        }
    }

    public class panel_user extends JPanel{
        JLabel lbl_days, lbl_error;
        JTextField text_days;
        JComboBox combo_list;

        public panel_user() throws Exception {
            setLayout(new GridBagLayout());

            lbl_days = new JLabel("Days Worked: ");
            text_days = new JTextField(10);
            lbl_error = new JLabel("Status OK");

            combo_list = new JComboBox();

            rset.first();
            do{
                combo_list.addItem(rset.getString("employeename"));
            }while (rset.next());

            combo_list.setSelectedIndex(-1);

            AutoCompleteDecorator.decorate(combo_list);

            GridBagConstraints c = new GridBagConstraints();

            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(5, 0, 0, 10);
            c.anchor = GridBagConstraints.LINE_START;
            add(combo_list, c);

            c.gridx = 1;
            c.insets = new Insets(5, 0, 0, 10);
            c.anchor = GridBagConstraints.LINE_END;
            add(lbl_days, c);

            c.gridx = 2;
            c.anchor = GridBagConstraints.LINE_START;
            add(text_days, c);

            c.gridx = 3;
            add(lbl_error, c);
        }
    }
}
