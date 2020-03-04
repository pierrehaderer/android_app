package com.twoplayers.legend.character.link.inventory;

public enum Raft {

    NONE("empty"),
    RAFT("raft");

    public String name;

    /**
     * Constructor
     */
    private Raft(String name) {
        this.name = name;
    }

}
