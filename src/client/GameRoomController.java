package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import messaging.Command;

public class GameRoomController {
    public TextArea chatTextArea;
    public TextField inputTextField;
    public Button exitRoomButton;
    Client client;
    @FXML


    public void setClient(Client client) {
        this.client = client;
        client.setRoomController(this);
    }

    /**
     * This method handles enter being pressed in the text box to send the message to the server
     * @param ke
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            System.out.println("ENTER PRESSED");
           client.sendMessage(Command.SEND_CHAT_MESSAGE,inputTextField.getText());
        }
    }
    public void displayNewMessage(String message){
        System.out.println("Message to display: "+message);
        chatTextArea.appendText(message+"\n");
    }
}
