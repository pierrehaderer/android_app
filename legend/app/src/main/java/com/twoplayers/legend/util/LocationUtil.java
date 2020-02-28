package com.twoplayers.legend.util;

import com.kilobolt.framework.Input.TouchEvent;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.map.WorldMapManager;

public class LocationUtil {

    public static final float TILE_SIZE = 16 * AllImages.COEF;

    public static boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1);
    }

    public static float getXFromGrid(int x) {
        return WorldMapManager.LEFT_MAP + (x - 1) * TILE_SIZE;
    }

    public static float getYFromGrid(int y) {
        return WorldMapManager.TOP_MAP + (y - 1) * TILE_SIZE;
    }
}
