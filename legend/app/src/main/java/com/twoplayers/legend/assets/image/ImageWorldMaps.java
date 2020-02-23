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
        images.put("map_8_8", g.newImage("map/map_8_8.png", ImageFormat.RGB565));
        images.put("map_8_7", g.newImage("map/map_8_7.png", ImageFormat.RGB565));
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