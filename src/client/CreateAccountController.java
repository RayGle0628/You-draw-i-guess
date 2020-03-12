package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import messaging.Command;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountController implements Initializable {
    private Client client;
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
    @FXML
    Text warningText;

    public CreateAccountController() {
        this.client = Client.getClient();
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
                        warningText.setFill(Color.GREEN);
                        warningText.setText("Account Created");
                        usernameField.clear();
                        emailField.clear();
                    } else {
                        warningText.setFill(Color.RED);
                        warningText.setText("This username is unavailable");
                    }
                } catch (Exception e) {
                    warningText.setFill(Color.RED);
                    warningText.setText("No response from server");
                }
                client.disconnect();
            } else {
                warningText.setFill(Color.RED);
                warningText.setText("No response from server");
            }
        }
        passwordField.clear();
        repeatPasswordField.clear();
    }

    public boolean validateDetails(String username, String email, String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            warningText.setFill(Color.RED);
            warningText.setText("Passwords do not match");
            return false;
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z!\\d]{8,}$")) {
            warningText.setFill(Color.RED);
            warningText.setText("Password must contain at least 8 characters including 1 capital letter and 1 number.");
            return false;
        }
        if (username.length() < 4 || username.length() > 10) {
            warningText.setFill(Color.RED);
            warningText.setText("Username must be 4-10 characters in length");
            return false;
        }
        if (!email.matches("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            warningText.setFill(Color.RED);
            warningText.setText("Invalid email address");
            return false;
        }
        return true;
    }
}
