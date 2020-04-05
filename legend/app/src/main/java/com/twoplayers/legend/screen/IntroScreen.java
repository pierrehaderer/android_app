package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageOther;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input.TouchEvent;
import com.kilobolt.framework.Screen;

import java.util.List;

public class IntroScreen extends Screen {

    public static final int LEFT_SCREEN = 150;
    public static final int TOP_SCREEN = 0;
    public static final int WIDTH_SCREEN = 548;
    public static final int HEIGHT_SCREEN = 480;

    private static final int INITIAL_WORLD_MAP_LOCATION_X = 7;
    private static final int INITIAL_WORLD_MAP_LOCATION_Y = 7;

    private static final int INITIAL_WORLD_MAP_POSITION_X = 7;
    private static final int INITIAL_WORLD_MAP_POSITION_Y = 5;

    private ImageOther imageOther;
    private MusicManager musicManager;
    private SaveManager saveManager;

    public IntroScreen(Game game) {
        super(game);
        Logger.info("Entering IntroScreen.");
        imageOther = ((MainActivity) game).getAllImages().getImageOther();
        musicManager = ((MainActivity) game).getMusicManager();
        saveManager = ((MainActivity) game).getSaveManager();
        musicManager.plan(10, "intro", false);
    }

    @Override
    public void update(float deltaTime) {
        musicManager.update(deltaTime, game.getGraphics());
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (LocationUtil.inBounds(event, LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN)) {
                    Location location = new Location(INITIAL_WORLD_MAP_LOCATION_X, INITIAL_WORLD_MAP_LOCATION_Y);
                    float positionX = LocationUtil.getXFromGrid(INITIAL_WORLD_MAP_POSITION_X) + LocationUtil.HALF_TILE_SIZE;
                    float positionY = LocationUtil.getYFromGrid(INITIAL_WORLD_MAP_POSITION_Y);
                    Coordinate position = new Coordinate(positionX, positionY);
                    game.setScreen(new WorldMapLoadingScreen(game, location, position));
                    break;
                }
                if (LocationUtil.inBounds(event, 0, TOP_SCREEN, LEFT_SCREEN, HEIGHT_SCREEN)) {
                    saveManager.reset();
                    break;
                }
            }
        }
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN, Color.BLACK);
        g.drawScaledImage(imageOther.get("intro_screen"), LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN);
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
        musicManager.stop();
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
