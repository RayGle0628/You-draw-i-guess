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
                try{
                roomScene(e);}
                catch(Exception ex){}
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




    public void roomScene(MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameRoom.fxml"));
        Parent createAccountView = loader.load();
        GameRoomController controller = loader.getController();
        Scene tableViewScene = new Scene(createAccountView);
        //This line gets the Stage information

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(tableViewScene);
        controller.setClient(client);
        stage.show();}


}
