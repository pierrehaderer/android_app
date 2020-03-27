package com.twoplayers.legend.map;

import java.util.HashMap;

public enum MapTile {

    PATH(' ', true, false),
    LADDER('l', true, false),
    BLOC('x', false, true),
    BLOC_TOP_UPPER('u', false, true),
    BLOC_TOP_LOWER('d', false, true),
    BLOC_BOT_UPPER('U', false, true),
    BLOC_BOT_LOWER('D', false, true),
    CAVE('g', true, false),
    WATER('w', false, false),
    STATUE('s', false, true),
    TOMB('t', false, true),
    BRIDGE('b', true, false),
    TREE_LEFT('f', false, true),
    TREE_RIGHT('e', false, true),
    OUT_OF_BOUNDS('%', false, true);

    public char character;
    public boolean walkable;
    public boolean isblockingMissile;

    private static HashMap<Character, MapTile> hashMap;

    /**
     * Constructor
     */
    private MapTile(char character, boolean walkable, boolean isblockingMissile) {
        this.character = character;
        this.walkable = walkable;
        this.isblockingMissile = isblockingMissile;
    }

    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (MapTile mapTile : MapTile.values()) {
            hashMap.put(mapTile.character, mapTile);
        }
    }

    public static MapTile getEnum(char c) {
        return hashMap.get(c);
    }
}
