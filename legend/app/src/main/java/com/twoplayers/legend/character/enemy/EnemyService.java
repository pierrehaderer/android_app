package com.twoplayers.legend.character.enemy;

import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.LocationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twoplayers.legend.character.enemy.Enemy.INITIAL_INVINCIBLE_COUNT;

public class EnemyService {

    private static final float PROBABILITY_TO_KEEP_SAME_ORIENTATION = 0.6f;

    protected static final float INITIAL_PUSH_DISTANCE = 4 * LocationUtil.TILE_SIZE;
    protected static final float PUSH_SPEED = 9f;

    public static final float ATTACK_TOLERANCE = 2f;

    private IZoneManager zoneManager;
    private LinkManager linkManager;
    private SoundEffectManager soundEffectManager;

    private static Map<Orientation, Orientation[][]> directionTree;

    /**
     * Constructor
     */
    public EnemyService(IZoneManager zoneManager, LinkManager linkManager, SoundEffectManager soundEffectManager) {
        this.zoneManager = zoneManager;
        this.soundEffectManager = soundEffectManager;
        this.linkManager = linkManager;
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
    public float goToNextTile(Orientation orientation, MoveOnTileEnemy enemy, float remainingMoves, float nextTileX, float nextTileY) {
        boolean nextTileIsReachable = false;
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
        if (nextTileIsReachable) {
            enemy.orientation = enemy.nextOrientation;
            enemy.currentAnimation = enemy.getMoveAnimation();
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

    /**
     * Get a spawn position
     */
    public Coordinate getSpawnPosition(EnemyToSpawn enemyToSpawn, Orientation orientation, int spawnCounter) {
        if (enemyToSpawn.mode == SpawnMode.RANDOM) {
            return zoneManager.findSpawnableCoordinate();
        }
        if (enemyToSpawn.spawnPossibilities.containsKey(orientation)) {
            int index = (spawnCounter / 2) % enemyToSpawn.spawnPossibilities.get(orientation).size();
            return enemyToSpawn.spawnPossibilities.get(orientation).get(index);
        }
        return new Coordinate(LocationUtil.getXFromGrid(1), LocationUtil.getYFromGrid(1));
    }

    /**
     * Remove the missiles that are not active anymore
     */
    public List<Missile> cleanMissiles(List<Missile> missiles, boolean cleanRequired) {
        if (cleanRequired) {
            List<Missile> newMissiles = new ArrayList<>();
            for (Missile missile : missiles) {
                if (missile.isActive) {
                    newMissiles.add(missile);
                }
            }
            return newMissiles;
        }
        return missiles;
    }

    /**
     * Handle when enemy has been hit
     */
    public void handleEnemyHasBeenHit(Enemy enemy, float deltaTime) {
        if (enemy.hasBeenHit) {
            Logger.info("Enemy " + this.getClass().getSimpleName() + " has been hit.");
            enemy.hasBeenHit = false;
            if (enemy.life <= 0) {
                Logger.info("Enemy " + this.getClass().getSimpleName() + " is dead.");
                // Move hitbox away when enemy is dead
                enemy.hitbox.x = 0;
                enemy.hitbox.y = 0;
                enemy.isDead = true;
                soundEffectManager.play("enemy_dies");
                enemy.currentAnimation = enemy.deathAnimation;
            } else {
                enemy.isInvincible = true;
                enemy.invicibleCounter = INITIAL_INVINCIBLE_COUNT;
                soundEffectManager.play("enemy_wounded");
            }
        }
        if (enemy.invicibleCounter >= 0) {
            enemy.invicibleCounter -= deltaTime;
            if (enemy.invicibleCounter < 0) {
                enemy.isInvincible = false;
            }
        }
    }

    public void handleEnemyIsPushed(MoveOnTileEnemy enemy, float deltaTime) {
        // The enemy is pushed
        if (!enemy.isDead && enemy.isPushed) {
            Logger.info("Enemy is pushed, remaining counter : " + enemy.pushCounter);
            float distance = Math.min(deltaTime * PUSH_SPEED, enemy.pushCounter);
            enemy.pushCounter -= distance;

            float deltaY = enemy.pushY * distance;
            boolean pushed = false;
            if ((deltaY < 0 && zoneManager.isUpValid(enemy.x, enemy.y + deltaY)) || (deltaY > 0 && zoneManager.isDownValid(enemy.x, enemy.y + deltaY))) {
                pushed = true;
                enemy.y += deltaY;
                enemy.hitbox.y += deltaY;
            }
            float deltaX = enemy.pushX * distance;
            if ((deltaX < 0 && zoneManager.isLeftValid(enemy.x + deltaX, enemy.y)) || (deltaX > 0 && zoneManager.isRightValid(enemy.x + deltaX, enemy.y))) {
                pushed = true;
                enemy.x += deltaX;
                enemy.hitbox.x += deltaX;
            }
            // Stop pushing if there is an obstacle or if the counter is down to 0
            if (!pushed || enemy.pushCounter <= 0) {
                enemy.isPushed = false;
            }
        }
    }

    public void handleEnemyIsWounded(MoveOnTileEnemy enemy, int damage, Hitbox hitbox, Orientation orientation) {
        if (enemy.getClass() == AttackingEnemy.class) {
            if (((AttackingEnemy) enemy).isAttacking && !enemy.isPushed) {
                float deltaX = enemy.x - LocationUtil.getXFromGrid(LocationUtil.getTileXFromPositionX(enemy.x));
                float deltaY = enemy.x - LocationUtil.getYFromGrid(LocationUtil.getTileYFromPositionY(enemy.y));
                if (deltaX < ATTACK_TOLERANCE && deltaY < ATTACK_TOLERANCE) {
                    enemy.isPushed = true;
                    enemy.pushCounter = INITIAL_PUSH_DISTANCE;
                    Float[] pushDirections = LocationUtil.computePushDirections(hitbox, enemy.hitbox, orientation);
                    enemy.pushX = pushDirections[0];
                    enemy.pushY = pushDirections[1];
                    Logger.info("Enemy push direction : " + enemy.pushX + ", " + enemy.pushY);
                }
            }
        } else {
            if (enemy.orientation.isSameAs(orientation)) {
                enemy.isPushed = true;
                enemy.pushCounter = INITIAL_PUSH_DISTANCE;
                Float[] pushDirections = LocationUtil.computePushDirections(hitbox, enemy.hitbox, enemy.orientation);
                enemy.pushX = pushDirections[0];
                enemy.pushY = pushDirections[1];
                Logger.info("Enemy push direction : " + enemy.pushX + ", " + enemy.pushY);
            }
        }
    }


    /**
     * Choose the orinetation according to link position
     */
    public Orientation chooseOrientation(Enemy enemy) {
        Orientation orientation = Orientation.UP;
        float deltaX = enemy.x - linkManager.getLink().x;
        float deltaY = enemy.y - linkManager.getLink().y;
        float ratio = 0;
        if (deltaX == 0) {
            if (deltaY > 0) {
                orientation = Orientation.UP;
            } else {
                orientation = Orientation.DOWN;
            }
        } else {
            ratio = deltaY / deltaX;
            if (deltaX < 0 && deltaY >= 0) {
                if (ratio > -1/4f) {
                    orientation = Orientation.RIGHT;
                } else if (ratio > -3/4f) {
                    orientation = Orientation.DEGREES_340;
                } else if (ratio > -4/3f) {
                    orientation = Orientation.DEGREES_315;
                } else if (ratio > -4f) {
                    orientation = Orientation.DEGREES_290;
                } else {
                    orientation = Orientation.UP;
                }
            } else if (deltaX > 0 && deltaY >= 0) {
                if (ratio < 1/4f) {
                    orientation = Orientation.LEFT;
                } else if (ratio < 3/4f) {
                    orientation = Orientation.DEGREES_200;
                } else if (ratio < 4/3f) {
                    orientation = Orientation.DEGREES_225;
                } else if (ratio < 4f) {
                    orientation = Orientation.DEGREES_250;
                } else {
                    orientation = Orientation.UP;
                }
            } else if (deltaX > 0 && deltaY < 0) {
                if (ratio > -1/4f) {
                    orientation = Orientation.LEFT;
                } else if (ratio > -3/4f) {
                    orientation = Orientation.DEGREES_160;
                } else if (ratio > -4/3f) {
                    orientation = Orientation.DEGREES_135;
                } else if (ratio > -4f) {
                    orientation = Orientation.DEGREES_110;
                } else {
                    orientation = Orientation.DOWN;
                }
            } else if (deltaX < 0 && deltaY < 0) {
                if (ratio < 1/4f) {
                    orientation = Orientation.RIGHT;
                } else if (ratio < 3/4f) {
                    orientation = Orientation.DEGREES_20;
                } else if (ratio < 4/3f) {
                    orientation = Orientation.DEGREES_45;
                } else if (ratio < 4f) {
                    orientation = Orientation.DEGREES_70;
                } else {
                    orientation = Orientation.DOWN;
                }
            }
        }
        Logger.info("deltaX=" + deltaX + ",deltaY=" + deltaY + ",ratio=" + ratio + ",orientation=" + orientation.toString());
        return orientation;
    }

}
