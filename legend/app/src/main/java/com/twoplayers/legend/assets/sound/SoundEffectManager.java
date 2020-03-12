package com.twoplayers.legend.assets.sound;

import android.content.res.AssetManager;

import com.kilobolt.framework.Audio;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Sound;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectManager implements IManager {

    private Map<String, Sound> sounds = new HashMap<>();

    /**
     * Load all sounds.
     */
    public void init(AssetManager assetManager, Audio a) {
        Logger.info("Loading all sound effects.");
        try {
            for (String fileName : assetManager.list("sound_effect")) {
                if (fileName.endsWith(".mp3")) {
                    sounds.put(fileName.substring(0, fileName.length() - 4), a.createSound("sound_effect/" + fileName));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load sound effects.");
        }
    }

    @Override
    public void update(float deltaTime, Graphics g) {
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
    }

    /**
     * Play a sound from this asset.
     */
    public void play(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).play(1);
        }
    }
}