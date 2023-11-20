package connect;

import java.sql.*;
import javax.swing.JOptionPane;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
       try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        //Advika2611 - password 
         Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/employee", "root", "root");
        return conn;
       }
       catch(ClassNotFoundException | SQLException ex)
       {
           JOptionPane.showMessageDialog(null, ex);
       }
        return null;
    }
}
