package messaging;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Messages are used to send information between clients and data that is more complex than single data types. The
 * Command enum defined the type of message and data is a series of strings to go with the command.
 * For example A message could be LOGIN command with the username and password as data.
 * Commands are defined in the Command Enum
 */
public class Message implements Serializable {

    Command command;
    String[] data;

    ArrayList<Coordinate> coordinates;

    int size;
    String colour;

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public int getSize() {
        return size;
    }

    public Message() {
        command = null;
        data = null;
    }

    public Message(Command command, String... data) {
        this.command = command;
        this.data = data;
    }

    public String getColour() {
        return colour;
    }

    public Message(Command command, int size, String colour, ArrayList<Coordinate> coordinates) {
        this.size = size;
        this.colour = colour;
        this.command = command;
        this.coordinates = coordinates;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "This message is a " + command;
    }
}
