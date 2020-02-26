package com.twoplayers.legend.character.object;

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
