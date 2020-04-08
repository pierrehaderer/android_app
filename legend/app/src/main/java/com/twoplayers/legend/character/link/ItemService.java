package com.twoplayers.legend.character.link;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.inventory.ArrowType;
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
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class ItemService {

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public ItemService(GuiManager guiManager, IZoneManager zoneManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.guiManager = guiManager;
        this.zoneManager = zoneManager;
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Handle link use the second item
     */
    public void handleLinkUsingSecondItem(Link link, float deltaTime) {
        if (!link.isAttacking && !link.isUsingSecondItem && guiManager.areButtonsActivated()
                && !link.isEnteringADoor && !link.isExitingADoor && !link.isShowingItem
                && zoneManager.isLinkFarEnoughFromBorderToAttack(link)) {
            if (guiManager.isbPressed()) {
                switch (link.secondItem) {
                    case 1:
                        initiateBoomerang(link);
                        break;
                    case 2:
                        break;
                    case 3:
                        initiateArrow(link);
                        break;
                    case 4:
                        initiateFireFromLight(link);
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
        handleBoomerang(link, deltaTime);
        handleFire(link, deltaTime);
        handleArrow(link, deltaTime);
        if (link.isUsingSecondItem || link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            if (link.currentAnimation.isAnimationOver()) {
                link.isUsingSecondItem = false;
                link.isAttacking = false;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
            }
        }
    }

    /**
     * Handle when link is picking an item
     */
    public void handleLinkPickingItem(Link link, float deltaTime) {
        for (Item item : zoneManager.getItems()) {
            if (LocationUtil.areColliding(link.hitbox, item.hitbox) && link.coins - link.coinsToRemove >= item.price && !alreadyInInventory(link, item)) {
                link.isPushed = false;
                item.hideItemForTheZone();
                link.coinCounter = 0;
                link.coinsToRemove += item.price;
                link.itemToShow = item;
                link.isShowingItem = true;
                link.showItemCounter = Link.INITIAL_SHOW_COUNT;
                link.currentAnimation = link.pickAnimations[item.pickAnimation];
                soundEffectManager.play("collect_item");
                putItemInInventory(link, item);
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
    }

    /**
     * Method to hide all items and their effects from screen
     */
    public void hideItemsAndEffects(Link link) {
        link.boomerang.isMovingForward = false;
        link.boomerang.isMovingBackward = false;
        link.lightCount = 0;
        link.fire1.isActive = false;
        link.fire2.isActive = false;
        link.arrow.isActive = false;
        link.arrow.isAnImpact = false;
    }

    /**
     * Initiate boomerang when link is throwing it
     */
    public void initiateBoomerang(Link link) {
        Boomerang boomerang = link.boomerang;
        if (!boomerang.isMovingForward && !boomerang.isMovingBackward) {
            Logger.info("Link is using boomerang.");
            link.isUsingSecondItem = true;
            link.currentAnimation = link.useAnimations.get(link.orientation);
            link.currentAnimation.reset();
            boomerang.isMovingForward = true;
            boomerang.counter = Boomerang.INITIAL_WOOD_BOOMERANG_COUNTER;
            boomerang.soundCounter = 0;
            boomerang.hitbox = boomerang.hitboxes.get(link.orientation);
            boomerang.hitbox.relocate(link.x, link.y);
            boomerang.x = boomerang.hitbox.x;
            boomerang.y = boomerang.hitbox.y;
            Logger.info("Boomerang is starting at position (" + boomerang.x + "," + boomerang.y + ")");
            if (guiManager.isUpPressed()) {
                if (guiManager.isLeftPressed()) {
                    boomerang.orientation = Orientation.DEGREES_135;
                } else if (guiManager.isRightPressed()) {
                    boomerang.orientation = Orientation.DEGREES_45;
                } else {
                    boomerang.orientation = Orientation.UP;
                }
            } else if (guiManager.isDownPressed()) {
                if (guiManager.isLeftPressed()) {
                    boomerang.orientation = Orientation.DEGREES_225;
                } else if (guiManager.isRightPressed()) {
                    boomerang.orientation = Orientation.DEGREES_315;
                } else {
                    boomerang.orientation = Orientation.DOWN;
                }
            } else {
                boomerang.orientation = link.orientation;
            }
        }
    }

    /**
     * Handle boomerang movements and interactions
     */
    protected void handleBoomerang(Link link, float deltaTime) {
        Boomerang boomerang = link.boomerang;
        if (boomerang.isMovingForward) {
            boomerang.getAnimation().update(deltaTime);
            boomerang.counter = (boomerang.type == BoomerangType.WOOD) ? boomerang.counter - deltaTime : Boomerang.INITIAL_WOOD_BOOMERANG_COUNTER;
            float speed = Boomerang.INITIAL_SPEED * boomerang.counter / Boomerang.INITIAL_WOOD_BOOMERANG_COUNTER;
            boomerang.soundCounter -= deltaTime;
            if (boomerang.soundCounter < 0) {
                soundEffectManager.play("boomerang");
                boomerang.soundCounter = Boomerang.INITIAL_SOUND_COUNTER;
            }
            switch (boomerang.orientation) {
                case UP:
                    boomerang.y -= deltaTime * speed;
                    boomerang.hitbox.y -= deltaTime * speed;
                    break;
                case DEGREES_135:
                    boomerang.x -= deltaTime * speed;
                    boomerang.y -= deltaTime * speed;
                    boomerang.hitbox.x -= deltaTime * speed;
                    boomerang.hitbox.y -= deltaTime * speed;
                    break;
                case DEGREES_45:
                    boomerang.x += deltaTime * speed;
                    boomerang.y -= deltaTime * speed;
                    boomerang.hitbox.x += deltaTime * speed;
                    boomerang.hitbox.y -= deltaTime * speed;
                    break;
                case LEFT:
                    boomerang.x -= deltaTime * speed;
                    boomerang.hitbox.x -= deltaTime * speed;
                    break;
                case RIGHT:
                    boomerang.x += deltaTime * speed;
                    boomerang.hitbox.x += deltaTime * speed;
                    break;
                case DOWN:
                    boomerang.y += deltaTime * speed;
                    boomerang.hitbox.y += deltaTime * speed;
                    break;
                case DEGREES_225:
                    boomerang.x -= deltaTime * speed;
                    boomerang.y += deltaTime * speed;
                    boomerang.hitbox.x -= deltaTime * speed;
                    boomerang.hitbox.y += deltaTime * speed;
                    break;
                case DEGREES_315:
                    boomerang.x += deltaTime * speed;
                    boomerang.y += deltaTime * speed;
                    boomerang.hitbox.x += deltaTime * speed;
                    boomerang.hitbox.y += deltaTime * speed;
                    break;
            }
            if (boomerang.counter < 0) {
                Logger.info("Boomerang starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                boomerang.isMovingBackward = true;
                boomerang.isMovingForward = false;
            }
            if (LocationUtil.isUpOutOfMap(boomerang.y + LocationUtil.QUARTER_TILE_SIZE)
                    || LocationUtil.isDownOutOfMap(boomerang.y)
                    || LocationUtil.isLeftOutOfMap(boomerang.x + LocationUtil.QUARTER_TILE_SIZE)
                    || LocationUtil.isRightOutOfMap(boomerang.x)) {
                // TODO add hit animation
                Logger.info("Boomerang is out of room and starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                boomerang.isMovingBackward = true;
                boomerang.isMovingForward = false;
            }
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isActive() && !enemy.isDead() && LocationUtil.areColliding(boomerang.hitbox, enemy.getHitbox())) {
                    Logger.info("Boomerang has hit an enemy and starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                    enemyManager.isHitByBoomerang(enemy);
                    if (boomerang.soundCounter == Boomerang.INITIAL_SOUND_COUNTER) {
                        soundEffectManager.play("enemy_wounded");
                    }
                    boomerang.isMovingBackward = true;
                    boomerang.isMovingForward = false;
                }
            }
        }
        if (boomerang.isMovingBackward) {
            boomerang.getAnimation().update(deltaTime);
            boomerang.counter = Math.min(boomerang.counter + deltaTime, Boomerang.INITIAL_WOOD_BOOMERANG_COUNTER);
            float speed = Boomerang.INITIAL_SPEED * boomerang.counter / Boomerang.INITIAL_WOOD_BOOMERANG_COUNTER;
            boomerang.soundCounter -= deltaTime;
            if (boomerang.soundCounter < 0) {
                soundEffectManager.play("boomerang");
                boomerang.soundCounter = Boomerang.INITIAL_SOUND_COUNTER;
            }
            float deltaX = link.x + LocationUtil.HALF_TILE_SIZE / 2 - boomerang.x;
            float deltaY = link.y + LocationUtil.HALF_TILE_SIZE / 2 - boomerang.y;
            float ratioX = 0.5f;
            float ratioY = 0.5f;
            if (deltaX != 0 || deltaY != 0) {
                ratioX = deltaX / (Math.abs(deltaX) + Math.abs(deltaY));
                ratioY = deltaY / (Math.abs(deltaX) + Math.abs(deltaY));
            }
            boomerang.x += ratioX * deltaTime * speed;
            boomerang.y += ratioY * deltaTime * speed;
            boomerang.hitbox.x += ratioX * deltaTime * speed;
            boomerang.hitbox.y += ratioY * deltaTime * speed;
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isActive() && !enemy.isDead() && LocationUtil.areColliding(boomerang.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has hit enemy with boomerang backward.");
                    enemyManager.isHitByBoomerang(enemy);
                    if (boomerang.soundCounter == Boomerang.INITIAL_SOUND_COUNTER) {
                        soundEffectManager.play("enemy_wounded");
                    }
                }
            }
            if (LocationUtil.areColliding(link.hitbox, boomerang.hitbox)) {
                boomerang.isMovingBackward = false;
                if (!link.isAttacking && !link.isShowingItem) {
                    link.isUsingSecondItem = true;
                    link.currentAnimation = link.useAnimations.get(link.orientation);
                    link.currentAnimation.reset();
                }
            }
        }
    }

    /**
     * Initiate fire when link is using light
     */
    public void initiateFireFromLight(Link link) {
        if (link.timeBeforeUseLight <= 0 && ((link.light == Light.BLUE && link.lightCount == 0) || (link.light == Light.RED && (!link.fire1.isActive || !link.fire2.isActive)))) {
            Logger.info("Link is using light.");
            link.isUsingSecondItem = true;
            link.currentAnimation.reset();
            link.currentAnimation = link.useAnimations.get(link.orientation);
            link.lightCount++;
            link.timeBeforeUseLight = Link.INITIAL_TIME_BEFORE_USE_LIGHT;
            soundEffectManager.play("fire");
            Fire fire = (link.fire1.isActive) ? link.fire2 : link.fire1;
            switch (link.orientation) {
                case UP:
                    fire.initFromLight(link.orientation, link.x, link.y - LocationUtil.TILE_SIZE);
                    break;
                case DOWN:
                    fire.initFromLight(link.orientation, link.x, link.y + LocationUtil.TILE_SIZE);
                    break;
                case LEFT:
                    fire.initFromLight(link.orientation, link.x - LocationUtil.TILE_SIZE, link.y);
                    break;
                case RIGHT:
                    fire.initFromLight(link.orientation, link.x + LocationUtil.TILE_SIZE, link.y);
                    break;
            }
        }
    }

    /**
     * Handle fire movements and interactions
     */
    protected void handleFire(Link link, float deltaTime) {
        if (link.timeBeforeUseLight > 0) {
            link.timeBeforeUseLight -= deltaTime;
        }
        link.fire1.update(deltaTime);
        link.fire2.update(deltaTime);
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible()) {
                if (link.fire1.isActive && LocationUtil.areColliding(link.fire1.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has hit enemy with fire 1.");
                    enemyManager.isHitByFire(enemy, link.fire1);
                }
                if (link.fire2.isActive && LocationUtil.areColliding(link.fire2.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has hit enemy with fire 2.");
                    enemyManager.isHitByFire(enemy, link.fire2);
                }
            }
        }
        if (link.fire1.hasJustFinished) {
            zoneManager.burnTheBushes(link.fire1);
            link.fire1.hasJustFinished = false;
            link.fire1.isActive = false;
            link.fire1.hitbox.relocate(0, 0);
        }
        if (link.fire2.hasJustFinished) {
            zoneManager.burnTheBushes(link.fire2);
            link.fire2.hasJustFinished = false;
            link.fire2.isActive = false;
            link.fire2.hitbox.relocate(0, 0);
        }
    }


    /**
     * Initiate arrow when link is throwing it
     */
    public void initiateArrow(Link link) {
        Arrow arrow = link.arrow;
        if (!arrow.isActive && !arrow.isAnImpact && link.coins > 0) {
            Logger.info("Link is using bow and arrow.");
            link.isUsingSecondItem = true;
            link.currentAnimation = link.useAnimations.get(link.orientation);
            link.currentAnimation.reset();
            link.coins--;
            soundEffectManager.play("coin_remove");
            arrow.isActive = true;
            arrow.hitbox = arrow.hitboxes.get(link.orientation);
            arrow.hitbox.relocate(link.x, link.y);
            arrow.x = (link.orientation == Orientation.RIGHT) ? arrow.hitbox.x - 8 * AllImages.COEF : arrow.hitbox.x;
            arrow.y = (link.orientation == Orientation.DOWN) ? arrow.hitbox.y - 8 * AllImages.COEF : arrow.hitbox.y;
            Logger.info("Arrow is starting at position (" + arrow.x + "," + arrow.y + ")");
            arrow.orientation = link.orientation;
            arrow.selectCurrentAnimation();
            arrow.currentAnimation.reset();
        }
    }

    /**
     * Handle arrow movements and interactions
     */
    protected void handleArrow(Link link, float deltaTime) {
        Arrow arrow = link.arrow;
        if (arrow.isActive) {
            arrow.currentAnimation.update(deltaTime);
            boolean removeArrow = false;
            switch (arrow.orientation) {
                case UP:
                    arrow.y -= Arrow.SPEED * deltaTime;
                    arrow.hitbox.y -= Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
                case DOWN:
                    arrow.y += Arrow.SPEED * deltaTime;
                    arrow.hitbox.y += Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y + arrow.hitbox.height)) {
                        removeArrow = true;
                    }
                    break;
                case LEFT:
                    arrow.x -= Arrow.SPEED * deltaTime;
                    arrow.hitbox.x -= Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
                case RIGHT:
                    arrow.x += Arrow.SPEED * deltaTime;
                    arrow.hitbox.x += Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x + arrow.hitbox.width, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
            }
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.arrow.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has hit enemy with arrow.");
                    enemyManager.isHitByArrow(enemy, link.arrow);
                    removeArrow = true;
                    break;
                }
            }
            if (removeArrow) {
                arrow.currentAnimation = arrow.deathAnimation;
                arrow.currentAnimation.reset();
                arrow.isActive = false;
                arrow.isAnImpact = true;
            }
        }
        if (arrow.isAnImpact) {
            arrow.currentAnimation.update(deltaTime);
            if (arrow.currentAnimation.isAnimationOver()) {
                arrow.isAnImpact = false;
            }
        }
    }

    /**
     * Check if link already has the item
     */
    public boolean alreadyInInventory(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                return link.arrow.type != ArrowType.NONE;
            case "white_arrow":
                return link.arrow.type == ArrowType.WHITE;
            case "wood_boomerang":
                return link.boomerang.type != BoomerangType.NONE;
            case "white_boomerang":
                return link.boomerang.type == BoomerangType.WHITE;
            case "bombs":
                return link.bomb < link.bombMax;
            case "bow":
                return link.bow == Bow.BOW;
            case "bracelet":
                return link.bracelet == Bracelet.BRACELET;
            case "compass":
                return link.compass == Compass.COMPASS;
            case "dungeon_map":
                return link.dungeonMap == DungeonMap.MAP;
            case "flute":
                return link.flute == Flute.FLUTE;
            case "key":
                return link.keys >= 99;
            case "infinite_key":
                return link.infiniteKey == InfiniteKey.KEY;
            case "ladder":
                return link.ladder == Ladder.LADDER;
            case "blue_light":
                return link.light != Light.NONE;
            case "red_light":
                return link.light == Light.RED;
            case "meat":
                return link.meat == Meat.MEAT;
            case "note":
                return link.potion != Potion.NONE;
            case "blue_potion":
                return link.potion == Potion.BLUE || link.potion == Potion.RED;
            case "red_potion":
                return link.potion == Potion.RED;
            case "raft":
                return link.raft == Raft.RAFT;
            case "blue_ring":
                return link.ring != Ring.NONE;
            case "red_ring":
                return link.ring == Ring.RED;
            case "scepter":
                return link.scepter == Scepter.SCEPTER;
            case "shield":
                return link.shield == Shield.BIG;
            case "spell_book":
                return link.spellBook == SpellBook.BOOK;
            case "wood_sword":
                return link.sword.type != SwordType.NONE;
            case "white_sword":
                return link.sword.type == SwordType.WHITE || link.sword.type == SwordType.MAGICAL;
            case "magical_sword":
                return link.sword.type == SwordType.MAGICAL;
        }
        return false;
    }

    /**
     * Give the corresponding item to link
     */
    public void putItemInInventory(Link link, Item item) {
        int possibleSecondItem = 0;
        switch (item.name) {
            case "wood_arrow":
                link.arrow.type = ArrowType.WOOD;
                if (link.bow != Bow.NONE) possibleSecondItem = 3;
                break;
            case "white_arrow":
                link.arrow.type = ArrowType.WHITE;
                if (link.bow != Bow.NONE) possibleSecondItem = 3;
                break;
            case "wood_boomerang":
                link.boomerang.type = BoomerangType.WOOD;
                possibleSecondItem = 1;
                break;
            case "white_boomerang":
                link.boomerang.type = BoomerangType.WHITE;
                possibleSecondItem = 1;
                break;
            case "bombs":
                link.bomb = Math.min(link.bomb + 4, link.bombMax);
                possibleSecondItem = 2;
                break;
            case "bow":
                link.bow = Bow.BOW;
                if (link.arrow.type != ArrowType.NONE) possibleSecondItem = 3;
                break;
            case "bracelet":
                link.bracelet = Bracelet.BRACELET;
                break;
            case "compass":
                link.compass = Compass.COMPASS;
                break;
            case "dungeon_map":
                link.dungeonMap = DungeonMap.MAP;
                break;
            case "flute":
                link.flute = Flute.FLUTE;
                possibleSecondItem = 5;
                break;
            case "infinite_key":
                link.infiniteKey = InfiniteKey.KEY;
                break;
            case "key":
                link.keys++;
                break;
            case "ladder":
                link.ladder = Ladder.LADDER;
                break;
            case "blue_light":
                link.light = Light.BLUE;
                possibleSecondItem = 4;
                break;
            case "red_light":
                link.light = Light.RED;
                possibleSecondItem = 4;
                break;
            case "meat":
                link.meat = Meat.MEAT;
                possibleSecondItem = 6;
                break;
            case "note":
                link.potion = Potion.NOTE;
                break;
            case "blue_potion":
                link.potion = Potion.BLUE;
                possibleSecondItem = 7;
                break;
            case "red_potion":
                link.potion = Potion.RED;
                possibleSecondItem = 7;
                break;
            case "raft":
                link.raft = Raft.RAFT;
                break;
            case "blue_ring":
                link.ring = Ring.BLUE;
                break;
            case "red_ring":
                link.ring = Ring.RED;
                break;
            case "scepter":
                link.scepter = Scepter.SCEPTER;
                possibleSecondItem = 8;
                break;
            case "spell_book":
                link.spellBook = SpellBook.BOOK;
                break;
            case "wood_sword":
                link.sword.type = SwordType.WOOD;
                break;
            case "white_sword":
                link.sword.type = SwordType.WHITE;
                break;
            case "magical_sword":
                link.sword.type = SwordType.MAGICAL;
                break;
        }
        if (link.secondItem == 0) {
            link.secondItem = possibleSecondItem;
            guiManager.updateCursor(link.secondItem);
        }
    }

}
