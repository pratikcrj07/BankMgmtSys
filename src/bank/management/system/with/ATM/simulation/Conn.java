package bank.management.system.with.ATM.simulation;

import java.sql.*;

public class Conn {
    public Connection c;
    public Statement s;

    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank_system", //  must exist!
                    "root",
                    "root" // ✅ your real password
            );
            s = c.createStatement(); //  `s` is the Statement
            System.out.println(" DB connection successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
