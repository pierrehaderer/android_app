package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.inventory.Arrow;
import com.twoplayers.legend.character.link.inventory.Boomerang;
import com.twoplayers.legend.character.link.inventory.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.Scepter;
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LinkManager implements IManager {

    private boolean initNotDone = true;

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private LinkService linkService;

    private ImagesLink imagesLink;
    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;

    private Link link;
    private LinkInvincibleColorMatrix linkInvincibleColorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, int zone, Coordinate position) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        zoneManager = ((MainActivity) game).getZoneManager(zone);
        enemyManager = ((MainActivity) game).getEnemyManager(zone);

        link.x = LocationUtil.getXFromGrid((int) position.x);
        link.y = (link.isExitingSomewhere) ? LocationUtil.getYFromGrid((int) position.y + 1) - 3 : LocationUtil.getYFromGrid((int) position.y);
        link.hitbox.relocate(link.x, link.y);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = (link.isExitingSomewhere) ? Orientation.DOWN : Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;
        link.isInvincible = false;
        link.isEnteringSomewhere = false;
        link.enterSomewhereCounter = 0;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        musicManager = ((MainActivity) game).getMusicManager();
        linkService = new LinkService();

        imagesLink = ((MainActivity) game).getAllImages().getImagesLink();
        imagesLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();

        link = new Link(imagesLink, game.getGraphics());

        link.life = 3;
        link.lifeMax = 3;
        link.coins = 255;
        link.keys = 1;
        link.isExitingSomewhere = false;

        //TODO Change it when it can be collected
        link.arrow = Arrow.WOOD;
        link.bomb = 4;
        link.bombMax = 8;
        link.boomerang = Boomerang.WOOD;
        link.bow = Bow.BOW;
        link.bracelet = Bracelet.BRACELET;
        link.compass = Compass.COMPASS;
        link.dungeonMap = DungeonMap.MAP;
        link.flute = Flute.FLUTE;
        link.infiniteKey = InfiniteKey.KEY;
        link.ladder = Ladder.LADDER;
        link.light = Light.NONE;
        link.meat = Meat.MEAT;
        link.potion = Potion.NOTE;
        link.raft = Raft.RAFT;
        link.ring = Ring.RED;
        link.scepter = Scepter.SCEPTER;
        link.shield = Shield.SMALL;
        link.spellBook = SpellBook.BOOK;
        link.sword = new Sword(imagesLink, game.getGraphics());
        link.sword.type = SwordType.NONE;

        linkInvincibleColorMatrix = new LinkInvincibleColorMatrix();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (!link.isAttacking && !link.isPushed && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
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
                    zoneManager.changeScreen(Orientation.UP);
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering();
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
                    zoneManager.changeScreen(Orientation.DOWN);
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
                    zoneManager.changeScreen(Orientation.LEFT);
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
                    zoneManager.changeScreen(Orientation.RIGHT);
                }
            }
        }

        // Attack of link
        if (!link.isAttacking && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
            // Start of link's attack
            if (guiManager.isaPressed() && link.sword.type != SwordType.NONE
                    && !LocationUtil.isTileAtBorder(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE)) {
                link.currentAnimation = link.attackAnimations.get(link.orientation);
                link.currentAnimation.reset();
                link.sword.x = link.x;
                link.sword.y = link.y;
                link.sword.currentAnimation = link.sword.getAnimation(link.orientation);
                link.sword.currentAnimation.reset();
                link.sword.hitbox = link.sword.hitboxes.get(link.orientation);
                link.sword.hitbox.relocate(link.x, link.y);
                soundEffectManager.play("sword");
                link.isAttacking = true;
                link.attackProgression = 0;
            }
        }
        if (link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            link.sword.currentAnimation.update(deltaTime);
            link.attackProgression += deltaTime;
            if (link.attackProgression > Sword.STEP_1_DURATION && link.attackProgression < Sword.STEP_1_DURATION + Sword.STEP_2_DURATION) {
                // Sword hitbox is active
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (!enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.sword.hitbox, enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link sword.");
                        enemyManager.damageEnemy(enemy, link.sword.type.damage);
                    }
                }
            }
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
            linkInvincibleColorMatrix.update(deltaTime);
        } else {
            if (!link.isShowingItem) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (!enemy.isDead() && enemy.isContactLethal() && LocationUtil.areColliding(link.hitbox, enemy.getHitbox())) {
                        Logger.info("Link has collided with enemy : " + enemy.getClass());
                        soundEffectManager.play("link_wounded");
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
        }
        if (link.isPushed) {
            Logger.info("Link is pushed, remaining counter : " + link.pushCounter);
            if (link.orientation == Orientation.UP || link.orientation == Orientation.DOWN) {
                float deltaY = Link.PUSH_SPEED * link.pushY * deltaTime;
                if ((deltaY > 0 && isDownValid(link.x, link.y + deltaY))
                        || (deltaY < 0 && isUpValid(link.x, link.y + deltaY))) {
                    moveLinkY(deltaY);
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering();
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

        // Link is picking an item
        for (Item item : zoneManager.getItems()) {
            if (LocationUtil.areColliding(link.hitbox, item.hitbox) && link.coins - link.coinsToRemove >= item.price && !linkService.alreadyInInventory(link, item)) {
                link.isPushed = false;
                item.hideItemForTheZone();
                link.coinCounter = 0;
                link.coinsToRemove += item.price;
                link.itemToShow = item;
                link.isShowingItem = true;
                link.showItemCounter = Link.INITIAL_SHOW_COUNT;
                link.currentAnimation = link.pickAnimations[item.pickAnimation];
                soundEffectManager.play("collect_item");
                linkService.putItemInInventory(link, item);
            }
        }
        if (link.isShowingItem) {
            link.showItemCounter -= deltaTime;
            if (link.showItemCounter < 0) {
                link.isShowingItem = false;
                link.currentAnimation = link.moveAnimations.get(Orientation.DOWN);
            }
        }
        if (link.coinsToRemove > 0) {
            link.coinCounter += deltaTime * Link.REMOVE_COINS_SPEED;
            if (link.coinCounter > 1) {
                int floor = (int) Math.min(Math.floor(link.coinCounter), link.coinsToRemove);
                link.coins -= floor;
                link.coinsToRemove -= floor;
                link.coinCounter -= floor;
                soundEffectManager.play("coin_payment");
            }
            if (link.coinsToRemove == 0) {
                soundEffectManager.play("coin_payment_end");
            }
        }

        // Link is entering somewhere
        if (link.isEnteringSomewhere) {
            link.currentAnimation.update(deltaTime);
            link.enterSomewhereCounter -= deltaTime;
            moveLinkY(deltaTime * Link.ENTER_CAVE_SPEED);
        }

        // Link is exiting somewhere
        if (link.isExitingSomewhere) {
            link.currentAnimation.update(deltaTime);
            link.exitSomewhereCounter -= deltaTime;
            moveLinkY(-1 * deltaTime * Link.ENTER_CAVE_SPEED);
            if (link.exitSomewhereCounter < 0) {
                link.isExitingSomewhere = false;
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw link
        if (link.isShowingItem) {
            g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
            g.drawScaledImage(link.itemToShow.image, Math.round(link.x) - 8, Math.round(link.y - LocationUtil.TILE_SIZE) + 2, AllImages.COEF);
        } else if (link.isInvincible) {
            g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y), linkInvincibleColorMatrix.getCurrentColorMatrix());
        } else if (link.isEnteringSomewhere || link.isExitingSomewhere) {
            g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
            int belowCaveX = (int) LocationUtil.getXFromGrid(LocationUtil.getTileLeftFromX(link.x + LocationUtil.HALF_TILE_SIZE));
            int belowCaveY = (int) LocationUtil.getYFromGrid(LocationUtil.getTileTopFromY(link.y + LocationUtil.TILE_SIZE + 3)) + 1;
            g.drawScaledImage(imagesLink.get("empty_tile"), belowCaveX, belowCaveY, AllImages.COEF);
        } else {
            g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
        }

        // Draw the sword
        if (link.isAttacking) {
            g.drawAnimation(link.sword.currentAnimation, Math.round(link.sword.x), Math.round(link.sword.y));
        }

        // Draw the hitboxes
        g.drawRect((int) link.hitbox.x, (int) link.hitbox.y, (int) link.hitbox.width, (int) link.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.sword.hitbox.x, (int) link.sword.hitbox.y, (int) link.sword.hitbox.width, (int) link.sword.hitbox.height, Hitbox.COLOR);
    }

    /**
     * Check if entering a cave and set the variables to make link enter a cave
     */
    private void checkAndInitCaveEntering() {
        if (zoneManager.isTileACave(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.TILE_SIZE)) {
            Logger.info("Link is entering a cave.");
            musicManager.stop();
            soundEffectManager.play("cave");
            enemyManager.unloadEnemies();
            link.isEnteringSomewhere = true;
            link.enterSomewhereCounter = Link.INITIAL_ENTER_COUNT;
            link.isPushed = false;
            link.isAttacking = false;
            link.isInvincible = false;
            link.currentAnimation = link.moveAnimations.get(Orientation.UP);
        }
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkX(float deltaX) {
        float nextX = link.x + deltaX;
        if (isLeftOutOfMap(nextX)) {
            link.x = LocationUtil.LEFT_MAP;
        } else if (isRightOutOfMap(nextX)) {
            link.x = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.x = nextX;
        }
        link.hitbox.x = link.x + link.hitbox.x_offset;
        link.sword.x = link.x;
        link.sword.hitbox.x = link.x + link.sword.hitbox.x_offset;
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkY(float deltaY) {
        float nextY = link.y + deltaY;
        if (isUpOutOfMap(nextY)) {
            link.y = LocationUtil.TOP_MAP;
        } else if (isDownOutOfMap(nextY)) {
            link.y = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - LocationUtil.TILE_SIZE;
        } else {
            link.y = nextY;
        }
        link.hitbox.y = link.y + link.hitbox.y_offset;
        link.sword.y = link.y;
        link.sword.hitbox.y = link.y + link.sword.hitbox.y_offset;
    }

    /**
     * Check if link can go up
     */
    private boolean isUpValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return zoneManager.isTileWalkable(linkLeft + 2, linkMiddle, true) && zoneManager.isTileWalkable(linkRight - 2, linkMiddle, true);
    }

    /**
     * Check if link can go down
     */
    private boolean isDownValid(float linkLeft, float linkTop) {
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return zoneManager.isTileWalkable(linkLeft + 2, linkBottom, true) && zoneManager.isTileWalkable(linkRight - 2, linkBottom, true);
    }

    /**
     * Check if link can go x
     */
    private boolean isLeftValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return zoneManager.isTileWalkable(linkLeft + 2, linkMiddle, true) && zoneManager.isTileWalkable(linkLeft + 2, linkBottom, true);
    }

    /**
     * Check if link can go right
     */
    private boolean isRightValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // -2 so that link can enter narrow path
        return zoneManager.isTileWalkable(linkRight - 2, linkMiddle, true) && zoneManager.isTileWalkable(linkRight - 2, linkBottom, true);
    }

    /**
     * Check if link is going to the next screen up
     */
    private boolean isUpOutOfMap(float linkTop) {
        return linkTop < LocationUtil.TOP_MAP;
    }

    /**
     * Check if link is going to the next screen down
     */
    private boolean isDownOutOfMap(float linkTop) {
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        return linkBottom > LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
    }

    /**
     * Check if link is going to the next screen x
     */
    private boolean isLeftOutOfMap(float linkLeft) {
        return linkLeft < LocationUtil.LEFT_MAP;
    }

    /**
     * Check if link is going to the next screen right
     */
    private boolean isRightOutOfMap(float linkLeft) {
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        return linkRight > LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
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

    /**
     * Prepare link to re-enter the world map
     */
    public void exitZone() {
        link.isExitingSomewhere = true;
        link.exitSomewhereCounter = Link.INITIAL_ENTER_COUNT;
        soundEffectManager.play("cave");
    }

    /**
     * Check if link has finished entering somewhere
     */
    public boolean hasEnteredSomewhere() {
        return link.enterSomewhereCounter < 0;
    }
}
