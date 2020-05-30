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
import com.twoplayers.legend.util.Orientation;

public abstract class Gel extends Enemy {

    private static final float SPEED = 0.8f;

    public Gel(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImages imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        nextTileX = x;
        nextTileY = y;
        timeBeforeFirstMove = DungeonEnemyManager.TIME_BEFORE_FIRST_MOVE;
        speed = SPEED;
        life = 1;
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        damage = -0.5f;
        currentAnimation = initialAnimation;
    }

    /**
     * Init enemy animations
     */
    protected abstract void initAnimations(IImages imagesEnemy, Graphics g);

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsPushed(this, deltaTime);
        enemyService.handleEnemyIsMovingWithPause(this, deltaTime);
        if (isActive) currentAnimation.update(deltaTime);
    }


    @Override
    public void isHitByBoomerang() {
        enemyService.handleEnemyIsWounded(this, 1, new Hitbox(), Orientation.UP);
    }
}
