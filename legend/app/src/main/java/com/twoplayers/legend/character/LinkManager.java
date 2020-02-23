package com.twoplayers.legend.character;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageLink;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.MapManager;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.util.LocationUtil;

public class LinkManager implements IManager {

    private GuiManager guiManager;
    private MapManager mapManager;

    private Link link;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        mapManager = ((MainActivity) game).getMapManager();

        ImageLink imageLink = ((MainActivity) game).getAllImages().getImageLink();
        imageLink.load(game.getGraphics());

        link = new Link(imageLink, game.getGraphics());
        link.x = LocationUtil.getXFromGrid(8);
        link.y = LocationUtil.getYFromGrid(6);
        link.orientation = Orientation.UP;
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (guiManager.isUpPressed()) {
            link.orientation = Orientation.UP;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
            float nextY = link.y - Link.LINK_SPEED * deltaTime;
            if (isUpValid(link.x, nextY)) {
                link.y = nextY;
            }
            if (isUpOutOfMap(nextY)) {
                mapManager.changeMapScreeen(Orientation.UP);
            }
        }
        if (guiManager.isDownPressed()) {
            link.orientation = Orientation.DOWN;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
            float nextY = link.y + Link.LINK_SPEED * deltaTime;
            if (isDownValid(link.x, nextY)) {
                link.y = nextY;
            }
            if (isDownOutOfMap(nextY)) {
                mapManager.changeMapScreeen(Orientation.DOWN);
            }
        }
        if (guiManager.isLeftPressed()) {
            link.orientation = Orientation.LEFT;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
            float nextX = link.x - Link.LINK_SPEED * deltaTime;
            if (isLeftValid(nextX, link.y)) {
                link.x = nextX;
            }
            if (isLeftOutOfMap(nextX)) {
                mapManager.changeMapScreeen(Orientation.LEFT);
            }
        }
        if (guiManager.isRightPressed()) {
            link.orientation = Orientation.RIGHT;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
            float nextX = link.x + Link.LINK_SPEED * deltaTime;
            if (isRightValid(nextX, link.y)) {
                link.x = nextX;
            }
            if (isRightOutOfMap(nextX)) {
                mapManager.changeMapScreeen(Orientation.RIGHT);
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        link.paint(deltaTime, g);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLink(float deltaX, float deltaY) {
        float nextX = link.x + deltaX;
        if (!isLeftOutOfMap(nextX) && !isRightOutOfMap(nextX)) {
            link.x = nextX;
        }
        float nextY = link.y + deltaY;
        if (!isUpOutOfMap(nextY) && !isDownOutOfMap(nextY)) {
            link.y = nextY;
        }
    }

    /**
     * Check if link can go up
     */
    private boolean isUpValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + 8 * AllImages.COEF;
        float linkRight = linkLeft + 16 * AllImages.COEF;
        // -2 so that link can enter narrow path
        return mapManager.isTileWalkable(linkLeft + 2, linkMiddle) && mapManager.isTileWalkable(linkRight - 2, linkMiddle);
    }

    /**
     * Check if link can go down
     */
    private boolean isDownValid(float linkLeft, float linkTop) {
        float linkBottom = linkTop + 16 * AllImages.COEF;
        float linkRight = linkLeft + 16 * AllImages.COEF;
        // -2 so that link can enter narrow path
        return mapManager.isTileWalkable(linkLeft + 2, linkBottom) && mapManager.isTileWalkable(linkRight - 2, linkBottom);
    }

    /**
     * Check if link can go left
     */
    private boolean isLeftValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + 8 * AllImages.COEF;
        float linkBottom = linkTop + 16 * AllImages.COEF;
        // -2 so that link can enter narrow path
        return mapManager.isTileWalkable(linkLeft + 2, linkMiddle) && mapManager.isTileWalkable(linkLeft + 2, linkBottom);
    }

    /**
     * Check if link can go right
     */
    private boolean isRightValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + 8 * AllImages.COEF;
        float linkBottom = linkTop + 16 * AllImages.COEF;
        float linkRight = linkLeft + 16 * AllImages.COEF;
        // -2 so that link can enter narrow path
        return mapManager.isTileWalkable(linkRight - 2, linkMiddle) && mapManager.isTileWalkable(linkRight - 2, linkBottom);
    }

    /**
     * Check if link is going to the next screen up
     */
    private boolean isUpOutOfMap(float linkTop) {
        return linkTop < MapManager.TOP_MAP;
    }

    /**
     * Check if link is going to the next screen down
     */
    private boolean isDownOutOfMap(float linkTop) {
        float linkBottom = linkTop + 16 * AllImages.COEF;
        return linkBottom > MapManager.TOP_MAP + MapManager.HEIGHT_MAP;
    }

    /**
     * Check if link is going to the next screen left
     */
    private boolean isLeftOutOfMap(float linkLeft) {
        return linkLeft < MapManager.LEFT_MAP;
    }

    /**
     * Check if link is going to the next screen right
     */
    private boolean isRightOutOfMap(float linkLeft) {
        float linkRight = linkLeft + 16 * AllImages.COEF;
        return linkRight > MapManager.LEFT_MAP + MapManager.WIDTH_MAP;
    }
}
