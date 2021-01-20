import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainframe {
    JFrame mainwindow;
    JMenu menu_function, menu_maintenance;
    JMenuItem menu_functionvoucher, menu_maintenanceemployee, menu_functionhome, menu_functionexit;
    JMenuBar mb;
    JPanel pnl_center;
    JScrollPane sp;

    public mainframe() throws Exception {

        HandleControlButton control = new HandleControlButton();

        mainwindow = new JFrame("UT Voucher System");
        mainwindow.setSize(800,600);
        mainwindow.setLayout(new BorderLayout());
        mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainwindow.setLocationRelativeTo(null);

        pnl_center = new JPanel(new BorderLayout());
        pnl_center.add(new panel_home(), BorderLayout.CENTER);
        sp = new JScrollPane(pnl_center);

        mb = new JMenuBar();
        menu_function = new JMenu("Function");
        menu_maintenance = new JMenu("Maintenance");

        menu_functionvoucher = new JMenuItem("Cash Voucher Entry");
        menu_functionvoucher.addActionListener(control);
        menu_maintenanceemployee = new JMenuItem("Update Employee Records");
        menu_maintenanceemployee.addActionListener(control);
        menu_functionhome = new JMenuItem("Home");
        menu_functionhome.addActionListener(control);
        menu_functionexit = new JMenuItem("Exit");
        menu_functionexit.addActionListener(control);

        menu_function.add(menu_functionhome);
        menu_function.add(menu_functionvoucher);
        menu_function.addSeparator();
        menu_function.add(menu_functionexit);
        menu_maintenance.add(menu_maintenanceemployee);
        mb.add(menu_function);
        mb.add(menu_maintenance);

        mainwindow.setJMenuBar(mb);
        mainwindow.add(sp, BorderLayout.CENTER);

        mainwindow.setVisible(true);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == menu_functionvoucher)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    sp = new JScrollPane(new panel_cashvoucher(0));
                    pnl_center.add(sp, BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if (source == menu_maintenanceemployee)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    sp = new JScrollPane(new panel_employee());
                    pnl_center.add(sp, BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if (source == menu_functionhome)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    sp = new JScrollPane(new panel_home());
                    pnl_center.add(sp, BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if (source == menu_functionexit) {
                System.exit(0);
            }
        }
    }
}
