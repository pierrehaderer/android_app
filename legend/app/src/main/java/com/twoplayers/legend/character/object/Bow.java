package com.twoplayers.legend.character.object;

public enum Bow {

    NONE("empty"),
    BOW("bow");

    public String name;

    /**
     * Constructor
     */
    private Bow(String name) {
        this.name = name;
    }

}
