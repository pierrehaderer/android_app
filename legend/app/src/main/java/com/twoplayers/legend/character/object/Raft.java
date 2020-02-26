package com.twoplayers.legend.character.object;

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
