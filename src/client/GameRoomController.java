package client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.Command;

public class GameRoomController {
    public TextArea chatTextArea;
    public TextField inputTextField;
    public Button exitRoomButton;
    public VBox userList;
    Client client;
    Stage stage;

    public GameRoomController() {
        client = Client.getClient();
        stage = Client.getStage();
        client.setRoomController(this);
        client.sendMessage(Command.REQUEST_USERS);
    }

    /**
     * This method handles enter being pressed in the text box to send the message to the server
     *
     * @param ke
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            if (inputTextField.getText().length() > 0) {
                client.sendMessage(Command.SEND_CHAT_MESSAGE, inputTextField.getText());
                inputTextField.clear();
            }
        }
    }

    public void displayNewMessage(String message) {
        System.out.println("Message to display: " + message);
        chatTextArea.appendText(message + "\n");
    }

    public void updateUsers(String[] users) {
        Platform.runLater(() -> userList.getChildren().clear());
        for (String user : users) {
            TextField userName = new TextField();
            userName.setText(user);
            userName.setEditable(false);
            Platform.runLater(() -> userList.getChildren().add(userName));
        }
        System.out.println("OK");
    }

    public void exitRoom() {
        client.sendMessage(Command.EXIT_ROOM);
    }
}
