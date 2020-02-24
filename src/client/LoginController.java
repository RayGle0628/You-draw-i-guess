package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    static Client client;
    @FXML
    GridPane gridPane;
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

    public void login(ActionEvent e) throws IOException {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        Boolean success = client.login(username, pass);
        if (success) {
            homeScene(e);
        } else {
            loginWarning.setText("Could not log in");
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void createAccountScene(ActionEvent e) throws IOException {
        Parent createAccountView = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));
        Scene tableViewScene = new Scene(createAccountView);
        //This line gets the Stage information
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(tableViewScene);
       tableViewScene.getStylesheets().add(getClass().getResource(
               "CreatAccountStyle" +
               ".css").toExternalForm());


        stage.show();
    }

    public void homeScene(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        Parent createAccountView = loader.load();
        HomeController controller = loader.getController();
        Scene tableViewScene = new Scene(createAccountView);
        //This line gets the Stage information
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(tableViewScene);
        controller.setClient(client);
        stage.show();
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
//        Parent homeParent= loader.load();
//        Scene homeScene = new Scene(homeParent);
//        HomeController controller = loader.getController();
//        controller.setClient(this.client);
//        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
//
//        stage.setScene(new Scene(homeParent));
//       stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> gridPane.requestFocus());
    }
}
