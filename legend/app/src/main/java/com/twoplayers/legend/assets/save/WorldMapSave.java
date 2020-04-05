package com.twoplayers.legend.assets.save;

import java.util.HashMap;
import java.util.Map;

public class WorldMapSave {

    protected Boolean[][] exploredRooms;
    protected Boolean[][] openedEntrances;
    protected Map<String, Integer[]> worldMapEnemies;

    /**
     * Constructor
     */
    protected WorldMapSave() {
        exploredRooms = new Boolean[16][8];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                exploredRooms[i][j] = false;
            }
        }
        exploredRooms[7][7] = true;

        openedEntrances = new Boolean[16][8];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                openedEntrances[i][j] = false;
            }
        }

        worldMapEnemies = new HashMap<>();
    }

    public Boolean[][] getExploredRooms() {
        return exploredRooms;
    }

    public Boolean[][] getOpenedEntrances() {
        return openedEntrances;
    }

    public Map<String, Integer[]> getWorldMapEnemies() {
        return worldMapEnemies;
    }
}
