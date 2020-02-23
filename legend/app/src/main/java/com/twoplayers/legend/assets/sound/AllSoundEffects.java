package com.twoplayers.legend.assets.sound;

import android.content.res.AssetManager;

import com.kilobolt.framework.Audio;
import com.kilobolt.framework.Sound;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AllSoundEffects {

    private Map<String, Sound> sounds = new HashMap<>();

    /**
     * Load all sounds.
     */
    public void load(AssetManager assetManager, Audio a) {
        Logger.info("Loading all sound effects.");
        try {
            for (String fileName : assetManager.list("sound/sound_effect")) {
                if (fileName.endsWith(".mp3")) {
                    sounds.put(fileName.substring(0, fileName.length() - 4), a.createSound("sound/sound_effect/" + fileName));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load sound effects.");
        }
    }

    /**
     * Get an image from this asset.
     */
    public Sound get(String name) {
        if (sounds.containsKey(name)) {
            return sounds.get(name);
        }
        return sounds.get("empty");
    }
}