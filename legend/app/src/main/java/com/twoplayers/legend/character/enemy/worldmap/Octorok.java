package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;

public abstract class Octorok extends Enemy {

    public Octorok(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImagesEnemy imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        nextTileX = x;
        nextTileY = y;
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        timeBeforeAttack = enemyService.chooseTimeBeforeAttack(MIN_TIME_BEFORE_ATTACK, MAX_TIME_BEFORE_ATTACK);
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        damage = -0.5f;
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(IImagesEnemy imagesEnemy, Graphics g);

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsPushed(this, deltaTime);
        enemyService.handleEnemyIsStunned(this, deltaTime);
        enemyService.handleEnemyIsAttacking(this, deltaTime);
        enemyService.handleAttackingEnemyIsMoving(this, deltaTime);
        if (isActive) currentAnimation.update(deltaTime);
    }
}
