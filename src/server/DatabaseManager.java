package server;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    Connection c = null;
    Statement stmt = null;

    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk/", "reading", "ds018nlznq");
            c.setAutoCommit(false);
            System.out.println("Server successfully connected to the database");
        } catch (Exception e) {
            System.out.println("Unable to connect to database, server stopping.");
            System.exit(0);
        }
    }



    public boolean login(String username, String password) {
        try {
            stmt = c.createStatement();
            String sql =
                    "SELECT COUNT(*) FROM users " + "WHERE LOWER(username)=LOWER('" + username + "')" + "AND " +
                            "password='" + password + "';";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int size = rs.getInt("COUNT");
            rs.close();
            stmt.close();
            if (size == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createAccount(String username, String password, String email) {
        try {
            stmt = c.createStatement();
            String sql =
                    "INSERT INTO users(USERNAME, PASSWORD, EMAIL) VALUES ('" + username.toLowerCase() + "', '" + password + "', '" + email + "'); ";
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            System.out.println("Could not create this account.");
            return false;
        }
        return true;
    }
}
