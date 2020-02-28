package client;

import messaging.Command;
import messaging.Message;

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
        //   System.out.println("LISTENER STARTED");
//        try {
//            while (input.available() > 0) {
//                input.read();
//            }
//        } catch (Exception e) {
//            System.out.println("Could not clear queue");
//        }
        while (true) {
            //     System.out.println("Waiting for message");
if (endFlag)break;
            Message message;
            try {
              //  System.out.println("Listener blocked");
                message = (Message) input.readObject();
              //  System.out.println("Listener unblocked");
            } catch (Exception e) {
                System.out.println("Listener failed/terminated");
           //     e.printStackTrace();
                break;
            }


            switch (message.getCommand()) {
                case RECEIVE_CHAT_MESSAGE:
                    System.out.println("Receive returned message");
                    client.chatToRoom(message.getData()[0]);
                    break;
                case USERS_IN_ROOM:
                    client.updateRoomUsers(message.getData());
                    break;
                case CONFIRM_EXIT:
                    System.out.println("Listener ended by server");
                    endFlag=true;
                    break;
                case START_DRAWING:
                    client.getRoomController().unlockDrawing();
                    break;
                case INCOMING_PATH:
                    System.out.println("Path returned");
                    break;
            }
            System.out.println("Message processed");
        }
        System.out.println("LISTENER ENDED");
    }



    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
}
