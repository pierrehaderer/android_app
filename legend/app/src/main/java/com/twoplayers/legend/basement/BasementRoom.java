package com.twoplayers.legend.basement;

/**
 * 16x11 Tiles that represent a screen in this game
 */
public class BasementRoom {

    private int j;
    private BasementTile[][] content;

    public BasementRoom() {
        j = 0;
        content = new BasementTile[16][11];
    }

    public BasementTile getTile(int x, int y) {
        if (x < 0 || x > 15 || y < 0 || y > 10) {
            return BasementTile.OUT_OF_BOUNDS;
        }
        return content[x][y];
    }

    public boolean addALine(String row) {
        boolean isARealRoom = false;
        for (int i = 0; i < 16; i++) {
            char character = row.charAt(i);
            isARealRoom |= (character != 'x');
            content[i][j] = BasementTile.getEnum(character);
        }
        j++;
        return isARealRoom;
    }

    public void changeTile(int i, int j, BasementTile dungeonTile) {
        content[i][j] = dungeonTile;
    }
}
