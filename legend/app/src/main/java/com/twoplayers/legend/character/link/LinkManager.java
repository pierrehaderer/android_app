package com.twoplayers.legend.character.link;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.assets.sound.AllSoundEffects;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.WorldMapEnemyManager;
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
    private WorldMapEnemyManager worldMapEnemyManager;

    private AllSoundEffects allSoundEffects;

    private Link link;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        worldMapManager = ((MainActivity) game).getWorldMapManager();
        worldMapEnemyManager = ((MainActivity) game).getWorldMapEnemyManager();

        ImagesLink imagesLink = ((MainActivity) game).getAllImages().getImagesLink();
        imagesLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        allSoundEffects = ((MainActivity) game).getAllSoundEffects();

        link = new Link(imagesLink, game.getGraphics());
        link.x = LocationUtil.getXFromGrid(8);
        link.y = LocationUtil.getYFromGrid(6);
        link.hitbox.relocate(link.x, link.y);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;

        link.life = 3;
        link.lifeMax = 3;

        //TODO Change it when it can be collected
        link.arrow = Arrow.WOOD;
        link.bomb = 4;
        link.boomerang = Boomerang.WOOD;
        link.bow = Bow.BOW;
        link.bracelet = Bracelet.BRACELET;
        link.compass = Compass.COMPASS;
        link.dungeonMap = DungeonMap.MAP;
        link.flute = Flute.FLUTE;
        link.infiniteKey = InfiniteKey.KEY;
        link.ladder = Ladder.LADDER;
        link.light = Light.BLUE;
        link.meat = Meat.MEAT;
        link.potion = Potion.NOTE;
        link.raft = Raft.RAFT;
        link.ring = Ring.RED;
        link.scepter = Scepter.SCEPTER;
        link.spellBook = SpellBook.BOOK;
        link.sword = Sword.WOOD;
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (!link.isAttacking && !link.isPushed) {
            // Movement of Link
            if (guiManager.isUpPressed()) {
                link.orientation = Orientation.UP;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = -1 * Link.LINK_SPEED * deltaTime;
                if (isUpValid(link.x, link.y + deltaY)) {
                    moveLinkY(deltaY);
                }
                if (isUpOutOfMap(link.y + deltaY)) {
                    worldMapManager.changeMapScreeen(Orientation.UP);
                }
            }
            if (guiManager.isDownPressed()) {
                link.orientation = Orientation.DOWN;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = Link.LINK_SPEED * deltaTime;
                if (isDownValid(link.x, link.y + deltaY)) {
                    moveLinkY(deltaY);
                }
                if (isDownOutOfMap(link.y + deltaY)) {
                    worldMapManager.changeMapScreeen(Orientation.DOWN);
                }
            }
            if (guiManager.isLeftPressed()) {
                link.orientation = Orientation.LEFT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = -1 * Link.LINK_SPEED * deltaTime;
                if (isLeftValid(link.x + deltaX, link.y)) {
                    moveLinkX(deltaX);
                }
                if (isLeftOutOfMap(link.x + deltaX)) {
                    worldMapManager.changeMapScreeen(Orientation.LEFT);
                }
            }
            if (guiManager.isRightPressed()) {
                link.orientation = Orientation.RIGHT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = Link.LINK_SPEED * deltaTime;
                if (isRightValid(link.x + deltaX, link.y)) {
                    moveLinkX(deltaX);
                }
                if (isRightOutOfMap(link.x + deltaX)) {
                    worldMapManager.changeMapScreeen(Orientation.RIGHT);
                }
            }
        }

        // Attack of link
        if (!link.isAttacking) {
            // Start of link's attack
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

        // Link is wounded
        if (link.isInvincible) {
            Logger.info("Link is invincible, remaining counter : " + link.invicibleCounter);
            link.invicibleCounter -= deltaTime;
            if (link.invicibleCounter < 0) {
                link.isInvincible = false;
            }
        }
        if (!link.isInvincible) {
            for (Enemy enemy : worldMapEnemyManager.getEnemies()) {
                if (enemy.isContactLethal() && LocationUtil.areColliding(link.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has collided with enemy : " + enemy.getClass());
                    Logger.info("Hitbox link : " + link.hitbox.x + "," + link.hitbox.y + "," + link.hitbox.width + "," + link.hitbox.height);
                    Logger.info("Hitbox enemy : " + enemy.getHitbox().x + "," + enemy.getHitbox().y + "," + enemy.getHitbox().width + "," + enemy.getHitbox().height);
                    updateLinkLife(enemy.getContactDamage());
                    link.isInvincible = true;
                    link.invicibleCounter = Link.INITIAL_INVINCIBLE_COUNT;
                    link.isPushed = true;
                    link.pushCounter = Link.INITIAL_PUSH_COUNT;
                    Float[] pushDirections = LocationUtil.computePushDirections(enemy.getHitbox(), link.hitbox);
                    link.pushX = pushDirections[0];
                    link.pushY = pushDirections[1];
                    Logger.info("Link push direction : " + link.pushX + ", " + link.pushY);
                    break;
                }
            }
        }
        if (link.isPushed) {
            Logger.info("Link is pushed, remaining counter : " + link.pushCounter);
            if (link.orientation == Orientation.UP || link.orientation == Orientation.DOWN) {
                float deltaY = Link.PUSH_SPEED * link.pushY * deltaTime;
                if ((deltaY > 0 && isDownValid(link.x, link.y + deltaY))
                        || (deltaY < 0 && isUpValid(link.x, link.y + deltaY))) {
                    moveLinkY(deltaY);
                }
            }
            if (link.orientation == Orientation.LEFT || link.orientation == Orientation.RIGHT) {
                float deltaX = Link.PUSH_SPEED * link.pushX * deltaTime;
                if ((deltaX > 0 && isRightValid(link.x + deltaX, link.y))
                        || (deltaX < 0 && isLeftValid(link.x + deltaX, link.y))) {
                    moveLinkX(deltaX);
                }
            }
            link.pushCounter -= deltaTime;
            if (link.pushCounter < 0) {
                link.isPushed = false;
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
        //g.drawRect((int) link.hitbox.x, (int) link.hitbox.y, (int) link.hitbox.width, (int) link.hitbox.height, Color.GREEN);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkX(float deltaX) {
        float nextX = link.x + deltaX;
        if (isLeftOutOfMap(nextX)) {
            link.x = WorldMapManager.LEFT_MAP;
        } else if (isRightOutOfMap(nextX)) {
            link.x = WorldMapManager.LEFT_MAP + WorldMapManager.WIDTH_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.x = nextX;
        }
        link.hitbox.x = link.x + link.hitbox.x_offset;
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkY(float deltaY) {
        float nextY = link.y + deltaY;
        if (isUpOutOfMap(nextY)) {
            link.y = WorldMapManager.TOP_MAP;
        } else if (isDownOutOfMap(nextY)) {
            link.y = WorldMapManager.TOP_MAP + WorldMapManager.HEIGHT_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.y = nextY;
        }
        link.hitbox.y = link.y + link.hitbox.y_offset;
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
     * Check if link can go x
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
     * Check if link is going to the next screen x
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

    /**
     * Increase Link life max by 1
     */
    public void increaseLinkLifeMax() {
        if (link.lifeMax < 16) {
            link.lifeMax++;
        }
    }

    /**
     * Increase Link life
     */
    public void updateLinkLife(float value) {
        link.life = Math.min(link.lifeMax, link.life + value);
    }
}
