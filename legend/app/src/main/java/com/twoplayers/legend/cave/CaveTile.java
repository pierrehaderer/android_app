package com.twoplayers.legend.cave;

import java.util.HashMap;

public enum CaveTile {

    PATH(' ', true),
    BLOC('x', false),
    LIMIT('¨', true),
    OUT_OF_BOUNDS('%', false);

    public char character;
    public boolean walkable;

    private static HashMap<Character, CaveTile> hashMap;

    /**
     * Constructor
     */
    private CaveTile(char character, boolean walkable) {
        this.character = character;
        this.walkable = walkable;
    }

    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (CaveTile mapTile : CaveTile.values()) {
            hashMap.put(mapTile.character, mapTile);
        }
    }

    public static CaveTile getEnum(char c) {
        return hashMap.get(c);
    }
}
