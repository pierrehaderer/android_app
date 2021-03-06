package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;

public abstract class Goriya extends Enemy {

    public Goriya(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImages imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        nextTileX = x;
        nextTileY = y;
        timeBeforeFirstMove = DungeonEnemyManager.TIME_BEFORE_FIRST_MOVE;
        enemyService.chooseTimeBeforeAttack(this, MIN_TIME_BEFORE_ATTACK, MAX_TIME_BEFORE_ATTACK);
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(IImages imagesEnemy, Graphics g);

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsStunned(this, deltaTime);
        if (!isAttacking) {
            enemyService.handleEnemyIsPushed(this, deltaTime);
            enemyService.handleAttackingEnemyIsMoving(this, deltaTime, 0);
        }
        enemyService.handleEnemyIsAttackingWithBoomerang(this, deltaTime, MIN_TIME_BEFORE_ATTACK, MAX_TIME_BEFORE_ATTACK);
        if (isActive) currentAnimation.update(deltaTime);
    }
}
