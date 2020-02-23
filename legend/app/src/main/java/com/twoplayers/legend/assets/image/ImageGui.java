package com.twoplayers.legend.assets.image;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class ImageGui {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load all images and sounds.
     */
    public void load(Graphics g) {
        Logger.info("Loading all gui images.");
        images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
        images.put("button_arrows", g.newImage("gui/button_arrows.png", ImageFormat.RGB565));
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