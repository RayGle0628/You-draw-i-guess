package server;

public class DatabaseManager {
    public boolean login(String username, String password) {
        //TODO
        //Checks if username is in database and password matches.
        //returns true if it is, false if not
        return true;
    }

    public boolean createAccount(String username, String password, String email) {
        //TODO
        //Creates an entry in the database with username, password and email.
        //Username is the primary key
        // Username ignores capitals so "User" and "user" can not both be made.
        return true;
    }
}
