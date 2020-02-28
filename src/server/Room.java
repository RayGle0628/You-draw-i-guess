package server;

import messaging.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;

public class Room extends Thread implements Serializable, Comparable<Room> {

    private String roomName;
    private ArrayList<ServerThread> users;

    private ServerThread currentDrawer;

    public void beginGame() {
        currentDrawer = users.get(0);
        if (users.size() > 0) {
            disperseMessage("A game is starting!");
        }
        currentDrawer.startDrawing();
    }

    public Room(String name) {
        roomName = name;
        users = new ArrayList<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public ArrayList<ServerThread> getUsers() {
        return users;
    }

    public void addUser(ServerThread user) {
        users.add(user);
        System.out.println("There are " + users.size() + " users present");
    }

    public void removeUser(ServerThread user) {
        users.remove(user);
        System.out.println("Removed " + user);
        currentUserList();
    }

    public void disperseMessage(String text) {

        for (ServerThread user : users) {
            user.outgoingChatMessage( text);
        }
        if (text.contains("!start")) beginGame();
    }

    public void disperseStroke(int size, String colour, ArrayList<Coordinate> coordinates) {
        for (ServerThread user:users)
        {user.outgoingStroke(size,colour,coordinates);}
    }

    /**
     * Gets the names of all the users in this room and sends it to each client to see who is currently present.
     */
    public void currentUserList() {
        String[] playersInRoom = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            playersInRoom[i] = users.get(i).getUsername();
        }
        for (ServerThread user : users) {
            user.pushNames(playersInRoom);
        }
    }

    @Override
    public String toString() {
        return roomName;
    }

    @Override
    public int compareTo(Room otherRoom) {
        return roomName.compareTo(otherRoom.roomName);
    }
}
