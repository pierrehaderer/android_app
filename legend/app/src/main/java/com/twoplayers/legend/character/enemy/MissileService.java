package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.enemy.missile.EmptyMissile;
import com.twoplayers.legend.character.enemy.missile.EnemyBoomerang;
import com.twoplayers.legend.character.enemy.missile.Missile;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class MissileService {

    private IZoneManager zoneManager;

    /**
     * Constructor
     */
    public MissileService(IZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    /**
     * Spawn a missile from the provided enemy
     */
    public Missile spawnMissile(IImagesEnemy imagesEnemy, Graphics graphics, Enemy enemy, Class<? extends Missile> missileClass) {
        try {
            Constructor<? extends Missile> constructor = missileClass.getConstructor(MissileService.class);
            Missile missile = constructor.newInstance(this);
            missile.x = enemy.x + LocationUtil.QUARTER_TILE_SIZE;
            missile.y = enemy.y + LocationUtil.QUARTER_TILE_SIZE;
            missile.orientation = enemy.orientation;
            missile.creator = enemy;
            missile.init(imagesEnemy, graphics);
            return missile;
        } catch (Exception e) {
            Logger.error("Could not create missile with enemy " + enemy.getClass().getSimpleName() + " : " + e.getMessage());
            return new EmptyMissile(this);
        }
    }

    /**
     * Remove the missiles that are not active anymore
     */
    public List<Missile> cleanMissiles(List<Missile> missiles, boolean cleanRequired) {
        if (cleanRequired) {
            List<Missile> newMissiles = new ArrayList<>();
            for (Missile missile : missiles) {
                if (missile.isActive) {
                    newMissiles.add(missile);
                }
            }
            return newMissiles;
        }
        return missiles;
    }

    /**
     * Move the missile straight
     */
    public void moveStraightMissile(Missile missile, float deltaTime) {
        missile.currentAnimation.update(deltaTime);
        switch (missile.orientation) {
            case UP:
                missile.y -= deltaTime * missile.speed;
                missile.hitbox.y -= deltaTime * missile.speed;
                break;
            case DOWN:
                missile.y += deltaTime * missile.speed;
                missile.hitbox.y += deltaTime * missile.speed;
                break;
            case LEFT:
                missile.x -= deltaTime * missile.speed;
                missile.hitbox.x -= deltaTime * missile.speed;
                break;
            case RIGHT:
                missile.x += deltaTime * missile.speed;
                missile.hitbox.x += deltaTime * missile.speed;
                break;
            default:
                missile.x += deltaTime * missile.speed * Math.cos(missile.orientation.angle);
                missile.y -= deltaTime * missile.speed * Math.sin(missile.orientation.angle);
                missile.hitbox.relocate(missile.x, missile.y);
                break;
        }
    }

    /**
     * Handle missile at border
     */
    public void handleStraightMissileHits(Missile missile) {
        if (LocationUtil.isTileAtBorder(missile.x, missile.y)
            || LocationUtil.isTileAtBorder(missile.x + LocationUtil.QUARTER_TILE_SIZE, missile.y + LocationUtil.QUARTER_TILE_SIZE)
            || (zoneManager.isTileBlockingMissile(missile.x, missile.y) && missile.isBlockedByObstacle)) {
            missile.hitbox.relocate(0, 0);
            missile.isActive = false;
        }
    }

    /**
     * Move boomerang
     */
    public void moveBoomerang(EnemyBoomerang boomerang, float deltaTime) {
        boomerang.currentAnimation.update(deltaTime);
        if (boomerang.isMovingForward) {
            boomerang.counter = boomerang.counter - deltaTime;
            boomerang.speed = EnemyBoomerang.INITIAL_SPEED * boomerang.counter / EnemyBoomerang.INITIAL_BOOMERANG_COUNTER;
            switch (boomerang.orientation) {
                case UP:
                    boomerang.y -= deltaTime * boomerang.speed;
                    boomerang.hitbox.y -= deltaTime * boomerang.speed;
                    break;
                case DOWN:
                    boomerang.y += deltaTime * boomerang.speed;
                    boomerang.hitbox.y += deltaTime * boomerang.speed;
                    break;
                case LEFT:
                    boomerang.x -= deltaTime * boomerang.speed;
                    boomerang.hitbox.x -= deltaTime * boomerang.speed;
                    break;
                case RIGHT:
                    boomerang.x += deltaTime * boomerang.speed;
                    boomerang.hitbox.x += deltaTime * boomerang.speed;
                    break;
            }
            if (boomerang.counter < 0) {
                Logger.info("Enemy boomerang starts to move backward at position (" + boomerang.x + "," + boomerang.y + ")");
                boomerang.isMovingForward = false;
            }
        } else {
            boomerang.counter = Math.min(boomerang.counter + deltaTime, EnemyBoomerang.INITIAL_BOOMERANG_COUNTER);
            boomerang.speed = EnemyBoomerang.INITIAL_SPEED * boomerang.counter / EnemyBoomerang.INITIAL_BOOMERANG_COUNTER;
            // Reverse move compared to moving forward
            switch (boomerang.orientation) {
                case UP:
                    boomerang.y += deltaTime * boomerang.speed;
                    boomerang.hitbox.y += deltaTime * boomerang.speed;
                    break;
                case DOWN:
                    boomerang.y -= deltaTime * boomerang.speed;
                    boomerang.hitbox.y -= deltaTime * boomerang.speed;
                    break;
                case LEFT:
                    boomerang.x += deltaTime * boomerang.speed;
                    boomerang.hitbox.x += deltaTime * boomerang.speed;
                    break;
                case RIGHT:
                    boomerang.x -= deltaTime * boomerang.speed;
                    boomerang.hitbox.x -= deltaTime * boomerang.speed;
                    break;
            }
        }
    }

    /**
     * Go back to sender when boomerang hits
     */
    public void handleBoomerangHits(EnemyBoomerang boomerang) {
        if (LocationUtil.isTileAtBorder(boomerang.x, boomerang.y)
                || LocationUtil.isTileAtBorder(boomerang.x + LocationUtil.QUARTER_TILE_SIZE, boomerang.y + LocationUtil.QUARTER_TILE_SIZE)
                || (zoneManager.isTileBlockingMissile(boomerang.x, boomerang.y) && boomerang.isBlockedByObstacle)) {
            boomerang.isMovingForward = false;
        }
    }

    /**
     * Remove boomerang when it hits the sender
     */
    public void handleBoomerangBackToSender(EnemyBoomerang boomerang) {
        if (!boomerang.isMovingForward && LocationUtil.areColliding(boomerang.hitbox, boomerang.creator.hitbox)) {
            boomerang.isActive = false;
            boomerang.creator.isAttacking = false;
        }
    }
}
