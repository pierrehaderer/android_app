package com.twoplayers.legend.character.enemy.worldmap;

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
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public abstract class Tektite extends Enemy {

    private static final float PAUSE_BEFORE_FIRST_MOVE = 200f;
    private static final float MIN_PAUSE_BEFORE_JUMP = 50f;
    private static final float MAX_PAUSE_BEFORE_JUMP = 400f;
    private static final float PROBABILITY_TO_JUMP_AGAIN = 0.5f;
    private static final float SPEED = 1.4f;

    private static final int MOVE_FUNTION_DOWN = 0;
    private static final int MOVE_FUNTION_MIDDLE = 1;
    private static final int MOVE_FUNTION_UP = 2;

    private static Coordinate[][] destinationTree;
    private static int[][] moveFunctionTree;

    private float nextPositionX;
    private float nextPositionY;
    private float pauseBeforeJump;
    private boolean isJumping;
    private int leftOrRight;
    private float distance;
    private float deltaX;
    private float deltaY;
    private int moveFunction;

    protected Animation waitAnimation;
    protected Animation prepareAnimation;
    protected Animation jumpAnimation;

    public Tektite(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImages imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        initDestinationTree();
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        isJumping = true;
        pauseBeforeJump = 0;
        moveFunction = MOVE_FUNTION_MIDDLE;
        life = 1;
        hitbox = new Hitbox(x, y, 3, 3, 11, 11);
        damage = -0.5f;
        nextPositionX = x;
        nextPositionY = y;
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(IImages imagesEnemy, Graphics g);

    /**
     * Initialize the tree of destination which will ease the randow choice of destination
     */
    private void initDestinationTree() {
        if (destinationTree == null) {
            Coordinate coordinate0 = new Coordinate(2 * LocationUtil.TILE_SIZE, 2 * LocationUtil.TILE_SIZE);
            Coordinate coordinate1 = new Coordinate(1.2f * LocationUtil.TILE_SIZE, -0.1f * LocationUtil.TILE_SIZE);
            Coordinate coordinate2 = new Coordinate(0.5f * LocationUtil.TILE_SIZE, -2.2f * LocationUtil.TILE_SIZE);
            destinationTree = new Coordinate[3][22];
            destinationTree[0][0] = coordinate0;
            destinationTree[1][0] = coordinate1;
            destinationTree[2][0] = coordinate2;
            destinationTree[0][10] = coordinate1;
            destinationTree[0][11] = coordinate2;
            destinationTree[1][10] = coordinate0;
            destinationTree[1][11] = coordinate2;
            destinationTree[2][10] = coordinate0;
            destinationTree[2][11] = coordinate1;
            destinationTree[0][20] = coordinate2;
            destinationTree[0][21] = coordinate1;
            destinationTree[1][20] = coordinate2;
            destinationTree[1][21] = coordinate0;
            destinationTree[2][20] = coordinate1;
            destinationTree[2][21] = coordinate0;
            moveFunctionTree = new int[3][22];
            moveFunctionTree[0][0] = MOVE_FUNTION_DOWN;
            moveFunctionTree[1][0] = MOVE_FUNTION_MIDDLE;
            moveFunctionTree[2][0] = MOVE_FUNTION_UP;
            moveFunctionTree[0][10] = MOVE_FUNTION_MIDDLE;
            moveFunctionTree[0][11] = MOVE_FUNTION_UP;
            moveFunctionTree[1][10] = MOVE_FUNTION_DOWN;
            moveFunctionTree[1][11] = MOVE_FUNTION_UP;
            moveFunctionTree[2][10] = MOVE_FUNTION_DOWN;
            moveFunctionTree[2][11] = MOVE_FUNTION_MIDDLE;
            moveFunctionTree[0][20] = MOVE_FUNTION_UP;
            moveFunctionTree[0][21] = MOVE_FUNTION_MIDDLE;
            moveFunctionTree[1][20] = MOVE_FUNTION_UP;
            moveFunctionTree[1][21] = MOVE_FUNTION_DOWN;
            moveFunctionTree[2][20] = MOVE_FUNTION_MIDDLE;
            moveFunctionTree[2][21] = MOVE_FUNTION_DOWN;
        }
    }

    @Override
    public void update(float deltaTime, Graphics g) {

        enemyService.handleEnemyAppears(this, deltaTime);
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsStunned(this, deltaTime);

        if (isActive && !isStunned && !isDead) {
            if (isJumping) {
                goToNextPosition(deltaTime);
            } else {
                pauseBeforeJump(deltaTime);
            }
            currentAnimation.update(deltaTime);
        }
    }

    /**
     * The enemy is waiting before he jumps
     */
    private void pauseBeforeJump(float deltaTime) {
        pauseBeforeJump -= deltaTime;
        if (pauseBeforeJump < 80) {
            currentAnimation = prepareAnimation;
        }
        if (pauseBeforeJump < 0) {
            // The enemy moves
            isJumping = true;
            deltaX = 0;
            deltaY = 0;
            distance = 0;
            currentAnimation = jumpAnimation;
        }
    }

    /**
     * Move until the enemy has arrived at the next position or until remainingTime is consumed
     */
    private void goToNextPosition(float deltaTime) {
        if (x == nextPositionX && y == nextPositionY) {
            isJumping = false;
            pauseBeforeJump = computePauseBeforeNextJump();
            chooseNextPosition();
            waitAnimation.reset();
            currentAnimation = waitAnimation;
        } else {
            applyMoveFunction(deltaTime * SPEED);
        }
    }

    /**
     * Compute the next pause before the next jump
     */
    private float computePauseBeforeNextJump() {
        float pause = MIN_PAUSE_BEFORE_JUMP + (MAX_PAUSE_BEFORE_JUMP - MIN_PAUSE_BEFORE_JUMP) * (float) Math.random();
        pause = (Math.random() < PROBABILITY_TO_JUMP_AGAIN) ? pause / 10 : pause;
        return pause;
    }

    /**
     * Randomly choose the next position to go
     */
    private void chooseNextPosition() {
        leftOrRight = 1 - 2 * (int) (Math.floor(2 * Math.random())); // -1 or 1
        int decision1 = (int) (Math.floor(3 * Math.random()));
        int decision2 = 10 + (int) (Math.floor(2 * Math.random()));
        int decision3 = 10 + decision2;
        nextPositionX = x + leftOrRight * destinationTree[decision1][0].x;
        nextPositionY = y + destinationTree[decision1][0].y;
        moveFunction = moveFunctionTree[decision1][0];
        if (nextPositionNotValid()) {
            leftOrRight = -1 * leftOrRight;
            nextPositionX = x + leftOrRight * destinationTree[decision1][0].x;
            nextPositionY = y + destinationTree[decision1][0].y;
            moveFunction = moveFunctionTree[decision1][0];
            if (nextPositionNotValid()) {
                leftOrRight = -1 * leftOrRight;
                nextPositionX = x + leftOrRight * destinationTree[decision1][decision2].x;
                nextPositionY = y + destinationTree[decision1][decision2].y;
                moveFunction = moveFunctionTree[decision1][decision2];
                if (nextPositionNotValid()) {
                    leftOrRight = -1 * leftOrRight;
                    nextPositionX = x + leftOrRight * destinationTree[decision1][decision2].x;
                    nextPositionY = y + destinationTree[decision1][decision2].y;
                    moveFunction = moveFunctionTree[decision1][decision2];
                    if (nextPositionNotValid()) {
                        leftOrRight = -1 * leftOrRight;
                        nextPositionX = x + leftOrRight * destinationTree[decision1][decision3].x;
                        nextPositionY = y + destinationTree[decision1][decision3].y;
                        moveFunction = moveFunctionTree[decision1][decision3];
                        if (nextPositionNotValid()) {
                            leftOrRight = -1 * leftOrRight;
                            nextPositionX = x + leftOrRight * destinationTree[decision1][decision3].x;
                            nextPositionY = y + destinationTree[decision1][decision3].y;
                            moveFunction = moveFunctionTree[decision1][decision3];
                        }
                    }
                }
            }
        }
        Logger.debug("Tecktite decided to go to (" + nextPositionX + "," + nextPositionY + ")");
    }

    /**
     * Evaluate if the next next position is Valid
     */
    private boolean nextPositionNotValid() {
        return LocationUtil.isTileAtBorder(nextPositionX, nextPositionY + LocationUtil.TILE_SIZE)
                || LocationUtil.isTileAtBorder(nextPositionX + LocationUtil.TILE_SIZE, nextPositionY + LocationUtil.TILE_SIZE);
    }

    /**
     * Get the new value of x after the application of deltaDistance on the provided function
     */
    private void applyMoveFunction(float deltaDistance) {
        float nextDeltaX = 0;
        float nextDeltaY = 0;
        float nextDistance = distance + deltaDistance;
        switch (moveFunction) {
            case MOVE_FUNTION_DOWN:
                // f(0)=0; f(15)=0.5; f(30)=1; f(60)=2
                if (nextDistance < 60) {
                    nextDeltaX = leftOrRight * 2 * LocationUtil.TILE_SIZE * nextDistance / 60;
                    x += nextDeltaX - deltaX;
                } else {
                    x = nextPositionX;
                }

                //f(0)=0; f(15)=-0.8; f(30)=0; f(60)=2
                if (nextDistance < 15) {
                    nextDeltaY = -0.8f * LocationUtil.TILE_SIZE * nextDistance / 15;
                    y += nextDeltaY - deltaY;
                } else if (nextDistance < 30) {
                    nextDeltaY = 0.8f * LocationUtil.TILE_SIZE * nextDistance / 15 - 1.6f * LocationUtil.TILE_SIZE;
                    y += nextDeltaY - deltaY;
                } else if (nextDistance < 60) {
                    nextDeltaY = 2 * LocationUtil.TILE_SIZE * nextDistance / 30 - 2 * LocationUtil.TILE_SIZE;
                    y += nextDeltaY - deltaY;
                } else {
                    y = nextPositionY;
                }
                break;
            case MOVE_FUNTION_MIDDLE:
                // f(0)=0; f(20)=0.6; f(40)=1.2
                if (nextDistance < 40) {
                    nextDeltaX = leftOrRight * 1.2f * LocationUtil.TILE_SIZE * nextDistance / 40;
                    x += nextDeltaX - deltaX;
                } else {
                    x = nextPositionX;
                }
                // f(0)=0; f(20)=-0.8; f(40)=-0.1
                if (nextDistance < 20) {
                    nextDeltaY = -0.8f * LocationUtil.TILE_SIZE * nextDistance / 20;
                    y += nextDeltaY - deltaY;
                } else if (nextDistance < 40) {
                    nextDeltaY = 0.7f * LocationUtil.TILE_SIZE * nextDistance / 20 - 1.5f * LocationUtil.TILE_SIZE;
                    y += nextDeltaY - deltaY;
                } else {
                    y = nextPositionY;
                }
                break;
            case MOVE_FUNTION_UP:
                // f(0)=0; f(25)=0.25; f(40)=0.5
                if (nextDistance < 25) {
                    nextDeltaX = leftOrRight * 0.25f * LocationUtil.TILE_SIZE * nextDistance / 25;
                    x += nextDeltaX - deltaX;
                } else if (nextDistance < 40) {
                    nextDeltaX = leftOrRight * LocationUtil.TILE_SIZE * (0.25f * nextDistance / 15 - 0.25f * 10 / 15);
                    x += nextDeltaX - deltaX;
                } else {
                    x = nextPositionX;
                }
                // f(0)=0; f(25)=-2.2; f(40)=-2.2
                if (nextDistance < 25) {
                    nextDeltaY = -2.2f * LocationUtil.TILE_SIZE * nextDistance / 25;
                    y += nextDeltaY - deltaY;
                } else if (nextDistance < 40) {
                    // nextDeltaY = LocationUtil.TILE_SIZE * (0.2f * nextDistance / 15 - 4.1f * 10 / 15); <= This is for f(25)=-2.4; f(40)=-2.2
                    nextDeltaY = LocationUtil.TILE_SIZE * -2.2f;
                    y += nextDeltaY - deltaY;
                } else {
                    y = nextPositionY;
                }
                break;
        }
        hitbox.relocate(x, y);
        deltaX = nextDeltaX;
        deltaY = nextDeltaY;
        distance = nextDistance;
    }
}
