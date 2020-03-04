package com.twoplayers.legend.character.link.inventory;

public enum Arrow {

    NONE("empty"),
    WOOD("wood_arrow"),
    WHITE("white_arrow");

    public String name;

    /**
     * Constructor
     */
    private Arrow(String name) {
        this.name = name;
    }

}
