package com.twoplayers.legend.util;

public class Destination extends Coordinate {

    public boolean isValid;
    public Orientation orientation;

    public Destination(float x, float y, Orientation orientation, boolean isValid) {
        super(x, y);
        this.orientation = orientation;
        this.isValid = isValid;
    }
}
