package server;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

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

    public synchronized boolean login(String username, String password) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT COUNT(*) FROM user_details " + "WHERE LOWER(username)=LOWER('" + username + "')" +
                    "AND " + "password='" + password + "';";
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

    public synchronized boolean createAccount(String username, String password, String email) {
        try {
            stmt = c.createStatement();
            String sql1 = "INSERT INTO user_details(USERNAME, PASSWORD, EMAIL) VALUES ('" + username.toLowerCase() +
                    "', '" + password + "', '" + email.toLowerCase() + "'); ";
            String sql2 = "INSERT INTO user_scores(USERNAME) VALUES ('" + username.toLowerCase() + "'); ";
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            c.commit();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not create this account.");
            try {
                c.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public synchronized void updateScore(String username, int score) {
        try {
            stmt = c.createStatement();
            String sql =
                    "UPDATE user_scores SET total_score = total_score+" + score + " WHERE username='" + username.toLowerCase() + "';";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not update score.");
        }
    }

    public synchronized void updateWin(String username) {
        try {
            stmt = c.createStatement();
            String sql =
                    "UPDATE user_scores SET total_wins = total_score+1 WHERE username='" + username.toLowerCase() +
                            "';";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not update wins.");
        }
    }

    public synchronized TreeMap<String, int[]> getHighScores() {
        TreeMap<String, int[]> test = new TreeMap<>();
        try {
            stmt = c.createStatement();
            String sql = "";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                test.put(rs.getString("username"), new int[]{rs.getInt("total_score"), rs.getInt("total_wins")});
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return test;
    }
}
