package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private HashMap<Text, String> textRoom;
    private Client client;
    private Stage stage;

    public HomeController() {
        textRoom = new HashMap<>();
        client = Client.getClient();
        stage = Client.getStage();
    }

    @FXML
    Button getRoomsButton;
    @FXML
    VBox roomListVBox;

    public void getRooms() {
        roomListVBox.getChildren().clear();
        ArrayList<String> rooms = client.getRoomsList(); // EXCEPTION
        for (String room : rooms) {
            Text roomText = new Text();
            textRoom.put(roomText, room);
            roomText.setOnMouseClicked(e -> {
                joinRoom(textRoom.get(roomText));
                roomScene();
            });
            roomText.setText(room);
            roomListVBox.getChildren().add(roomText);
        }
    }

    public void joinRoom(String room) {
        if (client.joinRoom(room)) { // TRANSITION TO ROOM VIEW IF JOINED
            System.out.println("SUCCESS");
        } else {
            System.out.println("FAILURE");
        }
    }

    public void roomScene() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("GameRoom.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene roomScene = new Scene(root);
        stage.setScene(roomScene);
        roomScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getRooms();
    }
}
