package com.twoplayers.legend.character.link.inventory.light;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.map.EntranceInfo;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LightService {

    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public LightService(IZoneManager zoneManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.zoneManager = zoneManager;
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Reset the light and the fires
     */
    public void reset(Link link) {
        link.lightCount = 0;
        link.fire1.isActive = false;
        link.fire2.isActive = false;
    }
    /**
     * Initiate fire when link is using light
     */
    public void initiateFireFromLight(Link link) {
        if (link.timeBeforeUseLight <= 0 && ((link.light == Light.BLUE && link.lightCount == 0) || (link.light == Light.RED && (!link.fire1.isActive || !link.fire2.isActive)))) {
            Logger.info("Link is using light.");
            link.isUsingSecondItem = true;
            link.switchToUseAnimation();
            link.lightCount++;
            link.timeBeforeUseLight = Link.INITIAL_TIME_BEFORE_USE_LIGHT;
            soundEffectManager.play("fire");
            Fire fire = (link.fire1.isActive) ? link.fire2 : link.fire1;
            fire.isActive = true;
            fire.timeBeforeDespawn = Fire.INITIAL_TIME_BEFORE_DESPAWN;
            fire.remainingMoves = LocationUtil.TILE_SIZE;
            fire.orientation = link.orientation;
            switch (link.orientation) {
                case UP:
                    fire.x = link.x;
                    fire.y = link.y - LocationUtil.TILE_SIZE;
                    break;
                case DOWN:
                    fire.x = link.x;
                    fire.y = link.y + LocationUtil.TILE_SIZE;
                    break;
                case LEFT:
                    fire.x = link.x - LocationUtil.TILE_SIZE;
                    fire.y = link.y;
                    break;
                case RIGHT:
                    fire.x = link.x + LocationUtil.TILE_SIZE;
                    fire.y = link.y;
                    break;
            }
            fire.hitbox.relocate(fire.x, fire.y);
        }
    }

    /**
     * Handle fire movements and interactions
     */
    public void handleFire(Link link, float deltaTime) {
        if (link.timeBeforeUseLight > 0) {
            link.timeBeforeUseLight -= deltaTime;
        }
        updateFire(link.fire1, deltaTime);
        updateFire(link.fire2, deltaTime);
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
            zoneManager.fireHasJustFinished(link.fire1);
            link.fire1.hasJustFinished = false;
            link.fire1.isActive = false;
            link.fire1.hitbox.relocate(0, 0);
        }
        if (link.fire2.hasJustFinished) {
            zoneManager.fireHasJustFinished(link.fire2);
            link.fire2.hasJustFinished = false;
            link.fire2.isActive = false;
            link.fire2.hitbox.relocate(0, 0);
        }
    }


    /**
     * Update fire animation and position
     */
    protected void updateFire(Fire fire, float deltaTime) {
        if (fire.isActive) {
            fire.animation.update(deltaTime);
            if (fire.remainingMoves > 0) {
                float distance = Math.min(fire.remainingMoves, deltaTime * Fire.SPEED);
                fire.remainingMoves -= distance;
                switch (fire.orientation) {
                    case UP:
                        fire.y -= distance;
                        fire.hitbox.y -= distance;
                        break;
                    case DOWN:
                        fire.y += distance;
                        fire.hitbox.y += distance;
                        break;
                    case LEFT:
                        fire.x -= distance;
                        fire.hitbox.x -= distance;
                        break;
                    case RIGHT:
                        fire.x += distance;
                        fire.hitbox.x += distance;
                        break;
                }
            } else {
                fire.timeBeforeDespawn -= deltaTime;
                if (fire.timeBeforeDespawn < 0) {
                    fire.hasJustFinished = true;
                }
            }
        }
    }

}
