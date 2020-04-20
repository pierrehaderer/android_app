package com.twoplayers.legend.dungeon;

import com.twoplayers.legend.util.Location;

public class DungeonDoor {

    public DungeonDoorPlacement placement;
    public DungeonDoorType type;
    public Location pushLocation;
    public boolean isOpen;

    public DungeonDoor(DungeonDoorPlacement placement, DungeonDoorType type) {
        this.placement = placement;
        this.type = type;
        this.pushLocation = new Location();
        this.isOpen = false;
    }

    public DungeonDoor(DungeonDoorPlacement placement, DungeonDoorType type, Location pushLocation) {
        this.placement = placement;
        this.type = type;
        this.pushLocation = pushLocation;
        this.isOpen = false;
    }
}
