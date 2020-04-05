package com.twoplayers.legend.screen;

import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageOther;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.util.Logger;
import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Screen;

public class SplashLoadingScreen extends Screen {

    public static final int LEFT_SCREEN = 150;
    public static final int TOP_SCREEN = 0;
    public static final int WIDTH_SCREEN = 548;
    public static final int HEIGHT_SCREEN = 480;

    private ImageOther imageOther;
    private SoundEffectManager soundEffectManager;
    private MusicManager musicManager;
    private SaveManager saveManager;

    public SplashLoadingScreen(Game game) {
        super(game);
        Logger.info("Entering SplashLoadingScreen.");
        imageOther = ((MainActivity) game).getAllImages().getImageOther();
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();
        musicManager = ((MainActivity) game).getMusicManager();
        saveManager = ((MainActivity) game).getSaveManager();
    }

    @Override
    public void update(float deltaTime) {
        imageOther.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager.init(((MainActivity) game).getAssetManager(), game.getAudio());
        musicManager.init(((MainActivity) game).getAssetManager(), game.getAudio());
        saveManager.init(game);
        game.setScreen(new IntroScreen(game));
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawScaledImage(imageOther.get("splash_screen"), LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {

    }
}
