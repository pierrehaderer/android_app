package com.twoplayers.legend.map;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class MapRoom {

    private int j;
    private MapTile[][] content;

    public MapRoom() {
        j = 0;
        content = new MapTile[16][11];
    }

    public MapTile getTile(int x, int y) {
        if (x < 0 || x > 15 || y < 0 || y > 10) {
            return MapTile.OUT_OF_BOUNDS;
        }
        return content[x][y];
    }

    public void addALine(String row) {
        for (int i = 0; i < 16; i++) {
            content[i][j] = MapTile.getEnum(row.charAt(i));
        }
        j++;
    }

    public void changeTile(int i, int j, MapTile mapTile) {
        content[i][j] = mapTile;
    }
}
