package client;

import javafx.application.Platform;
import messaging.Message;
import messaging.MessagePath;

import java.io.ObjectInputStream;

/**
 * ClientListener listens for messages from the server and executes them as required. It is started when a client has
 * logged in and ends when the client logs out.
 */
public class ClientListener extends Thread {

    private ObjectInputStream input;
    private Client client;
    private boolean endFlag;

    /**
     * Constructor for the ClientListener class.
     *
     * @param client - The origin client class.
     */
    public ClientListener(Client client) {
        this.client = client;
        endFlag = false;
    }

    /**
     * Starts the loop of continuously listening for inputs incoming from the server and using the message headers to
     * decide what action to take.
     */
    @Override
    public void run() {
        while (true) {
            if (endFlag) break;
            Message message;
            try {
                message = (Message) input.readObject();
            } catch (Exception e) {
                client.returnToLogin("Disconnected from server");
                client.renewListener();
                break;
            }
            switch (message.getCommand()) {
                case CONFIRM_JOIN_ROOM:
                    if (client.getHomeController() != null) {
                        Platform.runLater(() -> {
                            client.getHomeController().roomScene();
                            client.setHomeController(null);
                        });
                    }
                    break;
                case RETURN_ROOMS:
                    if (client.getHomeController() != null) {
                        Platform.runLater(() -> client.getHomeController().getRooms(message.getData()));
                    }
                    break;
                case CHAT_MESSAGE_TO_CLIENT:
                    if (client.getRoomController() != null) {
                        client.getRoomController().displayNewMessage(message.getData()[0]);
                    }
                    break;
                case USERS_IN_ROOM:
                    if (client.getRoomController() != null) {
                        client.getRoomController().updateUsers(message.getData());
                    }
                    break;
                case LOGOUT:
                    System.out.println("Successfully logged out");
                    endFlag = true;
                    break;
                case START_DRAWING:
                    if (client.getRoomController() != null) {
                        Platform.runLater(() -> client.getRoomController().enableDraw(message.getData()[0]));
                    }
                    break;
                case STOP_DRAWING:
                    if (client.getRoomController() != null) {
                        client.getRoomController().disableDraw();
                    }
                    break;
                case DRAW_PATH_TO_CLIENT:
                    if (client.getRoomController() != null) {
                        client.getRoomController().drawFromMessage(((MessagePath) message).getPath().getSize(),
                                ((MessagePath) message).getPath().getColour(),
                                ((MessagePath) message).getPath().getCoordinates());
                    }
                    break;
                case CLEAR_CANVAS:
                    if (client.getRoomController() != null) {
                        client.getRoomController().clearCanvas();
                    }
                    break;
                case RETURN_SCORES:
                    if (client.getHomeController() != null) {
                        String[] data = message.getData();
                        for (String s : data) {
                            if (s != null) {
                                client.getHomeController().addRows(s);
                            }
                        }
                    }
                    break;
                case RETURN_MY_SCORE:
                    if (client.getHomeController() != null) {
                        String[] data = message.getData();
                        if (data[0] != null) {
                            if (Integer.parseInt(data[0].split(":")[0]) > 10) {
                                client.getHomeController().addRows(data[0]);
                            }
                            break;
                        }
                    }
            }
        }
        if (endFlag) {
            client.returnToLogin("");
        }
    }

    /**
     * Sets the ObjectInputStream on which the ClientListener is to monitor for messages.
     *
     * @param input - The ObjectInputSteam to listen on.
     */
    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
}
