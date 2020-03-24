package com.twoplayers.legend.util;

public class Coordinate {
    public float x;
    public float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(String coordinateAsString) {
        this.x = 0;
        this.y = 0;
        String[] coordinateAsArray = coordinateAsString.split(",");
        if (coordinateAsArray.length == 2) {
            this.x = LocationUtil.getXFromGrid(Integer.valueOf(coordinateAsArray[0]));
            this.y = LocationUtil.getYFromGrid(Integer.valueOf(coordinateAsArray[1]));
        }
    }
}
