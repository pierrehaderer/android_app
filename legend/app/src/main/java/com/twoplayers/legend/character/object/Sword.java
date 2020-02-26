package com.twoplayers.legend.character.object;

public enum Sword {

    NONE("empty"),
    WOOD("wood_sword"),
    WHITE("white_sword"),
    MAGICAL("magical_sword");

    public String name;

    /**
     * Constructor
     */
    private Sword(String name) {
        this.name = name;
    }

}
