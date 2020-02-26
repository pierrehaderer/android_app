package com.twoplayers.legend.character.object;

public enum Light {

    NONE("empty"),
    BLUE("blue_light"),
    RED("red_light");

    public String name;

    /**
     * Constructor
     */
    private Light(String name) {
        this.name = name;
    }

}
