package com.twoplayers.legend.character.link.inventory;

public enum Meat {

    NONE("empty"),
    MEAT("meat");

    public String name;

    /**
     * Constructor
     */
    private Meat(String name) {
        this.name = name;
    }

}
