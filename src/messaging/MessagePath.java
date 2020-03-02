package messaging;

import java.util.ArrayList;

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
