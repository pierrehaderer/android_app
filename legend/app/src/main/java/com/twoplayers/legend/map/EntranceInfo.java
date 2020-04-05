package com.twoplayers.legend.map;

import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class EntranceInfo {

    public static final int CAVE = 1;
    public static final int DUNGEON = 2;

    public static final int DOOR = 3;
    public static final int STAIRS = 4;
    public static final int WALL = 5;
    public static final int BUSH = 6;
    public static final int LAKE = 7;

    public int type;
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
