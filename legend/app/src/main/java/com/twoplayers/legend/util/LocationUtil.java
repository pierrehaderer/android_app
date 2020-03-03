package com.twoplayers.legend.util;

import com.kilobolt.framework.Input.TouchEvent;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.WorldMapManager;

public class LocationUtil {

    public static final float TILE_SIZE = 16 * AllImages.COEF;
    public static final float HALF_TILE_SIZE = 8 * AllImages.COEF;

    /**
     * Check if a touch event is in the provided zone
     */
    public static boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1);
    }

    /**
     * Check that 2 hitbox areColliding together
     */
    public static boolean areColliding(Hitbox hitbox1, Hitbox hitbox2) {
        boolean collide = false;
        if (hitbox1.x < hitbox2.x) {
            collide = (hitbox2.x - hitbox1.x < hitbox1.width);
        } else {
            collide = (hitbox1.x - hitbox2.x < hitbox2.width);
        }
        if (collide) {
            if (hitbox1.y < hitbox2.y) {
                collide = (hitbox2.y - hitbox1.y < hitbox1.height);
            } else {
                collide = (hitbox1.y - hitbox2.y < hitbox2.height);
            }
        }
        return collide;
    }

    /**
     * Compute the directions where pushingHitbox is pushing pushedHitbox
     */
    public static Float[] computePushDirections(Hitbox pushingHitbox, Hitbox pushedHitbox) {
        float deltaX = (pushedHitbox.x + pushedHitbox.width / 2) - (pushingHitbox.x + pushingHitbox.width / 2);
        float deltaY = (pushedHitbox.y + pushedHitbox.height / 2) - (pushingHitbox.y + pushingHitbox.height / 2);
        deltaX = (deltaX == 0) ? 1 : deltaX / Math.abs(deltaX);
        deltaY = (deltaY == 0) ? 1 : deltaY / Math.abs(deltaY);
        return new Float[] {deltaX, deltaY};
    }

    public static float getXFromGrid(int x) {
        return WorldMapManager.LEFT_MAP + (x - 1) * TILE_SIZE;
    }

    public static float getYFromGrid(int y) {
        return WorldMapManager.TOP_MAP + (y - 1) * TILE_SIZE;
    }
}
