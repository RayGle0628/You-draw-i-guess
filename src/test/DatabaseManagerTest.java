package test;

import org.junit.jupiter.api.*;
import server.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mingrui Li
 * @data 2020/3/16
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerTest {

    static DatabaseManager databaseManager;

    static String testUsername1;
    static String testPassword1;
    static String testEmail1;

    static String testUsername2;
    static String testPassword2;
    static String testEmail2;

    static String testUsername3;
    static String testPassword3;
    static String testEmail3;

    @BeforeAll
    static void init() {
        databaseManager = new DatabaseManager();

        testUsername1 = "test1234";
        testPassword1 = "Password1234!";
        testEmail1 = "test1@gmail.com";

        testUsername2 = "test2344";
        testPassword2 = "Password2344!";
        testEmail2 = "test2@gmail.com";

        testUsername3 = "test3456";
        testPassword3 = "Password3456!";
        testEmail3 = "test3@gmail.com";

    }

    @AfterAll
    static void cleanup() {
        if (databaseManager.login(testUsername1, testPassword1)) {
            deleteTestUser(testUsername1);
        }

        if (databaseManager.login(testUsername2, testPassword2)) {
            deleteTestUser(testUsername2);
        }

        if (databaseManager.login(testUsername2, testPassword2)) {
            deleteTestUser(testUsername3);
        }
    }

    @Order(1)
    @Test
    void createAccount() {
        assertTrue(databaseManager.createAccount(testUsername1, testPassword1, testEmail1));
        assertTrue(databaseManager.createAccount(testUsername2, testPassword2, testEmail2));
        assertFalse(databaseManager.createAccount(testUsername3, testPassword3, testEmail3));

        assertFalse(databaseManager.createAccount(testUsername1, testPassword1, testEmail1));
    }

    @Order(2)
    @Test
    void login() {
        assertTrue(databaseManager.login(testUsername1, testPassword1));
        assertTrue(databaseManager.login(testUsername2, testPassword2));
        assertFalse(databaseManager.login("test1111", testPassword1));
        assertFalse(databaseManager.login(testUsername1, "Password1111!"));
        assertFalse(databaseManager.login(testUsername1, testPassword2));
    }

    @Order(3)
    @Test
    void updateScoreAndWin() {
        String myScore = databaseManager.getMyScore(testUsername1);
        assertEquals(0, getScore(myScore));
        assertEquals(0, getWin(myScore));

        databaseManager.updateScore(testUsername1, 100);
        databaseManager.updateWin(testUsername1);
        myScore = databaseManager.getMyScore(testUsername1);
        assertEquals(100, getScore(myScore));
        assertEquals(1, getWin(myScore));

        databaseManager.updateScore(testUsername1, 300);
        databaseManager.updateWin(testUsername1);
        myScore = databaseManager.getMyScore(testUsername1);
        assertEquals(400, getScore(myScore));
        assertEquals(2, getWin(myScore));
    }

    @Order(4)
    @Test
    void getHighScores() {
        databaseManager.updateScore(testUsername1, 100);
        databaseManager.updateScore(testUsername2, 300);
        databaseManager.updateWin(testUsername2);
        databaseManager.updateWin(testUsername2);
        databaseManager.updateScore(testUsername3, 400);
        databaseManager.updateWin(testUsername3);

        String[] highScores = databaseManager.getHighScores();
        for (int i = 1; i < highScores.length; i++) {
            int score1 = getScore(highScores[i - 1]);
            int win1 = getWin(highScores[i - 1]);
            int score2 = getScore(highScores[i]);
            int win2 = getWin(highScores[i]);

            assertTrue((score1 > score2) || (score1 == score2 && win1 >= win2));
        }
    }

    @Order(5)
    @Test
    void encryptPassword() {
        assertEquals("11120e59ba137123ab0264b751c361e8135edb5d029cdf1ca3d2b9da6"
                + "c6f309c7fa7551692816cab79711726920cb8402bdf05aa1b5be4ca246c26f48f4ee0da",
                DatabaseManager.encryptPassword(testPassword1));
    }

    @Order(6)
    @Test
    void byteToHex() {
        byte[] bytes = {118, -101, -62, -65, 5, -117, -98, -120, -119, -27, 31, -30, -8, -117,
                -112, 8, 63, 45, 41, -11, 93, 125, 115, 18, 100, -118, -97, 97, -58, -2, -58,
                2, -36, -34, -82, 78, -74, 55, -80, 56, 44, 41, -51, -67, 83, 54, 121, 58, 72,
                36, 53, 71, -72, 91, 45, -65, -92, -37, -8, 68, -38, 119, 48, 3};
        assertEquals("769bc2bf058b9e8889e51fe2f88b90083f2d29f55d7d7312648a9f61c6fec"
                        + "602dcdeae4eb637b0382c29cdbd5336793a48243547b85b2dbfa4dbf844da773003",
                DatabaseManager.byteToHex(bytes));
    }

    static void deleteTestUser(String username) {
        try {
            String sql1 = "DELETE FROM user_scores WHERE LOWER(username)=LOWER(?);";
            PreparedStatement preparedStatement1 = databaseManager.getConnection().prepareStatement(sql1);
            preparedStatement1.setString(1, username);
            preparedStatement1.executeUpdate();

            String sql2 = "DELETE FROM user_details WHERE LOWER(username)=LOWER(?);";
            PreparedStatement preparedStatement2 = databaseManager.getConnection().prepareStatement(sql2);
            preparedStatement2.setString(1, username);
            preparedStatement2.executeUpdate();

            databaseManager.getConnection().commit();
            preparedStatement1.close();
            preparedStatement2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void listUsers() {
        try {
            String sql = "SELECT * FROM user_details;";
            PreparedStatement preparedStatement = databaseManager.getConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1) + "  "
                        + rs.getString(2) + "  "
                        + rs.getString(3));
            }

            rs.close(); // Close the result set and the statement.
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static int getScore(String myScore) {
        return Integer.parseInt(myScore.split(":")[2]);
    }

    static int getWin(String myScore) {
        return Integer.parseInt(myScore.split(":")[3]);
    }
}