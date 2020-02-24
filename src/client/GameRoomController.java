package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameRoomController {
    public TextArea chatTextArea;
    public TextField inputTextField;
    public Button exitRoomButton;
    Client client;
    @FXML


    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * This method handles enter being pressed in the text box to send the message to the server
     * @param ke
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            // doSomething();
        }
    }
}
