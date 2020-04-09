package com.twoplayers.legend.character.link.inventory.sword;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class SwordService {

    private IEnemyManager enemyManager;

    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public SwordService(IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Initiate link attack
     */
    public void initiateSword(Link link) {
        if (link.sword.type != SwordType.NONE) {
            link.switchToUseAnimation();
            link.sword.x = link.x;
            link.sword.y = link.y;
            link.sword.orientation = link.orientation;
            link.sword.getAnimation().reset();
            link.sword.getHitbox().relocate(link.x, link.y);
            soundEffectManager.play("sword");
            link.isAttacking = true;
            link.attackProgression = 0;
        }
    }

    /**
     * Handle link attack
     */
    public void handleLinkAttack(Link link, float deltaTime) {
        if (link.isAttacking) {
            link.sword.getAnimation().update(deltaTime);
            link.attackProgression += deltaTime;
            if (link.attackProgression > Link.STEP_1_DURATION && link.attackProgression < Link.STEP_1_DURATION + Link.STEP_2_ATTACK_DURATION) {
                // Sword hitbox is active
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.sword.getHitbox(), enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link sword.");
                        enemyManager.isHitBySword(enemy, link.sword);
                    }
                }
            }
        }
    }

}
