package com.twoplayers.legend.character.link.inventory;

public enum Ladder {

    NONE("empty"),
    LADDER("ladder");

    public String name;

    /**
     * Constructor
     */
    private Ladder(String name) {
        this.name = name;
    }

}
