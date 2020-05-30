package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Orientation;

public abstract class Keese extends Enemy {

    private static final float MIN_TIME_BEFORE_STOP = 200f;
    private static final float MAX_TIME_BEFORE_STOP = 1200f;
    private static final float INITIAL_TIME_BEFORE_RESTART = 200f;
    private static final int MAX_DISTANCE = 12;
    private static final float MAX_SPEED = 1.2f;
    private static final float ACCELERATION = 0.005f;

    private float timeBeforeStop;
    private float timeBeforeRestart;

    private float distance;

    private boolean isStopping;
    private boolean isStarting;

    protected Animation moveAnimation;

    public Keese(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImages imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        timeBeforeFirstMove = DungeonEnemyManager.TIME_BEFORE_FIRST_MOVE;
        timeBeforeStop = 0;
        timeBeforeRestart = 0;
        isStarting = true;
        isStopping = false;
        life = 1;
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        damage = -0.5f;
        orientation = Orientation.getRandomOrientation(Math.random());
        chooseNextDestination();
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(IImages imagesEnemy, Graphics g);

    @Override
    public void update(float deltaTime, Graphics g) {
        handleKeeseAppears(deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);

        if (isActive) {
            if (isStarting) {
                speed += ACCELERATION * deltaTime;
                goStraightAhead(deltaTime);
                if (speed >= MAX_SPEED) {
                    isStarting = false;
                    timeBeforeStop = getInitialTimeBeforeStop();
                }
            }
            if (timeBeforeRestart > 0) {
                timeBeforeRestart -= deltaTime;
                if (timeBeforeRestart <= 0) {
                    isStarting = true;
                }
            }
            if (isStopping) {
                speed -= ACCELERATION * deltaTime;
                goStraightAhead(deltaTime);
                if (speed <= 0) {
                    speed = 0;
                    isStopping = false;
                    timeBeforeRestart = INITIAL_TIME_BEFORE_RESTART;
                }
            }
            if (timeBeforeStop > 0) {
                timeBeforeStop -= deltaTime;
                goToNextDestination(deltaTime);
                if (timeBeforeStop <= 0) {
                    isStopping = true;
                    speed = MAX_SPEED;
                }
            }
        }
    }

    /**
     * Handle when enemy appears
     */
    private void handleKeeseAppears(float deltaTime) {
        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
            if (timeBeforeFirstMove <= 0) {
                isLethal = true;
                isActive = true;
                isInvincible = false;
                currentAnimation = moveAnimation;
            }
        }
    }

    /**
     * Enemy is moving straight ahead when accelarating or decelerating
     */
    private void goStraightAhead(float deltaTime) {
        currentAnimation.update(deltaTime * speed / MAX_SPEED);
        float distanceToDo = deltaTime * speed;
        float nextX = x + distanceToDo * (float) Math.cos(orientation.angle);
        float nextY = y - distanceToDo * (float) Math.sin(orientation.angle);
        if (shouldTurnAround(nextX, nextY)) {
            // It is time to turn around
            orientation = orientation.reverseOrientation();
            chooseNextDestination();
        } else {
            x = nextX;
            y = nextY;
            hitbox.relocate(x, y);
        }
    }

    /**
     * Move until the enemy has arrived at the next destination or until remainingTime is consumed
     */
    private void goToNextDestination(float deltaTime) {
        currentAnimation.update(deltaTime);
        float completeDistanceToDo = deltaTime * MAX_SPEED;
        while (completeDistanceToDo > 0) {
            float distanceToDo = Math.min(completeDistanceToDo, distance);
            float nextX = x + distanceToDo * (float) Math.cos(orientation.angle);
            float nextY = y - distanceToDo * (float) Math.sin(orientation.angle);
            if (shouldTurnAround(nextX, nextY)) {
                // It is time to turn around
                orientation = orientation.reverseOrientation();
                chooseNextDestination();
            } else {
                x = nextX;
                y = nextY;
                hitbox.relocate(x, y);
            }
            distance -= distanceToDo;
            completeDistanceToDo -= distanceToDo;
            if (distance <= 0) {
                // Destination has been reached, go to next destination
                chooseNextDestination();
            }
        }
    }

    /**
     * Check if enemy is to close from the wall and should turn around
     */
    private boolean shouldTurnAround(float nextX, float nextY) {
        return LocationUtil.isTileAtBorderMinusOne(nextX, nextY) || LocationUtil.isTileAtBorderMinusOne(nextX + LocationUtil.TILE_SIZE, nextY + LocationUtil.TILE_SIZE) ;
    }

    /**
     * Compute the time before the keeze stops
     */
    private float getInitialTimeBeforeStop() {
        return MIN_TIME_BEFORE_STOP + ((MAX_TIME_BEFORE_STOP - MIN_TIME_BEFORE_STOP) * (float) Math.random());
    }

    /**
     * Randomly choose the next orientation to go
     */
    private void chooseNextDestination() {
        int orientationChoice = (int) (Math.random() * 2);
        orientation = orientation.getOrientationsBesideAt45Degrees()[orientationChoice];
        distance = ((int) (Math.random() * MAX_DISTANCE)) * LocationUtil.HALF_TILE_SIZE;
    }

    @Override
    public void isHitByBoomerang() {
        enemyService.handleEnemyIsWounded(this, 1, new Hitbox(), Orientation.UP);
    }
}
