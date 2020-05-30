package com.twoplayers.legend.dungeon;

import java.util.HashMap;

public enum DungeonTile {

    PATH(' ', true),
    BLOC('x', false),
    LIMIT('_', false),
    WATER('w', false),
    DOOR_LEFT('e', false),
    DOOR_RIGHT('f', false),
    CLOSED_DOOR_LEFT('c', false),
    CLOSED_DOOR_RIGHT('d', false),
    CLOSED_DOOR('a', false),
    DOOR_UP('X', false),
    BOMB_HOLE_LEFT('E', false),
    BOMB_HOLE_RIGHT('F', false),
    BOMB_HOLE('b', false),
    OUT_OF_BOUNDS('%', false);

    public char character;
    public boolean walkable;

    private static HashMap<Character, DungeonTile> hashMap;

    /**
     * Constructor
     */
    private DungeonTile(char character, boolean walkable) {
        this.character = character;
        this.walkable = walkable;
    }

    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (DungeonTile dungeonTile : DungeonTile.values()) {
            hashMap.put(dungeonTile.character, dungeonTile);
        }
    }

    public static DungeonTile getEnum(char c) {
        return hashMap.get(c);
    }
}
