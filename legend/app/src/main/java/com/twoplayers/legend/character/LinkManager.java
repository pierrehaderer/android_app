package com.twoplayers.legend.character;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.assets.sound.AllSoundEffects;
import com.twoplayers.legend.character.object.Arrow;
import com.twoplayers.legend.character.object.Boomerang;
import com.twoplayers.legend.character.object.Bow;
import com.twoplayers.legend.character.object.Bracelet;
import com.twoplayers.legend.character.object.Compass;
import com.twoplayers.legend.character.object.DungeonMap;
import com.twoplayers.legend.character.object.Flute;
import com.twoplayers.legend.character.object.InfiniteKey;
import com.twoplayers.legend.character.object.Ladder;
import com.twoplayers.legend.character.object.Light;
import com.twoplayers.legend.character.object.Meat;
import com.twoplayers.legend.character.object.Potion;
import com.twoplayers.legend.character.object.Raft;
import com.twoplayers.legend.character.object.Ring;
import com.twoplayers.legend.character.object.Scepter;
import com.twoplayers.legend.character.object.SpellBook;
import com.twoplayers.legend.character.object.Sword;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LinkManager implements IManager {

    private GuiManager guiManager;
    private WorldMapManager worldMapManager;

    private AllSoundEffects allSoundEffects;

    private Link link;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        worldMapManager = ((MainActivity) game).getWorldMapManager();

        ImagesLink imagesLink = ((MainActivity) game).getAllImages().getImagesLink();
        imagesLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        allSoundEffects = ((MainActivity) game).getAllSoundEffects();

        link = new Link(imagesLink, game.getGraphics());
        link.x = LocationUtil.getXFromGrid(8);
        link.y = LocationUtil.getYFromGrid(6);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;

        link.arrow = Arrow.WOOD;
        link.bomb = 4;
        link.boomerang = Boomerang.WOOD; //TODO Change it when it can be collected
        link.bow = Bow.BOW;
        link.bracelet = Bracelet.BRACELET;
        link.compass = Compass.COMPASS;
        link.dungeonMap = DungeonMap.MAP;
        link.flute = Flute.FLUTE;
        link.infiniteKey = InfiniteKey.KEY;
        link.ladder = Ladder.LADDER;
        link.light = Light.BLUE; //TODO Change it when it can be collected
        link.meat = Meat.MEAT;
        link.potion = Potion.NOTE;
        link.raft = Raft.RAFT;
        link.ring = Ring.RED;
        link.scepter = Scepter.SCEPTER;
        link.spellBook = SpellBook.BOOK;
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
                    worldMapManager.changeMapScreeen(Orientation.UP);
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
                    worldMapManager.changeMapScreeen(Orientation.DOWN);
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
                    worldMapManager.changeMapScreeen(Orientation.LEFT);
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
                    worldMapManager.changeMapScreeen(Orientation.RIGHT);
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
            link.x = WorldMapManager.LEFT_MAP;
        } else if (isRightOutOfMap(nextX)) {
            link.x = WorldMapManager.LEFT_MAP + WorldMapManager.WIDTH_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.x = nextX;
        }

        float nextY = link.y + deltaY;
        if (isUpOutOfMap(nextY)) {
            link.y = WorldMapManager.TOP_MAP;
        } else if (isDownOutOfMap(nextY)) {
            link.y = WorldMapManager.TOP_MAP + WorldMapManager.HEIGHT_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.y = nextY;
        }
    }

    /**
     * Check if link can go up
     */
    private boolean isUpValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return worldMapManager.isTileWalkable(linkLeft + 2, linkMiddle, true) && worldMapManager.isTileWalkable(linkRight - 2, linkMiddle, true);
    }

    /**
     * Check if link can go down
     */
    private boolean isDownValid(float linkLeft, float linkTop) {
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return worldMapManager.isTileWalkable(linkLeft + 2, linkBottom, true) && worldMapManager.isTileWalkable(linkRight - 2, linkBottom, true);
    }

    /**
     * Check if link can go left
     */
    private boolean isLeftValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return worldMapManager.isTileWalkable(linkLeft + 2, linkMiddle, true) && worldMapManager.isTileWalkable(linkLeft + 2, linkBottom, true);
    }

    /**
     * Check if link can go right
     */
    private boolean isRightValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return worldMapManager.isTileWalkable(linkRight - 2, linkMiddle, true) && worldMapManager.isTileWalkable(linkRight - 2, linkBottom, true);
    }

    /**
     * Check if link is going to the next screen up
     */
    private boolean isUpOutOfMap(float linkTop) {
        return linkTop < WorldMapManager.TOP_MAP;
    }

    /**
     * Check if link is going to the next screen down
     */
    private boolean isDownOutOfMap(float linkTop) {
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        return linkBottom > WorldMapManager.TOP_MAP + WorldMapManager.HEIGHT_MAP;
    }

    /**
     * Check if link is going to the next screen left
     */
    private boolean isLeftOutOfMap(float linkLeft) {
        return linkLeft < WorldMapManager.LEFT_MAP;
    }

    /**
     * Check if link is going to the next screen right
     */
    private boolean isRightOutOfMap(float linkLeft) {
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        return linkRight > WorldMapManager.LEFT_MAP + WorldMapManager.WIDTH_MAP;
    }

    /**
     * Provide link to access his objects
     */
    public Link getLink() {
        return link;
    }
}
