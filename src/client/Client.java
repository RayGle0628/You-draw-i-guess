package client;

import javafx.application.Application;
import javafx.application.Platform;
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
    ClientListener clientListener;
    private String username;
    private String password;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private DataInputStream inputData;
    private DataOutputStream outputData;

    private GameRoomController roomController;

    public Client() {
        clientListener = new ClientListener(this);
//        clientListener.start();
    }

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
            //      System.out.println("message sent");
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

    public void updateRoomUsers(String [] currentUsers){
        System.out.println(currentUsers[0]);
        roomController.updateUsers(currentUsers);
    }
    public boolean joinRoom(String room) {
        sendMessage(Command.JOIN_ROOM, room);
        try {
            Message roomConfirmed = ((Message) input.readObject());
            System.out.println(roomConfirmed);
            if (roomConfirmed.getBool()) {
                clientListener.start();
            }
            return roomConfirmed.getBool();
        } catch (Exception e) {
            System.out.println("Join room did not receive a response");
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
    public void stop(){
        try{
       socket.close();}
        catch(Exception e){
            System.out.println("Error closing socket");
        }
        Platform.exit();
    }

}





