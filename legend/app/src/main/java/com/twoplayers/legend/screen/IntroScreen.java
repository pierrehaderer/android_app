package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageOther;
import com.twoplayers.legend.util.Coordinate;
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

    public IntroScreen(Game game) {
        super(game);
        Logger.info("Entering IntroScreen.");
        imageOther = ((MainActivity) game).getAllImages().getImageOther();
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (LocationUtil.inBounds(event, LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN)) {
                    Coordinate location = new Coordinate(INITIAL_WORLD_MAP_LOCATION_X, INITIAL_WORLD_MAP_LOCATION_Y);
                    Coordinate position = new Coordinate(INITIAL_WORLD_MAP_POSITION_X, INITIAL_WORLD_MAP_POSITION_Y);
                    game.setScreen(new WorldMapLoadingScreen(game, location, position));
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
        android.os.Process.killProcess(android.os.Process.myPid());
        //imageOther.theme.stop();
    }
}
