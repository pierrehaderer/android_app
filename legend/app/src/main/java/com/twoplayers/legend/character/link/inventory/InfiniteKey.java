package com.twoplayers.legend.character.link.inventory;

public enum InfiniteKey {

    NONE("empty"),
    KEY("infinite_key");

    public String name;

    /**
     * Constructor
     */
    private InfiniteKey(String name) {
        this.name = name;
    }

}
