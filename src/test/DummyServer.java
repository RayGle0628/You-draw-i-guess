package test;

import messaging.Command;
import messaging.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer extends Thread {

    ServerSocket serverSocket;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    DataInputStream inputData;
    DataOutputStream outputData;
    Message message;
    String username;
    String room;
    boolean open = true;

    public static void main(String[] args) {
        DummyServer server = new DummyServer();
        server.run();
    }

    @Override
    public void run() {
        System.out.println("Dummy server starting and waiting for connection.");
        try {
            serverSocket = new ServerSocket(50000);
            socket = serverSocket.accept();
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            outputData = new DataOutputStream(socket.getOutputStream());
            outputData.flush();
            input = new ObjectInputStream(socket.getInputStream());
            inputData = new DataInputStream(socket.getInputStream());
            System.out.println("Dummy server connected to client.");
        } catch (Exception e) {
            e.printStackTrace();
            //   System.exit(1);
        }
        while (open) {
            start2();
        }
    }

    public Message start2() {
        try {
            message = (Message) input.readObject();
            System.out.println();
            System.out.println("Received: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            open = false;
            return null;
        }
        switch (message.getCommand()) {
            case LOGIN:
                username = message.getData()[0];
                returnBoolean(true);
                break;
            case LOGOUT:
                returnMessage(Command.LOGOUT);
                break;
            case GET_ROOMS:
                returnMessage(Command.RETURN_ROOMS, "Room 1 (5/10)", "Room 2 (10/10)", "Room 3 (0/10)");
                break;
            case JOIN_ROOM:
                returnMessage(Command.CONFIRM_JOIN_ROOM);
                room = message.getData()[0];
                break;
            case CHAT_MESSAGE_FROM_CLIENT:
                returnMessage(Command.CHAT_MESSAGE_TO_CLIENT, username + ": " + message.getData()[0]);
                if (message.getData()[0].equals("!start")) returnMessage(Command.START_DRAWING, "Testing");
                break;
            case REQUEST_GAME_INFO:
                if (room.equals("Room 1"))
                    returnMessage(Command.USERS_IN_ROOM, "user1", "user2", "user3", "user4", "user5", username);
                if (room.equals("Room 2")) returnMessage(Command.USERS_IN_ROOM, username);
                break;
            case EXIT_ROOM:
                break;
            case DRAW_PATH_FROM_CLIENT:
                break;
            case CLEAR_CANVAS:
                returnMessage(Command.CLEAR_CANVAS);
                break;
            case CREATE_ACCOUNT:
                returnBoolean(true);
                break;
            case GET_SCORES:
                returnMessage(Command.RETURN_SCORES, "1:user1:612:15", "2:user2:405:11", "3:user3:398:10", "4" +
                        ":user4:330:9", "5:user5:297:7", "6:user6:245:7", "7:user7:222:5", "8:user8:211:5", "9" +
                        ":user9:187:4", "10:user10:132:3");
                break;
            case GET_MY_SCORE:
                returnMessage(Command.RETURN_MY_SCORE, "14:" + username + ":98:2");
                break;
        }
        return message;
    }

    public void returnMessage(Command command, String... data) {
        try {
            Message m = new Message(command, data);
            output.writeObject(m);
            System.out.println("Returned: " + m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnBoolean(boolean bool) {
        try {
            output.writeBoolean(bool);
            output.flush();
            System.out.println("Returned: " + bool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message getMessage() {
        return message;
    }

    public void closeServer() {
        System.out.println("Closed server");
        try {
            serverSocket.close();
            socket.close();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}
