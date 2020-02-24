package server;

import messaging.Command;
import messaging.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
                System.out.println(username + " disconnected");
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
                    incomingChatMessage(message.getData()[0]);
                    break;
            }
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
        } catch (Exception e) {
            System.out.println("User requested a list of rooms but it failed.");
        }
    }

    public void joinRoom(String roomName) {
        room = server.getRoom(roomName);
        System.out.println(room);
        room.addUser(this);
        try {
            output.writeObject(new Message(Command.CONFIRM_ROOM_JOIN, true));
        } catch (Exception e) {
            System.out.println("Could not return room join status.");
        }
    }

    public void cleanup() {
        server.getConnectedUsers().remove(this);
        System.out.println(username + " disconnected");
    }

    public void incomingChatMessage(String text) {
        text = username + ": " + text;
        room.disperseMessage(text);
    }

    public void outgoingChatMessage(String text) {
        try {
            output.writeObject(new Message(Command.RECEIVE_CHAT_MESSAGE, text));
        } catch (Exception e) {
            System.out.println("unable to send message out");
        }
    }
}

