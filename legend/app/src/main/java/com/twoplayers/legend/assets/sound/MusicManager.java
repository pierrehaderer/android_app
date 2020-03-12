package com.twoplayers.legend.assets.sound;

import android.content.res.AssetManager;

import com.kilobolt.framework.Audio;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Music;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicManager implements IManager {

    private Map<String, Music> musics = new HashMap<>();

    private List<MusicToPlay> musicsToPlay;

    /**
     * Load all musics.
     */
    public void init(AssetManager assetManager, Audio a) {
        Logger.info("Loading all musics.");
        try {
            musics.put("empty", a.createMusic("sound_effect/empty.mp3"));
            for (String fileName : assetManager.list("music")) {
                if (fileName.endsWith(".mp3")) {
                    musics.put(fileName.substring(0, fileName.length() - 4), a.createMusic("music/" + fileName));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load musics.");
        }

        musicsToPlay = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (musicsToPlay.size() > 0) {
            MusicToPlay musicToPlay = musicsToPlay.get(0);
            if (musicToPlay.delay > 0) {
                musicToPlay.delay -= deltaTime;
            } else {
                if (!musicToPlay.hasBeenPlayed) {
                    musicToPlay.hasBeenPlayed = true;
                    musicToPlay.music.play();
                } else {
                    if (!musicToPlay.music.isPlaying()) {
                        Logger.info("Music is finished. Remove it from the list.");
                        musicsToPlay.remove(0);
                    }
                }
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
    }

    /**
     * Clear all musics to play
     */
    public void clear() {
        Logger.info("Stopping all musics.");
        List<Music> tempMusicList = new ArrayList<>();
        for (MusicToPlay musicToPlay : musicsToPlay) {
            tempMusicList.add(musicToPlay.music);
        }
        musicsToPlay.clear();
        for (Music music : tempMusicList) {
            if (music.isPlaying()) {
                music.stop();
            }
        }
    }

    /**
     * Add a music to the list of music to play
     */
    public void plan(float delay, String name, boolean isLooping) {
        if (musics.containsKey(name)) {
            Logger.info("Adding music : " + name);
            musicsToPlay.add(new MusicToPlay(delay, musics.get(name), isLooping));
        } else {
            Logger.error("Trying to add an unknown music : " + name);
        }
    }

    /**
     * Stop the music currently playing.
     */
    public void stop() {
        if (musicsToPlay.size() > 0) {
            musicsToPlay.get(0).delay = Float.MAX_VALUE;
            musicsToPlay.get(0).music.stop();
        }
    }

    /**
     * Resume the music currently playing.
     */
    public void resume() {
        if (musicsToPlay.size() > 0) {
            musicsToPlay.get(0).music.play();
            musicsToPlay.get(0).delay = 0;
        }
    }
}