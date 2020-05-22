package com.twoplayers.legend.character.link.inventory.boomerang;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public class BoomerangService {

    private GuiManager guiManager;
    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public BoomerangService(GuiManager guiManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.guiManager = guiManager;
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Reset the boomerang
     */
    public void reset(Link link) {
        link.boomerang.isMovingForward = false;
        link.boomerang.isMovingBackward = false;
        link.boomerang.counter = 0;
    }

    /**
     * Initiate boomerang when link is throwing it
     */
    public void initiateBoomerang(Link link) {
        Boomerang boomerang = link.boomerang;
        if (!boomerang.isMovingForward && !boomerang.isMovingBackward) {
            Logger.info("Link is using boomerang.");
            link.startToUseItem();
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
    public void handleBoomerang(Link link, float deltaTime) {
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
            float deltaX = link.x + LocationUtil.QUARTER_TILE_SIZE - boomerang.x;
            float deltaY = link.y + LocationUtil.QUARTER_TILE_SIZE - boomerang.y;
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
                if (!link.isUsingItem && !link.isShowingItem) {
                    link.startToUseItem();
                }
            }
        }
    }

}
