package com.twoplayers.legend.character.object;

public enum Potion {

    NONE("empty"),
    NOTE("note"),
    BLUE("blue_potion"),
    RED("red_potion");

    public String name;

    /**
     * Constructor
     */
    private Potion(String name) {
        this.name = name;
    }

}
