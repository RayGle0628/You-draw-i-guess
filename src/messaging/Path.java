package messaging;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Path is an class that encapsulates all the information required to draw a path on a canvas using its GraphicsContext.
 */
public class Path implements Serializable {

    private ArrayList<Coordinate> coordinates;
    private int size;
    private String colour;

    /**
     * Constructor for the Path object.
     *
     * @param coordinates the coordinates that make up the path.
     * @param size        the width of the path being drawn.
     * @param colour      the colour of the path being drawn.
     */
    public Path(ArrayList<Coordinate> coordinates, int size, String colour) {
        this.coordinates = coordinates;
        this.size = size;
        this.colour = colour;
    }

    /**
     * Getter for the coordinates.
     *
     * @return the list of coordinates.
     */
    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    /**
     * Getter for the size variable.
     *
     * @return the size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Getter for the colour variable.
     *
     * @return the colour.
     */
    public String getColour() {
        return colour;
    }
}
