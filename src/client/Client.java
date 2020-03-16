package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import messaging.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * java --module-path /home/alex/IdeaProjects/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml,javafx
 * .media client.Client
 */
public class Client extends Application {

    private Socket socket;
    private static Client client;
    private static Stage stage;
    private static int port;
    private static String host;
    private ClientListener clientListener;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private GameRoomController roomController;
    private HomeController homeController;
    private LoginController loginController;
    private String username;

    public Client() {
        Client.client = this;
        clientListener = new ClientListener(this);
        readConfig();
    }

    public void readConfig() {
        File file = new File(this.getClass().getResource("/Resources/config").getFile());
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            host = in.readLine();
            port = Integer.parseInt(in.readLine());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Client.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.setTitle("Sketcher");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(new File("Resources/Images/pen.gif").toURI().toString()));//set all panes icon
        stage.show();
    }

    public boolean login(String username, String password) {
        if (connect()) {
            sendMessage(Command.LOGIN, username, password);
            try {
                boolean response = input.readBoolean();
                if (!response) {
                    String error = (String) input.readObject();
                    loginController.setLoginWarning(error);
                    socket.close();
                } else {
                    this.username = username;
                    clientListener.start();
                }
                return response;
            } catch (IOException e) {
                loginController.setLoginWarning("Unable to connect to the server");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public void sendMessage(Command command, String... data) {
        Message message = new Message(command, data);
        try {
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
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
            socket = new Socket(host, port);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            clientListener.setInput(input);
        } catch (Exception e) {
            loginController.setLoginWarning("Unable to connect to the server");
            System.out.println("Unable to connect to " + host + ":" + port);
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
            assert loginView != null;
            Scene tableViewScene = new Scene(loginView);
            stage.setScene(tableViewScene);
            stage.setResizable(false);
            stage.show();
            loginController.setLoginWarning(error);
        });
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

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public GameRoomController getRoomController() {
        return roomController;
    }

    public void setRoomController(GameRoomController roomController) {
        this.roomController = roomController;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}





