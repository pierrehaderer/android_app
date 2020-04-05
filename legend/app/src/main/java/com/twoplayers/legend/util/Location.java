package com.twoplayers.legend.util;

public class Location {
    public int x;
    public int y;

    public Location() {
        this.x = 0;
        this.y = 0;
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location(String locationAsString) {
        this.x = 0;
        this.y = 0;
        if (locationAsString.indexOf(",") > 0) {
            String[] locationAsArray = locationAsString.split(",");
            this.x = Integer.valueOf(locationAsArray[0]);
            this.y = Integer.valueOf(locationAsArray[1]);
        } else if (locationAsString.indexOf(";") > 0) {
            String[] locationAsArray = locationAsString.split(";");
            this.x = Integer.valueOf(locationAsArray[0]);
            this.y = Integer.valueOf(locationAsArray[1]);
        }
    }

    public boolean is(int x, int y) {
        return this.x == x && this.y == y;
    }
}
