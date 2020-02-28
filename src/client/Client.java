package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import messaging.Command;
import messaging.Coordinate;
import messaging.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {
    private Socket socket;
    private static Client client;
    private static Stage stage;
    private static final int PORT = 50000;
    private static final String HOST = "127.0.0.1";
    private ClientListener clientListener;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private DataInputStream inputData;
    private DataOutputStream outputData;
   private GameRoomController roomController;

    public GameRoomController getRoomController() {
        return roomController;
    }

    public Client() {
        Client.client = this;
        clientListener = new ClientListener(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Client getClient() {
        return client;
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Client.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.setTitle("Untitled");
        stage.setScene(new Scene(root));
        root.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    public boolean login(String username, String password) {
        if (connect()) {
            sendMessage(Command.LOGIN, username, password);
        }
        try {
            return inputData.readBoolean();
        } catch (Exception e) {
            System.out.println("No response from server regarding login status");
        }
        System.out.println("return not received");
        return false;
    }

    public void sendMessage(Command command, String... data) {
        Message message = new Message(command, data);
        try {
            output.writeObject(message);
            //      System.out.println("message sent");
        } catch (Exception e) {
        }
    }
    public void sendMessagePath( Command command, int size, String colour,ArrayList<Coordinate>coordinates) {
        Message message = new Message(command,size,colour, coordinates);
        System.out.println("entered method for path");
        try {
            output.writeObject(message);
            System.out.println("Sent path to server successfully");
        } catch (Exception e) {
            System.out.println("Could not sent message");
            e.printStackTrace();
        }
    }

    public boolean connect() {
        try {
            socket = new Socket(HOST, PORT);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            inputData = new DataInputStream(socket.getInputStream());
            outputData = new DataOutputStream(socket.getOutputStream());
            outputData.flush();
            clientListener.setInput(input);
            //   System.out.println("Listener updated");
        } catch (Exception e) {
            System.out.println("Unable to connect to " + HOST + ":" + PORT);
            return false;
        }
        return true;
    }

    public ArrayList<String> getRoomsList() throws Exception {
        sendMessage(Command.GET_ROOMS);
        return (ArrayList<String>) input.readObject();
    }

    public void updateRoomUsers(String[] currentUsers) {
        System.out.println(currentUsers[0]);
        roomController.updateUsers(currentUsers);
    }

    public boolean joinRoom(String room) {
        sendMessage(Command.JOIN_ROOM, room);
        try {
            //  Message roomConfirmed = ((Message) input.readObject());
            Boolean confirmation = inputData.readBoolean();
            if (confirmation) {
                clientListener.start();
            }
            return confirmation;
        } catch (Exception e) {
            return false;
        }
    }

    public void setRoomController(GameRoomController roomController) {
        this.roomController = roomController;
    }

    public void chatToRoom(String message) {
        roomController.displayNewMessage(message);
    }

    @Override
    public String toString() {
        return "I am a client lol";
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing socket");
        }
        Platform.exit();
    }

    public void killThread() throws Exception {
        clientListener = new ClientListener(this);
        clientListener.setInput(input);
        System.out.println("listener killed and reset");
    }
}





