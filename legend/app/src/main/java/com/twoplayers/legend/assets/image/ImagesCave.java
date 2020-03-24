package com.twoplayers.legend.assets.image;

import android.content.res.AssetManager;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImagesCave {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load all images.
     */
    public void load(AssetManager assetManager, Graphics g) {
        Logger.info("Loading cave images.");
        try {
            images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
            for (String fileName : assetManager.list("cave")) {
                if (fileName.endsWith(".png")) {
                    images.put(fileName.substring(0, fileName.length() - 4), g.newImage("cave/" + fileName, ImageFormat.RGB565));
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load cave images.");
        }
    }

    /**
     * Get an image from this asset.
     */
    public Image get(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        Logger.warn("Could not find image : " + name);
        return images.get("empty");
    }
}