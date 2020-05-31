package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.map.CaveInfo;
import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class DungeonLoadingScreen extends Screen {

    private static final int WIDTH_PHONE_SCREEN = 800;
    private static final int HEIGHT_PHONE_SCREEN = 480;

    private boolean notLoaded;

    private DungeonInfo dungeonInfo;
    private Coordinate linkPosition;

    public DungeonLoadingScreen(Game game, DungeonInfo dungeonInfo) {
        super(game);
        Logger.info("Entering DungeonLoadingScreen.");
        notLoaded = true;
        this.dungeonInfo = dungeonInfo;
        linkPosition = dungeonInfo.linkStartCoordinateInTheDungeon;

    }

    @Override
    public void update(float deltaTime) {
        if (notLoaded) {
            notLoaded = false;
            ((MainActivity) game).getDungeonManager().load(game, dungeonInfo);
            ((MainActivity) game).getDungeonEnemyManager().load(game);
            ((MainActivity) game).getLinkManager().load(game, LocationUtil.ZONE_DUNGEON, linkPosition);
            ((MainActivity) game).getGuiManager().load(game, LocationUtil.ZONE_DUNGEON);
        }
        game.setScreen(new DungeonScreen(game));
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
