package client;

import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import messaging.Command;
import messaging.Coordinate;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * GameRoomController is the controller class for the game room scene and is initialised from the GameRoom.fxml file.
 */
public class GameRoomController implements Initializable {

    @FXML
    private TextArea chatTextArea;
    @FXML
    private ColorPicker colourPicker;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Circle guideCircle;
    @FXML
    private TextField inputTextField;
    @FXML
    private VBox userList;
    @FXML
    private Canvas canvas;
    @FXML
    private Button clearButton;
    @FXML
    private Text wordToDraw;
    private Client client;
    private Stage stage;
    private GraphicsContext gc;
    private ArrayList<Coordinate> path;
    private int brushSize;
    private Color colour;
    private SoundFX soundFX;

    /**
     * Constrictor for GameRoomController.
     */
    public GameRoomController() {
        client = Client.getClient();
        client.setHomeController(null);
        colour = Color.web("000000"); // Initial path colour.
        brushSize = 5; // Initial path size.
        stage = Client.getStage();
        client.setRoomController(this);
        soundFX = new SoundFX(); // Creates new soundFX object for game sounds.
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
        colourPicker.setValue(colour); // Sets colour picker to initial colour.
        gc = canvas.getGraphicsContext2D(); // Creates the GraphicsContext from the canvas used to draw.
        gc.setStroke(colour); // Sets the GraphicsContext colour to the initial colour.
        gc.setLineWidth(brushSize); // Sets the GraphicsContext size to the initial size.
        gc.setLineCap(StrokeLineCap.ROUND); // Changes stroke cap shape to circular.
        gc.setLineJoin(StrokeLineJoin.ROUND); // Changes stroke line shape to circular.
        gc.setFill(Color.WHITE); // Sets the canvas to white.
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(2);
        gc.setEffect(blur); // Adds blue effect to path drawing to remove jaggedness.
        disableDraw(); // Ensures drawing is disabled by default.
        sizeSlider.setValue(brushSize);// Sets size slider to initial size.
        guideCircle.setFill(colour); // Sets the guide circle to represent the default values.
        guideCircle.setStroke(Color.TRANSPARENT);
        guideCircle.setRadius(brushSize / 2.0);
        sizeSlider.valueProperty().addListener((arg0, arg1, arg2) -> { // Adds a listener to the slider, so path size
            // is updated when it is moved.
            guideCircle.setRadius(sizeSlider.getValue() / 2);
            gc.setLineWidth(sizeSlider.getValue());
            brushSize = (int) sizeSlider.getValue();
        });
        clearButton.setOnAction(e -> { // Sets the clear canvas button to clear the canvas and send a message to the
            // server, informing other users to clear their canvases.
            clearCanvas();
            client.sendMessage(Command.CLEAR_CANVAS);
        });
        clearCanvas(); // Ensures canvas is cleared.
        client.sendMessage(Command.REQUEST_GAME_INFO); // At the end of initialisation, request all the info about the
        // game room and state to populate the GUI.
    }

    /**
     * Enables users to draw on the canvas and send that drawing information to the server during their turn.
     */
    public void enableDraw(String word) {
        wordToDraw.setText(word); // Sets the word for the user to draw.
        wordToDraw.setVisible(true);
        guideCircle.setFill(colourPicker.getValue());
        gc.setStroke(colourPicker.getValue());
        colour = colourPicker.getValue();
        canvas.setOnMousePressed(event -> { // Mouse event for starting a path, creates a new lsit of coordinates and
            // starts adding.
            path = new ArrayList<>();
            path.add(new Coordinate(event.getX(), event.getY()));
            this.draw(path); // This initial point is drawn onto the canvas
            client.sendMessagePath(Command.DRAW_PATH_FROM_CLIENT, brushSize, colour.toString(), path); // And is sent
            // to the server for others to see.
        });
        canvas.setOnMouseDragged(event -> {
            path.add(new Coordinate(event.getX(), event.getY()));// Adds more coordinates as the mouse is dragged.
            draw(path); // Also draws the path to the canvas as it is drawn.
            client.sendMessagePath(Command.DRAW_PATH_FROM_CLIENT, brushSize, colour.toString(),
                    new ArrayList<>(path.subList(path.size() - 2, path.size()))); // Sends the latest two coordinates
            // to the server to be drawn for everyone.
        });
        inputTextField.setEditable(false); // Disables outgoing chat while drawing.
        inputTextField.setVisible(false);
        colourPicker.setEditable(true); // All the drawing tools are unhidden and editable while drawing.
        colourPicker.setVisible(true);
        sizeSlider.setVisible(true);
        guideCircle.setVisible(true);
        clearButton.setVisible(true);
        gc.setLineWidth(brushSize);
        Platform.runLater(() -> canvas.requestFocus()); // Defocus the chat so messages can't be sent
        soundFX.playYouDraw(); // Plays the current drawer a unique sound to indicate their turn to draw.
    }

    /**
     * Stops the user from being able to draw and disables drawing tools when it is not their turn. Chat is enabled.
     */
    public void disableDraw() {
        wordToDraw.setVisible(false);
        wordToDraw.setText("");
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        inputTextField.setEditable(true);
        inputTextField.setVisible(true);
        colourPicker.setEditable(false);
        colourPicker.setVisible(false);
        sizeSlider.setVisible(false);
        guideCircle.setVisible(false);
        clearButton.setVisible(false);
    }

    /**
     * Turns the coordinates generated while drawing to their actual representation on the canvas.
     *
     * @param path the list of coordinates making up the path.
     */
    public synchronized void draw(ArrayList<Coordinate> path) {
        Platform.runLater(() -> { // Platform.runLater is used as drawing from another thread (e.g incoming path
            // messages from the ClientListener can cause threading issues and the canvas to become unresponsive.
            if (path.size() == 1) { // If the path is a single point, draw the path starting and ending at the same
                // point for a dot.
                gc.strokeLine(path.get(0).getX(), path.get(0).getY(), path.get(0).getX(), path.get(0).getY());
            } else { // Otherwise draw a path between the most recently added coordinate and the previous.
                gc.strokeLine(path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY(),
                        path.get(path.size() - 2).getX(), path.get(path.size() - 2).getY());
            }
        });
    }

    /**
     * Detects if the enter key is pressed while a text field is focused and tried to submit the values entered so far.
     *
     * @param ke the key event detected when typing.
     */
    public void enterPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {// If the key event is an enter, try to submit message and clear the
            // text field, otherwise do nothing.
            if (inputTextField.getText().length() > 0) {
                client.sendMessage(Command.CHAT_MESSAGE_FROM_CLIENT, inputTextField.getText());
                inputTextField.clear();
            }
        }
    }

    /**
     * Displays incoming chat messages in the chat area.
     *
     * @param message the text message to be sent to the server and other users.
     */
    public void displayNewMessage(String message) {
        if (!message.contains(":")) { // Discounts all user messages as they will always contain a colon after a
            // username.
            if (!message.contains(client.getUsername())) { // If it is this user, different sound is played by enable
                // draw.
                if (message.contains(" is now drawing for 60 seconds!"))
                    soundFX.playStartRound(); // Plays sound in response to this key phrase.
            }
            if (message.equals(client.getUsername() + " has guessed correctly."))
                soundFX.playGuess2(); // If client guesses correctly play correct sound.
            else if (message.contains(" has guessed correctly."))
                soundFX.playGuess(); // if any other client guesses correctly play different sound.
        }
        chatTextArea.appendText(message + "\n");
    }

    /**
     * Displays a list of all users in a game room in response to a server message.
     *
     * @param users the list of usernames in the current room.
     */
    public void updateUsers(String[] users) {
        Platform.runLater(() -> { // Platform.runLater is used as this GUI element is modified from outside the
            // application thread.
            userList.getChildren().clear(); // Clear the user list.
            for (String user : users) { // Iterate through the list of users and add them as a label to the user list.
                Label userName = new Label();
                userName.setText(user);
                userList.getChildren().add(userName);
            }
        });
    }

    /**
     * Allows the user to exit a game room back to the home screen.
     */
    public void exitRoom() {
        client.sendMessage(Command.EXIT_ROOM); // Inform the room a user is leaving.
        homeScene(); // Transition to home scene.
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
     * Takes an incoming drawing message and shows it on the users canvas.
     *
     * @param size   the size of the path.
     * @param colour the colour of the path.
     * @param path   to coordinates of the path.
     */
    public synchronized void drawFromMessage(int size, String colour, ArrayList<Coordinate> path) {
        Platform.runLater(() -> { // Called form outside the application thread again.
            gc.setLineWidth(size); // Colour and size are set.
            gc.setStroke(Color.web(colour));
            draw(path); // Incoming path is then drawn to canvas.
        });
    }

    /**
     * Clears the canvas. This should run at the end of every round and if the user who is drawing wants to restart
     * drawing.
     */
    public void clearCanvas() {
        Platform.runLater(() -> gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight())); // Fills the canvas with a
        // white rectangle to clear.
    }

    /**
     * Allows the user to pick the current colour of their drawing.
     */
    public void pickColour() {
        guideCircle.setFill(colourPicker.getValue()); // Updates guide to new colour.
        gc.setStroke(colourPicker.getValue()); // Sets the GraphicsContext to new colour.
        colour = colourPicker.getValue();   // Sets the current colour to new colour.
    }
}
