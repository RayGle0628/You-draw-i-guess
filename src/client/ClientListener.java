package client;

import messaging.Message;
import messaging.MessagePath;

import java.io.ObjectInputStream;

/**
 * ClientListener listens for messages from the server and executes them as required.
 */
public class ClientListener extends Thread {
    private ObjectInputStream input;
    private Client client;
    private boolean endFlag;

    public ClientListener(Client client) {
        this.client = client;
        endFlag = false;
    }

    @Override
    public void run() {
        while (true) {
            if (endFlag) break;
            Message message;
            try {
                message = (Message) input.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Socket closed.");
                client.returnToLogin("");
                break;
            }
            switch (message.getCommand()) {
                case RECEIVE_CHAT_MESSAGE:
                    client.getRoomController().displayNewMessage(message.getData()[0]);
                    break;
                case USERS_IN_ROOM:
                    client.getRoomController().updateUsers(message.getData());
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
//                    client.getRoomController().drawFromMessage(((MessagePath) message).getSize(),
//                            ((MessagePath) message).getColour(), ((MessagePath) message).getCoordinates());
//                    System.out.println("RECEIVING PATH");
//                    System.out.println(message);
//                    System.out.println(((MessagePath)message).getPath());
//                    System.out.println(client.getRoomController());
                    client.getRoomController().drawFromMessage(

                   ((MessagePath) message).getPath().getSize(),
                            ((MessagePath) message).getPath().getColour(),
                            ((MessagePath) message).getPath().getCoordinates());




                    break;
                case CLEAR_CANVAS:
                    client.getRoomController().clearCanvas();
                    break;
            }
        }
        System.out.println("LISTENER ENDED");
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
}
