package com.twoplayers.legend.character.link.inventory;

public enum Shield {

    SMALL("empty"),
    BIG("shield");

    public String name;

    /**
     * Constructor
     */
    private Shield(String name) {
        this.name = name;
    }

}
