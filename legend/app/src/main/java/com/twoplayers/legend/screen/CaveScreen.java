package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.cave.CaveManager;
import com.twoplayers.legend.character.enemy.cave.CaveEnemyManager;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Logger;

public class CaveScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private CaveManager caveManager;
    private CaveEnemyManager caveEnemyManager;
    private LinkManager linkManager;
    private GuiManager guiManager;

    public CaveScreen(Game game) {
        super(game);
        Logger.info("Entering CaveRoom.");
        caveManager = ((MainActivity) game).getCaveManager();
        caveEnemyManager = ((MainActivity) game).getCaveEnemyManager();
        linkManager = ((MainActivity) game).getLinkManager();
        guiManager = ((MainActivity) game).getGuiManager();
    }

    @Override
    public void update(float deltaTime) {
        caveManager.update(deltaTime, game.getGraphics());
        caveEnemyManager.update(deltaTime, game.getGraphics());
        linkManager.update(deltaTime, game.getGraphics());
        guiManager.update(deltaTime, game.getGraphics());
        if (caveManager.hasExitedZone()) {
            game.setScreen(new WorldMapLoadingScreen(game, caveManager.getCaveLocation(), caveManager.getCaveEntrance()));
        }
    }

    @Override
    public void paint(float deltaTime) {
        game.getGraphics().drawRect(0, 0, WIDTH_PHONE_SCREEN + 1, HEIGHT_PHONE_SCREEN + 1, Color.BLACK);
        caveManager.paint(deltaTime, game.getGraphics());
        linkManager.paint(deltaTime, game.getGraphics());
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
