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

public class Octorok extends Enemy {

    public static final float RED_SPEED = 0.6f;
    public static final float BLUE_SPEED = 1.1f;
    private static final float PAUSE_BEFORE_FIRST_MOVE = 100f;
    private static final float PAUSE_BEFORE_ATTACK = 100f;
    private static final float MIN_TIME_BEFORE_ATTACK = 500.0f;
    private static final float MAX_TIME_BEFORE_ATTACK = 1000.0f;

    private float timeBeforeAttack;
    private float timeBeforeFirstMove;
    private boolean isAttacking;

    public Orientation orientation;
    public Orientation nextOrientation;
    private float speed;
    private float remainingMoves;
    private float nextTileX;
    private float nextTileY;
    private float nextNextTileX;
    private float nextNextTileY;

    public Octorok(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        super(imagesEnemyWorldMap, g);
        initMoveAnimation(g);
        speed = RED_SPEED;
        isAttacking = false;
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        currentAnimation = animations.get(orientation);
        timeBeforeFirstMove = PAUSE_BEFORE_FIRST_MOVE;
    }

    /**
     * Initialise the move animations
     */
    private void initMoveAnimation(Graphics g) {
        animations = new HashMap<>();
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
        if (timeBeforeFirstMove == PAUSE_BEFORE_FIRST_MOVE) {
            nextTileX = x;
            nextTileY = y;
            chooseNextNextTile(worldMapManager);
            chooseTimeBeforeAttack();
        }

        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
        } else {
            if (!isAttacking) {
                // The enemy moves
                remainingMoves = deltaTime * speed;
                goToNextTile();
                while (remainingMoves > 0) {
                    Logger.debug("Octorok is on a new Tile (" + x + "," + y + ", " + timeBeforeAttack + ", " + timeBeforeFirstMove + ")");
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
                    Logger.debug("Octorok is attacking (" + x + "," + y + ", " + timeBeforeAttack + ", " + timeBeforeFirstMove + ")");
                    isAttacking = false;
                    // TODO ATTACK !!!!!
                    chooseTimeBeforeAttack();
                }
            }
        }

        currentAnimation.update(deltaTime);
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
        int direction = (int) Math.floor(4 * Math.random());
        switch (direction) {
            case 0:
                if (tryToChooseUpForNextNextTile(worldMapManager)) return;
                if (tryToChooseDownForNextNextTile(worldMapManager)) return;
                if (tryToChooseLeftForNextNextTile(worldMapManager)) return;
                if (tryToChooseRightForNextNextTile(worldMapManager)) return;
            case 1:
                if (tryToChooseDownForNextNextTile(worldMapManager)) return;
                if (tryToChooseLeftForNextNextTile(worldMapManager)) return;
                if (tryToChooseRightForNextNextTile(worldMapManager)) return;
                if (tryToChooseUpForNextNextTile(worldMapManager)) return;
            case 2:
                if (tryToChooseLeftForNextNextTile(worldMapManager)) return;
                if (tryToChooseRightForNextNextTile(worldMapManager)) return;
                if (tryToChooseUpForNextNextTile(worldMapManager)) return;
                if (tryToChooseDownForNextNextTile(worldMapManager)) return;
            case 3:
                if (tryToChooseRightForNextNextTile(worldMapManager)) return;
                if (tryToChooseUpForNextNextTile(worldMapManager)) return;
                if (tryToChooseDownForNextNextTile(worldMapManager)) return;
                if (tryToChooseLeftForNextNextTile(worldMapManager)) return;
            default:
                // Should never happen, it means monster is stuck somewhere
                remainingMoves = 0;
        }
    }

    /**
     * Try to choose up. +2 to be sure we are not at the boundaries of 2 tiles.
     */
    private boolean tryToChooseUpForNextNextTile(WorldMapManager worldMapManager) {
        if (worldMapManager.isTileWalkable(nextTileX + 2, nextTileY - LocationUtil.TILE_SIZE + 2, false)) {
            nextNextTileX = nextTileX;
            nextNextTileY = nextTileY - LocationUtil.TILE_SIZE;
            nextOrientation = Orientation.UP;
            return true;
        }
        return false;
    }

    /**
     * Try to choose down. +2 to be sure we are not at the boundaries of 2 tiles.
     */
    private boolean tryToChooseDownForNextNextTile(WorldMapManager worldMapManager) {
        // Try to go down. +2 to be sure we are not at the boundaries of 2 tiles.
        if (worldMapManager.isTileWalkable(nextTileX + 2, nextTileY + LocationUtil.TILE_SIZE + 2, false)) {
            nextNextTileX = nextTileX;
            nextNextTileY = nextTileY + LocationUtil.TILE_SIZE;
            nextOrientation = Orientation.DOWN;
            return true;
        }
        return false;
    }

    /**
     * Try to choose left. +2 to be sure we are not at the boundaries of 2 tiles.
     */
    private boolean tryToChooseLeftForNextNextTile(WorldMapManager worldMapManager) {
        // Try to go left. +2 to be sure we are not at the boundaries of 2 tiles.
        if (worldMapManager.isTileWalkable(nextTileX - LocationUtil.TILE_SIZE + 2, nextTileY + 2, false)) {
            nextNextTileX = nextTileX - LocationUtil.TILE_SIZE;
            nextNextTileY = nextTileY;
            nextOrientation = Orientation.LEFT;
            return true;
        }
        return false;
    }

    /**
     * Try to choose right. +2 to be sure we are not at the boundaries of 2 tiles.
     */
    private boolean tryToChooseRightForNextNextTile(WorldMapManager worldMapManager) {
        if (worldMapManager.isTileWalkable(nextTileX + LocationUtil.TILE_SIZE + 2, nextTileY + 2, false)) {
            nextNextTileX = nextTileX + LocationUtil.TILE_SIZE;
            nextNextTileY = nextTileY;
            nextOrientation = Orientation.RIGHT;
            return true;
        }
        return false;
    }
}
