package com.twoplayers.legend.character.object;

public enum Compass {

    NONE("empty"),
    COMPASS("compass");

    public String name;

    /**
     * Constructor
     */
    private Compass(String name) {
        this.name = name;
    }

}
