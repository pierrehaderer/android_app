package com.twoplayers.legend.character.object;

public enum DungeonMap {

    NONE("empty"),
    MAP("dungeon_map");

    public String name;

    /**
     * Constructor
     */
    private DungeonMap(String name) {
        this.name = name;
    }

}
