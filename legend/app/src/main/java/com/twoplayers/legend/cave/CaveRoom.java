package com.twoplayers.legend.cave;

import com.twoplayers.legend.map.MapTile;

import java.util.ArrayList;
import java.util.List;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class CaveRoom {

    private List<List<CaveTile>> content;

    public CaveRoom() {
        content = new ArrayList<>();
    }

    public CaveTile getTile(int x, int y) {
        if (y > 10) {
            return CaveTile.OUT_OF_BOUNDS;
        }
        return content.get(y).get(x);
    }

    public void addALine(String row) {
        List<CaveTile> mapRow = new ArrayList<>();
        for (int index = 0; index < 16; index++) {
            mapRow.add(CaveTile.getEnum(row.charAt(index)));
        }
        content.add(mapRow);
    }
}
