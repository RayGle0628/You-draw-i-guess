package messaging;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Coordinate implements Serializable {


    private double x;
    private double y;

    public Coordinate( double x, double y ) {

        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
