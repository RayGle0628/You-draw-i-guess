package messaging;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
    private ArrayList<Coordinate> coordinates;
    private int size;
    private String colour;

    public Path(ArrayList<Coordinate> coordinates, int size, String colour) {
        this.coordinates = coordinates;
        this.size = size;
        this.colour = colour;
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
