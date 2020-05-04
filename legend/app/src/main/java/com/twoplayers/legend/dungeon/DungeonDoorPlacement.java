package com.twoplayers.legend.dungeon;

import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.LocationUtil;

public enum DungeonDoorPlacement {
    UP(7.5f, 1, "up"), DOWN(7.5f, 9, "down"), LEFT(1, 5, "left") , RIGHT(14, 5, "right");

    public float x;
    public float y;
    public String imagePrefix;
    public Hitbox hitbox;

    private DungeonDoorPlacement(float i, float j, String imagePrefix) {
        x = i * LocationUtil.TILE_SIZE;
        y = j * LocationUtil.TILE_SIZE;
        this.imagePrefix = imagePrefix;
        hitbox = new Hitbox(LocationUtil.LEFT_MAP + x, LocationUtil.TOP_MAP + y, -4, -4, 24, 24);
    }

    public DungeonDoorPlacement reversePlacement() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return UP;
    }
}
