package com.twoplayers.legend.character.link.inventory.rod;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class RodService {

    private IEnemyManager enemyManager;
    private IZoneManager zoneManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public RodService(IEnemyManager enemyManager, IZoneManager zoneManager, SoundEffectManager soundEffectManager) {
        this.enemyManager = enemyManager;
        this.zoneManager = zoneManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Reset the rod wave
     */
    public void reset(Link link) {
        link.rodWave.isActive = false;
    }

    /**
     * Initiate link attack
     */
    public void initiateRod(Link link) {
        Rod rod = link.rod;
        RodWave wave = link.rodWave;
        if (rod.type != RodType.NONE) {
            Logger.info("Link is attacking with its rod.");
            link.startToUseItem();
            rod.isActive = true;
            rod.x = link.x;
            rod.y = link.y;
            rod.orientation = link.orientation;
            rod.getHitbox().relocate(rod.x, rod.y);
            rod.image = rod.emptyImage;
            if (!wave.isActive) {
                wave.isActive = true;
                wave.delayBeforeActive = RodWave.INITIAL_DELAY;
                wave.x = link.x;
                wave.y = link.y;
                wave.orientation = link.orientation;
                wave.hitbox.relocate(link.x, link.y);
                soundEffectManager.play("sorcerer");
            }
        }
    }

    /**
     * Handle link attack
     */
    public void handleLinkRod(Link link, float deltaTime) {
        Rod rod = link.rod;
        if (rod.isActive) {
            if (link.useItemStepHasChanged) {
                switch (link.useItemStep) {
                    case 0:
                        rod.image = rod.emptyImage;
                        break;
                    case 1:
                        rod.x = link.x + rod.positionDeltaX.get(link.orientation)[link.useItemStep];
                        rod.y = link.y + rod.positionDeltaY.get(link.orientation)[link.useItemStep];
                        rod.image = rod.images.get(rod.orientation);
                        for (Enemy enemy : enemyManager.getEnemies()) {
                            if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(rod.getHitbox(), enemy.getHitbox())) {
                                Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link rod.");
                                enemyManager.isHitByRod(enemy, rod);
                            }
                        }
                    case 2:
                    case 3:
                        rod.x = link.x + rod.positionDeltaX.get(link.orientation)[link.useItemStep];
                        rod.y = link.y + rod.positionDeltaY.get(link.orientation)[link.useItemStep];
                        rod.image = rod.images.get(rod.orientation);
                        break;
                    default:
                        rod.isActive = false;
                }
            }
        }
    }

    /**
     * Handle link rod wave
     */
    public void handleLinkRodWave(Link link, float deltaTime) {
        RodWave wave = link.rodWave;
        Fire fire = link.rodFire;
        if (wave.isActive) {
            if (wave.delayBeforeActive > 0) {
                wave.delayBeforeActive -= deltaTime;
            }
            wave.getAnimation().update(deltaTime);
            switch (wave.orientation) {
                case UP:
                    wave.y -= deltaTime * RodWave.SPEED;
                    wave.hitbox.y -= deltaTime * RodWave.SPEED;
                    break;
                case DOWN:
                    wave.y += deltaTime * RodWave.SPEED;
                    wave.hitbox.y += deltaTime * RodWave.SPEED;
                    break;
                case LEFT:
                    wave.x -= deltaTime * RodWave.SPEED;
                    wave.hitbox.x -= deltaTime * RodWave.SPEED;
                    break;
                case RIGHT:
                    wave.x += deltaTime * RodWave.SPEED;
                    wave.hitbox.x += deltaTime * RodWave.SPEED;
                    break;
            }
            if (zoneManager.hasRodWaveHitBorder(wave)) {
                endRodWaveAndStartFire(wave, fire, link.spellBook);
            }
            if (wave.delayBeforeActive <= 0) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.rodWave.hitbox, enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link rod wave.");
                        enemyManager.isHitByRodWave(enemy, link.rodWave);
                        endRodWaveAndStartFire(wave, fire, link.spellBook);
                    }
                }
            }
        }
    }

    private void endRodWaveAndStartFire(RodWave wave, Fire fire, SpellBook spellBook) {
        wave.isActive = false;
        wave.hitbox.relocate(0, 0);
        if (spellBook == SpellBook.BOOK) {
            fire.isActive = true;
            fire.timeBeforeDespawn = Fire.INITIAL_TIME_BEFORE_DESPAWN;
            fire.remainingMoves = 0;
            fire.x = wave.x;
            fire.y = wave.y;
            fire.hitbox.relocate(fire.x, fire.y);
        }
    }
}
