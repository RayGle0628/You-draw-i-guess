package server;

import messaging.Command;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;

public class Server {

    private static final int PORT = 50000;
    private ArrayList<ServerThread> connectedUsers;
    private TreeMap<String, Room> rooms;
    private DatabaseManager db;

    public DatabaseManager getDb() {
        return db;
    }

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
            ServerSocket serverSocket = new ServerSocket(PORT);
            getIP();
            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread t = new ServerThread(this, socket);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Unable to open server on port " + PORT);
        }
    }

    /**
     * Returns a list of active ServerThreads that are being used by client son the server.
     *
     * @return a list of active ServerThread instances.
     */
    public ArrayList<ServerThread> getConnectedUsers() {
        return connectedUsers;
    }

    /**
     * Returns a list of rooms currently open on the server.
     *
     * @return a list of the name of the available rooms.
     */
    public String[] getAllRooms() {
        ArrayList<String> roomsList = new ArrayList<>();
        for (String room : rooms.keySet()) {
            roomsList.add(room + " (" + rooms.get(room).getPopulation() + "/10)");
        }
        //String[] arrayList= (String[])roomsList.toArray();
        String[] arrayList = roomsList.toArray(new String[roomsList.size()]);
        return arrayList;
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

    public void createRoom(String name) {
        Room r = new Room(name, this);
        r.start();
        rooms.put(r.getRoomName(), r);
    }

    public void getIP() throws Exception {
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        NetworkInterface e = n.nextElement();
        Enumeration<InetAddress> a = e.getInetAddresses();
        while (a.hasMoreElements()) {
            String addr = a.nextElement().getHostAddress();
            if (addr.contains(":")) continue;
            System.out.println("Running on local IP: " + addr);
            System.out.println("Server is ready to accept connections");
        }
    }

    public synchronized boolean login(String[] credentials) {
        return db.login(credentials[0], credentials[1]);
    }

    public synchronized boolean createAccount(String[] credentials) {
        return db.createAccount(credentials[0], credentials[1], credentials[2]);
    }

    public synchronized void addUser(ServerThread user) {
        connectedUsers.add(user);
    }

    public synchronized void updateAllRooms() {
        for (ServerThread user : connectedUsers) {
            if (user.getRoom() == null) {
                user.sendMessage(Command.RETURN_ROOMS, getAllRooms());
            }
        }
    }
}



