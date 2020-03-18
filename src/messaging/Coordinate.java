package messaging;

import java.io.Serializable;

/**
 * Coordinate is a class that simply represents a pair of coordinates.
 */
public class Coordinate implements Serializable {

    private double x;
    private double y;

    /**
     * Constructor for the coordinate object.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the x coordinate.
     *
     * @return the x coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Getter for the y coordinate.
     *
     * @return the y coordinate.
     */
    public double getY() {
        return y;
    }
}
