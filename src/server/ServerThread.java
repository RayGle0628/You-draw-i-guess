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

    public String getUsername() {
        return username;
    }

    public void getAllRooms() {
        try {
            output.writeObject(server.getAllRooms());
            System.out.println("ROOMS SENT");
        } catch (Exception e) {
            System.out.println("User requested a list of rooms but it failed.");
        }
    }

    public void joinRoom(String roomName) {
        room = server.getRoom(roomName);
        System.out.println(room);
        room.addUser(this);
        try {
            //output.writeObject(new Message(Command.CONFIRM_ROOM_JOIN, true));
            outputData.writeBoolean(true);
        } catch (Exception e) {
            System.out.println("Could not return room join status.");
        }
    }

    public void pushNames(String[] playersInRoom) {
        try {
            output.writeObject(new Message(Command.USERS_IN_ROOM, playersInRoom));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    public void requestNames() {
    }

    public void cleanup() {
        server.getConnectedUsers().remove(this);
        try {
            room.removeUser(this);
        } catch (Exception e) {
        }
        System.out.println(username + " disconnected");
    }
//    public void incomingChatMessage(String text) {
//        room.disperseMessage(username, text);
//    }

    public void outgoingChatMessage(String text) {
        try {
            output.writeObject(new Message(Command.RECEIVE_CHAT_MESSAGE, text));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    public void outgoingStroke(int size, String colour, ArrayList<Coordinate> coordinates) {
        try {
            output.writeObject(new MessagePath(Command.INCOMING_PATH, size, colour, coordinates));
        } catch (Exception e) {
        }
        //System.out.println("Returning draw path");
    }

    public void startDrawing(String word) {
        try {
            output.writeObject(new Message(Command.START_DRAWING, word));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    public void stopDrawing() {
        try {
            output.writeObject(new Message(Command.STOP_DRAWING));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }

    public void clearCanvas() {
        try {
            output.writeObject(new Message(Command.CLEAR_CANVAS));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }
}

