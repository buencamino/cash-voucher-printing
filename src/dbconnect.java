import java.sql.*;

public class dbconnect {
    private static final String database_driver = "com.mysql.jdbc.Driver";
    private static final String database_url = "jdbc:mysql://localhost:3306/dbut?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String user = "mike";
    private static final String password = "12345";

    private Connection conn = null;
    private Statement statement = null;
    private ResultSet rset = null;

    public void connect() throws Exception
    {
        try
        {
            Class.forName(database_driver);
            conn = DriverManager.getConnection(database_url, user, password);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getlist() throws Exception {
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_employeedata");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addVoucher (String employeename, String rate, Double total, String dailyrate, String startdate, String enddate, String days, int batchid) throws Exception {
        try
        {
            connect();
            PreparedStatement pstatement = conn.prepareStatement("insert into tbl_voucher (employeename, rate, total, startdate, enddate, dailyrate, days, id_batch) values (?, ?, ?, str_to_date('" + startdate + "', '%m/%d/%Y'), str_to_date('" + enddate + "', '%m/%d/%Y'), ?, ?, ?)");
            pstatement.setString(1, employeename);
            pstatement.setString(2, rate);
            pstatement.setString(3, String.valueOf(total));
            pstatement.setString(4, dailyrate);
            pstatement.setString(5, days);
            pstatement.setInt(6, batchid);

            pstatement.executeUpdate();

            pstatement.close();
            conn.close();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResultSet getBatchdetails(int batchid) throws Exception {
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select *, date_format(startdate, '%m-%d-%Y') as newstartdate, date_format(enddate, '%m-%d-%Y') as newenddate, year(enddate) as year, month(enddate) as month, day(enddate) as day from tbl_voucher where id_batch = " + batchid);
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public ResultSet getBatchid() throws Exception {
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("SELECT * FROM tbl_voucher ORDER BY id_batch DESC LIMIT 1");
            //rset = statement.executeQuery("select * from tbl_voucher where enddate = str_to_date('" + enddate + "', '%m/%d/%Y') && employeename = '" + employeename + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void updateVoucher(String employeename, String days, int batch, String startdate, String enddate) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("update tbl_voucher set days = " + days + ", startdate = str_to_date('" + startdate + "', '%m/%d/%Y'), enddate = str_to_date('" + enddate + "', '%m/%d/%Y') where employeename = '" + employeename + "' and id_batch = " + batch);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public ResultSet checkDuplicateemployee (String employeename) throws Exception{
        rset = null;

        try
        {
            connect();
            statement = conn.createStatement();
            rset = statement.executeQuery("select * from tbl_employeedata where employeename = '" + employeename + "'");
        }
        catch (Exception e)
        {
            throw e;
        }

        return rset;
    }

    public void addEmployee (String employeename, String rate, int daily, String total) throws Exception {
        try
        {
            connect();

            if (daily == 0)
            {
                PreparedStatement pstatement = conn.prepareStatement("insert into tbl_employeedata (employeename, rate, dailyrate, fixedrate) values (?, ?, ?, ?)");
                pstatement.setString(1, employeename);
                pstatement.setString(2, rate);
                pstatement.setString(3, String.valueOf(daily));
                pstatement.setString(4, total);

                pstatement.executeUpdate();

                pstatement.close();
                conn.close();
            }
            else if (daily == 1)
            {
                PreparedStatement pstatement = conn.prepareStatement("insert into tbl_employeedata (employeename, rate, dailyrate) values (?, ?, ?)");
                pstatement.setString(1, employeename);
                pstatement.setString(2, rate);
                pstatement.setString(3, String.valueOf(daily));

                pstatement.executeUpdate();

                pstatement.close();
                conn.close();
            }

        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void deleteEmployee(String empid) throws Exception
    {
        try
        {
            connect();
            statement = conn.createStatement();
            statement.executeUpdate("delete from tbl_employeedata where id_emp = " + empid);
        }
        catch (Exception x)
        {
            throw x;
        }
    }

    public void modifyEmployee(String empid, String employeename, String rate, int daily, String total) throws Exception
    {
        if (daily == 1)
        {
            try
            {
                connect();
                statement = conn.createStatement();
                statement.executeUpdate("update tbl_employeedata set employeename = '" + employeename + "', rate = " + rate + ", dailyrate = " + daily + ", fixedrate = null where id_emp = " + empid);
            }
            catch (Exception x)
            {
                throw x;
            }
        }
        else if (daily == 0)
        {
            try
            {
                connect();
                statement = conn.createStatement();
                statement.executeUpdate("update tbl_employeedata set employeename = '" + employeename + "', rate = " + rate + ", dailyrate = " + daily + ", fixedrate = " + total + " where id_emp = " + empid);
            }
            catch (Exception x)
            {
                throw x;
            }
        }


    }

    public void close() throws Exception
    {
        try
        {
            conn.close();
            statement.close();
            rset = null;
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
