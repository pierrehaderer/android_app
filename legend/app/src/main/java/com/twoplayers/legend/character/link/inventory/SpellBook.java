package com.twoplayers.legend.character.link.inventory;

public enum SpellBook {

    NONE("empty"),
    BOOK("spell_book");

    public String name;

    /**
     * Constructor
     */
    private SpellBook(String name) {
        this.name = name;
    }

}
