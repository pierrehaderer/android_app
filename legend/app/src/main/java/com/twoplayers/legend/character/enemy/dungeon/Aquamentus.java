package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public class Aquamentus extends Enemy {

    private static final float MIN_TIME_BEFORE_AQUAMENTUS_ATTACK = 200f;
    private static final float MAX_TIME_BEFORE_AQUAMENTUS_ATTACK = 400f;
    private static final float ATTACK_PREPARATION_TIME = 50f;
    private static final float SPEED = 0.2f;

    private Animation moveAnimation;
    private Animation attackAnimation;

    public Aquamentus(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImagesEnemy imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        orientation = Orientation.LEFT;
        nextTileX = x;
        nextTileY = y;
        timeBeforeFirstMove = DungeonEnemyManager.TIME_BEFORE_FIRST_MOVE;
        timeBeforeAttack = ATTACK_PREPARATION_TIME;
        life = 6;
        hitbox = new Hitbox(x, y, 3, 3, 18, 26);
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(IImagesEnemy imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getFastCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        moveAnimation = g.newAnimation();
        moveAnimation.addFrame(imagesEnemy.get("aquamentus_1"), AllImages.COEF, 15);
        moveAnimation.addFrame(imagesEnemy.get("aquamentus_2"), AllImages.COEF, 15);
        attackAnimation = g.newAnimation();
        attackAnimation.addFrame(imagesEnemy.get("aquamentus_attack_1"), AllImages.COEF, 15);
        attackAnimation.addFrame(imagesEnemy.get("aquamentus_attack_2"), AllImages.COEF, 15);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);

        if (isActive) {
            handleAquamentusIsMoving(deltaTime);
            handleAquamentusIsAttacking(deltaTime);
            moveAnimation.update(deltaTime);
            attackAnimation.update(deltaTime);
        }
    }

    /**
     * handle movements
     */
    private void handleAquamentusIsMoving(float deltaTime) {
        float remainingMoves = SPEED * deltaTime;
        remainingMoves = enemyService.goToNextTile(this, remainingMoves);
        if (remainingMoves > 0) {
            boolean goToTheLeft = (Math.random() > 0.5);
            boolean isAtMaxLeft = x < LocationUtil.LEFT_MAP + 9 * LocationUtil.TILE_SIZE;
            boolean isAtMaxRight = x >= LocationUtil.LEFT_MAP + 12.5f * LocationUtil.TILE_SIZE;
            if (isAtMaxLeft || (!isAtMaxRight && !goToTheLeft)) {
                orientation = Orientation.RIGHT;
                nextTileX = x + LocationUtil.HALF_TILE_SIZE;
            }
            if (isAtMaxRight || (!isAtMaxLeft && goToTheLeft)) {
                orientation = Orientation.LEFT;
                nextTileX = x - LocationUtil.HALF_TILE_SIZE;
            }
            enemyService.goToNextTile(this, remainingMoves);
        }
    }

    /**
     * handle attack
     */
    private void handleAquamentusIsAttacking(float deltaTime) {
        timeBeforeAttack -= deltaTime;
        if (!isDead && !isAttacking) {
            if (timeBeforeAttack < ATTACK_PREPARATION_TIME) {
                isAttacking = true;
                currentAnimation = attackAnimation;
            }
        }
        if (!isDead && isAttacking) {
            if (timeBeforeAttack < 0) {
                Logger.info("Enemy is attacking (" + x + "," + y + ")");
                spawnMissiles();
                isAttacking = false;
                enemyService.chooseTimeBeforeAttack(this, MIN_TIME_BEFORE_AQUAMENTUS_ATTACK, MAX_TIME_BEFORE_AQUAMENTUS_ATTACK);
                currentAnimation = moveAnimation;
            }
        }
    }

    /**
     * Spawn 3 missiles
     */
    private void spawnMissiles() {
        Orientation realOrientation = orientation;
        orientation = enemyService.chooseTurretOrientation(this);
        Orientation[] otherMissileOrientations = orientation.getOrientationsBeside();
        enemyManager.spawnMissile(this);
        orientation = otherMissileOrientations[0];
        enemyManager.spawnMissile(this);
        orientation = otherMissileOrientations[1];
        enemyManager.spawnMissile(this);
        orientation = realOrientation;
    }
}
