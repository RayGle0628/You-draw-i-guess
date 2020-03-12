package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import messaging.*;

import java.io.*;
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
    private HomeController homeController;
    private LoginController loginController;
    private String username;

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

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

    public String getUsername() {
        return username;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Client.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.setTitle("Sketcher");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public boolean login(String username, String password) {
        if (connect()) {
            sendMessage(Command.LOGIN, username, password);
        }
        try {
            boolean response = inputData.readBoolean();
            if (!response) {
                String error = (String) input.readObject();
                loginController.setLoginWarning(error);
                socket.close();
            } else {
                this.username = username;
                clientListener.start();
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No response from server.");
            loginController.setLoginWarning("No response from server");
        }
        try {
            socket.close();
        } catch (Exception ignored) {
        }
        return false;
    }

    public void sendMessage(Command command, String... data) {
        Message message = new Message(command, data);
        try {
            output.writeObject(message);
        } catch (IOException e) {
            //   e.printStackTrace();
        }
    }

    public void sendMessagePath(Command command, int size, String colour, ArrayList<Coordinate> coordinates) {
        MessagePath message = new MessagePath(command, new Path(coordinates, size, colour));
        try {
            output.reset();
            output.writeObject(message);
        } catch (IOException e) {
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
        } catch (Exception e) {
            System.out.println("Unable to connect to " + HOST + ":" + PORT);
            return false;
        }
        return true;
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO
    @Deprecated
    public ArrayList<String> getRoomsList() {
        sendMessage(Command.GET_ROOMS);
        try {
            return (ArrayList<String>) input.readObject();
        } catch (Exception e) {
            System.out.println("SERVER DISCONNECT HERE REFRESH CLICKED");
            returnToLogin("Server closed unexpectedly");
        }
        return new ArrayList<>();
    }

    public DataInputStream getInputData() {
        return inputData;
    }

    public void setRoomController(GameRoomController roomController) {
        this.roomController = roomController;
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

    public void renewListener() {
        clientListener = new ClientListener(this);
        clientListener.setInput(input);
    }

    public void returnToLogin(String error) {
        clientListener.interrupt();
        renewListener();
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            Parent loginView = null;
            try {
                loginView = FXMLLoader.load(getClass().getResource("Login.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene tableViewScene = new Scene(loginView);
            stage.setScene(tableViewScene);
            stage.show();
            loginController.setLoginWarning(error);
        });
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}





