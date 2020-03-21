package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class Octorok extends Enemy {

    private static final float PAUSE_BEFORE_FIRST_MOVE = 300f;
    private static final float PAUSE_BEFORE_ATTACK = 100f;
    private static final float MIN_TIME_BEFORE_ATTACK = 500.0f;
    private static final float MAX_TIME_BEFORE_ATTACK = 1000.0f;

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

    public Octorok(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
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
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
        contactDamage = -0.5f;
        speed = getSpeed();
        currentAnimation = animations.get(Orientation.INIT);
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(Graphics g);

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
    protected abstract float getSpeed();

    /**
     * Obtain the initial life of the enemy
     */
    protected abstract int getInitialLife();

}
