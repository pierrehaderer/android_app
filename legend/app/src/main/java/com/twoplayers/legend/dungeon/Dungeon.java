package com.twoplayers.legend.dungeon;

import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class Dungeon {

    protected String id;
    protected Location location;
    protected Coordinate entrance;

    public Dungeon(DungeonInfo dungeonInfo) {
        id = dungeonInfo.id;
        location = dungeonInfo.location;
        entrance = dungeonInfo.entrance;

    }
}
