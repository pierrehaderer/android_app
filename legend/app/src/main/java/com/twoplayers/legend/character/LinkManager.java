package com.twoplayers.legend.character;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageLink;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.MapManager;
import com.twoplayers.legend.util.LocationUtil;

public class LinkManager implements IManager {

    private GuiManager guiManager;
    private MapManager mapManager;

    private Link link;

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
            if (isUpValid(link.x, link.y - link.speed * deltaTime)) {
                link.y -= link.speed * deltaTime;
            }
            link.orientation = Orientation.UP;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
        }
        if (guiManager.isDownPressed()) {
            if (isDownValid(link.x, link.y + link.speed * deltaTime)) {
                link.y += link.speed * deltaTime;
            }
            link.orientation = Orientation.DOWN;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
        }
        if (guiManager.isLeftPressed()) {
            if (isLeftValid(link.x - link.speed * deltaTime, link.y)) {
                link.x -= link.speed * deltaTime;
            }
            link.orientation = Orientation.LEFT;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
        }
        if (guiManager.isRightPressed()) {
            if (isRightValid(link.x + link.speed * deltaTime, link.y)) {
                link.x += link.speed * deltaTime;
            }
            link.orientation = Orientation.RIGHT;
            link.getMoveAnimations().get(link.orientation).update(deltaTime);
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        link.paint(deltaTime, g);
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
}
