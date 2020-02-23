package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.util.Logger;

public class WorldMapLoadingScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    public WorldMapLoadingScreen(Game game) {
        super(game);
        Logger.info("Entering WorldMapLoadingScreen.");
    }

    @Override
    public void update(float deltaTime) {
        ((MainActivity) game).getMapManager().init(game);
        ((MainActivity) game).getLinkManager().init(game);
        ((MainActivity) game).getGuiManager().init(game);
        game.setScreen(new WorldMapScreen(game));
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        game.getGraphics().drawRect(0, 0, WIDTH_PHONE_SCREEN + 1, HEIGHT_PHONE_SCREEN + 1, Color.BLACK);
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
