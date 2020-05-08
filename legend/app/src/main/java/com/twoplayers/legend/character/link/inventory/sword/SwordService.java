package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Animation;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class SwordService {

    private IEnemyManager enemyManager;
    private IZoneManager zoneManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public SwordService(IEnemyManager enemyManager, IZoneManager zoneManager, SoundEffectManager soundEffectManager) {
        this.enemyManager = enemyManager;
        this.zoneManager = zoneManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Reset the throwing sword
     */
    public void reset(Link link) {
        link.throwingSword.isActive = false;
        link.swordSplash.isActive = false;
    }

    /**
     * Initiate link attack
     */
    public void initiateSword(Link link) {
        if (link.sword.type != SwordType.NONE) {
            Logger.info("Link is attacking with its sword.");
            link.startToUseItem();
            link.sword.isActive = true;
            link.sword.x = link.x;
            link.sword.y = link.y;
            link.sword.orientation = link.orientation;
            link.sword.getHitbox().relocate(link.sword.x, link.sword.y);
            if (link.life == link.lifeMax && !link.throwingSword.isActive) {
                link.throwingSword.isActive = true;
                link.throwingSword.delayBeforeActive = ThrowingSword.INITIAL_DELAY;
                link.throwingSword.x = link.x;
                link.throwingSword.y = link.y;
                link.throwingSword.orientation = link.orientation;
                link.throwingSword.hitbox.relocate(link.x, link.y);
                soundEffectManager.play("throwing_sword");
            } else {
                soundEffectManager.play("sword");
            }
        }
    }

    /**
     * Handle link attack
     */
    public void handleLinkSword(Link link, float deltaTime) {
        Sword sword = link.sword;
        if (sword.isActive) {
            // Update sword position and image
            if (link.useItemStepHasChanged) {
                if (link.useItemStep == 0 || link.useItemStep > 3) {
                    sword.image = sword.emptyImage;
                } else {
                    sword.image = sword.images.get(sword.type).get(sword.orientation);
                    sword.x = link.x + sword.positionDeltaX.get(link.orientation)[link.useItemStep];
                    sword.y = link.y + sword.positionDeltaY.get(link.orientation)[link.useItemStep];
                }
            }
            // Handle sword hitting enemies
            if (link.useItemStep > 2) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(sword.getHitbox(), enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link sword.");
                        enemyManager.isHitBySword(enemy, sword);
                    }
                }
            }
        }
    }

    /**
     * Handle link throwing sword
     */
    public void handleLinkThrowingSword(Link link, float deltaTime) {
        ThrowingSword throwingSword = link.throwingSword;
        SwordSplash swordSplash = link.swordSplash;
        if (throwingSword.isActive) {
            if (throwingSword.delayBeforeActive > 0) {
                throwingSword.delayBeforeActive -= deltaTime;
            }
            throwingSword.getAnimation().update(deltaTime);
            switch (throwingSword.orientation) {
                case UP:
                    throwingSword.y -= deltaTime * ThrowingSword.SPEED;
                    throwingSword.hitbox.y -= deltaTime * ThrowingSword.SPEED;
                    break;
                case DOWN:
                    throwingSword.y += deltaTime * ThrowingSword.SPEED;
                    throwingSword.hitbox.y += deltaTime * ThrowingSword.SPEED;
                    break;
                case LEFT:
                    throwingSword.x -= deltaTime * ThrowingSword.SPEED;
                    throwingSword.hitbox.x -= deltaTime * ThrowingSword.SPEED;
                    break;
                case RIGHT:
                    throwingSword.x += deltaTime * ThrowingSword.SPEED;
                    throwingSword.hitbox.x += deltaTime * ThrowingSword.SPEED;
                    break;
            }
            if (zoneManager.hasThrowingSwordHitBorder(throwingSword)) {
                endThrowingSwordAndStartSplash(throwingSword, swordSplash);
            }
            if (throwingSword.delayBeforeActive <= 0) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.throwingSword.hitbox, enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link throwing sword.");
                        enemyManager.isHitBySword(enemy, link.sword);
                        endThrowingSwordAndStartSplash(throwingSword, swordSplash);
                    }
                }
            }
        }
        if (swordSplash.isActive) {
            swordSplash.count -= deltaTime;
            if (swordSplash.count < 0) {
                swordSplash.isActive = false;
            }
            for (Animation animation : swordSplash.animations) {
                animation.update(deltaTime);
            }
            swordSplash.animationPositions[0].x -= SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[0].y -= SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[1].x += SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[1].y -= SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[2].x -= SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[2].y += SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[3].x += SwordSplash.SPEED * deltaTime;
            swordSplash.animationPositions[3].y += SwordSplash.SPEED * deltaTime;
        }
    }

    /**
     * Deactivate the throwing sword and activate the sword splash
     */
    private void endThrowingSwordAndStartSplash(ThrowingSword throwingSword, SwordSplash swordSplash) {
        throwingSword.isActive = false;
        throwingSword.hitbox.relocate(0, 0);
        swordSplash.isActive = true;
        swordSplash.count = SwordSplash.INITIAL_COUNT;
        swordSplash.animationPositions[0].x = throwingSword.x;
        swordSplash.animationPositions[0].y = throwingSword.y - 5f * AllImages.COEF;
        swordSplash.animationPositions[1].x = throwingSword.x + 10f * AllImages.COEF;
        swordSplash.animationPositions[1].y = throwingSword.y - 5f * AllImages.COEF;
        swordSplash.animationPositions[2].x = throwingSword.x;
        swordSplash.animationPositions[2].y = throwingSword.y + 5f * AllImages.COEF;
        swordSplash.animationPositions[3].x = throwingSword.x + 10f * AllImages.COEF;
        swordSplash.animationPositions[3].y = throwingSword.y + 5f * AllImages.COEF;
    }
}
