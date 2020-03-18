package server;

import messaging.Command;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Server is the main runnable class of the server portion of our program. Server accepts incoming socket connections
 * and gives them their own treads to interact with. Server also established a connection to the database through
 * DatabaseManager and creates the game rooms at startup.
 * It can be run with:
 * HEREW
 * Server also requires the messaging package  in the same directory and the postgres library.
 * as the server package to run and to be able to connect to the database.
 */
public class Server {

    private static final int PORT = 50000;
    private ArrayList<ServerThread> connectedUsers;
    private TreeMap<String, Room> rooms;
    private DatabaseManager db;

    public DatabaseManager getDb() {
        return db;
    }

    /**
     * Constructor for the server class. Creates a number of rooms and connects to the database.
     */
    public Server() {
        System.out.println("Server starting");
        db = new DatabaseManager();
        connectedUsers = new ArrayList<>();
        rooms = new TreeMap<>();
        createRoom("Room 1");
        createRoom("Room 2");
        createRoom("Room 3");
        createRoom("Room 4");
        createRoom("Room 5");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    /**
     * Creates the ServerSocket for the server then enters a loop to accept client connections. Each client is given
     * it's own ServerThread instance.
     */
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT); // Creates a new ServerSocket on the designated port.
            getIP();
            while (true) { // Continuously waits for, and accepts socket connections.
                Socket socket = serverSocket.accept();
                ServerThread t = new ServerThread(this, socket); // A new ServerThread thread is created for each
                // connection and started.
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Unable to open server on port " + PORT);
        }
    }

    /**
     * Returns a list of rooms currently open on the server.
     *
     * @return a list of the name of the available rooms.
     */
    public String[] getAllRooms() {
        ArrayList<String> roomsList = new ArrayList<>();
        for (String room : rooms.keySet()) { // For each rooms in the TreeSet
            roomsList.add(room + " (" + rooms.get(room).getPopulation() + "/10)"); // Add to a list of strings the
            // room name and how many players are in it.
        }
        return roomsList.toArray(new String[0]); // Return the list as an array of strings.
    }

    /**
     * Creates new rooms on the server.
     *
     * @param name the name of the new Room.
     */
    public void createRoom(String name) {
        Room r = new Room(name, this);
        r.start();
        rooms.put(r.getRoomName(), r); // Tracks the rooms in a TreeSet. This is so rooms are stored alphabetically.
    }

    /**
     * Gets the network IP address for the server.
     */
    public void getIP() {
        Enumeration<NetworkInterface> n = null;
        try {
            n = NetworkInterface.getNetworkInterfaces(); // Get all network interfaces on the machine.
        } catch (SocketException e) {
            e.printStackTrace();
        }
        assert n != null;
        NetworkInterface e = n.nextElement(); // Get the next interface.
        Enumeration<InetAddress> a = e.getInetAddresses(); // Save IPs of interface to enumeration of Internet
        // Addresses.
        while (a.hasMoreElements()) {
            String addr = a.nextElement().getHostAddress(); // Get the IP address as a string.
            if (addr.contains(":")) continue; // Ignore IPv6 addresses.
            System.out.println("Running on local IP: " + addr); // Print IP address.
            System.out.println("Server is ready to accept connections");
        }
    }

    /**
     * Attempts to log into the server with given credentials via a lookup in the database via the DatabaseManager.
     *
     * @param credentials the login credentials.
     * @return boolean on whether successful or not.
     */
    public synchronized boolean login(String[] credentials) {
        return db.login(credentials[0], credentials[1]);
    }

    /**
     * Attempts to create a new account in the database via the DatabaseManager.
     *
     * @param credentials the details of the new account.
     * @return boolean on whether successful or not.
     */
    public synchronized boolean createAccount(String[] credentials) {
        return db.createAccount(credentials[0], credentials[1], credentials[2]);
    }

    /**
     * Tracks active ServerThreads on the server.
     *
     * @param user is a new ServerThread to be tracked.
     */
    public synchronized void addUser(ServerThread user) {
        connectedUsers.add(user);
    }

    /**
     * Sends an updated list of rooms and populations to all users whenever the population of a room changes.
     */
    public synchronized void updateAllRooms() {
        for (ServerThread user : connectedUsers) { // Sends to every user.
            if (user.getRoom() == null) {
                user.sendMessage(Command.RETURN_ROOMS, getAllRooms());
            }
        }
    }

    /**
     * Returns a list of active ServerThreads that are being used by clients on the server.
     *
     * @return a list of active ServerThread instances.
     */
    public ArrayList<ServerThread> getConnectedUsers() {
        return connectedUsers;
    }

    /**
     * Gets a reference to a room when given the name of said room.
     *
     * @param roomName the name of the room required.
     * @return a reference to the room requested.
     */
    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }
}



