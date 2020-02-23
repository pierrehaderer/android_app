package com.twoplayers.legend;

import android.content.res.AssetManager;
import android.view.View;

import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.sound.AllSounds;
import com.twoplayers.legend.character.LinkManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.MapManager;
import com.twoplayers.legend.screen.SplashLoadingScreen;
import com.kilobolt.framework.Screen;
import com.kilobolt.framework.implementation.AndroidGame;
import com.twoplayers.legend.util.Logger;

public class MainActivity extends AndroidGame {

    private AssetManager assetManager;
    private AllImages allImages;
    private AllSounds allSounds;
    private MapManager mapManager;
    private LinkManager linkManager;
    private GuiManager guiManager;

    @Override
    public Screen getInitScreen() {
        // THIS IS WHERE IT ALL STARTS !!!!!
        assetManager = getAssets();
        allImages = new AllImages();
        allSounds = new AllSounds();
        mapManager = new MapManager();
        linkManager = new LinkManager();
        guiManager = new GuiManager();
        hideNavigationBar();
        allImages.getImageOthers().loadSplashLoadingScreen(this.getGraphics());
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

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AllImages getAllImages() {
        return allImages;
    }

    public AllSounds getAllSounds() {
        return allSounds;
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
