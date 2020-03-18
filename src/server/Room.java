package server;

import messaging.Command;
import messaging.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Room is a class that encapsulates a game instance. It contains all game logic and stats. It is created by the
 * server at start up. Users can join rooms via their ServerThreads to play with other users.
 */
public class Room extends Thread implements Comparable<Room> {

    private String roomName;
    private ArrayList<ServerThread> users;
    private ServerThread currentDrawer;
    private String currentWord;
    private Timer timer;
    private int round;
    private ArrayList<String> words;
    private Server server;
    private HashMap<String, Integer> scores;
    private boolean wordGuessed = false;
    private ArrayList<ServerThread> correctlyGuessed = new ArrayList<>();
    private ArrayList<Path> currentImage;
    private boolean gameRunning;
    private int currentReward;

    /**
     * The constructor of the Room class.
     *
     * @param name is the name of the room being created.
     */
    public Room(String name, Server server) {
        roomName = name;
        this.server = server;
        users = new ArrayList<>();
        timer = new Timer("Timer");
        scores = new HashMap<>();
        gameRunning = false;
    }

    /**
     * Resets all the scores to zero at the start of a new game.
     */
    public void resetScores() {
        for (ServerThread user : users) {
            scores.put(user.getUsername(), 0);
        }
    }

    /**
     * Displays the final scores at the end of the game in the users chat area.
     */
    public void finalScores() {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(scores.entrySet()); // Gets all the scores from the
        // scores HashMap, and puts them in a list of Map entries.
        list.sort((user1, user2) -> { // The list is then sorted by score then username.
            if (user1.getValue().equals(user2.getValue())) {
                return user1.getKey().compareTo(user2.getKey());
            }
            return user2.getValue().compareTo(user1.getValue());
        });
        if (list.get(0).getValue() > 0) { // If the highest score was zero, the game effectively wasn't played or
            // worth recording.
            disperseMessage(null, "The winner is " + list.get(0).getKey() + "!"); // Declare the winner as the person
            // with the highest score.
            server.getDb().updateWin(list.get(0).getKey()); // Give the winner a win point in the database.
            disperseMessage(null, "The final scores are:");
            for (Map.Entry<String, Integer> score : list) {
                disperseMessage(null, score.getKey() + " : " + score.getValue()); // List all the scores in their
                // chat in descending order.
                server.getDb().updateScore(score.getKey(), score.getValue()); // Give all users their points in the
                // database.
            }
        }
    }

    /**
     * Creates a list of 10 words randomly selected from the WordList file to be used in a game.
     */
    public void wordList() {
        words = new ArrayList<>();
        // File wordList = new File("WordList");
        File wordList = new File(this.getClass().getResource("WordList").getFile());
        try {
            BufferedReader in = new BufferedReader(new FileReader(wordList)); // Opens WordList file
            String word;
            while ((word = in.readLine()) != null) {
                words.add(word); // Add every word to an ArrayList.
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.shuffle(words); // Randomly shuffle the word list.
        words = new ArrayList<>(words.subList(0, 10)); // Keep only the first 10 needed for a game.
    }

    /**
     * Starts a new game.
     */
    public void beginGame() {
        if (users.size() > 1) {
            gameRunning = true;
            correctlyGuessed = new ArrayList<>();
            scores = new HashMap<>();
            resetScores();
            round = 1;
            wordList(); // Generates new words for the game.
            disperseMessage(null, "A game is starting in 5 seconds!");
            TimerTask task = new TimerTask() {
                public void run() {
                    clearCanvas();
                    startRound();
                }
            };
            timer.schedule(task, 5000); // Starts the round after 5 seconds.
        } else disperseMessage(null, "There are not enough users to start a game.");
    }

    /**
     * Starts a round of a game.
     */
    public void startRound() {
        currentReward = 10; // Initially the first guess earns 10 points.
        currentImage = new ArrayList<>(); // Saves all path data associated with the current image.
        currentWord = words.get(round - 1);
        selectNextDrawer(); // Selects next person to draw.
        disperseMessage(null, currentDrawer.getUsername() + " is now drawing for 60 seconds!");
        currentDrawer.sendMessage(Command.START_DRAWING, currentWord);
        TimerTask task = new TimerTask() {
            public void run() {
                endRound();
            }
        };
        timer.schedule(task, 60000); // After 60 seconds, end the round.
    }

    /**
     * Ends the current round and prepares for the next round.
     */
    public void endRound() {
        if (currentWord != null) {
            disperseMessage(null, "Round " + round + " has ended, the word was " + currentWord + "!");
        }
        round++;
        if (currentDrawer != null) currentDrawer.sendMessage(Command.STOP_DRAWING); // Stops the drawer from drawing.
        currentDrawer = null;
        currentWord = null;
        wordGuessed = false;
        correctlyGuessed = new ArrayList<>();
        if (users.size() == 1) { // If there are not enough users, the game ends here.
            timer.cancel();
            timer = new Timer("Timer");
            round = 0;
            disperseMessage(null, "Not enough players, ending game.");
            gameRunning = false;
            finalScores();
            return;
        }
        if (round < 11 && gameRunning) {
            TimerTask task = new TimerTask() {
                public void run() {
                    clearCanvas();
                    startRound();
                }
            };
            disperseMessage(null, "The next round starts in 5 seconds!");
            timer.schedule(task, 5000); // If the game is still ongoing, start a new round after 5 seconds.
        } else {
            disperseMessage(null, "10 rounds completed, game over!"); // Else 10 rounds have elapsed so end the game
            // and display scores.
            gameRunning = false;
            finalScores();
            if (!gameRunning && users.size() > 2) beginGame();
        }
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    /**
     * Tells all users in the room to clear their canvas.
     */
    public void clearCanvas() {
        for (ServerThread user : users) {
            user.sendMessage(Command.CLEAR_CANVAS);
        }
    }

    /**
     * Selects the next person to draw.
     */
    public void selectNextDrawer() {
        currentDrawer = users.get((round - 1) % users.size());//Iterates through users in room in order that they
        // joined, repeating until game over.
    }

    /**
     * Adds a new user to the game room when they join.
     *
     * @param user is the user to be added to the room.
     */
    public synchronized void addUser(ServerThread user) {
        users.add(user);
        scores.putIfAbsent(user.getUsername(), 0); // If they are a new player, add them to the scoreboard.
    }

    /**
     * Removes a user from the room when they leave.
     *
     * @param user is the user to be removed.
     */
    public synchronized void removeUser(ServerThread user) {
        users.remove(user);
        currentUserList(null);
        if (users.size() == 0 && gameRunning) { // If the users drops to 0, immediately end the game.
            timer.cancel();
            timer = new Timer("Timer");
            round = 0;
            gameRunning = false;
            endRound();
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
        if (correctlyGuessed.contains(fromUser)) { // Checks if a user has already made a correct guess.
            fromUser.sendMessage(Command.CHAT_MESSAGE_TO_CLIENT, "You have already guessed correctly.");
            return; // If so tell them they can't chat and end.
        }
        if (fromUser != null) { // If the message is from a user, not the server.
            if (parseGuess(text)) { // Check the message for a correct guess.
                correctlyGuessed.add(fromUser); // If so add them to users who have guessed correctly.
                disperseMessage(null, fromUser.getUsername() + " has guessed correctly.");
                TimerTask task;
                if (!wordGuessed) { // If this player is the first to guess correctly.
                    scores.merge(currentDrawer.getUsername(), 10, Integer::sum); // Give drawer 10 points.
                    disperseMessage(null, "You have 10 seconds to make any final guesses.");
                    timer.cancel();
                    timer = new Timer("Timer");
                    task = new TimerTask() {
                        public void run() {
                            endRound();
                        }
                    };
                    timer.schedule(task, 10000); // Start a 10 second cool-down before the round ends.
                    wordGuessed = true;
                } else {
                    scores.merge(currentDrawer.getUsername(), 1, Integer::sum); // Gives drawer one more point for
                    // each subsequent correct guess in the cool-down period.
                }
                scores.merge(fromUser.getUsername(), currentReward, Integer::sum);// Gives first correct guesser 10
                // points then each subsequent one less.
                currentReward--;
            } else { // Incorrect guess/general chat message show in chat room
                text = fromUser.getUsername() + ": " + text;
                for (ServerThread user : users) {
                    user.sendMessage(Command.CHAT_MESSAGE_TO_CLIENT, text);
                }
            }
        }
        if (fromUser == null) { // Always send server message to everyone.
            for (ServerThread user : users) {
                user.sendMessage(Command.CHAT_MESSAGE_TO_CLIENT, text);
            }
        }
    }

    /**
     * Checks if a guess is correct or not before it is displayed to users.
     *
     * @param text is the text being checked.
     * @return true if the text contains a correct guess, otherwise false.
     */
    public boolean parseGuess(String text) {
        if (currentWord != null) { // Only checks when a game is actually running.
            return text.toLowerCase().matches(".*\\b" + currentWord.toLowerCase() + "\\b.*"); // To be a correct
            // guess, the whole word must be matched. Case is disregarded. The guess can not be a part of another
            // word, or it will be ignored.
        }
        return false;
    }

    /**
     * Takes an incoming path being drawn and sends it to all other users. This will not send the path if the origin
     * is not the current allowed drawer and will not send it back to the drawer.
     *
     * @param fromUser is the origin of the drawing.
     */
    public synchronized void disperseStroke(ServerThread fromUser, Path path) {
        if (currentImage != null) { // Only considered during a game.
            currentImage.add(path); // Adds incoming path to the current image list of paths.
        }
        if (currentDrawer != null) {
            if (currentDrawer.equals(fromUser)) { // Prevents users who are not supposed to be drawing from sending
                // drawing path data.
                for (ServerThread user : users) {
                    if (user.equals(fromUser)) continue;
                    user.outgoingStroke(path); // Relay the incoming path back out to other users.
                }
            }
        }
    }

    /**
     * Gets the names of all the users in this room and sends it to each client to see who is currently present.
     * Will also send all current drawing paths out for the current round to new joiners.
     */
    public void currentUserList(ServerThread userImage) {
        String[] playersInRoom = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            playersInRoom[i] = users.get(i).getUsername(); // Collates a list of player names in the room.
        }
        for (ServerThread user : users) {
            user.sendMessage(Command.USERS_IN_ROOM, playersInRoom); // Updates the user list for every player in the
            // room as soemone new has joined.
        }
        if (currentImage != null && userImage != null) {
            for (Path path : currentImage) {
                userImage.outgoingStroke(path); // Sends path data out to new joiners.
                if (currentDrawer != null && currentDrawer.getUsername().equals(userImage.getUsername()))
                    userImage.sendMessage(Command.START_DRAWING, currentWord); // If the joiner is supposed to be
                // drawing, enable it for them.
            }
        }
        if (!gameRunning && users.size() >= 2)
            beginGame(); // Starts a game if one is not already running and there are at least 2 players.
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

    /**
     * Getter for the population of a room.
     *
     * @return the population of a room.
     */
    public int getPopulation() {
        return users.size();
    }

    /**
     * Getter for the name of this room.
     *
     * @return the name of the room.
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Getter for the list of words in the current game.
     *
     * @return the list of words.
     */
    public ArrayList<String> getWords() {
        return words;
    }
}