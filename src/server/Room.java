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
    private String currentWord;
    private Timer t = new Timer("Timer");
    private int round;

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
        disperseMessage(null, currentDrawer.getUsername() + " is the now drawing for 100 seconds!");
        currentDrawer.startDrawing();
        //AFTER 10 SECS STOP DRAWING
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("Here is the timer every 10 seconds");
                endRound();
            }
        };
        t.schedule(task, 100000);
    }

    public void endRound() {
        disperseMessage(null, "The round has ended!");
        round++;
        currentDrawer.stopDrawing();
        currentDrawer = null;
        currentWord = null;
        System.out.println(round);
        if (round <= 5) {
            TimerTask task = new TimerTask() {
                public void run() {
                    clearCanvas();
                    startRound();
                }
            };
            disperseMessage(null, "The next round starts in 5 seconds!");
            t.schedule(task, 5000);
        } else {
            disperseMessage(null, "10 rounds completed, game over!");
        }
    }
    public void clearCanvas(){
        for (ServerThread user:users){user.clearCanvas();}
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
        if (users.size() == 0) {
            t.cancel();
            t = new Timer("Timer");
            round = 0;
            System.out.println("Not enough players, ending game.");
        }
    }

    public synchronized void disperseMessage(ServerThread fromUser, String text) {
        if (fromUser != null) {
            if (parseGuess(text)) {
                disperseMessage(null, fromUser.username + " has guessed correctly.");
                t.cancel();
                t = new Timer("Timer");
                endRound();
            } else {
                text = fromUser.getUsername() + ": " + text;
                for (ServerThread user : users) {
                    user.outgoingChatMessage(text);
                }
            }
        }
        if (fromUser == null) {
            for (ServerThread user : users) {
                user.outgoingChatMessage(text);
            }
        }
        if (text.contains("!start")) {
            beginGame();
            System.out.println("Begining game");
        }
    }

    public boolean parseGuess(String text) {
        if (currentWord != null) {
            if (text.toLowerCase().contains(currentWord.toLowerCase())) {
                return true;
            }
        }
        return false;
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