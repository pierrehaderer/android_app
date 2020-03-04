package com.twoplayers.legend.character.link.inventory;

public enum Ring {

    NONE("empty"),
    BLUE("blue_ring"),
    RED("red_ring");

    public String name;

    /**
     * Constructor
     */
    private Ring(String name) {
        this.name = name;
    }

}
