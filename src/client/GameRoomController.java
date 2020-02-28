package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import messaging.Command;
import messaging.Coordinate;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameRoomController implements Initializable {
    private Boolean canDraw;
    private int brushSize;
    private Color colour;
    @FXML
    public TextArea chatTextArea;
    @FXML
    public TextField inputTextField;
    @FXML
    public Button exitRoomButton;
    @FXML
    public VBox userList;
    @FXML
    public Canvas canvas;
    private Client client;
    private Stage stage;
    private GraphicsContext gc;
    private ArrayList<Coordinate> path;

    public GameRoomController() {
        colour = Color.web("35d946");
        brushSize = 10;
        canDraw = false;
        client = Client.getClient();
        stage = Client.getStage();
        client.setRoomController(this);
        client.sendMessage(Command.REQUEST_USERS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(colour);
        gc.setLineWidth(brushSize);
        gc.setLineCap(StrokeLineCap.ROUND);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            path = new ArrayList<>();
            path.add(new Coordinate(event.getX(), event.getY()));
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            path.add(new Coordinate(event.getX(), event.getY()));
            draw(path);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            this.draw(path);
            client.sendMessagePath(Command.DRAW_PATH, brushSize, colour.toString(), path);
            System.out.println("sending path");
        });
    }

    public void draw(ArrayList<Coordinate> path) {
//        System.out.println(path.size());
        if (path.size() == 1) {
            gc.strokeLine(path.get(0).getX(), path.get(0).getY(), path.get(0).getX(), path.get(0).getY());
        } else {
            gc.strokeLine(path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY(),
                    path.get(path.size() - 2).getX(), path.get(path.size() - 2).getY());
        }
    }

    /**
     * This method handles enter being pressed in the text box to send the message to the server
     *
     * @param ke
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            if (inputTextField.getText().length() > 0) {
                client.sendMessage(Command.SEND_CHAT_MESSAGE, inputTextField.getText());
                inputTextField.clear();
            }
        }
    }

    public void displayNewMessage(String message) {
        System.out.println("Message to display: " + message);
        chatTextArea.appendText(message + "\n");
    }

    public void updateUsers(String[] users) {
        Platform.runLater(() -> userList.getChildren().clear());
        for (String user : users) {
            Label userName = new Label();
            userName.setText(user);
            //userName.setEditable(false);
            Platform.runLater(() -> userList.getChildren().add(userName));
        }
        System.out.println("OK");
    }

    public void exitRoom() throws Exception {
        client.sendMessage(Command.EXIT_ROOM);
        System.out.println("Trying to exit");
        client.killThread();
//        try {
        homeScene();
//        } catch (Exception e) {
//            System.out.println("FUCK");
//        }
    }

    public void homeScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene homeScene = new Scene(root);
            stage.setScene(homeScene);
            homeScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
            stage.show();
        } catch (Exception e) {
        }
    }

    public void unlockDrawing() {
        canDraw = true;
    }

    public void drawFromMessage(int size, String colour, ArrayList<Coordinate> path) {
        System.out.println("Drawing received");
        gc.setLineWidth(size);
        gc.setStroke(Color.web(colour));
        if (path.size() == 1) {
            gc.strokeLine(path.get(0).getX(), path.get(0).getY(), path.get(0).getX(), path.get(0).getY());
        } else {
            for (int i = 0; i < path.size() - 1; i++) {
                gc.strokeLine(path.get(i).getX(), path.get(i).getY(), path.get(i + 1).getX(), path.get(i + 1).getY());
            }
        }
    }
}
