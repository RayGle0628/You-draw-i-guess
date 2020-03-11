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
import messaging.Command;

public class HomeController implements Initializable {
    @FXML
    public Button logOutButton;
    private HashMap<Text, String> textRoom;
    private Client client;
    private Stage stage;

    public HomeController() {
        client = Client.getClient();
        client.setRoomController(null);
        textRoom = new HashMap<>();

        stage = Client.getStage();
        client.setHomeController(this);
    }

    @FXML
    VBox roomListVBox;

    //TODO
    public void getRooms(String[] rooms) {
        roomListVBox.getChildren().clear();
//        ArrayList<String> rooms = client.getRoomsList(); // EXCEPTION
        for (String room : rooms) {
            if (room.matches("[1-9][0-9][/]")) continue;
            Text roomText = new Text();
            textRoom.put(roomText, room);
            roomText.setOnMouseClicked(e -> {
//                joinRoom(textRoom.get(roomText));
                client.sendMessage(Command.JOIN_ROOM, room.replaceAll("\\([0-9\\/]*\\)", "").trim());
            });
            roomText.setText(room);
            roomListVBox.getChildren().add(roomText);
        }
    }

    //TODO

//    public void joinRoom(String room) {
//        room = room.replaceAll("\\([0-9\\/]*\\)", "").trim();
//        client.sendMessage(Command.JOIN_ROOM, room);
////        if (client.joinRoom(room)) { // TRANSITION TO ROOM VIEW IF JOINED
////            roomScene();
////        } else {
////            System.out.println("FAILURE");
////        }
//    }

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

    public void requestUIInfo() {
        client.sendMessage(Command.GET_ROOMS);
        client.sendMessage(Command.GET_SCORES);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //getRooms();
        requestUIInfo();
    }

    public void logOut(ActionEvent actionEvent) {
        client.sendMessage(Command.LOGOUT);

    }
}
