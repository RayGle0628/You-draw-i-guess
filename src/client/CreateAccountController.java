package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import messaging.Command;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountController implements Initializable {
    private Client client;
    private Stage stage;
    @FXML
    GridPane gridPane;
    @FXML
    TextField usernameField;
    @FXML
    TextField emailField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;
    @FXML
    Button createAccountButton;
    @FXML
    Button returnButton;

    public CreateAccountController() {
        this.client = Client.getClient();
        this.stage = Client.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> gridPane.requestFocus());
    }

    public void loginScene() {
        client.returnToLogin("");
    }

    public void createAccount() {
        if (!passwordField.getText().equals(repeatPasswordField.getText())) {
            System.out.println("Passwords not equal");
            //TODO add text warnings here
            return;
        }
        System.out.println(usernameField.getText());
        System.out.println(passwordField.getText());
        System.out.println(emailField.getText());
        if (client.connect()) {
            client.sendMessage(Command.CREATE_ACCOUNT, usernameField.getText(), passwordField.getText(),
                    emailField.getText());
            try {
                if (client.getInputData().readBoolean()) {
                    System.out.println("Account created");
                } else {
                    System.out.println("Creation rejected by server");
                }
            } catch (Exception e) {
                System.out.println("No repsonse from server");
            }
            client.disconnect();
        }else{ System.out.println("No repsonse from server");}
    }
}
