import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class panel_viewvoucher extends JPanel {
    Date date;
    String datenow, s;
    ResultSet rsetbatch;
    JButton btn_back;
    JPanel pnl_center, pnl_south;
    int batch;

    public panel_viewvoucher(int batchid) throws SQLException {
        HandleControlButton control = new HandleControlButton();
        batch = batchid;

        setLayout(new BorderLayout());

        pnl_center = new JPanel(new BorderLayout());
        pnl_south = new JPanel(new FlowLayout());

        btn_back = new JButton("Back");
        btn_back.addActionListener(control);

        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        datenow = formatter.format(date);

        removeAll();
        repaint();
        revalidate();

        dbconnect conn2 = new dbconnect();

        try {
            rsetbatch = conn2.getBatchdetails(batchid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        dbconnect conn = new dbconnect();

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        try {
            rsetbatch.first();
            do {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("employeename", rsetbatch.getString("employeename"));

                String end, start;
                DateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
                Date enddate = format1.parse(rsetbatch.getString("newenddate"));
                Date startdate = format1.parse(rsetbatch.getString("newstartdate"));

                end = new SimpleDateFormat("MMM. dd, yyyy").format(enddate);
                start = new SimpleDateFormat("MMM. dd, yyyy").format(startdate);

                m.put("date", end);
                m.put("startdate", start);
                m.put("enddate", end);
                m.put("rate", rsetbatch.getString("rate"));
                m.put("days", rsetbatch.getString("days"));

                String total = rsetbatch.getString("total");
                double formattotal = Double.parseDouble(total);
                DecimalFormat formatter1 = new DecimalFormat("#,###.00");

                m.put("total", formatter1.format(formattotal));

                Float num = rsetbatch.getFloat("total");
                int left = (int)Math.floor(num);
                int right = (int)Math.floor((num-left)*100.0f);

                s = EnglishNumberToWords.convert(left) + " and " + EnglishNumberToWords.convert(right) + " cents only";

                m.put("amountwords", s.toUpperCase());

                dataSource.add(m);
            } while (rsetbatch.next());

            conn2.close();
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }

        JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
        JasperReport report = null;

        try {
            //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
            //JasperDesign dis = JRXmlLoader.load(url1);

            report = JasperCompileManager.compileReport(getClass().getResourceAsStream("utcashvoucher.jrxml"));
            //report = JasperCompileManager.compileReport(dis);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, jrDatasource);

            pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
            pnl_center.repaint();
            pnl_center.revalidate();
        } catch (JRException jrException) {
            jrException.printStackTrace();
        }

        pnl_south.add(btn_back);

        add(pnl_center, BorderLayout.CENTER);
        add(pnl_south, BorderLayout.SOUTH);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_back) {
                removeAll();
                repaint();
                revalidate();

                try {
                    add(new panel_cashvoucher(batch), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                repaint();
                revalidate();
            }
        }
    }
}



