package com.twoplayers.legend.map;

import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class EntranceInfo {

    public static final int DOOR = 1;
    public static final int STAIRS = 2;
    public static final int WALL = 3;
    public static final int BUSH = 4;
    public static final int LAKE = 5;

    public int hiddenStyle;
    public int style;
    public boolean hidden;
    public Location location;
    public Coordinate entrance;
    public Coordinate exit;
    public Hitbox hitbox;

    public EntranceInfo() {
        style = DOOR;
        location = new Location();
        entrance = new Coordinate();
        exit = new Coordinate();
        hitbox = new Hitbox(0,0,0,0,16,16);
    }

    public static int getStyle(String style) {
        switch (style) {
            case "STAIRS":
                return STAIRS;
            case "WALL":
                return WALL;
            case "BUSH":
                return BUSH;
            case "LAKE":
                return LAKE;
            default:
                return DOOR;
        }
    }
}
