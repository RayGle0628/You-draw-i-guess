package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeController {
    private HashMap<Text, String> textRoom;
    private Client client;

    public HomeController() {
        textRoom = new HashMap<>();
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
            });
            roomText.setText(room.toString());
            roomListVBox.getChildren().add(roomText);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void joinRoom(String room) {
        System.out.println("Clicked " + room);
        if (client.joinRoom(room)) { // TRANSITION TO ROOM VIEW IF JOINED
            System.out.println("SUCCESS");
        } else {
            System.out.println("FAILURE");
        }
    }
}
