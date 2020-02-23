package com.twoplayers.legend.assets.sound;

import android.content.res.AssetManager;

import com.kilobolt.framework.Audio;
import com.kilobolt.framework.Music;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AllMusics {

    private Map<String, Music> musics = new HashMap<>();

    /**
     * Load all musics.
     */
    public void load(AssetManager assetManager, Audio a) {
        Logger.info("Loading all musics.");
        try {
            musics.put("empty", a.createMusic("sound/sound_effect/empty.mp3"));
            for (String fileName : assetManager.list("sound/music")) {
                if (fileName.endsWith(".mp3")) {
                    musics.put(fileName.substring(0, fileName.length() - 4), a.createMusic("sound/music/" + fileName));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load musics.");
        }
    }

    /**
     * Get an image from this asset.
     */
    public Music get(String name) {
        if (musics.containsKey(name)) {
            return musics.get(name);
        }
        return musics.get("empty");
    }
}