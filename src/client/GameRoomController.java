package client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import messaging.Command;
import messaging.Coordinate;
import javafx.scene.control.ColorPicker;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameRoomController implements Initializable {
    @FXML
    public TextArea chatTextArea;
    @FXML
    public ColorPicker colourPicker;
    @FXML
    public Slider sizeSlider;
    @FXML
    public Circle guideCircle;
    private int brushSize;
    private Color colour;
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
    @FXML
    private Button clear;

    /**
     * Constrictor for GameRoomController.
     */
    public GameRoomController() {
        colour = Color.web("000000");
        brushSize = 5;
        client = Client.getClient();
        stage = Client.getStage();
        client.setRoomController(this);
        client.sendMessage(Command.REQUEST_USERS);
    }
    //TODO fix guide sizing

    /**
     * initialize is automatically run after the GameRoomController has been instantiated.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colourPicker.setValue(colour);
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(colour);
        gc.setLineWidth(brushSize);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.setFill(Color.WHITE);
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(2);
        gc.setEffect(blur);
        enableDraw();
//        disableDraw();
        colourPicker.setValue(colour);
        sizeSlider.setValue(brushSize);
        guideCircle.setFill(colour);
        guideCircle.setStroke(Color.TRANSPARENT);
        guideCircle.setRadius(brushSize / 2.0);
        sizeSlider.valueProperty().addListener((arg0, arg1, arg2) -> {
            guideCircle.setRadius(sizeSlider.getValue() / 2);
            gc.setLineWidth(sizeSlider.getValue());
            brushSize = (int) sizeSlider.getValue();
        });
        clear.setOnAction(e -> { //TODO make this so it clears for everyone
            clearCanvas();
        });
        clearCanvas();
        client.sendMessage(Command.CLEAR_CANVAS);
    }

    /**
     * Enables users to draw on the canvas and send that drawing information to the server during their turn.
     */
    public void enableDraw() {
        guideCircle.setFill(colourPicker.getValue());
        gc.setStroke(colourPicker.getValue());
        colour = colourPicker.getValue();
        canvas.setOnMousePressed(event -> {
            path = new ArrayList<>();
            path.add(new Coordinate(event.getX(), event.getY()));
        });
        canvas.setOnMouseDragged(event -> {
            path.add(new Coordinate(event.getX(), event.getY()));
            draw(path);
            client.sendMessagePath(Command.DRAW_PATH, brushSize, colour.toString(),
                    new ArrayList<>(path.subList(path.size() - 2, path.size())));
        });
        canvas.setOnMouseReleased(event -> {
            this.draw(path);
            client.sendMessagePath(Command.DRAW_PATH, brushSize, colour.toString(), path);
        });
    }

    /**
     * Stops the user from being able to draw when it is not their turn.
     */
    public void disableDraw() {
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseReleased(null);
    }

    /**
     * Turns the coordinates generated while drawing to their actual representation on the canavas.
     *
     * @param path
     */
    public void draw(ArrayList<Coordinate> path) {
        if (path.size() == 1) {
            gc.strokeLine(path.get(0).getX(), path.get(0).getY(), path.get(0).getX(), path.get(0).getY());
        } else {
            gc.strokeLine(path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY(),
                    path.get(path.size() - 2).getX(), path.get(path.size() - 2).getY());
        }
    }

    /**
     * This method handles enter being pressed in the text box to send the message to the server.
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

    /**
     * Displays incoming chat messages in the chat area.
     *
     * @param message
     */
    public void displayNewMessage(String message) {
        chatTextArea.appendText(message + "\n");
    }

    public void updateUsers(String[] users) {
        Platform.runLater(() -> userList.getChildren().clear());
        for (String user : users) {
            Label userName = new Label();
            userName.setText(user);
            Platform.runLater(() -> userList.getChildren().add(userName));
        }
    }

    /**
     * Allows the user to exit a game room back to the home screen.
     */
    public void exitRoom() {
        client.sendMessage(Command.EXIT_ROOM);
        client.killThread();
        homeScene();
    }

    /**
     * Builds and shows the home scene when a user exits the game room.
     */
    public void homeScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene homeScene = new Scene(root);
            stage.setScene(homeScene);
            homeScene.getStylesheets().add(getClass().getResource("CreatAccountStyle" + ".css").toExternalForm());
            stage.show();
        } catch (Exception e) {
            System.out.println("Couldn't move back to home scene");
            e.printStackTrace();
        }
    }

    /**
     * takes an incoming drawing message and shows it on the usrs canvas.
     *
     * @param size
     * @param colour
     * @param path
     */
    public void drawFromMessage(int size, String colour, ArrayList<Coordinate> path) {
        gc.setLineWidth(size);
        gc.setStroke(Color.web(colour));
        if (path.size() == 1) {
            gc.strokeLine(path.get(0).getX(), path.get(0).getY(), path.get(0).getX(), path.get(0).getY());
        } else {
            gc.strokeLine(path.get(path.size() - 2).getX(), path.get(path.size() - 2).getY(),
                    path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY());
        }
    }

    /**
     * Clears the canvas. This should run at the end of every round and if the user who is drawing wants to restart
     * drawing.
     */
    public void clearCanvas() {
  //      gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Allows the user to pick the current colour of their drawing.
     */
    public void pickColour() {
        System.out.println("Changing colour");
        guideCircle.setFill(colourPicker.getValue());
        gc.setStroke(colourPicker.getValue());
        colour = colourPicker.getValue();
    }
}
