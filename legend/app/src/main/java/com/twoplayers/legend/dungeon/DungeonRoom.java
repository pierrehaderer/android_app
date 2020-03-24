package com.twoplayers.legend.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class DungeonRoom {

    private List<List<DungeonTile>> content;

    public DungeonRoom() {
        content = new ArrayList<>();
    }

    public DungeonTile getTile(int x, int y) {
        if (x < 1 || x > 14 || y < 1 || y > 9) {
            return DungeonTile.OUT_OF_BOUNDS;
        }
        return content.get(y).get(x);
    }

    public void addALine(String row) {
        List<DungeonTile> mapRow = new ArrayList<>();
        for (int index = 0; index < 16; index++) {
            mapRow.add(DungeonTile.getEnum(row.charAt(index)));
        }
        content.add(mapRow);
    }
}
