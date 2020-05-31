package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.basement.BasementManager;
import com.twoplayers.legend.character.enemy.dungeon.BasementEnemyManager;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Logger;

public class BasementScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private BasementManager basementManager;
    private BasementEnemyManager basementEnemyManager;
    private LinkManager linkManager;
    private GuiManager guiManager;

    public BasementScreen(Game game) {
        super(game);
        Logger.info("Entering BasementScreen.");
        basementManager = ((MainActivity) game).getBasementManager();
        basementEnemyManager = ((MainActivity) game).getBasementEnemyManager();
        linkManager = ((MainActivity) game).getLinkManager();
        guiManager = ((MainActivity) game).getGuiManager();
    }

    @Override
    public void update(float deltaTime) {
        basementManager.update(deltaTime, game.getGraphics());
        basementEnemyManager.update(deltaTime, game.getGraphics());
        linkManager.update(deltaTime, game.getGraphics());
        guiManager.update(deltaTime, game.getGraphics());
        if (basementManager.hasExitedZone()) {
            game.setScreen(new DungeonLoadingScreen(game, basementManager.getDungeonInfo()));
        }
    }

    @Override
    public void paint(float deltaTime) {
        game.getGraphics().drawRect(0, 0, WIDTH_PHONE_SCREEN + 1, HEIGHT_PHONE_SCREEN + 1, Color.BLACK);
        basementManager.paint(deltaTime, game.getGraphics());
        basementEnemyManager.paint(deltaTime, game.getGraphics());
        linkManager.paint(deltaTime, game.getGraphics());
        basementManager.paintCache(game.getGraphics());
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
