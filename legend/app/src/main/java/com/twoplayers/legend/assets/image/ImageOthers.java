package com.twoplayers.legend.assets.image;

import android.content.res.AssetManager;

import com.twoplayers.legend.util.Logger;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageOthers {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load splash_screen loading screen.
     */
    public void loadSplashLoadingScreen(Graphics g) {
        images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
        images.put("splash_screen", g.newImage("other/intro_screen.png", ImageFormat.RGB565));
    }

    /**
     * Load all images.
     */
    public void load(AssetManager assetManager, Graphics g) {
        Logger.info("Loading all other screens images.");
        try {
            for (String fileName : assetManager.list("other")) {
                if (fileName.endsWith(".png")) {
                    images.put(fileName.substring(0, fileName.length() - 4), g.newImage("other/" + fileName, ImageFormat.RGB565));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load other images.");
        }
    }

    /**
     * Get an image from this asset.
     */
    public Image get(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        return images.get("empty");
    }

//    /**
//     * Load main theme once
//     */
//    public static void loadTheme(Audio a) {
//        if (firstTimeThemeLoaded) {
//            theme = a.createMusic("sound/memory/main_theme.mp3");
//            theme.setLooping(true);
//            theme.setVolume(0.005f);
//            firstTimeThemeLoaded = false;
//        }
//    }
}