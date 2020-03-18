package server;

import messaging.Command;
import messaging.Message;
import messaging.MessagePath;
import messaging.Path;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * ServerThread is a class that runs on it's own thread. Each user that connects to the server is given its own
 * ServerThread in order to allow concurrent communications with the server.
 */
public class ServerThread extends Thread {

    private String username;
    private Server server;
    private Room room;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /**
     * Constructor for the ServerThread. Sets up input and outputs for the socket.
     *
     * @param server is the main server which this thread originated from.
     * @param socket is the socket of the connected user for this thread.
     */
    public ServerThread(Server server, Socket socket) {
        this.server = server;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Couldn't get input/output streams");
        }
    }

    /**
     * Runs when this class has finished its constructor. run continuously listens for messages from the client and
     * executes them as required.
     */
    @Override
    public void run() {
        while (true) {
            Message message;
            try {
                message = (Message) input.readObject();
            } catch (Exception e) {
                break;
            }
            switch (message.getCommand()) {
                case LOGIN:
                    login(message.getData());
                    break;
                case LOGOUT:
                    sendMessage(Command.LOGOUT);
                    break;
                case GET_ROOMS:
                    sendMessage(Command.RETURN_ROOMS, server.getAllRooms());
                    break;
                case JOIN_ROOM:
                    joinRoom(message.getData()[0]);
                    break;
                case CHAT_MESSAGE_FROM_CLIENT:
                    room.disperseMessage(this, message.getData()[0]);
                    break;
                case REQUEST_GAME_INFO:
                    room.currentUserList(this);
                    break;
                case EXIT_ROOM:
                    exitRoom();
                    break;
                case DRAW_PATH_FROM_CLIENT:
                    room.disperseStroke(this, ((MessagePath) message).getPath());
                    break;
                case CLEAR_CANVAS:
                    room.clearCanvas();
                    break;
                case CREATE_ACCOUNT:
                    createAccount(message.getData());
                    break;
                case GET_SCORES:
                    sendMessage(Command.RETURN_SCORES, server.getDb().getHighScores());
                    break;
                case GET_MY_SCORE:
                    sendMessage(Command.RETURN_MY_SCORE, server.getDb().getMyScore(username));
                    break;
            }
        }
        cleanup();
    }

    /**
     * Exits a room on user command.
     */
    public void exitRoom() {
        room.removeUser(this); // Removes this user from the room.
        this.room = null; // Removes the room from this user.
        server.updateAllRooms(); // Forces the server to update rooms for each client.
    }

    /**
     * Attempts a log in through the server.
     *
     * @param details the log in credentials.
     */
    public void login(String[] details) {
        try {
            boolean response = server.login(details); // Attempts login via database lookup.
            if (response) { // If successful
                if (!server.getConnectedUsers().isEmpty()) { // If there are also other users online.
                    for (ServerThread user : server.getConnectedUsers()) {
                        if (user.getUsername().toLowerCase().equals(details[0].toLowerCase())) { // Checks logging in
                            // user is not already online on the server.
                            try {
                                output.writeBoolean(false); // If already online, reject login and send an error
                                // message.
                                output.flush();
                                output.writeObject("You are already logged on.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                }
            }
            output.writeBoolean(response); // If no other users are online or user is not already logged in, return
            // the result of the database lookup.
            output.flush();
            if (response)
                server.addUser(this); // If login is successful also add this user to the server pool of users.
            else output.writeObject("The details you entered were invalid."); // If unsuccessful warn user.
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = details[0]; // Sets the username associated with this ServerThread.
    }

    /**
     * Attempts to create a new account through the servers DatabaseManager.
     *
     * @param credentials the details of the new account.
     */
    private void createAccount(String[] credentials) {
        try {
            output.writeBoolean(server.createAccount(credentials)); // Send response back to the user.
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will attempt to join a room and will tell the client whether successful or not.
     *
     * @param roomName is the name of the room the user wants to join.
     */
    public void joinRoom(String roomName) {
        room = server.getRoom(roomName); // gets the room associated with the given room name.
        if (room.getPopulation() >= 10) { // If a room is full, reject joining.
            room = null; // Dereference the room.
            try {
                output.writeObject(new Message(Command.REJECT_JOIN_ROOM));
            } catch (Exception e) {
                System.out.println("Could not return room join status.");
            }
            return;
        }
        try { // Otherwise confirm the join attempt.
            output.writeObject(new Message(Command.CONFIRM_JOIN_ROOM));
        } catch (Exception e) {
            System.out.println("Could not return room join status.");
        }
        room.addUser(this); // Add this user to the joined room.
        server.updateAllRooms(); // Update the room population for all users.
    }

    /**
     * Removes this client from any rooms they are active in and from the list of online users.
     */
    public void cleanup() {
        server.getConnectedUsers().remove(this); // Remove client from server pool of users.
        try {
            room.removeUser(this); // Remove user from any rooms they are in.
        } catch (NullPointerException ignored) {
        }
        server.updateAllRooms(); // Update the room population for all users.
    }

    /**
     * Sends part of the drawing to the canvas of the user that the room has received from another user.
     */
    public void outgoingStroke(Path path) {
        try {
            output.reset(); // Reset so old path objects are not reused.
            output.writeObject(new MessagePath(Command.DRAW_PATH_TO_CLIENT, path)); // Sends path to client.
        } catch (Exception ignored) {
        }
    }

    /**
     * Sends a Message object to the client.
     *
     * @param command the Command header of the object.
     * @param data    the data associated with the given message header.
     */
    public void sendMessage(Command command, String... data) {
        try {
            output.reset();
            output.writeObject(new Message(command, data));
        } catch (Exception e) {
            System.out.println("Unable to send message out.");
        }
    }

    /**
     * Getter for the users current room.
     *
     * @return the room the user is currently in.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Gets the username of the client associated with this ServerThread.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }
}

