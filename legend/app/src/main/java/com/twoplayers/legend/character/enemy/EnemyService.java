package com.twoplayers.legend.character.enemy;

import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.LocationUtil;

import java.util.HashMap;
import java.util.Map;

public class EnemyService {

    private static final float PROBABILITY_TO_KEEP_SAME_ORIENTATION = 0.6f;

    private IZoneManager zoneManager;

    private static Map<Orientation, Orientation[][]> directionTree;

    /**
     * Constructor
     */
    public EnemyService(IZoneManager zoneManager) {
        this.zoneManager = zoneManager;
        initDirectionTree();
    }

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

    /**
     * Move until the enemy has arrived at the next tile or until remainingMoves is consumed
     */
    public float goToNextTile(Orientation orientation, Enemy enemy, float remainingMoves, float nextTileX, float nextTileY) {
        boolean nextTileIsReachable;
        switch (orientation) {
            case UP:
                nextTileIsReachable = (enemy.y - remainingMoves < nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.y - nextTileY);
                    enemy.y = nextTileY;
                } else {
                    enemy.y -= remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.y = enemy.y + enemy.hitbox.y_offset;
                break;
            case DOWN:
                nextTileIsReachable = (enemy.y + remainingMoves > nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (nextTileY - enemy.y);
                    enemy.y = nextTileY;
                } else {
                    enemy.y += remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.y = enemy.y + enemy.hitbox.y_offset;
                break;
            case LEFT:
                nextTileIsReachable = (enemy.x - remainingMoves < nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.x - nextTileX);
                    enemy.x = nextTileX;
                } else {
                    enemy.x -= remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.x = enemy.x + enemy.hitbox.x_offset;
                break;
            case RIGHT:
                nextTileIsReachable = (enemy.x + remainingMoves > nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (nextTileX - enemy.x);
                    enemy.x = nextTileX;
                } else {
                    enemy.x += remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.x = enemy.x + enemy.hitbox.x_offset;
                break;
            default:
                remainingMoves = 0;
        }

        return remainingMoves;
    }

    /**
     * Randomly choose the next tile to go
     */
    public Destination chooseNextNextTile(Orientation orientation, float nextTileX, float nextTileY) {
        // Continue in the same direction if possible
        if (Math.random() < PROBABILITY_TO_KEEP_SAME_ORIENTATION) {
            Destination destination = tryToChooseThisOrientationForNextNextTile(orientation, nextTileX, nextTileY);
            if (destination.isValid) {
                return destination;
            }
        }
        // Either the enemy has chosen to change direction or continue is not possible
        int direction1 = (int) (Math.floor(3 * Math.random()));
        int direction2 = 10 + (int) (Math.floor(2 * Math.random()));
        int direction3 = 10 + direction2;
        Orientation orientation1 = directionTree.get(orientation)[direction1][0];
        Destination destination1 = tryToChooseThisOrientationForNextNextTile(orientation1, nextTileX, nextTileY);
        if (destination1.isValid) {
            return destination1;
        }
        Orientation orientation2 = directionTree.get(orientation)[direction1][direction2];
        Destination destination2 = tryToChooseThisOrientationForNextNextTile(orientation2, nextTileX, nextTileY);
        if (destination2.isValid) {
            return destination2;
        }
        Orientation orientation3 = directionTree.get(orientation)[direction1][direction3];
        Destination destination3 = tryToChooseThisOrientationForNextNextTile(orientation3, nextTileX, nextTileY);
        if (destination3.isValid) {
            return destination3;
        }
        return new Destination(0, 0, Orientation.UP, false);
    }

    /**
     * Try to choose an orientation. +HALF_TILE_SIZE to be sure we are not at the boundaries of 2 tiles.
     */
    private Destination tryToChooseThisOrientationForNextNextTile(Orientation chosenOrientation, float nextTileX, float nextTileY) {
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
                        && zoneManager.isTileWalkable(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE)) {
                    return new Destination(nextTileX, nextTileYCandidate, chosenOrientation, true);
                }
                break;
            case LEFT:
            case RIGHT:
                if ((LocationUtil.isTileAtBorder(nextTileX + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)
                        || !LocationUtil.isTileAtBorder(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE))
                        && zoneManager.isTileWalkable(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, nextTileY + LocationUtil.HALF_TILE_SIZE)) {
                    return new Destination(nextTileXCandidate, nextTileY, chosenOrientation, true);
                }
                break;
        }
        return new Destination(0, 0, Orientation.UP, false);
    }
}
