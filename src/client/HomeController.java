package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import messaging.Command;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * HomeController is the controller class for the home scene and is initialised from the Home.fxml file.
 */
public class HomeController implements Initializable {

    @FXML
    private VBox roomListVBox;
    @FXML
    private TableView<Ranking> scoreTable;
    @FXML
    private TableColumn<Ranking, Integer> rankColumn;
    @FXML
    private TableColumn<Ranking, String> usernameColumn;
    @FXML
    private TableColumn<Ranking, Integer> scoreColumn;
    @FXML
    private TableColumn<Ranking, Integer> winsColumn;
    private Client client;
    private Stage stage;

    /**
     * Constructor for the HomeController class.
     */
    public HomeController() {
        client = Client.getClient();
        client.setRoomController(null);
        stage = Client.getStage();
        client.setHomeController(this);
    }

    /**
     * Recieves a list of rooms and their population as an array for strings from the server.
     *
     * @param rooms an array of rooms and their population in a single string.
     */
    public void getRooms(String[] rooms) {
        roomListVBox.getChildren().clear(); // Clears room list.
        for (String room : rooms) {
            if (room.matches(".*[1-9][0-9][/].*")) // Iterate through the rooms, ignoring those with "(10/10)" or
                // more in them.
                continue; // If a room has 10+ players, it is hidden because it is at capacity.
            Text roomText = new Text(room); // Create a new text field with the room name and population.
            roomText.setOnMouseClicked(e -> client.sendMessage(Command.JOIN_ROOM, room.replaceAll("\\([0-9/]*\\)",
                    "").trim())); // Set it so when a room name is clicked on, that name is sent to the server fo the
            // user can join it. Before the room name is sent to the server, the population text is removed.
            roomListVBox.getChildren().add(roomText); // Add the room text to the list of rooms.
        }
    }

    /**
     * Transitions to the game room scene when a room is successfully joined.
     */
    public void roomScene() {
        BorderPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("GameRoom.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene roomScene = new Scene(root);
        stage.setScene(roomScene);
        roomScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param url            - The location used to resolve relative paths for the root object, or null if the
     *                       location is not known.
     * @param resourceBundle - The resources used to localize the root object, or null if the root object was not
     *                       localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username")); // Sets each column in the table
        // to accept a different field variable from the Ranking class.
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        client.sendMessage(Command.GET_ROOMS); // Requests the list of rooms form the server.
        client.sendMessage(Command.GET_SCORES); // Requests the top 10 players rankings from the server.
        client.sendMessage(Command.GET_MY_SCORE); // Requests user ranking from the server.
    }

    /**
     * Tells the server the client intends to log off when the log off button is pressed. This allows a graceful exit
     * of the ClientListener.
     */
    public void logOut() {
        client.sendMessage(Command.LOGOUT);
    }

    /**
     * Coverts a string containing ranking data from the server into a Ranking object which can then be added to the
     * score table.
     *
     * @param rankingString the string containing ranking data.
     */
    public void addRows(String rankingString) {
        // Strings appear in a single string in the format "rank:username:totalscore:totalwins". This is split into
        // its constituents by splitting on the colon and passing to the Ranking constructor.
        Ranking ranking = new Ranking(rankingString.split(":"));
        scoreTable.getItems().add(ranking);
    }
}
