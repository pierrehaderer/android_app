package com.twoplayers.legend.assets.image;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class ImageLink {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load all images.
     */
    public void load(Graphics g) {
        Logger.info("Loading all link images.");
        images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
        images.put("link_up_1", g.newImage("link/link_up_1.png", ImageFormat.RGB565));
        images.put("link_up_2", g.newImage("link/link_up_2.png", ImageFormat.RGB565));
        images.put("link_down_1", g.newImage("link/link_down_1.png", ImageFormat.RGB565));
        images.put("link_down_2", g.newImage("link/link_down_2.png", ImageFormat.RGB565));
        images.put("link_left_1", g.newImage("link/link_left_1.png", ImageFormat.RGB565));
        images.put("link_left_2", g.newImage("link/link_left_2.png", ImageFormat.RGB565));
        images.put("link_right_1", g.newImage("link/link_right_1.png", ImageFormat.RGB565));
        images.put("link_right_2", g.newImage("link/link_right_2.png", ImageFormat.RGB565));
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