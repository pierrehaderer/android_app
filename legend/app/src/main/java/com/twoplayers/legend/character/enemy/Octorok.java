package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class Octorok extends Enemy {

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
    protected Map<Orientation, Animation> animations;
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

    /**
     * Initialize the tree of direction which will ease direction decision
     */
    private void initDirectionTree() {
        if(directionTree == null) {
            directionTree = new HashMap<>();
            Orientation[][] orientationUp = new Orientation[3][22];
            orientationUp[0][0] = Orientation.DOWN;
            orientationUp[1][0] = Orientation.LEFT;
            orientationUp[2][0] = Orientation.RIGHT;
            orientationUp[0][10] = Orientation.LEFT;
            orientationUp[0][11] = Orientation.RIGHT;
            orientationUp[1][10] = Orientation.DOWN;
            orientationUp[1][11] = Orientation.RIGHT;
            orientationUp[2][10] = Orientation.DOWN;
            orientationUp[2][11] = Orientation.LEFT;
            orientationUp[0][20] = Orientation.RIGHT;
            orientationUp[0][21] = Orientation.LEFT;
            orientationUp[1][20] = Orientation.RIGHT;
            orientationUp[1][21] = Orientation.DOWN;
            orientationUp[2][20] = Orientation.LEFT;
            orientationUp[2][21] = Orientation.DOWN;
            directionTree.put(Orientation.UP, orientationUp);
            Orientation[][] orientationDown = new Orientation[3][22];
            orientationDown[0][0] = Orientation.UP;
            orientationDown[1][0] = Orientation.LEFT;
            orientationDown[2][0] = Orientation.RIGHT;
            orientationDown[0][10] = Orientation.LEFT;
            orientationDown[0][11] = Orientation.RIGHT;
            orientationDown[1][10] = Orientation.UP;
            orientationDown[1][11] = Orientation.RIGHT;
            orientationDown[2][10] = Orientation.UP;
            orientationDown[2][11] = Orientation.LEFT;
            orientationDown[0][20] = Orientation.RIGHT;
            orientationDown[0][21] = Orientation.LEFT;
            orientationDown[1][20] = Orientation.RIGHT;
            orientationDown[1][21] = Orientation.UP;
            orientationDown[2][20] = Orientation.LEFT;
            orientationDown[2][21] = Orientation.UP;
            directionTree.put(Orientation.DOWN, orientationDown);
            Orientation[][] orientationLeft = new Orientation[3][22];
            orientationLeft[0][0] = Orientation.UP;
            orientationLeft[1][0] = Orientation.DOWN;
            orientationLeft[2][0] = Orientation.RIGHT;
            orientationLeft[0][10] = Orientation.DOWN;
            orientationLeft[0][11] = Orientation.RIGHT;
            orientationLeft[1][10] = Orientation.UP;
            orientationLeft[1][11] = Orientation.RIGHT;
            orientationLeft[2][10] = Orientation.UP;
            orientationLeft[2][11] = Orientation.DOWN;
            orientationLeft[0][20] = Orientation.RIGHT;
            orientationLeft[0][21] = Orientation.DOWN;
            orientationLeft[1][20] = Orientation.RIGHT;
            orientationLeft[1][21] = Orientation.UP;
            orientationLeft[2][20] = Orientation.DOWN;
            orientationLeft[2][21] = Orientation.UP;
            directionTree.put(Orientation.LEFT, orientationLeft);
            Orientation[][] orientationRight = new Orientation[3][22];
            orientationRight[0][0] = Orientation.UP;
            orientationRight[1][0] = Orientation.DOWN;
            orientationRight[2][0] = Orientation.LEFT;
            orientationRight[0][10] = Orientation.DOWN;
            orientationRight[0][11] = Orientation.LEFT;
            orientationRight[1][10] = Orientation.UP;
            orientationRight[1][11] = Orientation.LEFT;
            orientationRight[2][10] = Orientation.UP;
            orientationRight[2][11] = Orientation.DOWN;
            orientationRight[0][20] = Orientation.LEFT;
            orientationRight[0][21] = Orientation.DOWN;
            orientationRight[1][20] = Orientation.LEFT;
            orientationRight[1][21] = Orientation.UP;
            orientationRight[2][20] = Orientation.DOWN;
            orientationRight[2][21] = Orientation.UP;
            directionTree.put(Orientation.RIGHT, orientationRight);
        }
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
            if (timeBeforeFirstMove <= 0) {
                isContactLethal = true;
                isInvincible = false;
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
                hitbox.y = y + hitbox.y_offset;
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
                hitbox.y = y + hitbox.y_offset;
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
                hitbox.x = x + hitbox.x_offset;
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
                hitbox.x = x + hitbox.x_offset;
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
        int direction2 = 10 + (int) (Math.floor(2 * Math.random()));
        int direction3 = 10 + direction2;
        Orientation orientation1 = directionTree.get(orientation)[direction1][0];
        if (tryToChooseThisOrientationForNextNextTile(orientation1, worldMapManager)) return;
        Orientation orientation2 = directionTree.get(orientation)[direction1][direction2];
        if (tryToChooseThisOrientationForNextNextTile(orientation2, worldMapManager)) return;
        Orientation orientation3 = directionTree.get(orientation)[direction1][direction3];
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
                if ((LocationUtil.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)
                        || !LocationUtil.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE))
                        && worldMapManager.isTileWalkable(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE, false)) {
                    nextNextTileX = nextTileX;
                    nextNextTileY = nextTileYCandidate;
                    nextOrientation = chosenOrientation;
                    return true;
                }
                break;
            case LEFT:
            case RIGHT:
                if ((LocationUtil.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)
                        || !LocationUtil.isTileAtBorder(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE))
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

    /**
     * Obtain the speed of the Octorok
     */
    protected abstract float getSpeed();

    /**
     * Obtain the initial life of the enemy
     */
    protected abstract int getInitialLife();

}
