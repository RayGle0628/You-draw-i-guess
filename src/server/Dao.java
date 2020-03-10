package server;

import client.CreateAccountController;

import javax.swing.*;
import java.sql.*;

public class Dao {
	protected static String dbClassname = "org.postgresql.Driver"; // name of the postgreSQL driver
	protected static String dbUrl = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk/"; // way to visit the postgreSQL database
	protected static String dbUser = "reading"; // user name for the database 
	protected static String dbPassword = "ds018nlznq"; // password for the database
	protected static String dbName = "reading"; //entity to visit the database
	protected static String second = null; 
	public static Connection conn = null; // the SQL connection
	public static PreparedStatement preparedStatement;
	
	public static void main(String[]args) {// initiate static the server.Dao class
		try {
			if(conn == null) {
				Class.forName(dbClassname).newInstance();// entity the driver of the MySQL database
				conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);// connect to the MySQL database
			}
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Please put the JDBC of the MySQL copy into the lib");
			// when catch the abnormal, eject the prompt box
			System.exit(-1); //System exit working
		}catch(Exception e){
			e.printStackTrace();
		}
        }
     /**
     * input the data into database area
     * @throws SQLException
     */
	public static void input() throws SQLException{
	//	String sql = "INSERT INTO userr(username,password,e_mail)values(?,?,?)";
	//	PreparedStatement ps = conn.prepareStatement(sql);
        CreateAccountController CreateAccountController = new CreateAccountController();
//        
//	    ps.setString(1, usernameField.getText());
//		ps.setString(2, passwordField.getText());
//		ps.setString(3, e_mailField.getText);
		
		
		}
	
	/**
	 * query the data from the database
	 */
	
//	public String output(){
//		String sql = "SELECT ";
//		
//		return ;
//	}
    /**
     * implementation of dynamic operation of database and return the data
     * operate the SQL by calling this method
     * @param sql
     * @param data
     * @throws SQLException
     */
	public ResultSet execResult(String sql,String... data) throws SQLException{
		
		preparedStatement = conn.prepareStatement(sql);
		for(int i=1; i<data.length; i++) {
			preparedStatement.setString(i,data[i-1]);
		}
		return preparedStatement.executeQuery();
	}
	/**
	 * implementation of dynamic operation of database and update the data(no data return)
	 * @param sql
	 * @param data
	 * @throws SQLException
	 */
	public void exec(String sql,String... data) throws SQLException{
		preparedStatement = conn.prepareStatement(sql);
		for(int i=1; i<=data.length;i++) {
			preparedStatement.setString(i, data[i-1]);
			preparedStatement.executeUpdate();
		}
	}
	/**
	 * the static operation of the database
	 * only for edit the sql 
	 * @param sql
	 * @throws SQLException
	 */
	public  static void exec1(String sql) throws SQLException{
		preparedStatement =conn.prepareStatement(sql);
		preparedStatement.executeUpdate();
	}
    //read the Username
	public static void getnameInfo(String name) {
		
//		String string = execResult();
	}
	
    }


