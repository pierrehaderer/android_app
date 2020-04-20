package com.twoplayers.legend.assets.save;

import com.google.gson.Gson;

import java.util.Date;

public class Save {

    protected Date date;
    protected int attempt;
    protected WorldMapSave worldMapSave;
    protected DungeonSave[] dungeonSaves;

    /**
     * Constructor
     */
    public Save() {
        attempt = 0;
        date = new Date();
        worldMapSave = new WorldMapSave();
        dungeonSaves = new DungeonSave[10];
        for (int i = 1; i < 10; i++) {
            dungeonSaves[i] = new DungeonSave();
        }
    }

    /**
     * Convert to json
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Get object from json
     */
    public static Save fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Save.class);
    }

    public int getAttempt() {
        return attempt;
    }

    public WorldMapSave getWorldMapSave() {
        return worldMapSave;
    }

    public DungeonSave getDungeonSave(String dungeonId) {
        return dungeonSaves[Integer.valueOf(dungeonId)];
    }
}
