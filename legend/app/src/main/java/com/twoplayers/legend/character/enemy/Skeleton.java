package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class Skeleton extends Enemy {

    private static final float PAUSE_BEFORE_FIRST_MOVE = 300f;
    private static final float PAUSE_BEFORE_ATTACK = 100f;
    private static final float MIN_TIME_BEFORE_ATTACK = 500.0f;
    private static final float MAX_TIME_BEFORE_ATTACK = 1000.0f;
    private static final float SPEED = 0.6f;

    private Orientation orientation;
    private Orientation nextOrientation;
    protected Map<Orientation, Animation> animations;

    private boolean initNotDone;
    private float timeBeforeFirstMove;
    private boolean isActive;
    private float timeBeforeAttack;
    private boolean isAttacking;
    private float immobilisationCounter;

    private float speed;
    private float nextTileX;
    private float nextTileY;
    private float nextNextTileX;
    private float nextNextTileY;

    public Skeleton(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        initNotDone = true;
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        isActive = false;
        isInvincible = true;
        chooseTimeBeforeAttack();
        isAttacking = false;
        life = getInitialLife();
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 3, 3, 11, 11);
        contactDamage = -0.5f;
        speed = getSpeed();
        currentAnimation = animations.get(Orientation.INIT);
    }

    /**
     * Init enemy animations
     */
    protected void initAnimations(Graphics g) {
        animations = new HashMap<>();

        EnemyUtil enemyUtil = new EnemyUtil();
        animations.put(Orientation.INIT, enemyUtil.getCloudAnimation(imagesEnemy, g));

        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemy.get("red_octorok_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("red_octorok_up_2"), AllImages.COEF, 15);
        animations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemy.get("red_octorok_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("red_octorok_down_2"), AllImages.COEF, 15);
        animations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemy.get("red_octorok_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesEnemy.get("red_octorok_left_2"), AllImages.COEF, 15);
        animations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemy.get("red_octorok_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesEnemy.get("red_octorok_right_2"), AllImages.COEF, 15);
        animations.put(Orientation.RIGHT, animationRight);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        // Init
        if (initNotDone) {
            initNotDone = false;
            nextTileX = x;
            nextTileY = y;
            Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
            nextNextTileX = destination.x;
            nextNextTileY = destination.y;
            nextOrientation = destination.orientation;
        }

        // Move hitbox away when enemy is dead
        if (isDead) {
            hitbox.x = 0;
            hitbox.y = 0;
        }

        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
            if (timeBeforeFirstMove <= 0) {
                isContactLethal = true;
                isInvincible = false;
                isActive = true;
            }
        } else {
            if (immobilisationCounter > 0) {
                immobilisationCounter -= deltaTime;
                if (immobilisationCounter <= 0) {
                    isContactLethal = true;
                }
            } else {
                if (!isAttacking) {
                    // The enemy moves
                    float remainingMoves = deltaTime * speed;
                    remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                    while (remainingMoves > 0) {
                        Logger.debug("Octorok is on a new Tile (" + x + "," + y + ")");
                        nextTileX = nextNextTileX;
                        nextTileY = nextNextTileY;
                        orientation = nextOrientation;
                        currentAnimation = animations.get(orientation);
                        Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
                        nextNextTileX = destination.x;
                        nextNextTileY = destination.y;
                        nextOrientation = destination.orientation;
                        remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                    }
                    timeBeforeAttack -= deltaTime;
                    if (timeBeforeAttack < PAUSE_BEFORE_ATTACK) {
                        isAttacking = true;
                    }
                } else {
                    // The enemy attacks
                    timeBeforeAttack -= deltaTime;
                    if (timeBeforeAttack < 0) {
                        Logger.info("Octorok is attacking (" + x + "," + y + ")");
                        isAttacking = false;
                        // TODO ATTACK !!!!!
                        chooseTimeBeforeAttack();
                    }
                }
            }
            currentAnimation.update(deltaTime);
        }

    }

    @Override
    public void isHitByBoomerang() {
        soundEffectManager.play("enemy_wounded");
        if (isActive) {
            immobilisationCounter = Enemy.INITIAL_IMMOBILISATION_COUNTER;
            isContactLethal = false;
        }
    }

    /**
     * Randomly choose a duration before the next attack
     */
    private void chooseTimeBeforeAttack() {
        timeBeforeAttack = (float) ((MAX_TIME_BEFORE_ATTACK - MIN_TIME_BEFORE_ATTACK) * Math.random() + MIN_TIME_BEFORE_ATTACK);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    /**
     * Obtain the speed of the Octorok
     */
    protected float getSpeed() {
        return SPEED;
    }

    /**
     * Obtain the initial life of the enemy
     */
    protected int getInitialLife() {
        return 2;
    }
}
