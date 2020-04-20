package com.twoplayers.legend.assets.save;

public class DungeonSave {

    protected Boolean[][][] exploredRooms;

    /**
     * Constructor
     */
    protected DungeonSave() {
        exploredRooms = new Boolean[10][8][8];
        for (int dungeonId = 1; dungeonId <= 9; dungeonId++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    exploredRooms[dungeonId][i][j] = false;
                }
            }
        }
    }

    public Boolean[][] getExploredRooms(String dungeonId) {
        return exploredRooms[Integer.valueOf(dungeonId)];
    }
}
