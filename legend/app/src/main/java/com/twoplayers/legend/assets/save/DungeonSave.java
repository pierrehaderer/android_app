package com.twoplayers.legend.assets.save;

import com.twoplayers.legend.util.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonSave {

    protected Boolean[][] exploredRooms;
    protected List<String> openedDoors;
    protected List<String> openedBasements;
    protected Map<String, Integer> itemsTaken;

    /**
     * Constructor
     */
    protected DungeonSave() {
        exploredRooms = new Boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                exploredRooms[i][j] = false;
            }
        }
        openedDoors = new ArrayList<>();
        openedBasements = new ArrayList<>();
        itemsTaken = new HashMap<>();
    }

    public Boolean[][] getExploredRooms() {
        return exploredRooms;
    }

    public List<String> getOpenedDoors() {
        return openedDoors;
    }

    public List<String> getOpenedBasements() {
        return openedBasements;
    }

    public Integer getItemsTaken(Location location) {
        return itemsTaken.containsKey(location.toString()) ? itemsTaken.get(location.toString()) : 0;
    }
}
