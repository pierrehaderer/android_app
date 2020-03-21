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
import com.twoplayers.legend.character.link.inventory.BoomerangType;
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

    private static final float LINK_MAX_SHIFT = 5f;
    public static final float A_TINY_BIT_MORE = 0.01f;

    private boolean initNotDone = true;

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private ItemService itemService;

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
        itemService = new ItemService();

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
        link.boomerang = new Boomerang(imagesLink, game.getGraphics());
        link.boomerang.type = BoomerangType.NONE;
        link.bomb = 4;
        link.bombMax = 8;
        link.boomerang.isMovingForward = false;
        link.boomerang.isMovingBackward = false;
        link.boomerang.counter = 0;
        link.bow = Bow.BOW;
        link.arrow = Arrow.WOOD;
        link.light = Light.NONE;
        link.flute = Flute.FLUTE;
        link.meat = Meat.MEAT;
        link.potion = Potion.NOTE;
        link.scepter = Scepter.SCEPTER;

        link.bracelet = Bracelet.BRACELET;
        link.raft = Raft.RAFT;
        link.ladder = Ladder.LADDER;
        link.spellBook = SpellBook.BOOK;
        link.ring = Ring.RED;
        link.infiniteKey = InfiniteKey.KEY;

        link.compass = Compass.COMPASS;
        link.dungeonMap = DungeonMap.MAP;

        link.sword = new Sword(imagesLink, game.getGraphics());
        link.sword.type = SwordType.NONE;
        link.shield = Shield.SMALL;
        link.secondItem = (link.boomerang.type == BoomerangType.NONE) ? 0 : 1;
        link.isUsingSecondItem = false;


        linkInvincibleColorMatrix = new LinkInvincibleColorMatrix();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (!link.isAttacking && !link.isUsingSecondItem && !link.isPushed && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
            // Movement of Link
            boolean linkHasNotMovedYet = true;
            if (guiManager.isUpPressed() && guiManager.areButtonsActivated()) {
                link.orientation = Orientation.UP;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = -1 * Link.LINK_SPEED * deltaTime;
                float nextX = evaluateXAfterShift(link.x);
                if (zoneManager.isUpValid(nextX, link.y + deltaY)) {
                    linkHasNotMovedYet = false;
                    shiftLinkX(nextX);
                    moveLinkY(deltaY);
                }
                if (LocationUtil.isUpOutOfMap(link.y + deltaY)) {
                    itemService.hideItemsAndEffects(link);
                    zoneManager.changeRoom(Orientation.UP);
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering();
            }
            if (guiManager.isDownPressed() && guiManager.areButtonsActivated()) {
                link.orientation = Orientation.DOWN;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = Link.LINK_SPEED * deltaTime;
                float nextX = evaluateXAfterShift(link.x);
                if (zoneManager.isDownValid(nextX, link.y + deltaY)) {
                    linkHasNotMovedYet = false;
                    shiftLinkX(nextX);
                    moveLinkY(deltaY);
                }
                if (LocationUtil.isDownOutOfMap(link.y + LocationUtil.TILE_SIZE + deltaY)) {
                    itemService.hideItemsAndEffects(link);
                    zoneManager.changeRoom(Orientation.DOWN);
                }
            }
            if (guiManager.isLeftPressed() && guiManager.areButtonsActivated() && linkHasNotMovedYet) {
                link.orientation = Orientation.LEFT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = -1 * Link.LINK_SPEED * deltaTime;
                float nextY = evaluateYAfterShift(link.y);
                if (zoneManager.isLeftValid(link.x + deltaX, nextY)) {
                    shiftLinkY(nextY);
                    moveLinkX(deltaX);
                }
                if (LocationUtil.isLeftOutOfMap(link.x + deltaX)) {
                    itemService.hideItemsAndEffects(link);
                    zoneManager.changeRoom(Orientation.LEFT);
                }
            }
            if (guiManager.isRightPressed() && guiManager.areButtonsActivated() && linkHasNotMovedYet) {
                link.orientation = Orientation.RIGHT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = Link.LINK_SPEED * deltaTime;
                float nextY = evaluateYAfterShift(link.y);
                if (zoneManager.isRightValid(link.x + deltaX, nextY)) {
                    shiftLinkY(nextY);
                    moveLinkX(deltaX);
                }
                if (LocationUtil.isRightOutOfMap(link.x + LocationUtil.TILE_SIZE + deltaX)) {
                    itemService.hideItemsAndEffects(link);
                    zoneManager.changeRoom(Orientation.RIGHT);
                }
            }
        }

        // Attack of link
        if (!link.isAttacking && !link.isUsingSecondItem && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
            // Start of link's attack
            if (guiManager.isaPressed() && guiManager.areButtonsActivated() && link.sword.type != SwordType.NONE
                    && !LocationUtil.isTileAtBorder(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE)) {
                link.currentAnimation = link.attackAnimations.get(link.orientation);
                link.currentAnimation.reset();
                link.sword.x = link.x;
                link.sword.y = link.y;
                link.sword.getAnimation(link.orientation).reset();
                link.sword.hitbox = link.sword.hitboxes.get(link.orientation);
                link.sword.hitbox.relocate(link.x, link.y);
                soundEffectManager.play("sword");
                link.isAttacking = true;
                link.attackProgression = 0;
            }
        }
        if (link.isAttacking) {
            link.sword.getAnimation(link.orientation).update(deltaTime);
            link.attackProgression += deltaTime;
            if (link.attackProgression > Link.STEP_1_DURATION && link.attackProgression < Link.STEP_1_DURATION + Link.STEP_2_ATTACK_DURATION) {
                // Sword hitbox is active
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (!enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.sword.hitbox, enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link sword.");
                        if (LocationUtil.areColliding(link.hitbox, enemy.getHitbox())) {
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
                        }
                        enemyManager.damageEnemy(enemy, link.sword.type.damage);
                    }
                }
            }
        }

        // Link is using the second object
        if (!link.isAttacking && !link.isUsingSecondItem && guiManager.areButtonsActivated()
                && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem
                && !LocationUtil.isTileAtBorder(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE)) {
            if (guiManager.isbPressed()) {
                switch (link.secondItem) {
                    case 1:
                        itemService.initiateBoomerang(guiManager, link);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                }
            }
        }
        itemService.handleBoomerang(soundEffectManager, enemyManager, link, deltaTime);
        if (link.isUsingSecondItem || link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            if (link.currentAnimation.isAnimationOver()) {
                link.isUsingSecondItem = false;
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
                        enemyManager.hasHitLink(enemy);
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
                float nextX = evaluateXAfterShift(link.x);
                if (deltaY < 0) {
                    if (zoneManager.isUpValid(nextX, link.y + deltaY)) {
                        shiftLinkX(nextX);
                        moveLinkY(deltaY);
                    }
                }
                if (deltaY > 0) {
                    if (zoneManager.isDownValid(nextX, link.y + deltaY)) {
                        shiftLinkX(nextX);
                        moveLinkY(deltaY);
                    }
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering();
            }
            if (link.orientation == Orientation.LEFT || link.orientation == Orientation.RIGHT) {
                float deltaX = Link.PUSH_SPEED * link.pushX * deltaTime;
                float nextY = evaluateYAfterShift(link.y);
                if (deltaX < 0) {
                    if (zoneManager.isLeftValid(link.x + deltaX, nextY)) {
                        shiftLinkY(nextY);
                        moveLinkX(deltaX);
                    }
                }
                if (deltaX > 0) {
                    if (zoneManager.isRightValid(link.x + deltaX, nextY)) {
                        shiftLinkY(nextY);
                        moveLinkX(deltaX);
                    }
                }
            }
            link.pushCounter -= deltaTime;
            if (link.pushCounter < 0) {
                link.isPushed = false;
            }
        }

        // Link is picking an item
        for (Item item : zoneManager.getItems()) {
            if (LocationUtil.areColliding(link.hitbox, item.hitbox) && link.coins - link.coinsToRemove >= item.price && !itemService.alreadyInInventory(link, item)) {
                link.isPushed = false;
                item.hideItemForTheZone();
                link.coinCounter = 0;
                link.coinsToRemove += item.price;
                link.itemToShow = item;
                link.isShowingItem = true;
                link.showItemCounter = Link.INITIAL_SHOW_COUNT;
                link.currentAnimation = link.pickAnimations[item.pickAnimation];
                soundEffectManager.play("collect_item");
                itemService.putItemInInventory(link, item);
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
            itemService.hideItemsAndEffects(link);
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
            int belowCaveX = (int) LocationUtil.getXFromGrid(LocationUtil.getTileXFromPositionX(link.x + LocationUtil.HALF_TILE_SIZE));
            int belowCaveY = (int) LocationUtil.getYFromGrid(LocationUtil.getTileYFromPositionY(link.y + LocationUtil.TILE_SIZE + 3)) + 1;
            g.drawScaledImage(imagesLink.get("empty_tile"), belowCaveX, belowCaveY, AllImages.COEF);
        } else {
            g.drawAnimation(link.currentAnimation, Math.round(link.x), Math.round(link.y));
        }

        // Draw the sword
        if (link.isAttacking) {
            g.drawAnimation(link.sword.getAnimation(link.orientation), Math.round(link.sword.x), Math.round(link.sword.y));
        }

        // Draw the boomerang
        if (link.boomerang.isMovingForward || link.boomerang.isMovingBackward) {
            g.drawAnimation(link.boomerang.getAnimation(), Math.round(link.boomerang.x), Math.round(link.boomerang.y));
        }

        // Draw the hitboxes
        g.drawRect((int) link.hitbox.x, (int) link.hitbox.y, (int) link.hitbox.width, (int) link.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.sword.hitbox.x, (int) link.sword.hitbox.y, (int) link.sword.hitbox.width, (int) link.sword.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.boomerang.hitbox.x, (int) link.boomerang.hitbox.y, (int) link.boomerang.hitbox.width, (int) link.boomerang.hitbox.height, Hitbox.COLOR);
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
     * If Link is almost at the border of a tile put him on a border
     */
    private float evaluateXAfterShift(float x) {
        int tileX = LocationUtil.getTileXFromPositionX(x);
        float tilePositionX = LocationUtil.getXFromGrid(tileX);
        float deltaX = x - tilePositionX;
        if (deltaX < LINK_MAX_SHIFT) {
            return tilePositionX + A_TINY_BIT_MORE;
        }
        if (deltaX > LocationUtil.TILE_SIZE - LINK_MAX_SHIFT) {
            return tilePositionX + LocationUtil.TILE_SIZE + A_TINY_BIT_MORE;
        }
        return x;
    }

    /**
     * If Link is almost at the border of a tile put him on a border
     */
    private float evaluateYAfterShift(float y) {
        int tileY = LocationUtil.getTileYFromPositionY(y);
        float tilePositionY = LocationUtil.getYFromGrid(tileY);
        float deltaY = y - tilePositionY;
        if (deltaY < LINK_MAX_SHIFT) {
            return tilePositionY + A_TINY_BIT_MORE;
        }
        if (deltaY > LocationUtil.TILE_SIZE - LINK_MAX_SHIFT) {
            return tilePositionY + LocationUtil.TILE_SIZE + A_TINY_BIT_MORE;
        }
        return y;
    }

    /**
     * Ask LinkManager to shift link
     */
    private void shiftLinkX(float nextX) {
        if (nextX != link.x) {
            if (LocationUtil.isLeftOutOfMap(nextX)) {
                link.x = LocationUtil.LEFT_MAP;
            } else if (LocationUtil.isRightOutOfMap(nextX + LocationUtil.TILE_SIZE)) {
                link.x = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - LocationUtil.TILE_SIZE;
            } else {
                link.x = nextX;
            }
            link.hitbox.x = link.x + link.hitbox.x_offset;
            link.sword.x = link.x;
            link.sword.hitbox.x = link.x + link.sword.hitbox.x_offset;
        }
    }

    /**
     * Ask LinkManager to move link
     */
    private void shiftLinkY(float nextY) {
        if (nextY != link.y) {
            if (LocationUtil.isUpOutOfMap(nextY)) {
                link.y = LocationUtil.TOP_MAP;
            } else if (LocationUtil.isDownOutOfMap(nextY + LocationUtil.TILE_SIZE)) {
                link.y = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - LocationUtil.TILE_SIZE;
            } else {
                link.y = nextY;
            }
            link.hitbox.y = link.y + link.hitbox.y_offset;
            link.sword.y = link.y;
            link.sword.hitbox.y = link.y + link.sword.hitbox.y_offset;
        }
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkX(float deltaX) {
        shiftLinkX(link.x + deltaX);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkY(float deltaY) {
        shiftLinkY(link.y + deltaY);
    }

    /**
     * Check if link can go up
     */
    private boolean isUpValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // +/-2 so that link can enter narrow path
        if (zoneManager.isTileWalkable(linkLeft + 2, linkMiddle, true) && zoneManager.isTileWalkable(linkRight - 2, linkMiddle, true)) {
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft + 6, linkMiddle, true) && zoneManager.isTileWalkable(linkRight + 2 , linkMiddle, true)) {
            moveLinkX(4);
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft - 2, linkMiddle, true) && zoneManager.isTileWalkable(linkRight - 6 , linkMiddle, true)) {
            moveLinkX(-4);
            return true;
        }
        return false;
    }

    /**
     * Check if link can go down
     */
    private boolean isDownValid(float linkLeft, float linkTop) {
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // +/-2 so that link can enter narrow path
        if (zoneManager.isTileWalkable(linkLeft + 2, linkBottom, true) && zoneManager.isTileWalkable(linkRight - 2, linkBottom, true)) {
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft + 6, linkBottom, true) && zoneManager.isTileWalkable(linkRight + 2, linkBottom, true)) {
            moveLinkX(4);
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft - 2, linkBottom, true) && zoneManager.isTileWalkable(linkRight - 6, linkBottom, true)) {
            moveLinkX(-4);
            return true;
        }
        return false;
    }

    /**
     * Check if link can go x
     */
    private boolean isLeftValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        // +/-2 so that link can enter narrow path
        if (zoneManager.isTileWalkable(linkLeft, linkMiddle + 2, true) && zoneManager.isTileWalkable(linkLeft, linkBottom - 2, true)) {
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft, linkMiddle - 2, true) && zoneManager.isTileWalkable(linkLeft, linkBottom - 6, true)) {
            moveLinkY(-4);
            return true;
        }
        if (zoneManager.isTileWalkable(linkLeft, linkMiddle + 6, true) && zoneManager.isTileWalkable(linkLeft, linkBottom + 2, true)) {
            moveLinkY(4);
            return true;
        }
        return false;
    }

    /**
     * Check if link can go right
     */
    private boolean isRightValid(float linkLeft, float linkTop) {
        float linkMiddle = linkTop + LocationUtil.HALF_TILE_SIZE;
        float linkBottom = linkTop + LocationUtil.TILE_SIZE;
        float linkRight = linkLeft + LocationUtil.TILE_SIZE;
        // +/-2 so that link can enter narrow path
        if (zoneManager.isTileWalkable(linkRight, linkMiddle + 2, true) && zoneManager.isTileWalkable(linkRight, linkBottom - 2, true)) {
            return true;
        }
        if (zoneManager.isTileWalkable(linkRight, linkMiddle - 2, true) && zoneManager.isTileWalkable(linkRight, linkBottom - 6, true)) {
            moveLinkY(-4);
            return true;
        }
        if (zoneManager.isTileWalkable(linkRight, linkMiddle + 6, true) && zoneManager.isTileWalkable(linkRight, linkBottom + 2, true)) {
            moveLinkY(4);
            return true;
        }
        return false;
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
