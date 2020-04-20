package com.twoplayers.legend.dungeon;

public enum DungeonDoorType {
    KEY("door_key"), BOMB(""), PUSH("door_locked"), NO_MORE_ENEMY("door_locked");

    public String imagePrefix;

    private DungeonDoorType(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }
}
