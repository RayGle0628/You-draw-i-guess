package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

public class Server {
    public static final int PORT = 5000;
    private ArrayList<ServerThread> connectedUsers;
    private TreeMap<String, Room> rooms;

    public Server() {
        connectedUsers = new ArrayList<ServerThread>();
        rooms = new TreeMap<>();
        Room r = new Room("Default");
        r.start();
        rooms.put(r.getRoomName(), r);
        r = new Room("Default2");
        r.start();
        rooms.put(r.getRoomName(), r);
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
            System.out.println("Server starting");
            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread t = new ServerThread(this, socket);
                t.start();
                connectedUsers.add(t);
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
    public ArrayList<String> getAllRooms() {
        ArrayList<String> roomsList = new ArrayList<>();
        for (String room : rooms.keySet()) {
            roomsList.add(room);
        }
        return roomsList;
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
