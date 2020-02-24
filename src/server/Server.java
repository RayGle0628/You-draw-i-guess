package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

public class Server {
    public static final int PORT = 50000;

    private ArrayList<ServerThread> connectedUsers;

    private TreeMap<String,Room> rooms;

    public Server() {
        connectedUsers = new ArrayList<ServerThread>();
        rooms = new TreeMap<>();
        Room r = new Room("Default");
        r.start();
        rooms.put(r.getRoomName(),r);
        r = new Room("Default2");
        r.start();
        rooms.put(r.getRoomName(),r);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

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

    public ArrayList<ServerThread> getConnectedUsers() {
        return connectedUsers;
    }

    public ArrayList<String> getAllRooms() {
        ArrayList<String> roomsList = new ArrayList<>();
        for (String room : rooms.keySet()) {
            roomsList.add(room);
        }
        return roomsList;
    }
    public Room getRoom(String roomName){

        return rooms.get(roomName);
    }
}
