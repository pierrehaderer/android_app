package com.twoplayers.legend.util;

import com.kilobolt.framework.Input.TouchEvent;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.character.Hitbox;

public class LocationUtil {

    public static final int ZONE_WORLD_MAP = 0;
    public static final int ZONE_CAVE = 1;
    public static final int ZONE_DUNGEON = 2;

    public static final int LEFT_MAP = 150;
    public static final int TOP_MAP = 103;
    public static final int WIDTH_MAP = 548;
    public static final int HEIGHT_MAP = 377;

    public static final float TILE_SIZE = 16 * AllImages.COEF;
    public static final float HALF_TILE_SIZE = 8 * AllImages.COEF;
    public static final float QUARTER_TILE_SIZE = 4 * AllImages.COEF;
    public static final float OBSTACLE_TOLERANCE = 2f;

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

    /**
     * Compute the directions where pushingHitbox is pushing pushedHitbox
     */
    public static Float[] computePushDirections(Hitbox pushingHitbox, Hitbox pushedHitbox, Orientation orientation) {
        float deltaX = 0;
        float deltaY = 0;
        if (orientation == Orientation.UP || orientation == Orientation.DOWN || orientation == Orientation.ANY) {
            deltaY = (pushedHitbox.y + pushedHitbox.height / 2) - (pushingHitbox.y + pushingHitbox.height / 2);
            deltaY = (deltaY == 0) ? 1 : deltaY / Math.abs(deltaY);
        }
        if (orientation == Orientation.LEFT || orientation == Orientation.RIGHT) {
            deltaX = (pushedHitbox.x + pushedHitbox.width / 2) - (pushingHitbox.x + pushingHitbox.width / 2);
            deltaX = (deltaX == 0) ? 1 : deltaX / Math.abs(deltaX);
        }
        return new Float[] {deltaX, deltaY};
    }

    /**
     * Find the location of the next room
     */
    public static Location findNextRoomLocation(int x, int y, Orientation orientation) {
        int nextX = x;
        int nextY = y;
        switch (orientation) {
            case UP:
                nextY--;
                break;
            case DOWN:
                nextY++;
                break;
            case LEFT:
                nextX--;
                break;
            case RIGHT:
                nextX++;
                break;
        }
        return new Location(nextX, nextY);
    }

    /**
     * Check if a tile is on the border of the map
     */
    public static boolean isTileAtBorder(float x, float y) {
        int tileX = getTileXFromPositionX(x);
        if (tileX <= 0 || tileX >= 15) return true;
        int tileY = getTileYFromPositionY(y);
        if (tileY <= 0 || tileY >= 10) return true;
        return false;
    }

    /**
     * Check if a tile is on the border of the map
     */
    public static boolean isTileAtBorderMinusOne(float x, float y) {
        int tileX = getTileXFromPositionX(x);
        if (tileX <= 1 || tileX >= 14) return true;
        int tileY = getTileYFromPositionY(y);
        if (tileY <= 1 || tileY >= 9) return true;
        return false;
    }

    /**
     * Get the abscissa of the tile's left in position x
     */
    public static float getXFromGrid(int x) {
        return LEFT_MAP + x * TILE_SIZE;
    }

    /**
     * Get the ordinate of the tile's top in position y
     */
    public static float getYFromGrid(int y) {
        return TOP_MAP + y * TILE_SIZE;
    }

    /**
     * Get the tile's left position corresponding to abscissa x
     */
    public static int getTileXFromPositionX(float x) {
        return (int) ((x - LEFT_MAP) / TILE_SIZE);
    }

    /**
     * Get the tile's left position corresponding to abscissa x
     */
    public static int getTileYFromPositionY(float y) {
        return (int) ((y - TOP_MAP) / TILE_SIZE);
    }

    public static boolean isUpOutOfMap(float y) {
        return y <= LocationUtil.TOP_MAP;
    }

    public static boolean isDownOutOfMap(float y) {
        return y >= LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
    }

    public static boolean isLeftOutOfMap(float x) {
        return x <= LocationUtil.LEFT_MAP;
    }

    public static boolean isRightOutOfMap(float x) {
        return x >= LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
    }

    public static float getDeltaY(float y) {
        return y - getYFromGrid(getTileYFromPositionY(y));
    }

    public static float getDeltaX(float x) {
        return x - getXFromGrid(getTileXFromPositionX(x));
    }

}
