package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import messaging.Command;
import messaging.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {
    Socket socket;
    public static final int PORT = 50000;
    public static final String HOST = "127.0.0.1";
    private String username;
    private String password;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private DataInputStream inputData;
    private DataOutputStream outputData;
//    public Client() {
//        userInput = new BufferedReader(new InputStreamReader(System.in));
//    }

    public static void main(String[] args) {
        Client client = new Client();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setClient(this);
        primaryStage.setTitle("Untitled");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public boolean login(String username, String password) {
        if (connect()) {
            sendMessage(Command.LOGIN, username, password);
        }
        try {
            Boolean t = inputData.readBoolean();
            return t;
        } catch (Exception e) {
            System.out.println("No response from server regarding login status");
        }
        System.out.println("return not received");
        return true;
    }

    public void sendMessage(Command command, String... data) {
        Message message = new Message(command, data);
        try {
            output.writeObject(message);
            System.out.println("message sent");
        } catch (Exception e) {
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

    public boolean joinRoom(String room) {
        sendMessage(Command.JOIN_ROOM, room);
        try {
            return inputData.readBoolean();
        } catch (Exception e) {
            System.out.println("Join room did not receive a response");
            return false;
        }
    }

    @Override
    public String toString() {
        return "I am a client lol";
    }
}





