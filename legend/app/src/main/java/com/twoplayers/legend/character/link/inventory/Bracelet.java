package com.twoplayers.legend.character.link.inventory;

public enum Bracelet {

    NONE("empty"),
    BRACELET("bracelet");

    public String name;

    /**
     * Constructor
     */
    private Bracelet(String name) {
        this.name = name;
    }

}
