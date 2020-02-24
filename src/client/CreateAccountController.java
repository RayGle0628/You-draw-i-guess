package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->gridPane.requestFocus());
    }


    public void loginScene(ActionEvent e) throws IOException {
        Parent createAccountView = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene tableViewScene = new Scene(createAccountView);

        //This line gets the Stage information
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();

        stage.setScene(tableViewScene);
        stage.show();
    }
}
