package com.twoplayers.legend.character.link.inventory;

public enum BoomerangType {

    NONE("empty"),
    WOOD("wood_boomerang"),
    WHITE("white_boomerang");

    public String name;

    /**
     * Constructor
     */
    private BoomerangType(String name) {
        this.name = name;
    }

}
