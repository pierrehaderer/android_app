package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.WorldMapManager;
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

    private boolean initNotDone;
    private float timeBeforeFirstMove;

    private float pauseBeforeJump;
    private boolean isJumping;
    private float nextPositionX;
    private float nextPositionY;
    private float nextNextPositionX;
    private float nextNextPositionY;
    private int leftOrRight;
    private int nextLeftOrRight;
    private float distance;
    private float deltaX;
    private float deltaY;
    private int moveFunction;
    private int nextMoveFunction;

    protected Animation initAnimation;
    protected Animation waitAnimation;
    protected Animation prepareAnimation;
    protected Animation jumpAnimation;

    public Tektite(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        super(imagesEnemyWorldMap, g);
        initAnimations(g);
        initDestinationTree();
        initNotDone = true;
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        isJumping = false;
        computePauseBeforeNextJump();
        moveFunction = MOVE_FUNTION_MIDDLE;
        nextMoveFunction = MOVE_FUNTION_MIDDLE;
        isInvincible = true;
        life = 1;
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
        contactDamage = -0.5f;
        currentAnimation = initAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(Graphics g);

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
    public void update(float deltaTime, Graphics g, WorldMapManager worldMapManager) {
        // Init
        if (initNotDone) {
            initNotDone = false;
            nextPositionX = x;
            nextPositionY = y;
            chooseNextNextPosition();
            nextPositionX = nextNextPositionX;
            nextPositionY = nextNextPositionY;
            leftOrRight = nextLeftOrRight;
            moveFunction = nextMoveFunction;
            chooseNextNextPosition();
        }

        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
            if (timeBeforeFirstMove <= 0) {
                isContactLethal = true;
                isInvincible = false;
                currentAnimation = waitAnimation;
            }
        } else {
            if (!isJumping && pauseBeforeJump > 0) {
                pauseBeforeJump -= deltaTime;
                if (pauseBeforeJump < 80) {
                    currentAnimation = prepareAnimation;
                }
                if (pauseBeforeJump < 0) {
                    isJumping = true;
                    deltaX = 0;
                    deltaY = 0;
                    distance = 0;
                    currentAnimation = jumpAnimation;
                    computePauseBeforeNextJump();
                    chooseNextNextPosition();
                }
            }
            if (isJumping) {
                goToNextPosition(deltaTime);
            }
            currentAnimation.update(deltaTime);
        }
    }

    /**
     * Compute the next pause before the next jump
     */
    private void computePauseBeforeNextJump() {
        pauseBeforeJump = MIN_PAUSE_BEFORE_JUMP + (MAX_PAUSE_BEFORE_JUMP - MIN_PAUSE_BEFORE_JUMP) * (float) Math.random();
        pauseBeforeJump = (Math.random() < PROBABILITY_TO_JUMP_AGAIN) ? pauseBeforeJump / 10 : pauseBeforeJump;
    }

    /**
     * Move until the enemy has arrived at the next position or until remainingTime is consumed
     */
    private void goToNextPosition(float deltaTime) {
        applyMoveFunction(deltaTime * SPEED);
        if (x == nextPositionX && y == nextPositionY) {
            isJumping = false;
            nextPositionX = nextNextPositionX;
            nextPositionY = nextNextPositionY;
            leftOrRight = nextLeftOrRight;
            moveFunction = nextMoveFunction;
            waitAnimation.reset();
            currentAnimation = waitAnimation;
        }
    }

    /**
     * Randomly choose the next position to go
     */
    private void chooseNextNextPosition() {
        nextLeftOrRight = 1 - 2 * (int) (Math.floor(2 * Math.random())); // -1 or 1
        int decision1 = (int) (Math.floor(3 * Math.random()));
        int decision2 = 10 + (int) (Math.floor(2 * Math.random()));
        int decision3 = 10 + decision2;
        nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][0].x;
        nextNextPositionY = nextPositionY + destinationTree[decision1][0].y;
        nextMoveFunction = moveFunctionTree[decision1][0];
        if (isNextNextPositionNotValid()) {
            nextLeftOrRight = -1 * nextLeftOrRight;
            nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][0].x;
            nextNextPositionY = nextPositionY + destinationTree[decision1][0].y;
            nextMoveFunction = moveFunctionTree[decision1][0];
            if (isNextNextPositionNotValid()) {
                nextLeftOrRight = -1 * nextLeftOrRight;
                nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][decision2].x;
                nextNextPositionY = nextPositionY + destinationTree[decision1][decision2].y;
                nextMoveFunction = moveFunctionTree[decision1][decision2];
                if (isNextNextPositionNotValid()) {
                    nextLeftOrRight = -1 * nextLeftOrRight;
                    nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][decision2].x;
                    nextNextPositionY = nextPositionY + destinationTree[decision1][decision2].y;
                    nextMoveFunction = moveFunctionTree[decision1][decision2];
                    if (isNextNextPositionNotValid()) {
                        nextLeftOrRight = -1 * nextLeftOrRight;
                        nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][decision3].x;
                        nextNextPositionY = nextPositionY + destinationTree[decision1][decision3].y;
                        nextMoveFunction = moveFunctionTree[decision1][decision3];
                        if (isNextNextPositionNotValid()) {
                            nextLeftOrRight = -1 * nextLeftOrRight;
                            nextNextPositionX = nextPositionX + nextLeftOrRight * destinationTree[decision1][decision3].x;
                            nextNextPositionY = nextPositionY + destinationTree[decision1][decision3].y;
                            nextMoveFunction = moveFunctionTree[decision1][decision3];
                        }
                    }
                }
            }
        }
        Logger.debug("Tecktite decided to go to (" + nextNextPositionX + "," + nextNextPositionY + ")");
    }

    /**
     * Evaluate if the next next position is Valid
     */
    private boolean isNextNextPositionNotValid() {
        return LocationUtil.isTileAtBorder(nextNextPositionX, nextNextPositionY + LocationUtil.TILE_SIZE)
                || LocationUtil.isTileAtBorder(nextNextPositionX + LocationUtil.TILE_SIZE, nextNextPositionY + LocationUtil.TILE_SIZE);
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
