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
 * Client is the main runnable class of the client portion of our program and acts as the model. It can be run with:
 * java --module-path %PATH_TO_JAVAFX% --add-modules javafx.controls,javafx.fxml,javafx.media client.Client
 * Client also requires the messaging package and resources folder in the same directory as the client package to run.
 */
public class Client extends Application {

    private Socket socket;
    private static Client client;
    private static Stage stage;
    private int port;
    private String host;
    private ClientListener clientListener;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private GameRoomController roomController;
    private HomeController homeController;
    private LoginController loginController;
    private String username;

    /**
     * Constructor for the client class.
     */
    public Client() {
        Client.client = this;
        clientListener = new ClientListener(this);
        readConfig();
    }

    /**
     * Launches the client application
     *
     * @param args - The input arguments for the program.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Reads the config file in the client's Resources folder and extracts the hostname and port. These are the
     * connection details for the game server.
     */
    public void readConfig() {
        File file = new File(this.getClass().getResource("/Resources/config").getFile());
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            host = in.readLine(); // hostname is on the first line and port on the second.
            port = Integer.parseInt(in.readLine());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the user interface for the client application.
     *
     * @param stage - The stage to use.
     */
    @Override
    public void start(Stage stage) {
        Client.stage = stage;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Sketcher");
        stage.setResizable(false);
        assert root != null;
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(new File("Resources/Images/pen.gif").toURI().toString()));// Set all panes icon.
        stage.show();
    }

    /**
     * Attempts to connect to the server using a given username and password and returns a boolean response
     * representing whether logging in was successful or not.
     *
     * @param username - The username to log in with.
     * @param password - The password for the given username.
     * @return true if logging with the given details was successful, otherwise false.
     */
    public boolean login(String username, String password) {
        if (connect()) { // Firsts attempts a connection to the server.
            sendMessage(Command.LOGIN, username, password);  // Then passes the log in details.
            try {
                boolean response = input.readBoolean(); // Reads the response from the server
                if (!response) {
                    String error = (String) input.readObject(); // if the server rejects the log in details, a string
                    // is also returned back indicating the issue.
                    loginController.setLoginWarning(error); // This error is passed to the login controller to be
                    // displayed.
                    socket.close(); // Closes the connection
                } else {
                    this.username = username; // If the server log in was successful, start the client listener and
                    // save the username.
                    clientListener.start();
                }
                return response; // Return the log in response to the login controller.
            } catch (IOException e) {
                loginController.setLoginWarning("Unable to connect to the server");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                socket.close(); // If an error occurs, just close the socket if possible.
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    /**
     * Initiates a socket connection between the client and server, creates the Object input and output streams and
     * passes the input stream to the client listener.
     *
     * @return boolean representing whether a connection was established with the server or not.
     */
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

    /**
     * Sends a message to the server.
     *
     * @param command - The header of the message.
     * @param data    - The data contents of the message, if any.
     */
    public void sendMessage(Command command, String... data) {
        Message message = new Message(command, data);
        try {
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message containing a path object to the server.
     *
     * @param command     - The header of the message.
     * @param size        - The size of the path.
     * @param colour      - The colour of the path.
     * @param coordinates - The coordinates of the path.
     */
    public void sendMessagePath(Command command, int size, String colour, ArrayList<Coordinate> coordinates) {
        MessagePath message = new MessagePath(command, new Path(coordinates, size, colour));
        try {
            output.reset();
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects the client from the server.
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Overrides the default stop method that is called then the window is closed. CLoses the socket so that the
     * ClientListener will terminate rather than wait indefinitely.
     */
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

    /**
     * Returns the program to the log in scene.
     *
     * @param error - The error message to be displayed on the screen in the event of a disconnection from the server.
     */
    public void returnToLogin(String error) {
        renewListener(); // Resets the ClientListener for next log in attempt
        try {
            socket.close();
        } catch (Exception ignored) {
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

    /**
     * Static getter for the client object.
     *
     * @return client
     */
    public static Client getClient() {
        return client;
    }

    /**
     * Getter for the stage object.
     *
     * @return stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Getter for the username variable.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the HomeController object.
     *
     * @return homeController
     */
    public HomeController getHomeController() {
        return homeController;
    }

    /**
     * Setter for the HomeController object.
     *
     * @param homeController the new HomeController
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Getter for the RoomController object.
     *
     * @return roomController
     */
    public GameRoomController getRoomController() {
        return roomController;
    }

    /**
     * Setter for the RoomController object.
     *
     * @param roomController the new RoomController
     */
    public void setRoomController(GameRoomController roomController) {
        this.roomController = roomController;
    }

    /**
     * Getter for the ObjectInputStream object.
     *
     * @return input
     */
    public ObjectInputStream getInput() {
        return input;
    }

    /**
     * Setter for the LoginController object.
     *
     * @param loginController the new LoginController
     */
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * Getter for the port variable.
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter for the host variable.
     *
     * @return host
     */
    public String getHost() {
        return host;
    }
}





