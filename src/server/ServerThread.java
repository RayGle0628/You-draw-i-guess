package server;

import messaging.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    String username;
    private Socket socket;
    private Server server;
    private Room room;
    ObjectInputStream input;
    ObjectOutputStream output;
    DataInputStream inputData;
    DataOutputStream outputData;

    public ServerThread(Server server, Socket socket) {
        System.out.println("ServerThread started for a client.");
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
                case GET_ROOMS:
                    getAllRooms();
                    break;
                case JOIN_ROOM:
                    joinRoom(message.getData()[0]);
                    break;
                case SEND_CHAT_MESSAGE:
                    room.disperseMessage(this, message.getData()[0]);
                    break;
                case REQUEST_USERS:
                    room.currentUserList();
                    break;
                case EXIT_ROOM:
                    exitRoom();
                    break;
                case DRAW_PATH:
                    room.disperseStroke(this, ((MessagePath) message).getSize(), ((MessagePath) message).getColour(),
                            ((MessagePath) message).getCoordinates());
                    break;
                case CLEAR_CANVAS:
                    room.clearCanvas();
                    break;
            }
        }
    }

    public void exitRoom() {
        room.removeUser(this);
        this.room = null;
        try {
            output.writeObject(new Message(Command.CONFIRM_EXIT));
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
        System.out.println("A client has tried to log in as " + details[0] + " " + details[1]);
        try {
            if (details[0].equals("alex") || details[0].equals("mingrui") || details[0].equals("edward") || details[0].equals("lele") || details[0].equals("bowen") || details[0].equals("test")) {
                outputData.writeBoolean(true);
                this.username = details[0];
            } else outputData.writeBoolean(false);
        } catch (Exception e) {
            System.out.println("Could not return message");
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

    //TODO can be moved into switch case.
    public void getAllRooms() {
        try {
            output.writeObject(server.getAllRooms());
            System.out.println("ROOMS SENT");
        } catch (Exception e) {
            System.out.println("User requested a list of rooms but it failed.");
        }
    }

    //TODO room rejections
    public void joinRoom(String roomName) {
        room = server.getRoom(roomName);
        if (room.getPopulation() >= 10) {
            room = null;
            try {
                outputData.writeBoolean(false);
            } catch (Exception e) {
                System.out.println("Could not return room join status.");
            }
            return;
        }
        room.addUser(this);
        try {
            outputData.writeBoolean(true);
        } catch (Exception e) {
            System.out.println("Could not return room join status.");
        }
    }

    /**
     * Sends a list of names of users in the same game room as the client.
     *
     * @param playersInRoom is a list of names from the room to be sent to the client.
     */
    public void pushNames(String[] playersInRoom) {
        try {
            output.writeObject(new Message(Command.USERS_IN_ROOM, playersInRoom));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
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
     * Sends a text message to the chat pane of the client that a room has received.
     *
     * @param text is the text to be displayed to the client.
     */
    public void outgoingChatMessage(String text) {
        try {
            output.writeObject(new Message(Command.RECEIVE_CHAT_MESSAGE, text));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    /**
     * Sends part of the drawing to the canvas of the user that the room has received from another user.
     *
     * @param size        is the size of the path to be drawn in pixels.
     * @param colour      is the colour of the path to be drawn.
     * @param coordinates is the location of the path to be drawn on the canvas.
     */
    public void outgoingStroke(int size, String colour, ArrayList<Coordinate> coordinates) {
        try {
            output.writeObject(new MessagePath(Command.INCOMING_PATH, size, colour, coordinates));
        } catch (Exception e) {
        }
    }

    /**
     * Tells the client that it is their turn to draw and unlocks the related features in the GameRoomController.
     *
     * @param word is the picture that is to be drawn.
     */
    public void startDrawing(String word) {
        try {
            output.writeObject(new Message(Command.START_DRAWING, word));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    /**
     * Tells the client that their turn to draw has ended and locks the related features in the GameRoomController.
     */
    public void stopDrawing() {
        try {
            output.writeObject(new Message(Command.STOP_DRAWING));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    /**
     * Tells the client to clear their canvas in between rounds.
     */
    public void clearCanvas() {
        try {
            output.writeObject(new Message(Command.CLEAR_CANVAS));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }
}

