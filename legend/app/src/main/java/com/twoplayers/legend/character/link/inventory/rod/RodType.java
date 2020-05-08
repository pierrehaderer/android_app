package com.twoplayers.legend.character.link.inventory.rod;

public enum RodType {

    NONE("empty"),
    ROD("rod");

    public String name;

    /**
     * Constructor
     */
    private RodType(String name) {
        this.name = name;
    }

}
