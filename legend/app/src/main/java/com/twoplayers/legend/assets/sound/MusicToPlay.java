package com.twoplayers.legend.assets.sound;

import com.kilobolt.framework.Music;

public class MusicToPlay {
    protected float delay;
    protected Music music;
    protected boolean hasBeenPlayed;

    public MusicToPlay(float delay, Music music, boolean isLooping) {
        this.delay = delay;
        this.music = music;
        this.music.setLooping(isLooping);
        hasBeenPlayed = false;
    }
}
