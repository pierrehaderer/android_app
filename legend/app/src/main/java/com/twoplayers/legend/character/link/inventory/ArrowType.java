package com.twoplayers.legend.character.link.inventory;

public enum ArrowType {

    NONE("empty", 0),
    WOOD("wood_arrow", 2),
    WHITE("white_arrow", 4);

    public String name;
    public int damage;

    /**
     * Constructor
     */
    private ArrowType(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

}
