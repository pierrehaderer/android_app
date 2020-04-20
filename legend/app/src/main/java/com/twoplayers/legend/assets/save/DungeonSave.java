package com.twoplayers.legend.assets.save;

import java.util.ArrayList;
import java.util.List;

public class DungeonSave {

    protected Boolean[][] exploredRooms;
    protected List<String> openedDoors;

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
    }

    public Boolean[][] getExploredRooms() {
        return exploredRooms;
    }

    public List<String> getOpenedDoors() {
        return openedDoors;
    }
}
