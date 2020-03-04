package messaging;

import java.util.ArrayList;

/**
 * MessagePath is an extension of the Message class. MessagePath is used in a specific scenario when it is required
 * to send information about a path being drawn between a client and server. Like Message it contains a Command
 * header, but instead of strings, an ArrayList of coordinates is instead sent which contains the information about
 * the location of a drawn path alongside its size and colour.
 */
public class MessagePath extends Message {
    ArrayList<Coordinate> coordinates;
    int size;
    String colour;

    public MessagePath(Command command, int size, String colour, ArrayList<Coordinate> coordinates) {
        super(command);
        this.size = size;
        this.colour = colour;
        this.coordinates = coordinates;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public int getSize() {
        return size;
    }

    public String getColour() {
        return colour;
    }
}
