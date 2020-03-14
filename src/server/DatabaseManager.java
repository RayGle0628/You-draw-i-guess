package server;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private Connection c = null;

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
            String sql = "SELECT COUNT(*) FROM user_details WHERE LOWER(username)=LOWER(?) AND  password=?;";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int size = rs.getInt("COUNT");
            rs.close();
            preparedStatement.close();
            if (size == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean createAccount(String username, String password, String email) {
        try {
            String sql1 = "INSERT INTO user_details(USERNAME, PASSWORD, EMAIL) VALUES (?,?,?); ";
            String sql2 = "INSERT INTO user_scores(USERNAME) VALUES (?); ";
            PreparedStatement preparedStatement1 = c.prepareStatement(sql1);
            preparedStatement1.setString(1, username.toLowerCase());
            preparedStatement1.setString(2, password);
            preparedStatement1.setString(3, email);
            PreparedStatement preparedStatement2 = c.prepareStatement(sql2);
            preparedStatement2.setString(1, username.toLowerCase());
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            c.commit();
            preparedStatement1.close();
            preparedStatement2.close();
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
            String sql = "UPDATE user_scores SET total_score = total_score+? WHERE username=?;";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setInt(1, score);
            preparedStatement.setString(2, username.toLowerCase());
            preparedStatement.executeUpdate();
            c.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Could not update score.");
        }
    }

    public synchronized void updateWin(String username) {
        try {
            String sql = "UPDATE user_scores SET total_wins = total_wins+1 WHERE username=?;";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username.toLowerCase());
            c.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Could not update wins.");
        }
    }

    public synchronized String[] getHighScores() {
        String[] highScores = new String[10];
        int index = 0;
        try {
            String sql =
                    "SELECT  *, row_number() OVER (ORDER BY total_score DESC, total_wins DESC, username ASC) " +
                            "FROM user_scores LIMIT 10;";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                highScores[index] = rs.getInt("row_number") + ":" + rs.getString("username") + ":" + rs.getInt(
                        "total_score") + ":" + rs.getInt("total_wins");
                index++;
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highScores;
    }

    public synchronized String getMyScore(String username) {
        String myRank = null;
        try {
            String sql =
                    "SELECT * FROM (SELECT *, row_number() OVER (ORDER BY total_score DESC, total_wins DESC, " +
                            "username ASC) FROM user_scores) AS A WHERE username = ?;";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery(sql);
            rs.next();
            myRank =
                    rs.getInt("row_number") + ":" + rs.getString("username") + ":" + rs.getInt("total_score") + ":" + rs.getInt("total_wins");
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myRank;
    }
}
