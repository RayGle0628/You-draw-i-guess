package server;

import java.io.Serializable;
import java.util.ArrayList;

public class Room extends Thread implements Serializable, Comparable<Room> {

    private String roomName;
    private ArrayList<ServerThread> users;

    public Room(String name) {
        roomName = name;
        users=new ArrayList<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public ArrayList<ServerThread> getUsers() {
        return users;
    }

    public void addUser(ServerThread user) {
        System.out.println("Adding User to room");
         users.add(user);
        System.out.println("This is room "+roomName);
    }

    public void disperseMessage(String text) {
        for (ServerThread user : users) {
            user.outgoingChatMessage(text);
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
