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

        System.out.println(usernameField.getText());
        System.out.println(passwordField.getText());
        System.out.println(repeatPasswordField.getText());
        System.out.println(emailField.getText());
        if (validateDetails(usernameField.getText(), emailField.getText(), passwordField.getText(),
                repeatPasswordField.getText())) {
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
            } else {
                System.out.println("No repsonse from server");
            }
        }
    }

    public boolean validateDetails(String username, String email, String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            System.out.println("password mismatch");
            return false; // REJECT FOR MISMATCH PASSWORD
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z!\\d]{8,}$")) {
            System.out.println("password not complicated enough");
            return false; // Reject for not minimum requirements. 8 chars including at least 1 number and capital
        }
        if (username.length() < 4) {
            System.out.println("username not long enough");
            return false; // Reject not long enough username
        }
        if (!email.matches("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            System.out.println("invalid email");
            return false; // reject invalid email
        }
        return true;
    }
}
