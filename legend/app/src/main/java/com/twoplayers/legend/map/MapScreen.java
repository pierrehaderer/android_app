package com.twoplayers.legend.map;

import com.twoplayers.legend.util.Logger;

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
        if (x < 1) {
            Logger.warn("Link is out of the map.");
            x = 1;
        }
        if (x > 16) {
            Logger.warn("Link is out of the map.");
            x = 16;
        }
        if (y < 1) {
            Logger.warn("Link is out of the map.");
            y = 1;
        }
        if (y > 11) {
            Logger.warn("Link is out of the map.");
            y = 11;
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
