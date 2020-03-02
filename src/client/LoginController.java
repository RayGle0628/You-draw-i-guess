package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    private Client client;
    private Stage stage;
 
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button loginButton;
    @FXML
    public Button createAccountButton;
    @FXML
    public Text loginWarning;
    @FXML
    public Pane pane;

    public LoginController() {
        this.client = Client.getClient();
        this.stage = Client.getStage();
    }

    public void login()  {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        Boolean success = client.login(username, pass);
        if (true) {
            homeScene();
        } else {
            loginWarning.setText("Could not log in");
            loginWarning.setId("cannotLogin-text");
        }
    }

    public void createAccountScene(ActionEvent e) throws IOException {
        Parent createAccountView = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));
        Scene tableViewScene = new Scene(createAccountView);
        stage.setScene(tableViewScene);
        tableViewScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    public void homeScene()  {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene homeScene = new Scene(root);
        stage.setScene(homeScene);
        homeScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> pane.requestFocus());
    }

    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            if (usernameField.getText().length() > 0) {
                login();
            }
        }
    }
}
