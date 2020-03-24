package com.twoplayers.legend.util;

public class Location {
    public int x;
    public int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location(String locationAsString) {
        this.x = 0;
        this.y = 0;
        String[] locationAsArray = locationAsString.split(",");
        if (locationAsArray.length == 2) {
            this.x = Integer.valueOf(locationAsArray[0]);
            this.y = Integer.valueOf(locationAsArray[1]);
        }
    }
}
