package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;

/**
 * DatabaseManager is a class that manages access and queries relating to our game. This class is initialised by and
 * accessed through the Server class.
 */
public class DatabaseManager {

    private Connection c = null;

    /**
     * Constructor for the DatabaseManager class. This initiates the connection to the database, and in the case of
     * failure, closes the server.
     */
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

    /**
     * Login is used to make a log in query for the database by counting the number of accounts associates with the
     * username and password given.
     * If the count is 0, either the account doesn't exist or the password is incorrect.  If the count is 1, an
     * account matches the details given. More than 1 should not be possible as usernames are unique. A boolean is
     * returned representing a match or not.
     *
     * @param username the username given.
     * @param password the password given.
     * @return a boolean representing whether a match was found in the database or not.
     */
    public synchronized boolean login(String username, String password) {
        try {
            String sql = "SELECT COUNT(*) FROM user_details WHERE LOWER(username)=LOWER(?) AND  password=?;"; //
            // Query which finds the count of results with the matching details.
            PreparedStatement preparedStatement = c.prepareStatement(sql); // Create a prepared statement from the
            // query text.
            preparedStatement.setString(1, username); // Injects the username in the first "?".
            preparedStatement.setString(2, encryptPassword(password));  // Injects the hashed password in the second
            // "?".
            ResultSet rs = preparedStatement.executeQuery(); // Executes the query and stores the result.
            rs.next();
            int size = rs.getInt("COUNT"); // Gets the count form the result set.
            rs.close(); // Close the result set and the statement.
            preparedStatement.close();
            if (size == 1) return true; // If count is 1, then a match was found.
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no match found.
    }

    /**
     * Creates entries in the user_details and user_scores tables in the database when a user creates a new account.
     *
     * @param username the username of the new account.
     * @param password the password of the new account.
     * @param email    the email of the new account.
     * @return a boolean representing whether the action was successful or not.
     */
    public synchronized boolean createAccount(String username, String password, String email) {
        try {
            String sql1 = "INSERT INTO user_details(USERNAME, PASSWORD, EMAIL) VALUES (?,?,?); "; // Inserts values
            // into USERNAME, PASSWORD and EMAIL columns of user_details.
            String sql2 = "INSERT INTO user_scores(USERNAME) VALUES (?); "; // All scores default to 0, so only
            // username needs to be inserted into user_scores.
            PreparedStatement preparedStatement1 = c.prepareStatement(sql1); // Creates prepared statement from first
            // query.
            preparedStatement1.setString(1, username.toLowerCase()); // Injects the username in the first "?".
            preparedStatement1.setString(2, encryptPassword(password));  // Injects the encrypted password in the
            // second "?".
            preparedStatement1.setString(3, email); // Injects the email in the third "?".
            PreparedStatement preparedStatement2 = c.prepareStatement(sql2); // Creates prepared statement from
            // second query.
            preparedStatement2.setString(1, username.toLowerCase()); // Injects the username in the "?".
            preparedStatement1.executeUpdate(); // Execute both queries.
            preparedStatement2.executeUpdate();
            c.commit(); // Commit the changes.
            preparedStatement1.close(); // Close the statements.
            preparedStatement2.close();
        } catch (SQLException e) {
            try {
                c.rollback(); // If something goes wrong, e.g. an account already exists, roll back the connection to
                // before the queries were executed.
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false; // And return false to indicate an error occurred.
        }
        return true; // Else everything was successful so return true.
    }

    /**
     * Increases the total score associated with a player.
     *
     * @param username the username of the user to increase score of.
     * @param score    the score to increase by.
     */
    public synchronized void updateScore(String username, int score) {
        try {
            String sql = "UPDATE user_scores SET total_score = total_score+? WHERE username=?;"; // Increases the
            // given users score by the prescribed amount.
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setInt(1, score);
            preparedStatement.setString(2, username.toLowerCase());
            preparedStatement.executeUpdate();
            c.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increases the wins associated with a player by 1.
     *
     * @param username the username of the user to increase wins of.
     */
    public synchronized void updateWin(String username) {
        try {
            String sql = "UPDATE user_scores SET total_wins = total_wins+1 WHERE username=?;"; // Increases user wins
            // by 1.
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username.toLowerCase());
            preparedStatement.executeUpdate();
            c.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Could not update wins.");
        }
    }

    /**
     * Returns a list of the top 10 players based on their lifetime total score.
     *
     * @return the list of 10 players and their stats.
     */
    public synchronized String[] getHighScores() {
        String[] highScores = new String[10];
        int index = 0;
        try {
            String sql =
                    "SELECT  *, row_number() OVER (ORDER BY total_score DESC, total_wins DESC, username ASC) " +
                            "FROM user_scores LIMIT 10;";// Gets an ordered list of 10 users, ordered by score, wins
            // then username.
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                highScores[index] = rs.getInt("row_number") + ":" + rs.getString("username") + ":" + rs.getInt(
                        "total_score") + ":" + rs.getInt("total_wins"); // For each result, format it as
                // "rank:username:score:wins".
                index++;
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highScores; // Return the array of ranking strings.
    }

    /**
     * Gets the ranking associated with a given username.
     *
     * @param username the username given.
     * @return the ranking of a user.
     */
    public synchronized String getMyScore(String username) {
        String myRank = null;
        try {
            String sql =
                    "SELECT * FROM (SELECT *, row_number() OVER (ORDER BY total_score DESC, total_wins DESC, " +
                            "username ASC) FROM user_scores) AS A WHERE username = ?;"; // Creates a list of all
            // players ordered by score with row numbers and selects the given user from the set.
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, username.toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            myRank =
                    rs.getInt("row_number") + ":" + rs.getString("username") + ":" + rs.getInt("total_score") + ":" + rs.getInt("total_wins");
            rs.close(); //Format the result as "rank:username:score:wins".
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myRank;
    }

    /**
     * Return sql connection.
     * @return Sql connection
     */
    public Connection getConnection() {
        return this.c;
    }

    /**
     * Takes a given password and hashes it based on SHA-512. The returned hash is a 128 character hex string.
     *
     * @param password the password given.
     * @return the hash of the password.
     */
    public static String encryptPassword(String password) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512"); // The hash function to be used.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert messageDigest != null;
        byte[] digestBytes = messageDigest.digest(password.getBytes()); // Use the hash function to convert the
        // password into the hashed array of bytes.
        return byteToHex(digestBytes); // Converts the byte array into hex and returns.
    }

    /**
     * Converts a given byte array into a hex string.
     *
     * @param digestBytes the bytes given.
     * @return the bytes as a hex string.
     */
    public static String byteToHex(byte[] digestBytes) {
        StringBuilder hex = new StringBuilder(digestBytes.length * 2); // Hex string built up with string builder.
        for (byte b : digestBytes)
            hex.append(String.format("%02x", b)); // Each byte converted to hex and added to the string.
        return hex.toString();
    }
}
