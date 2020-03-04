package com.twoplayers.legend.character.link.inventory.sword;

public enum SwordType {

    NONE("empty"),
    WOOD("wood_sword"),
    WHITE("white_sword"),
    MAGICAL("magical_sword");

    public String name;

    /**
     * Constructor
     */
    private SwordType(String name) {
        this.name = name;
    }

}
