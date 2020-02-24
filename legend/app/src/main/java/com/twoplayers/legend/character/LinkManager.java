package com.twoplayers.legend.character;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageLink;
import com.twoplayers.legend.assets.sound.AllSoundEffects;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.MapManager;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.util.LocationUtil;

public class LinkManager implements IManager {

    private GuiManager guiManager;
    private MapManager mapManager;

    private AllSoundEffects allSoundEffects;

    private Link link;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        mapManager = ((MainActivity) game).getMapManager();

        ImageLink imageLink = ((MainActivity) game).getAllImages().getImageLink();
        imageLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        allSoundEffects = ((MainActivity) game).getAllSoundEffects();

        link = new Link(imageLink, game.getGraphics());
        link.x = LocationUtil.getXFromGrid(8);
        link.y = LocationUtil.getYFromGrid(6);
        link.orientation = Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;
        link.sword = Sword.WOOD; //TODO Change it when it can be collected
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (!link.isAttacking) {
            if (guiManager.isUpPressed()) {
                link.orientation = Orientation.UP;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
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
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
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
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
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
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float nextX = link.x + Link.LINK_SPEED * deltaTime;
                if (isRightValid(nextX, link.y)) {
                    link.x = nextX;
                }
                if (isRightOutOfMap(nextX)) {
                    mapManager.changeMapScreeen(Orientation.RIGHT);
                }
            }
            if (guiManager.isaPressed() && link.sword != Sword.NONE) {
                link.isAttacking = true;
                link.currentAnimation = link.attackAnimations.get(link.sword).get(link.orientation);
                link.currentAnimation.reset();
                allSoundEffects.get("sword").play(0.75f);

            }
        }
        if (link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            if (link.currentAnimation.isAnimationOver()) {
                link.isAttacking = false;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLink(float deltaX, float deltaY) {
        float nextX = link.x + deltaX;
        if (isLeftOutOfMap(nextX)) {
            link.x = MapManager.LEFT_MAP;
        } else if (isRightOutOfMap(nextX)) {
            link.x = MapManager.LEFT_MAP + MapManager.WIDTH_MAP - 16 * AllImages.COEF;
        } else {
            link.x = nextX;
        }

        float nextY = link.y + deltaY;
        if (isUpOutOfMap(nextY)) {
            link.y = MapManager.TOP_MAP;
        } else if (isDownOutOfMap(nextY)) {
            link.y = MapManager.TOP_MAP + MapManager.HEIGHT_MAP - 16 * AllImages.COEF;
        } else {
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

    /**
     * Provide the sword of link
     */
    public Sword getLinkSword() {
        return link.sword;
    }
}
