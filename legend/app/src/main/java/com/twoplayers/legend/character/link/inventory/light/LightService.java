package com.twoplayers.legend.character.link.inventory.light;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
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
    public void handleFire(Link link, float deltaTime) {
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
}
