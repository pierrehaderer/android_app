package com.twoplayers.legend.map;

import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class EntranceInfo {

    public static final int CAVE = 1;
    public static final int DUNGEON = 2;

    public int type;
    public Location location;
    public Coordinate entrance;

    public EntranceInfo() {
        type = CAVE;
        location = new Location();
        entrance = new Coordinate();
    }
}
