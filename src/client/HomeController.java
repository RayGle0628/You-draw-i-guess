package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeController {
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

    public void getRooms() throws Exception {
        System.out.println(client);
        ArrayList<String> rooms = client.getRoomsList();
        for (String room : rooms) {
            Text roomText = new Text();
            textRoom.put(roomText, room);
            roomText.setOnMouseClicked(e -> {
                joinRoom(textRoom.get(roomText));
                try {
                    roomScene();
                } catch (Exception ex) {
                }
            });
            roomText.setText(room.toString());
            roomListVBox.getChildren().add(roomText);
        }
    }
//    public void setClient(Client client) {
//        this.client = client;
//    }

    public void joinRoom(String room) {
        System.out.println("Clicked " + room);
        if (client.joinRoom(room)) { // TRANSITION TO ROOM VIEW IF JOINED
            System.out.println("SUCCESS");
        } else {
            System.out.println("FAILURE");
        }
    }

    public void roomScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GameRoom.fxml"));
        Scene roomScene = new Scene(root);
        stage.setScene(roomScene);
        roomScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }
}
