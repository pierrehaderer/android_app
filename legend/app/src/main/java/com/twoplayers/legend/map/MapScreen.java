package com.twoplayers.legend.map;

import java.util.ArrayList;
import java.util.List;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class MapScreen {

    private List<List<MapTile>> content;

    public MapScreen() {
        content = new ArrayList<>();
    }

    public MapTile getTile(int x, int y) {
        if (x < 0 || x > 15 || y < 0 || y > 10) {
            return MapTile.OUT_OF_BOUNDS;
        }
        return content.get(y).get(x);
    }

    public void addALine(String row) {
        List<MapTile> mapRow = new ArrayList<>();
        for (int index = 0; index < 16; index++) {
            mapRow.add(MapTile.getEnum(row.charAt(index)));
        }
        content.add(mapRow);
    }
}
