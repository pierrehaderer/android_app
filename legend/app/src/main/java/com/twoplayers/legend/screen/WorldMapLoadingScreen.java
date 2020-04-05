package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class WorldMapLoadingScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private boolean notLoaded;

    private Location linkLocation;
    private Coordinate linkPosition;

    public WorldMapLoadingScreen(Game game, Location location, Coordinate position) {
        super(game);
        Logger.info("Entering WorldMapLoadingScreen.");
        notLoaded = true;
        linkLocation = location;
        linkPosition = position;
    }

    @Override
    public void update(float deltaTime) {
        if (notLoaded) {
            notLoaded = false;
            ((MainActivity) game).getWorldMapManager().load(game, linkLocation);
            ((MainActivity) game).getWorldMapEnemyManager().load(game);
            ((MainActivity) game).getLinkManager().load(game, LocationUtil.ZONE_WORLD_MAP, linkPosition);
            ((MainActivity) game).getGuiManager().load(game, LocationUtil.ZONE_WORLD_MAP);
        }
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
