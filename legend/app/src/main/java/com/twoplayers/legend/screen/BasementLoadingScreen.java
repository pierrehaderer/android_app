package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.dungeon.BasementInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class BasementLoadingScreen extends Screen {

    private static final int WIDTH_PHONE_SCREEN = 800;
    private static final int HEIGHT_PHONE_SCREEN = 480;

    private static final int INITIAL_BASEMENT_POSITION_X = 3;
    private static final int INITIAL_BASEMENT_POSITION_Y = 1;

    private boolean notLoaded;

    private BasementInfo basementInfo;
    private Coordinate linkPosition;

    public BasementLoadingScreen(Game game, BasementInfo basementInfo) {
        super(game);
        Logger.info("Entering BasementLoadingScreen.");
        notLoaded = true;
        this.basementInfo = basementInfo;
        float positionX = LocationUtil.getXFromGrid(INITIAL_BASEMENT_POSITION_X);
        float positionY = LocationUtil.getYFromGrid(INITIAL_BASEMENT_POSITION_Y);
        linkPosition = new Coordinate(positionX, positionY);

    }

    @Override
    public void update(float deltaTime) {
        if (notLoaded) {
            notLoaded = false;
            ((MainActivity) game).getBasementEnemyManager().load(game);
            ((MainActivity) game).getBasementManager().load(game, basementInfo);
            ((MainActivity) game).getLinkManager().load(game, LocationUtil.ZONE_BASEMENT, linkPosition);
            ((MainActivity) game).getGuiManager().load(game, LocationUtil.ZONE_BASEMENT);
        }
        game.setScreen(new BasementScreen(game));
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
