package com.twoplayers.legend.assets.image;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class ImageWorldMaps {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load all images.
     */
    public void load(Graphics g) {
        Logger.info("Loading world map images.");
        images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
        addMapImage(g, "1_1");
        addMapImage(g, "2_1");
        addMapImage(g, "3_1");
        addMapImage(g, "4_1");
        addMapImage(g, "5_1");
        addMapImage(g, "6_1");
        addMapImage(g, "7_1");
        addMapImage(g, "8_7");
        addMapImage(g, "7_8");
        addMapImage(g, "8_8");
    }

    /**
     * Helper to add an image from asset folder "map"
     */
    private void addMapImage(Graphics g, String name) {
        images.put(name, g.newImage("map/" + name + ".png", ImageFormat.RGB565));
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
}