package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;

import java.util.HashMap;

public class Stalfos extends Enemy {

    private static final float SPEED = 0.6f;

    public Stalfos(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImagesEnemy imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        nextTileX = x;
        nextTileY = y;
        timeBeforeFirstMove = DungeonEnemyManager.TIME_BEFORE_FIRST_MOVE;
        speed = SPEED;
        life = 2;
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        damage = -0.5f;
        currentAnimation = initialAnimation;
    }

    /**
     * Init enemy animations
     */
    protected void initAnimations(IImagesEnemy imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getFastCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemy.get("stalfos_1"), AllImages.COEF, 15);
        animation.addFrame(imagesEnemy.get("stalfos_2"), AllImages.COEF, 15);
        moveAnimations = new HashMap<>();
        moveAnimations.put(Orientation.UP, animation);
        moveAnimations.put(Orientation.DOWN, animation);
        moveAnimations.put(Orientation.LEFT, animation);
        moveAnimations.put(Orientation.RIGHT, animation);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsPushed(this, deltaTime);
        enemyService.handleEnemyIsStunned(this, deltaTime);
        enemyService.handleEnemyIsAttacking(this, deltaTime);
        enemyService.handleEnemyIsMoving(this, deltaTime);
        if (isActive) currentAnimation.update(deltaTime);
    }
}
