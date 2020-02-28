package com.twoplayers.legend.map;

import java.util.HashMap;

public enum MapTile {

    PATH(' ', true),
    LADDER('l', true),
    BLOC('x', false),
    BLOC_TOP_UPPER('u', false),
    BLOC_TOP_LOWER('d', false),
    BLOC_BOT_UPPER('U', false),
    BLOC_BOT_LOWER('D', false),
    CAVE('g', true),
    OUT_OF_BOUNDS('%', false);

    public char character;
    public boolean walkable;

    private static HashMap<Character, MapTile> hashMap;

    /**
     * Constructor
     */
    private MapTile(char character, boolean walkable) {
        this.character = character;
        this.walkable = walkable;
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
