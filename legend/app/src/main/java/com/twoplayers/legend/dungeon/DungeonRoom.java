package com.twoplayers.legend.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class DungeonRoom {

    private int j;
    private DungeonTile[][] content;

    public DungeonRoom() {
        j = 0;
        content = new DungeonTile[16][11];
    }

    public DungeonTile getTile(int x, int y) {
        if (x < 0 || x > 15 || y < 0 || y > 10) {
            return DungeonTile.OUT_OF_BOUNDS;
        }
        return content[x][y];
    }

    public boolean addALine(String row) {
        boolean isARealRoom = false;
        for (int i = 0; i < 16; i++) {
            char character = row.charAt(i);
            isARealRoom |= (character != 'x');
            content[i][j] = DungeonTile.getEnum(character);
        }
        j++;
        return isARealRoom;
    }

    public void changeTile(int i, int j, DungeonTile dungeonTile) {
        content[i][j] = dungeonTile;
    }
}
