package com.twoplayers.legend;

import android.content.res.AssetManager;
import android.view.View;

import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.basement.BasementManager;
import com.twoplayers.legend.cave.CaveManager;
import com.twoplayers.legend.character.enemy.dungeon.BasementEnemyManager;
import com.twoplayers.legend.character.enemy.worldmap.CaveEnemyManager;
import com.twoplayers.legend.character.enemy.dungeon.DungeonEnemyManager;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.enemy.worldmap.WorldMapEnemyManager;
import com.twoplayers.legend.dungeon.DungeonManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.screen.SplashLoadingScreen;
import com.kilobolt.framework.Screen;
import com.kilobolt.framework.implementation.AndroidGame;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.TextUtil;

public class MainActivity extends AndroidGame {

    private AssetManager assetManager;
    private AllImages allImages;
    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;
    private WorldMapManager worldMapManager;
    private CaveManager caveManager;
    private DungeonManager dungeonManager;
    private BasementManager basementManager;
    private WorldMapEnemyManager worldMapEnemyManager;
    private CaveEnemyManager caveEnemyManager;
    private DungeonEnemyManager dungeonEnemyManager;
    private BasementEnemyManager basementEnemyManager;
    private LinkManager linkManager;
    private GuiManager guiManager;
    private SaveManager saveManager;

    @Override
    public Screen getInitScreen() {
        // THIS IS WHERE IT ALL STARTS !!!!!
        assetManager = getAssets();
        allImages = new AllImages();
        musicManager = new MusicManager();
        soundEffectManager = new SoundEffectManager();
        worldMapManager = new WorldMapManager();
        caveManager = new CaveManager();
        dungeonManager = new DungeonManager();
        basementManager = new BasementManager();
        worldMapEnemyManager = new WorldMapEnemyManager();
        caveEnemyManager = new CaveEnemyManager();
        dungeonEnemyManager = new DungeonEnemyManager();
        basementEnemyManager = new BasementEnemyManager();
        linkManager = new LinkManager();
        guiManager = new GuiManager();
        saveManager = new SaveManager();
        TextUtil.initPaint(this);
        hideNavigationBar();
        allImages.getImageOther().loadSplashLoadingScreen(this.getGraphics());
        return new SplashLoadingScreen(this);
    }

    /**
     * Hide the navigation bar (back, home, ...)
     */
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        Logger.info("Listener triggered");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        getCurrentScreen().backButton();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Obtain the correct location manager
     */
    public IZoneManager getZoneManager(int location) {
        if (location == LocationUtil.ZONE_WORLD_MAP) {
            return worldMapManager;
        } else if (location == LocationUtil.ZONE_CAVE) {
            return caveManager;
        } else if (location == LocationUtil.ZONE_BASEMENT) {
            return basementManager;
        }
        return dungeonManager;
    }

    /**
     * Obtain the correct enemy manager
     */
    public IEnemyManager getEnemyManager(int location) {
        if (location == LocationUtil.ZONE_WORLD_MAP) {
            return worldMapEnemyManager;
        } else if (location == LocationUtil.ZONE_CAVE) {
            return caveEnemyManager;
        } else if (location == LocationUtil.ZONE_BASEMENT) {
            return basementEnemyManager;
        }
        return dungeonEnemyManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AllImages getAllImages() {
        return allImages;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public SoundEffectManager getSoundEffectManager() {
        return soundEffectManager;
    }

    public WorldMapManager getWorldMapManager() {
        return worldMapManager;
    }

    public CaveManager getCaveManager() {
        return caveManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public BasementManager getBasementManager() {
        return basementManager;
    }

    public WorldMapEnemyManager getWorldMapEnemyManager() {
        return worldMapEnemyManager;
    }

    public CaveEnemyManager getCaveEnemyManager() {
        return caveEnemyManager;
    }

    public DungeonEnemyManager getDungeonEnemyManager() {
        return dungeonEnemyManager;
    }

    public BasementEnemyManager getBasementEnemyManager() {
        return basementEnemyManager;
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }
}
