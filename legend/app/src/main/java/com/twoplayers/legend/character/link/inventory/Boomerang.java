package com.twoplayers.legend.character.link.inventory;

public enum Boomerang {

    NONE("empty"),
    WOOD("wood_boomerang"),
    WHITE("white_boomerang");

    public String name;

    /**
     * Constructor
     */
    private Boomerang(String name) {
        this.name = name;
    }

}
