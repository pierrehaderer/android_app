package com.twoplayers.legend.assets.image;

import android.content.res.AssetManager;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImagesDungeon implements IImages {

    private Map<String, Image> images = new HashMap<>();

    /**
     * Load all images.
     */
    public void load(AssetManager assetManager, Graphics g) {
        Logger.info("Loading dungeon images.");
        try {
            images.put("empty", g.newImage("other/empty.png", ImageFormat.RGB565));
            for (String fileName : assetManager.list("dungeon")) {
                if (fileName.endsWith(".png")) {
                    images.put(fileName.substring(0, fileName.length() - 4), g.newImage("dungeon/" + fileName, ImageFormat.RGB565));
                }
            }
            String[] subfolders = new String[] {"", "/dungeon1", "/dungeon2", "/dungeon3", "/dungeon4", "/dungeon5", "/dungeon6", "/dungeon7", "/dungeon8", "/dungeon9"};
            for (String subfolder : subfolders) {
                for (String fileName : assetManager.list("dungeon" + subfolder)) {
                    if (fileName.endsWith(".png")) {
                        images.put(fileName.substring(0, fileName.length() - 4), g.newImage("dungeon" + subfolder + "/" + fileName, ImageFormat.RGB565));
                    }
                }
            }
        } catch (IOException exception) {
            Logger.error("Could not load dungeon images.");
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