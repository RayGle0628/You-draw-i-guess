package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class HomeController implements Initializable {
    @FXML
    public Button logOutButton;
    public TableView<Ranking> scoreTable;
    public TableColumn<Ranking, Integer> rankColumn;
    public TableColumn<Ranking, String> usernameColumn;
    public TableColumn<Ranking, Integer> scoreColumn;
    public TableColumn<Ranking, Integer> winsColumn;
    private Client client;
    private Stage stage;

    public HomeController() {
        client = Client.getClient();
        client.setRoomController(null);
        stage = Client.getStage();
        client.setHomeController(this);
    }

    @FXML
    VBox roomListVBox;

    public void getRooms(String[] rooms) {
        roomListVBox.getChildren().clear();
        for (String room : rooms) {

            if (room.matches(".*[1-9][0-9][/].*")) continue;
            Text roomText = new Text();
            roomText.setOnMouseClicked(e -> client.sendMessage(Command.JOIN_ROOM, room.replaceAll("\\([0-9/]*\\)",
                    "").trim()));
            roomText.setText(room);
            roomListVBox.getChildren().add(roomText);
        }
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        client.sendMessage(Command.GET_ROOMS);
        client.sendMessage(Command.GET_SCORES);
        client.sendMessage(Command.GET_MY_SCORE);
    }

    public void logOut() {
        client.sendMessage(Command.LOGOUT);
    }

    public void addRows(String rankingString) {
        Ranking ranking = new Ranking(rankingString.split(":"));
        scoreTable.getItems().add(ranking);
    }
}
