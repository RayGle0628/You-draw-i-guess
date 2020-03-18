package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import messaging.Command;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * CreateAccountController is the controller class for the account creation scene and is initialised from the
 * CreateAccount.fxml file.
 */
public class CreateAccountController implements Initializable {

    private Client client;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Text warningText;

    /**
     * Constructor of the CreateAccountController class.
     */
    public CreateAccountController() {
        this.client = Client.getClient();
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
        Platform.runLater(() -> gridPane.requestFocus());// Sets it so that the text fields are not focused by default.
    }

    /**
     * Returns to the login scene with no error messages.
     */
    public void loginScene() {
        client.returnToLogin("");
    }

    /**
     * Extracts all text from the input fields, checked that are valid through the validateDetails method and then
     * send them to the server.
     * A warning message is displayed based on the validation and response from the serer.
     */
    public void createAccount() {
        if (validateDetails(usernameField.getText(), emailField.getText(), passwordField.getText(),
                repeatPasswordField.getText())) { // Checks all details meet guidelines.
            if (client.connect()) { // Attempts to connect to the server and sends the details of the new account.
                client.sendMessage(Command.CREATE_ACCOUNT, usernameField.getText(), passwordField.getText(),
                        emailField.getText());
                try {
                    if (client.getInput().readBoolean()) { // If server confirms account creation inform user.
                        setWarningText(Color.GREEN, "Account Created");
                        usernameField.clear();
                        emailField.clear();
                    } else { // If false response then server rejected it due to the username already existing
                        setWarningText(Color.RED, "This username is unavailable");
                    }
                } catch (Exception e) { // Otherwise no response was received so assume account wasn't created.
                    setWarningText(Color.RED, "No response from server");
                }
                client.disconnect();
            } else {
                setWarningText(Color.RED, "No response from server");
            }
        }
        passwordField.clear();
        repeatPasswordField.clear();
    }

    /**
     * Validates entered details before they are sent to the server and displays an appropriate error message if they
     * fail validation.
     *
     * @param username       the text entered in the username field.
     * @param email          the text entered in the email field.
     * @param password       the text entered in the password field.
     * @param repeatPassword the text entered in the repeat password field.
     */
    public boolean validateDetails(String username, String email, String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) { // Checks password and repeatPassword match.
            setWarningText(Color.RED, "Passwords do not match");
            return false;
        }
        if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,17}$")) { // Checks password is between 8-16
            // characters in length inclusive and contains at least 1 lower case and uppercase letter and 1 number.
            // Special
            // characters are allowed.
            setWarningText(Color.RED,
                    "Password must be 8-16 characters including 1 upper and lower case letter and " + "1" + " " +
                            "number");
            return false;
        }
        if (username.length() < 4 || username.length() > 10 || !username.matches("^[a-zA-Z0-9_-]*$")) { // Checks
            // username is 4 - 10 characters long and only contains letters, numbers and "_" or "-".
            setWarningText(Color.RED,
                    "Username must be 4-10 characters in length and must only contain a-z,A-Z,0-9," + "_,- " +
                            "characters");
            return false;
        }
        if (!email.matches("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$")) { // Checks the email is
            // in the format something@domain.top_level_domain. Email can't contain special characters other than
            // "@", ".", "-" and "_". Top level domain must be between 2-4 characters long.
            setWarningText(Color.RED, "Invalid email address");
            return false;
        }
        return true;
    }

    /**
     * Detects if the enter key is pressed while a text field is focused and tried to submit the values entered so far.
     *
     * @param ke the key event detected when typing.
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) { // If the key event is an enter, try to create account, otherwise
            // do nothing.
            createAccount();
        }
    }

    /**
     * Sets the warning text and colour below the text fields following submission.
     *
     * @param colour  the colour of the text.
     * @param message the text to display.
     */
    public void setWarningText(Color colour, String message) {
        if (warningText != null) {
            warningText.setFill(colour);
            warningText.setText(message);
        }
    }
}
