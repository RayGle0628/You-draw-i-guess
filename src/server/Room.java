package server;

import messaging.Path;

import java.io.*;
import java.util.*;
public class Room extends Thread implements Serializable, Comparable<Room> {
    private String roomName;
    private ArrayList<ServerThread> users;
    private ServerThread currentDrawer;
    private String currentWord;
    private Timer timer;
    private int round;
    private ArrayList<String> words;
    private HashMap<String, Integer> scores;
    private boolean wordGuessed = false;
    private ArrayList<ServerThread> correctlyGuessed = new ArrayList<>();
   private ArrayList<Path> currentImage;
private boolean gameRunning;
    /**
     * Resets all the scores to zero at the start of a new game.
     */
    public void resetScores() {
        for (ServerThread user : users) {
            scores.put(user.getUsername(), 0);
        }
    }

    /**
     * Displays the final scores at the end of the game in the users chat areas.
     */
    public void finalScores() {
        disperseMessage(null, "The final scores are:");
        while (!scores.isEmpty()) {
            int score = Collections.max(scores.values());
            for (String key : scores.keySet()) {
                if (scores.get(key) == score) {
                    disperseMessage(null, key + " : " + score);
                    scores.remove(key);
                    break;
                }
            }
        }
    }

    /**
     * The constructor of the Room class.
     *
     * @param name is the name of the room being created.
     */
    public Room(String name) {
        roomName = name;
        users = new ArrayList<>();
        timer = new Timer("Timer");
        scores = new HashMap<>();
        gameRunning=false;
    }

    /**
     * Creates a list of 10 words randomly selected from the WordList file to be used in a game.
     */
    public void wordList() {
        words = new ArrayList<>();
        File wordList = new File("WordList");
        try {
            BufferedReader in = new BufferedReader(new FileReader(wordList));
            String word;
            while ((word = in.readLine()) != null) {
                words.add(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.shuffle(words);
        words = new ArrayList<>(words.subList(0, 10));
    }

    /**
     * Starts a new game.
     */
    public void beginGame() {
        gameRunning=true;
        correctlyGuessed = new ArrayList<>();
        scores = new HashMap<>();
        resetScores();
        round = 1;
        wordList();
        disperseMessage(null, "A game is starting in 5 seconds!");
        TimerTask task = new TimerTask() {
            public void run() {
                clearCanvas();
                startRound();
            }
        };
        timer.schedule(task, 5000);
    }

    /**
     * Starts a round of a game.
     */
    public void startRound() {
        currentImage = new ArrayList<>();
        currentWord = words.get(round - 1);
        selectNextDrawer();
        disperseMessage(null, currentDrawer.getUsername() + " is now drawing for 60 seconds!");
        currentDrawer.startDrawing(currentWord);
        //AFTER 10 SECS STOP DRAWING
        TimerTask task = new TimerTask() {
            public void run() {
                endRound();
            }
        };
        timer.schedule(task, 60000);
    }

    /**
     * Ends the current round and prepares for the next round.
     */
    public void endRound() {
        disperseMessage(null, "Round " + round + " has ended, the word was " + currentWord + "!");
        round++;
        currentDrawer.stopDrawing();
        currentDrawer = null;
        currentWord = null;
        wordGuessed = false;
        System.out.println(round);
        correctlyGuessed = new ArrayList<>();
        if (round < 11) {
            TimerTask task = new TimerTask() {
                public void run() {
                    clearCanvas();
                    startRound();
                }
            };
            disperseMessage(null, "The next round starts in 5 seconds!");
            timer.schedule(task, 5000);
        } else {
            disperseMessage(null, "10 rounds completed, game over!");
            gameRunning=false;
            finalScores();
            //TODO
        }
    }

    /**
     * Tells all users in the room to clear their canvas.
     */
    public void clearCanvas() {
        for (ServerThread user : users) {
            user.clearCanvas();
        }
    }

    public void selectNextDrawer() {
//        Random random = new Random();
//        int rand = random.nextInt(users.size());
//        currentDrawer = users.get(rand);
        currentDrawer = users.get((round - 1) % users.size());//Iterates through users in room in order that they
        // joined, repeating until game over.
    }

    /**
     * Returns the name of this room.
     *
     * @return the name of the room.
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Adds a new user to the game room when they join.
     *
     * @param user is the user to be added to the room.
     */
    public synchronized void addUser(ServerThread user) {
        users.add(user);
        scores.putIfAbsent(user.getUsername(), 0);
    }

    /**
     * Removes a user from the room when they leave.
     *
     * @param user is the user to be removed.
     */
    public synchronized void removeUser(ServerThread user) {
        users.remove(user);
        System.out.println("Removed " + user);
        currentUserList(null);
        if (users.size() == 0) {
            timer.cancel();
            timer = new Timer("Timer");
            round = 0;
            System.out.println("Not enough players, ending game.");
        }
    }

    /**
     * Sends a message to all users in a room that is to be displayed on their screen. Doesn't send in the case a
     * guess has been correctly made.
     *
     * @param fromUser the user the message originated from. Null if it is from the room directly.
     * @param text     the text that is to be sent.
     */
    public synchronized void disperseMessage(ServerThread fromUser, String text) {
        if (correctlyGuessed.contains(fromUser)) return; // Skip chat from guesser
        if (fromUser != null) {
            if (parseGuess(text)) {
                correctlyGuessed.add(fromUser);
                disperseMessage(null, fromUser.username + " has guessed correctly.");
                TimerTask task;
                if (!wordGuessed) {
                    disperseMessage(null, "You have 10 seconds to make any final guesses.");
                    timer.cancel();
                    timer = new Timer("Timer");
                    task = new TimerTask() {
                        public void run() {
                            endRound();
                        }
                    };
                    timer.schedule(task, 10000);
                }
                wordGuessed = true;
                scores.merge(fromUser.getUsername(), 1, Integer::sum);
                scores.merge(currentDrawer.getUsername(), 1, Integer::sum);
//                timer.cancel();
//                timer = new Timer("Timer");
//                endRound();
            } else { // Incorrect guess/general chat message show in chat room
                text = fromUser.getUsername() + ": " + text;
                for (ServerThread user : users) {
                    user.outgoingChatMessage(text);
                }
            }
        }
        if (fromUser == null) { // Always send server message to everyone.
            for (ServerThread user : users) {
                user.outgoingChatMessage(text);
            }
        }
        if (text.contains("!start")&&!gameRunning) {
            gameRunning=true;
            beginGame();
            System.out.println("Begining game");
        }
    }

    /**
     * Checks if a guess is correct or not before it is displayed to users.
     *
     * @param text is the text being checked.
     * @return true if the text contains a correct guess, otherwise false.
     */
    public boolean parseGuess(String text) {
        if (currentWord != null) {
            if (text.toLowerCase().contains(currentWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes an incoming path being drawn and sends it to all other users. This will not send the path if the origin
     * is not the current allowed drawer and will not send it back to the drawer.
     *
     * @param fromUser is the origin of the drawing.
     */
//    public synchronized void disperseStroke(ServerThread fromUser, int size, String colour,
//                                            ArrayList<Coordinate> coordinates) {
//        if (currentDrawer != null) {
//            if (currentDrawer.equals(fromUser)) { // prevents other clients sending draw data out of turn.
//                for (ServerThread user : users) {
//                    if (user.equals(fromUser)) continue;
//                    user.outgoingStroke(size, colour, coordinates);
//                }
//            }
//        }
//    }
    public synchronized void disperseStroke(ServerThread fromUser, Path path) {
        if (currentImage != null) {
            currentImage.add(path);
        }
        if (currentDrawer != null) {
            if (currentDrawer.equals(fromUser)) { // prevents other clients sending draw data out of turn.
                for (ServerThread user : users) {
                    if (user.equals(fromUser)) continue;
                    user.outgoingStroke(path);
                }
            }
        }
    }

    /**
     * Gets the names of all the users in this room and sends it to each client to see who is currently present.
     */
    public void currentUserList(ServerThread userImage) {
        String[] playersInRoom = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            playersInRoom[i] = users.get(i).getUsername();
        }
        for (ServerThread user : users) {
            user.pushNames(playersInRoom);
        }
        if (currentImage != null && userImage != null) {
            for (Path path : currentImage) {
                userImage.outgoingStroke(path);
            }
        }
    }

    /**
     * Compares rooms based on their names so that they are listed alphabetically.
     *
     * @param otherRoom is the room being compared to.
     * @return the ordering of the rooms for sorting.
     */
    @Override
    public int compareTo(Room otherRoom) {
        return roomName.compareTo(otherRoom.roomName);
    }

    public int getPopulation() {
        return users.size();
    }
}