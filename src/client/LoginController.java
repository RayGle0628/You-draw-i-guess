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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
        client.setLoginController(this);
    }

    public void login() {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        if (client.login(username, pass)) {
            homeScene();
        }
    }

    public void createAccountScene() throws IOException {
        Parent createAccountView = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));
        Scene tableViewScene = new Scene(createAccountView);
        stage.setScene(tableViewScene);
        tableViewScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    public void homeScene() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene homeScene = new Scene(root);
        stage.setScene(homeScene);
        homeScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> pane.requestFocus());
        loginWarning.setId("cannotLogin-text");
    }

    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            if (usernameField.getText().length() > 0) {
                login();
            }
        }
    }

    public void setLoginWarning(String error) {
        loginWarning.setText(error);
        loginWarning.setVisible(true);
    }
}
