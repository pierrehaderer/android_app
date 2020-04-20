package com.twoplayers.legend.screen;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Screen;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.character.enemy.dungeon.DungeonEnemyManager;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.dungeon.DungeonManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Logger;

public class DungeonScreen extends Screen {

    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private DungeonManager dungeonManager;
    private LinkManager linkManager;
    private DungeonEnemyManager dungeonEnemyManager;
    private GuiManager guiManager;
    private MusicManager musicManager;

    public DungeonScreen(Game game) {
        super(game);
        Logger.info("Entering DungeonScreen.");
        dungeonManager = ((MainActivity) game).getDungeonManager();
        dungeonEnemyManager = ((MainActivity) game).getDungeonEnemyManager();
        linkManager = ((MainActivity) game).getLinkManager();
        guiManager = ((MainActivity) game).getGuiManager();
        musicManager = ((MainActivity) game).getMusicManager();
    }

    @Override
    public void update(float deltaTime) {
        dungeonManager.update(deltaTime, game.getGraphics());
        dungeonEnemyManager.update(deltaTime, game.getGraphics());
        guiManager.update(deltaTime, game.getGraphics());
        linkManager.update(deltaTime, game.getGraphics());
        musicManager.update(deltaTime, game.getGraphics());
        if (dungeonManager.hasExitedZone()) {
            game.setScreen(new WorldMapLoadingScreen(game, dungeonManager.getDungeonLocation(), dungeonManager.getDungeonExit()));
        }

    }

    @Override
    public void paint(float deltaTime) {
        game.getGraphics().drawRect(0, 0, WIDTH_PHONE_SCREEN + 1, HEIGHT_PHONE_SCREEN + 1, Color.BLACK);
        dungeonManager.paint(deltaTime, game.getGraphics());
        dungeonEnemyManager.paint(deltaTime, game.getGraphics());
        linkManager.paint(deltaTime, game.getGraphics());
        dungeonManager.paintDoorCache(game.getGraphics());
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
