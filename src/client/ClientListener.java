package client;

import messaging.Message;
import messaging.MessagePath;

import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * ClientListener listens for messages from the server and executes them as required.
 */
public class ClientListener extends Thread {
    private ObjectInputStream input;
    Client client;
    public boolean endFlag = false;

    public ArrayList<String> getRoomList() {
        return roomList;
    }

    ArrayList<String> roomList;

    public ClientListener(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            if (endFlag) break;
            Message message;
            try {
                message = (Message) input.readObject();
            } catch (Exception e) {
                System.out.println("Listener failed due to server closure");
                break;
            }
            switch (message.getCommand()) {
                case RECEIVE_CHAT_MESSAGE:
                    client.chatToRoom(message.getData()[0]);
                    break;
                case USERS_IN_ROOM:
                    client.updateRoomUsers(message.getData());
                    break;
                case CONFIRM_EXIT:
                    System.out.println("Listener ended by server");
                    endFlag = true;
                    break;
                case START_DRAWING:

                    client.getRoomController().enableDraw(message.getData()[0]);
                    break;
                case STOP_DRAWING:
                    client.getRoomController().disableDraw();
                    break;
                case INCOMING_PATH:
                    client.getRoomController().drawFromMessage(((MessagePath) message).getSize(),
                            ((MessagePath) message).getColour(), ((MessagePath) message).getCoordinates());
                    break;
                case CLEAR_CANVAS:
                    client.getRoomController().clearCanvas();
                    break;
            }
            //   System.out.println("Message processed");
        }
        System.out.println("LISTENER ENDED");
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
}
