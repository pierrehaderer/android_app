package com.twoplayers.legend.map;

import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class DungeonInfo extends EntranceInfo {

    public String dungeonId;
    public Location linkStartLocationInTheDungeon;
    public Coordinate linkStartCoordinateInTheDungeon;
    public boolean startMusic;

    public DungeonInfo() {
        super();
    }
}
