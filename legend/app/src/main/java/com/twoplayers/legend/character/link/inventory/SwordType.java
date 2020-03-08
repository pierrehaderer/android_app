package com.twoplayers.legend.character.link.inventory;

public enum SwordType {

    NONE("empty", 0),
    WOOD("wood_sword", 1),
    WHITE("white_sword", 2),
    MAGICAL("magical_sword", 4);

    public String name;
    public int damage;

    /**
     * Constructor
     */
    private SwordType(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

}
