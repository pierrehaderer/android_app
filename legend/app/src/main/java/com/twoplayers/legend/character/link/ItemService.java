package com.twoplayers.legend.character.link;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
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
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class ItemService {

    /**
     * Check if link already has the item
     */
    public boolean alreadyInInventory(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                return link.arrow != Arrow.NONE;
            case "white_arrow":
                return link.arrow == Arrow.WHITE;
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
                link.arrow = Arrow.WOOD;
                break;
            case "white_arrow":
                link.arrow = Arrow.WHITE;
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
                possibleSecondItem = 3;
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
        }
    }

    /**
     * Method to hide all items and their effects from screen
     */
    public void hideItemsAndEffects(Link link) {
        link.boomerang.isMovingForward = false;
        link.boomerang.isMovingBackward = false;
    }

    /**
     * Initiate boomerang when link is throwing it
     */
    public void initiateBoomerang(GuiManager guiManager, Link link) {
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
            boomerang.x = link.x + boomerang.hitbox.x_offset;
            boomerang.y = link.y + boomerang.hitbox.y_offset;
            Logger.info("Boomerang is starting at position (" + boomerang.x + "," + boomerang.y + ")");
            if (guiManager.isUpPressed()) {
                if (guiManager.isLeftPressed()) {
                    boomerang.orientation = Orientation.UP_LEFT;
                } else if (guiManager.isRightPressed()) {
                    boomerang.orientation = Orientation.UP_RIGHT;
                } else {
                    boomerang.orientation = Orientation.UP;
                }
            } else if (guiManager.isDownPressed()) {
                if (guiManager.isLeftPressed()) {
                    boomerang.orientation = Orientation.DOWN_LEFT;
                } else if (guiManager.isRightPressed()) {
                    boomerang.orientation = Orientation.DOWN_RIGHT;
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
    protected void handleBoomerang(SoundEffectManager soundEffectManager, IEnemyManager enemyManager, Link link, float deltaTime) {
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
                case UP_LEFT:
                    boomerang.x -= deltaTime * speed;
                    boomerang.y -= deltaTime * speed;
                    boomerang.hitbox.x -= deltaTime * speed;
                    boomerang.hitbox.y -= deltaTime * speed;
                    break;
                case UP_RIGHT:
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
                case DOWN_LEFT:
                    boomerang.x -= deltaTime * speed;
                    boomerang.y += deltaTime * speed;
                    boomerang.hitbox.x -= deltaTime * speed;
                    boomerang.hitbox.y += deltaTime * speed;
                    break;
                case DOWN_RIGHT:
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
            if (LocationUtil.isUpOutOfMap(boomerang.y + LocationUtil.HALF_TILE_SIZE)
                    || LocationUtil.isDownOutOfMap(boomerang.y)
                    || LocationUtil.isLeftOutOfMap(boomerang.x + LocationUtil.HALF_TILE_SIZE)
                    || LocationUtil.isRightOutOfMap(boomerang.x)) {
                // TODO add hit animation
                Logger.info("Boomerang is out of room and starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                boomerang.isMovingBackward = true;
                boomerang.isMovingForward = false;
            }
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isActive() && !enemy.isDead() && LocationUtil.areColliding(boomerang.hitbox, enemy.getHitbox())) {
                    Logger.info("Boomerang has hit an enemy and starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                    enemyManager.boomerangHits(enemy);
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
                    enemyManager.boomerangHits(enemy);
                    if (boomerang.soundCounter == Boomerang.INITIAL_SOUND_COUNTER) {
                        soundEffectManager.play("enemy_wounded");
                    }
                }
            }
            if (LocationUtil.areColliding(link.hitbox, boomerang.hitbox)) {
                boomerang.isMovingBackward = false;
                link.isUsingSecondItem = true;
                if (!link.isAttacking && !link.isShowingItem) {
                    link.currentAnimation = link.useAnimations.get(link.orientation);
                    link.currentAnimation.reset();
                }
            }
        }
    }
}