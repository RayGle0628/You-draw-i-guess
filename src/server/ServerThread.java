package server;

import messaging.*;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private String username;
    private Socket socket;
    private Server server;
    private Room room;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private DataInputStream inputData;
    private DataOutputStream outputData;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            outputData = new DataOutputStream(socket.getOutputStream());
            outputData.flush();
            input = new ObjectInputStream(socket.getInputStream());
            inputData = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Couldn't get input/output streams");
        }
    }

    @Override
    public void run() {
        while (true) {
            Message message;
            try {
                message = (Message) input.readObject();
            } catch (Exception e) {
                cleanup();
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
                case CHAT_MESSAGE_TO_ALL:
                    room.disperseMessage(this, message.getData()[0]);
                    break;
                case REQUEST_USERS:
                    room.currentUserList(this);
                    break;
                case EXIT_ROOM:
                    exitRoom();
                    break;
                case DRAW_PATH_FROM_CLIENT:
                    room.disperseStroke(this, ((MessagePath) message).getPath());
                    break;
                case CLEAR_CANVAS:
                    sendMessage(Command.CLEAR_CANVAS);
                    break;
                case CREATE_ACCOUNT:
                    createAccount(message.getData());
                    break;
                case GET_SCORES:
                    sendMessage(Command.RETURN_SCORES, server.getDb().getHighScores());
                    break;
                case GET_MY_SCORE:
                    System.out.println("OK HERE");
                    sendMessage(Command.RETURN_MY_SCORE, server.getDb().getMyScore(username));
                    break;
            }
        }
    }

    private void getHighScores() {
        try {
            output.writeObject(server.getDb().getHighScores());
        } catch (Exception e) {
            System.out.println("User requested a list of High but it failed.");
        }
    }

    public void exitRoom() {
        room.removeUser(this);
        this.room = null;
        try {
            //     output.writeObject(new Message(Command.CONFIRM_EXIT));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    /**
     * CURRENTLY ALWAYS SAYS LOGIN fail - WAIT FOR DB IMPLEMENTATION
     *
     * @param details
     */
    public void login(String[] details) {
        try {
            boolean response = server.login(details);
            if (response) {
                if (!server.getConnectedUsers().isEmpty()) {
                    for (ServerThread user : server.getConnectedUsers()) {
                        if (user.getUsername().toLowerCase().equals(details[0].toLowerCase())) {
                            try {
                                outputData.writeBoolean(false);
                                output.writeObject("You are already logged on.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                }
            }
            outputData.writeBoolean(response);
            if (response) server.addUser(this);
            else output.writeObject("The details you entered were invalid.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = details[0];
    }

    private void createAccount(String[] credentials) {
        try {
            outputData.writeBoolean(server.createAccount(credentials));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the username of the client associated with this ServerThread.
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Will attempt to join a room and will tell the client whether successful or not.
     *
     * @param roomName
     */
    public void joinRoom(String roomName) {
        room = server.getRoom(roomName);
        if (room.getPopulation() >= 10) {
            room = null;
            try {
                // outputData.writeBoolean(false);
                output.writeObject(new Message(Command.REJECT_JOIN_ROOM));
            } catch (Exception e) {
                System.out.println("Could not return room join status.");
            }
            return;
        }
        try {
            output.writeObject(new Message(Command.CONFIRM_JOIN_ROOM));
        } catch (Exception e) {
            System.out.println("Could not return room join status.");
        }
        room.addUser(this);
    }

    /**
     * Removes this client from any rooms they are active in and from the list of online users.
     */
    public void cleanup() {
        server.getConnectedUsers().remove(this);
        try {
            room.removeUser(this);
        } catch (Exception e) {
        }
        System.out.println(username + " disconnected");
    }

    /**
     * Sends part of the drawing to the canvas of the user that the room has received from another user.
     */
    public void outgoingStroke(Path path) {
        try {
            output.reset();
            output.writeObject(new MessagePath(Command.DRAW_PATH_TO_ALL, path));
        } catch (Exception e) {
        }
    }

    public void sendMessage(Command command, String... data) {
        try {
            output.reset();
            output.writeObject(new Message(command, data));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }
}

