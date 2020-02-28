package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class Octorok extends Enemy {

    public static final float RED_SPEED = 0.6f;
    public static final float BLUE_SPEED = 1.1f;
    private static final float PAUSE_BEFORE_FIRST_MOVE = 300f;
    private static final float PAUSE_BEFORE_ATTACK = 100f;
    private static final float MIN_TIME_BEFORE_ATTACK = 500.0f;
    private static final float MAX_TIME_BEFORE_ATTACK = 1000.0f;

    private static Map<Orientation, Orientation[][]> directionTree;

    private boolean initNotDone;
    private float timeBeforeFirstMove;
    private float timeBeforeAttack;
    private boolean isAttacking;

    private Orientation orientation;
    private Orientation nextOrientation;
    private float speed;
    private float remainingMoves;
    private float nextTileX;
    private float nextTileY;
    private float nextNextTileX;
    private float nextNextTileY;

    public Octorok(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        super(imagesEnemyWorldMap, g);
        initAnimations(g);
        initDirectionTree();
        initNotDone = true;
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        chooseTimeBeforeAttack();
        isAttacking = false;
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        speed = RED_SPEED;
        currentAnimation = animations.get(Orientation.INIT);
    }

    /**
     * Initialize the tree of direction which will ease direction decision
     */
    private void initDirectionTree() {
        if(directionTree == null) {
            directionTree = new HashMap<>();
            Orientation[][] orientationUp = new Orientation[3][5];
            orientationUp[0][0] = Orientation.DOWN;
            orientationUp[1][0] = Orientation.LEFT;
            orientationUp[2][0] = Orientation.RIGHT;
            orientationUp[0][1] = Orientation.LEFT;
            orientationUp[0][2] = Orientation.RIGHT;
            orientationUp[1][1] = Orientation.DOWN;
            orientationUp[1][2] = Orientation.RIGHT;
            orientationUp[2][1] = Orientation.DOWN;
            orientationUp[2][2] = Orientation.LEFT;
            orientationUp[0][3] = Orientation.RIGHT;
            orientationUp[0][4] = Orientation.LEFT;
            orientationUp[1][3] = Orientation.RIGHT;
            orientationUp[1][4] = Orientation.DOWN;
            orientationUp[2][3] = Orientation.LEFT;
            orientationUp[2][4] = Orientation.DOWN;
            directionTree.put(Orientation.UP, orientationUp);
            Orientation[][] orientationDown = new Orientation[3][5];
            orientationDown[0][0] = Orientation.UP;
            orientationDown[1][0] = Orientation.LEFT;
            orientationDown[2][0] = Orientation.RIGHT;
            orientationDown[0][1] = Orientation.LEFT;
            orientationDown[0][2] = Orientation.RIGHT;
            orientationDown[1][1] = Orientation.UP;
            orientationDown[1][2] = Orientation.RIGHT;
            orientationDown[2][1] = Orientation.UP;
            orientationDown[2][2] = Orientation.LEFT;
            orientationDown[0][3] = Orientation.RIGHT;
            orientationDown[0][4] = Orientation.LEFT;
            orientationDown[1][3] = Orientation.RIGHT;
            orientationDown[1][4] = Orientation.UP;
            orientationDown[2][3] = Orientation.LEFT;
            orientationDown[2][4] = Orientation.UP;
            directionTree.put(Orientation.DOWN, orientationDown);
            Orientation[][] orientationLeft = new Orientation[3][5];
            orientationLeft[0][0] = Orientation.UP;
            orientationLeft[1][0] = Orientation.DOWN;
            orientationLeft[2][0] = Orientation.RIGHT;
            orientationLeft[0][1] = Orientation.DOWN;
            orientationLeft[0][2] = Orientation.RIGHT;
            orientationLeft[1][1] = Orientation.UP;
            orientationLeft[1][2] = Orientation.RIGHT;
            orientationLeft[2][1] = Orientation.UP;
            orientationLeft[2][2] = Orientation.DOWN;
            orientationLeft[0][3] = Orientation.RIGHT;
            orientationLeft[0][4] = Orientation.DOWN;
            orientationLeft[1][3] = Orientation.RIGHT;
            orientationLeft[1][4] = Orientation.UP;
            orientationLeft[2][3] = Orientation.DOWN;
            orientationLeft[2][4] = Orientation.UP;
            directionTree.put(Orientation.LEFT, orientationLeft);
            Orientation[][] orientationRight = new Orientation[3][5];
            orientationRight[0][0] = Orientation.UP;
            orientationRight[1][0] = Orientation.DOWN;
            orientationRight[2][0] = Orientation.LEFT;
            orientationRight[0][1] = Orientation.DOWN;
            orientationRight[0][2] = Orientation.LEFT;
            orientationRight[1][1] = Orientation.UP;
            orientationRight[1][2] = Orientation.LEFT;
            orientationRight[2][1] = Orientation.UP;
            orientationRight[2][2] = Orientation.DOWN;
            orientationRight[0][3] = Orientation.LEFT;
            orientationRight[0][4] = Orientation.DOWN;
            orientationRight[1][3] = Orientation.LEFT;
            orientationRight[1][4] = Orientation.UP;
            orientationRight[2][3] = Orientation.DOWN;
            orientationRight[2][4] = Orientation.UP;
            directionTree.put(Orientation.RIGHT, orientationRight);
        }
    }

    /**
     * Initialise the move animations
     */
    private void initAnimations(Graphics g) {
        animations = new HashMap<>();
        Animation animationNone = g.newAnimation();
        animationNone.addFrame(imagesEnemyWorldMap.get("cloud_1"), AllImages.COEF, 36);
        animationNone.addFrame(imagesEnemyWorldMap.get("cloud_2"), AllImages.COEF, 12);
        animationNone.addFrame(imagesEnemyWorldMap.get("cloud_3"), AllImages.COEF, 12);
        animations.put(Orientation.INIT, animationNone);
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemyWorldMap.get("red_octorok_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemyWorldMap.get("red_octorok_up_2"), AllImages.COEF, 15);
        animations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemyWorldMap.get("red_octorok_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemyWorldMap.get("red_octorok_down_2"), AllImages.COEF, 15);
        animations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemyWorldMap.get("red_octorok_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesEnemyWorldMap.get("red_octorok_left_2"), AllImages.COEF, 15);
        animations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemyWorldMap.get("red_octorok_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesEnemyWorldMap.get("red_octorok_right_2"), AllImages.COEF, 15);
        animations.put(Orientation.RIGHT, animationRight);
    }

    @Override
    public void update(float deltaTime, Graphics g, WorldMapManager worldMapManager) {
        // Init
        if (initNotDone) {
            initNotDone = false;
            nextTileX = x;
            nextTileY = y;
            chooseNextNextTile(worldMapManager);
        }

        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
        } else {
            if (!isAttacking) {
                // The enemy moves
                remainingMoves = deltaTime * speed;
                goToNextTile();
                while (remainingMoves > 0) {
                    Logger.debug("Octorok is on a new Tile (" + x + "," + y + ")");
                    nextTileX = nextNextTileX;
                    nextTileY = nextNextTileY;
                    orientation = nextOrientation;
                    currentAnimation = animations.get(orientation);
                    chooseNextNextTile(worldMapManager);
                    goToNextTile();
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
            currentAnimation.update(deltaTime);
        }

    }

    /**
     * Randomly choose a duration before the next attack
     */
    private void chooseTimeBeforeAttack() {
        timeBeforeAttack = (float) ((MAX_TIME_BEFORE_ATTACK - MIN_TIME_BEFORE_ATTACK) * Math.random() + MIN_TIME_BEFORE_ATTACK);
    }

    /**
     * Move until the enemy has arrived at the next tile or until remainingMoves is consumed
     */
    private void goToNextTile() {
        boolean nextTileIsReachable;
        switch (orientation) {
            case UP:
                nextTileIsReachable = (y - remainingMoves < nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (y - nextTileY);
                    y = nextTileY;
                } else {
                    y -= remainingMoves;
                    remainingMoves = 0;
                }
                break;
            case DOWN:
                nextTileIsReachable = (y + remainingMoves > nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (nextTileY - y);
                    y = nextTileY;
                } else {
                    y += remainingMoves;
                    remainingMoves = 0;
                }
                break;
            case LEFT:
                nextTileIsReachable = (x - remainingMoves < nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (x - nextTileX);
                    x = nextTileX;
                } else {
                    x -= remainingMoves;
                    remainingMoves = 0;
                }
                break;
            case RIGHT:
                nextTileIsReachable = (x + remainingMoves > nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (nextTileX - x);
                    x = nextTileX;
                } else {
                    x += remainingMoves;
                    remainingMoves = 0;
                }
                break;
        }
    }

    /**
     * Randomly choose the next tile to go
     */
    private void chooseNextNextTile(WorldMapManager worldMapManager) {
        // 1 chance out of 2 to continue if possible.
        if (Math.random() > 0.5) {
            if (tryToChooseThisOrientationForNextNextTile(orientation, worldMapManager)) return;
        }
        // Either the enemy has chosen to change direction or continue is not possible
        int direction1 = (int) (Math.floor(3 * Math.random()));
        int direction2 = (int) (Math.floor(2 * Math.random()));
        Orientation orientation1 = directionTree.get(orientation)[direction1][0];
        if (tryToChooseThisOrientationForNextNextTile(orientation1, worldMapManager)) return;
        Orientation orientation2 = directionTree.get(orientation)[direction1][direction2 + 1];
        if (tryToChooseThisOrientationForNextNextTile(orientation2, worldMapManager)) return;
        Orientation orientation3 = directionTree.get(orientation)[direction1][direction2 + 3];
        if (tryToChooseThisOrientationForNextNextTile(orientation3, worldMapManager)) return;

        // Should never happen, it means monster is stuck somewhere
        remainingMoves = 0;
    }

    /**
     * Try to choose an orientation. +HALF_TILE_SIZE to be sure we are not at the boundaries of 2 tiles.
     */
    private boolean tryToChooseThisOrientationForNextNextTile(Orientation chosenOrientation, WorldMapManager worldMapManager) {
        float nextTileXCandidate = nextTileX;
        float nextTileYCandidate = nextTileY;
        switch (chosenOrientation) {
            case UP:
                nextTileYCandidate = nextTileY - LocationUtil.TILE_SIZE;
                break;
            case DOWN:
                nextTileYCandidate = nextTileY + LocationUtil.TILE_SIZE;
                break;
            case LEFT:
                nextTileXCandidate = nextTileX - LocationUtil.TILE_SIZE;
                break;
            case RIGHT:
                nextTileXCandidate = nextTileX + LocationUtil.TILE_SIZE;
        }
        switch (chosenOrientation) {
            case UP:
            case DOWN:
                if ((worldMapManager.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)
                        || !worldMapManager.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE))
                        && worldMapManager.isTileWalkable(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE, false)) {
                    nextNextTileX = nextTileX;
                    nextNextTileY = nextTileYCandidate;
                    nextOrientation = chosenOrientation;
                    return true;
                }
                break;
            case LEFT:
            case RIGHT:
                if ((worldMapManager.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)
                        || !worldMapManager.isTileAtBorder(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE))
                        && worldMapManager.isTileWalkable(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE, false)) {
                    nextNextTileX = nextTileXCandidate;
                    nextNextTileY = nextTileY;
                    nextOrientation = chosenOrientation;
                    return true;
                }
                break;
        }
        return false;
    }

}
