package com.twoplayers.legend.util;

public class Coordinate {

    private static final float A_TINY_BIT_MORE = 0.01f;
    public static final float ONE_MORE_PIXEL = 1f;

    public float x;
    public float y;

    public Coordinate() {
        this.x = 0;
        this.y = 0;
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * In this constructor we need to adjust to avoid rounding errors and a "one pixel" issue with the empty tile hiding link when he exits the cave.
     */
    public Coordinate(String coordinateAsString) {
        this.x = 0;
        this.y = 0;
        if (coordinateAsString.indexOf(",") > 0) {
            String[] coordinateAsArray = coordinateAsString.split(",");
            this.x = LocationUtil.getXFromGrid(Integer.parseInt(coordinateAsArray[0])) + ONE_MORE_PIXEL;
            this.y = LocationUtil.getYFromGrid(Integer.parseInt(coordinateAsArray[1])) + A_TINY_BIT_MORE;
        } else if (coordinateAsString.indexOf(";") > 0) {
            String[] coordinateAsArray = coordinateAsString.split(";");
            this.x = LocationUtil.getXFromGrid(Integer.parseInt(coordinateAsArray[0])) + ONE_MORE_PIXEL;
            this.y = LocationUtil.getYFromGrid(Integer.parseInt(coordinateAsArray[1])) + A_TINY_BIT_MORE;
        }
    }
}
