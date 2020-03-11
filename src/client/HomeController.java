package client;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomeController implements Initializable {
    @FXML
    public Button logOutButton;
    private HashMap<Text, String> textRoom;
    private Client client;
    private Stage stage;

    public HomeController() {
        textRoom = new HashMap<>();
        client = Client.getClient();
        stage = Client.getStage();
    }

    @FXML
    VBox roomListVBox;

    public void getRooms() {
        roomListVBox.getChildren().clear();
        ArrayList<String> rooms = client.getRoomsList(); // EXCEPTION
        for (String room : rooms) {
            if (room.matches("[1-9][0-9][/]")) continue;
            Text roomText = new Text();
            textRoom.put(roomText, room);
            roomText.setOnMouseClicked(e -> {
                joinRoom(textRoom.get(roomText));
            });
            roomText.setText(room);
            roomListVBox.getChildren().add(roomText);
        }
    }

    public void joinRoom(String room) {
        room=room.replaceAll("\\([0-9\\/]*\\)","").trim();
        if (client.joinRoom(room)) { // TRANSITION TO ROOM VIEW IF JOINED
            roomScene();
        } else {
            System.out.println("FAILURE");
        }
    }

    public void roomScene() {
        BorderPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("GameRoom.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene roomScene = new Scene(root);      
        stage.setScene(roomScene);
        roomScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getRooms();
    }

    public void logOut(ActionEvent actionEvent) {
        client.returnToLogin("");
    }
}
