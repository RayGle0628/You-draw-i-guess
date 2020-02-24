package server;

import java.io.Serializable;
import java.util.ArrayList;

public class Room extends Thread implements Serializable,Comparable<Room> {

    private String roomName;
    private ArrayList<ServerThread> users;

    public Room(String name){
        roomName=name;
    }


    public String getRoomName() {
        return roomName;
    }

    public ArrayList<ServerThread> getUsers() {
        return users;
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
