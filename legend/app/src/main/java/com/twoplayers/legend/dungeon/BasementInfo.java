package com.twoplayers.legend.dungeon;

import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;

public class BasementInfo {

    public DungeonInfo dungeonInfo;
    public String item;
    public Location basementLocationInTheDungeon;
    public Location stairsLocationInTheRoom;
    public Coordinate linkStartCoordinateInTheBasement;
    public Coordinate linkExitCoordinateInTheDungeon;
    public boolean isOpen;

    public BasementInfo(DungeonInfo dungeonInfo, String item, Location basementLocationInTheDungeon, Location stairsLocationInTheRoom, Coordinate linkExitCoordinateInTheDungeon, boolean isOpen) {
        this.dungeonInfo = dungeonInfo;
        this.item = item;
        this.basementLocationInTheDungeon = basementLocationInTheDungeon;
        this.stairsLocationInTheRoom = stairsLocationInTheRoom;
        this.linkExitCoordinateInTheDungeon = linkExitCoordinateInTheDungeon;
        this.linkStartCoordinateInTheBasement = new Coordinate(LocationUtil.getXFromGrid(3), LocationUtil.getYFromGrid(0));
        this.isOpen = isOpen;
    }
}
