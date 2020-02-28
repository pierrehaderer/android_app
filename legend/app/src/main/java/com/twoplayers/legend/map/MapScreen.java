package com.twoplayers.legend.map;

import java.util.ArrayList;
import java.util.List;

/**
 * 11x16 Tiles that represent a screen in this game
 */
public class MapScreen {

    private List<List<MapTile>> content;

    public MapScreen() {
        content = new ArrayList<>();
        // This index 0 won't be used.
        content.add(null);
    }

    public MapTile getTile(int x, int y) {
        if (x < 1 || x > 16 || y < 1 || y > 11) {
            return MapTile.OUT_OF_BOUNDS;
        }
        return content.get(y).get(x);
    }

    public void addALine(String row) {
        List<MapTile> mapRow = new ArrayList<>();
        // This index 0 won't be used.
        mapRow.add(null);
        for (int index = 0; index < 16; index++) {
            mapRow.add(MapTile.getEnum(row.charAt(index)));
        }
        content.add(mapRow);
    }
}
