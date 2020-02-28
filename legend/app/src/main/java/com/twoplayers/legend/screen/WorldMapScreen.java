package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.character.LinkManager;
import com.twoplayers.legend.character.enemy.WorldMapEnemyManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.Logger;
import com.kilobolt.framework.Game;
import com.kilobolt.framework.Screen;

public class WorldMapScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private WorldMapManager worldMapManager;
    private LinkManager linkManager;
    private WorldMapEnemyManager worldMapEnemyManager;
    private GuiManager guiManager;

    public WorldMapScreen(Game game) {
        super(game);
        Logger.info("Entering WorldMapScreen.");
        worldMapManager = ((MainActivity) game).getWorldMapManager();
        worldMapEnemyManager = ((MainActivity) game).getWorldMapEnemyManager();
        linkManager = ((MainActivity) game).getLinkManager();
        guiManager = ((MainActivity) game).getGuiManager();
    }

    @Override
    public void update(float deltaTime) {
        worldMapManager.update(deltaTime, game.getGraphics());
        worldMapEnemyManager.update(deltaTime, game.getGraphics());
        linkManager.update(deltaTime, game.getGraphics());
        guiManager.update(deltaTime, game.getGraphics());
    }

    @Override
    public void paint(float deltaTime) {
        game.getGraphics().drawRect(0, 0, WIDTH_PHONE_SCREEN + 1, HEIGHT_PHONE_SCREEN + 1, Color.BLACK);
        worldMapManager.paint(deltaTime, game.getGraphics());
        linkManager.paint(deltaTime, game.getGraphics());
        worldMapEnemyManager.paint(deltaTime, game.getGraphics());
        guiManager.paint(deltaTime, game.getGraphics());
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
        //OtherScreenAssets.theme.stop();
    }
}
