package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

/**
 * LoginController is the controller class for the login scene and is initialised from the Login.fxml file.
 */
public class LoginController implements Initializable {

    private Client client;
    private Stage stage;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text loginWarning;
    @FXML
    private Pane pane;

    /**
     * Constructor for the LoginController class.
     */
    public LoginController() {
        this.client = Client.getClient();
        this.stage = Client.getStage();
        client.setLoginController(this);
    }

    /**
     * Takes user entered details to log in and passes them to the client to attempt to establish a connection with
     * the server.
     */
    public void login() {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        if (client.login(username, pass)) {
            homeScene(); // If the client reports login is successful, transition to the home scene.
        }
    }

    /**
     * Transitions to the create account scene in the event that the sign up button is pressed.
     */
    public void createAccountScene() {
        Parent createAccountView = null;
        try {
            createAccountView = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert createAccountView != null;
        Scene tableViewScene = new Scene(createAccountView);
        stage.setScene(tableViewScene);
        tableViewScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
        stage.show();
    }

    /**
     * Transitions to the home scene in the event that a successful log in has been made.
     */
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

    /**
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param url            - The location used to resolve relative paths for the root object, or null if the
     *                       location is not known.
     * @param resourceBundle - The resources used to localize the root object, or null if the root object was not
     *                       localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> pane.requestFocus()); // Sets it so that the text fields are not focused by default.
        loginWarning.setId("cannotLogin-text");
    }

    /**
     * Detects if the enter key is pressed while a text field is focused and tried to submit the values entered so far.
     *
     * @param ke the key event detected when typing.
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            if (usernameField.getText().length() > 0) {// If the key event is an enter and the username field isn't
                // empty, try to log in, otherwise do nothing.
                login();
            }
        }
    }

    /**
     * Sets a warning message under the text fields in the event of an error.
     *
     * @param error The error message to be displayed.
     */
    public void setLoginWarning(String error) {
        loginWarning.setText(error);
        loginWarning.setVisible(true);
    }
}
