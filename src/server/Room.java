package server;

import messaging.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Room extends Thread implements Serializable, Comparable<Room> {

    private String roomName;
    private ArrayList<ServerThread> users;

    private ServerThread currentDrawer;
    private String currentWord = "Dog";
    Timer t = new Timer("Timer");
    int round;

    public void beginGame() {
        round = 1;
        disperseMessage(null, "A game is starting in 5 seconds!");
        TimerTask task = new TimerTask() {
            public void run() {

                startRound();
            }
        };
        t.schedule(task, 5000);

    }

    public void startRound() {
        selectNextWord();
        selectNextDrawer();
        disperseMessage(null, currentDrawer.getUsername() + " is the now drawing for 10 seconds!");
        currentDrawer.startDrawing();
        //AFTER 10 SECS STOP DRAWING
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("Here is the timer every 10 seconds");
                endRound();
            }
        };
        t.schedule(task, 10000);
    }

    public void endRound() {
        disperseMessage(null, "The round has ended!");
        round++;
        currentDrawer.stopDrawing();
        currentDrawer = null;
        currentWord = null;
        System.out.println(round);
        if (round<=5){
        TimerTask task = new TimerTask() {
            public void run() {
                disperseMessage(null, "The next round starts in 5 seconds!");
                startRound();
            }
        };
        t.schedule(task, 5000);}
        else {disperseMessage(null, "10 rounds completed, game over!");}
    }

    public void selectNextDrawer() {
        Random random = new Random();
        int rand = random.nextInt(users.size());
        currentDrawer = users.get(rand);
    }

    public void selectNextWord() {
        currentWord = "Dog";
    }

    public Room(String name) {
        roomName = name;
        users = new ArrayList<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public synchronized void addUser(ServerThread user) {
        users.add(user);
        System.out.println("There are " + users.size() + " users present");
    }

    public synchronized void removeUser(ServerThread user) {
        users.remove(user);
        System.out.println("Removed " + user);
        currentUserList();
    }

    public synchronized void disperseMessage(ServerThread fromUser, String text) {
        if (fromUser != null) text = fromUser.getUsername() + ": " + text;
        for (ServerThread user : users) {
            user.outgoingChatMessage(text);
        }
        if (text.contains("!start")) beginGame();
    }

    public synchronized void disperseStroke(ServerThread fromUser, int size, String colour,
                                            ArrayList<Coordinate> coordinates) {
        if (currentDrawer.equals(fromUser)) { // prevents other clients sending draw data out of turn.
            for (ServerThread user : users) {
                user.outgoingStroke(size, colour, coordinates);
            }
        }
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