package com.twoplayers.legend.util;

import com.kilobolt.framework.Input.TouchEvent;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.map.MapManager;

public class LocationUtil {

    public static final int TILE_SIZE = 16;

    public static boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1);
    }

    public static float getXFromGrid(int x) {
        return MapManager.LEFT_MAP + (x - 1) * TILE_SIZE * AllImages.COEF;
    }

    public static float getYFromGrid(int y) {
        return MapManager.TOP_MAP + (y - 1) * TILE_SIZE * AllImages.COEF;
    }
}
