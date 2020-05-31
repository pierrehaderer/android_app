package com.twoplayers.legend.basement;

import java.util.HashMap;

public enum BasementTile {

    PATH(' ', true),
    BLOC('x', false),
    OUT_OF_BOUNDS('%', false);

    public char character;
    public boolean walkable;

    private static HashMap<Character, BasementTile> hashMap;

    /**
     * Constructor
     */
    private BasementTile(char character, boolean walkable) {
        this.character = character;
        this.walkable = walkable;
    }

    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (BasementTile dungeonTile : BasementTile.values()) {
            hashMap.put(dungeonTile.character, dungeonTile);
        }
    }

    public static BasementTile getEnum(char c) {
        return hashMap.get(c);
    }
}
