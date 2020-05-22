package com.twoplayers.legend.dungeon;

import com.twoplayers.legend.util.Location;

public class DungeonDoor {

    public DungeonDoorPlacement placement;
    public DungeonDoorType type;
    public boolean isOpen;

    public DungeonDoor(DungeonDoorPlacement placement, DungeonDoorType type) {
        this.placement = placement;
        this.type = type;
        this.isOpen = false;
    }
}
