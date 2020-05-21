package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.LocationUtil;

import java.util.HashMap;
import java.util.Map;

public class EnemyService {

    private static final float PROBABILITY_TO_KEEP_SAME_ORIENTATION = 0.6f;
    private static final float INITIAL_PUSH_DISTANCE = 4 * LocationUtil.TILE_SIZE;
    private static final float PUSH_SPEED = 9f;

    private static final float ATTACK_TOLERANCE = 2f;

    private IZoneManager zoneManager;
    private LinkManager linkManager;
    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    private static Map<Orientation, Orientation[][]> directionTree;

    /**
     * Constructor
     */
    public EnemyService(IZoneManager zoneManager, LinkManager linkManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.zoneManager = zoneManager;
        this.linkManager = linkManager;
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
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
     * Generate a initial slow animation with cloud
     */
    public Animation getSlowCloudAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        return createCloudAnimation(imagesEnemy, g, 36);
    }

    /**
     * Generate a initial slow animation with cloud
     */
    public Animation getFastCloudAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        return createCloudAnimation(imagesEnemy, g, 12);
    }

    /**
     * Genearate death animation
     */
    public Animation getDeathAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemy.get("enemy_death_1"), AllImages.COEF, 10);
        animation.addFrame(imagesEnemy.get("enemy_death_2"), AllImages.COEF, 10);
        animation.addFrame(imagesEnemy.get("enemy_death_3"), AllImages.COEF, 10);
        animation.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animation.setOccurrences(1);
        return animation;
    }

    /**
     * Generate a initial animation with cloud
     */
    private Animation createCloudAnimation(IImagesEnemy imagesEnemy, Graphics g, int firstStepDuration) {
        Animation animationCloud = g.newAnimation();
        animationCloud.addFrame(imagesEnemy.get("cloud_1"), AllImages.COEF, firstStepDuration);
        animationCloud.addFrame(imagesEnemy.get("cloud_2"), AllImages.COEF, 12);
        animationCloud.addFrame(imagesEnemy.get("cloud_3"), AllImages.COEF, 12);
        animationCloud.setOccurrences(1);
        return animationCloud;
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
     * Handle when enemy appears
     */
    public void handleEnemyAppears(Enemy enemy, float deltaTime) {
        if (enemy.timeBeforeFirstMove > 0) {
            enemy.timeBeforeFirstMove -= deltaTime;
            if (enemy.timeBeforeFirstMove <= 60) {
                enemy.currentAnimation.update(deltaTime);
            }
            if (enemy.timeBeforeFirstMove <= 0) {
                enemy.isLethal = true;
                enemy.isActive = true;
                enemy.isInvincible = false;
            }
        }
    }

    /**
     * Handle when enemy has been hit
     */
    public void handleEnemyHasBeenHit(Enemy enemy, float deltaTime) {
        if (enemy.hasBeenHit) {
            Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit.");
            enemy.hasBeenHit = false;
            if (enemy.life <= 0) {
                Logger.info("Enemy " + enemy.getClass().getSimpleName() + " is dead.");
                // Move hitbox away when enemy is dead
                enemy.hitbox.x = 0;
                enemy.hitbox.y = 0;
                enemy.isDead = true;
                soundEffectManager.play("enemy_dies");
                enemy.currentAnimation = enemy.deathAnimation;
                enemyManager.enemyHasDied(enemy);
            } else {
                enemy.isInvincible = true;
                enemy.invicibleCounter = Enemy.INITIAL_INVINCIBLE_COUNTER;
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

    /**
     * Handle when enemy is stunned by boomerang
     */
    public void handleEnemyIsStunned(Enemy enemy, float deltaTime) {
        if (enemy.isStunned) {
            enemy.stunCounter -= deltaTime;
            if (enemy.stunCounter <= 0) {
                enemy.isStunned = false;
                enemy.isLethal = true;
            }
        }
        if (enemy.hasBeenStunned) {
            enemy.hasBeenStunned = false;
            if (enemy.isActive) {
                soundEffectManager.play("enemy_wounded");
                enemy.isStunned = true;
                enemy.stunCounter = Enemy.INITIAL_STUN_COUNTER;
                enemy.isLethal = false;
            }
        }
    }

    /**
     * Handle when enemy is pushed
     */
    public void handleEnemyIsPushed(Enemy enemy, float deltaTime) {
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

    /**
     * Handle when enemy is wounded
     */
    public void handleEnemyIsWounded(Enemy enemy, int damage, Hitbox hitbox, Orientation orientation) {
        enemy.life -= damage;
        enemy.hasBeenHit = true;

        // For enemies that are spawning
        if (enemy.life <= 0) {
            enemy.isSpawning = false;
            enemy.hasSpawned = false;
        }

        Orientation pushedOrientation = null;

        // For enemies that are attacking on tile
        if (enemy.isAttacking) {
            float deltaX = enemy.x - LocationUtil.getXFromGrid(LocationUtil.getTileXFromPositionX(enemy.x));
            float deltaY = enemy.x - LocationUtil.getYFromGrid(LocationUtil.getTileYFromPositionY(enemy.y));
            if (deltaX < ATTACK_TOLERANCE && deltaY < ATTACK_TOLERANCE) {
                pushedOrientation = orientation;
            }
        }

        // For enemies that are moving on tile and for Peahat
        if (enemy.orientation.isSameAs(orientation)) {
            pushedOrientation = orientation;
        }

        if (pushedOrientation != null) {
            enemy.isPushed = true;
            enemy.pushCounter = INITIAL_PUSH_DISTANCE;
            Float[] pushDirections = LocationUtil.computePushDirections(hitbox, enemy.hitbox, pushedOrientation);
            enemy.pushX = pushDirections[0];
            enemy.pushY = pushDirections[1];
            Logger.info("Enemy push direction : " + enemy.pushX + ", " + enemy.pushY);
        }
    }

    /**
     * Handle when the enemy is attacking
     */
    public void handleEnemyIsAttacking(Enemy enemy, float deltaTime, float minTime, float maxTime) {
        if (!enemy.isDead && enemy.isAttacking && !enemy.isStunned) {
            enemy.timeBeforeAttack -= deltaTime;
            if (enemy.timeBeforeAttack < 0) {
                Logger.info("Enemy is attacking (" + enemy.x + "," + enemy.y + ")");
                enemy.enemyManager.spawnMissile(enemy);
                enemy.isAttacking = false;
                chooseTimeBeforeAttack(enemy, minTime, maxTime);
            }
        }
    }

    /**
     * Handle when the enemy is attacking
     */
    public void handleEnemyIsAttackingWithBoomerang(Enemy enemy, float deltaTime, float minTime, float maxTime) {
        if (!enemy.isDead && enemy.isAttacking && !enemy.isStunned) {
            enemy.timeBeforeAttack -= deltaTime;
            if (enemy.timeBeforeAttack < 0) {
                Logger.info("Enemy is attacking (" + enemy.x + "," + enemy.y + ")");
                enemy.enemyManager.spawnMissile(enemy);
                chooseTimeBeforeAttack(enemy, minTime, maxTime);
            }
        }
    }
    /**
     * Randomly choose a duration before the next attack
     */
    public void chooseTimeBeforeAttack(Enemy enemy, float min, float max) {
        enemy.timeBeforeAttack = (float) ((max - min) * Math.random() + min);
    }

    /**
     * Handle when the attacking enemy is moving
     */
    public void handleAttackingEnemyIsMoving(Enemy enemy, float deltaTime, float pauseBeforeAttack) {
        if (!enemy.isDead && enemy.isActive && !enemy.isAttacking && !enemy.isStunned) {
            if (enemy.timeBeforeAttack > pauseBeforeAttack) {
                enemy.timeBeforeAttack -= deltaTime;
            }
            float remainingMoves = deltaTime * enemy.speed;
            remainingMoves = goToNextTile(enemy, remainingMoves);
            if (remainingMoves > 0) {
                Destination destination = chooseNextTile(enemy.orientation, enemy.x, enemy.y);
                enemy.nextTileX = destination.x;
                enemy.nextTileY = destination.y;
                enemy.orientation = destination.orientation;
                enemy.currentAnimation = enemy.moveAnimations.get(enemy.orientation);
            }
            if (enemy.timeBeforeAttack <= pauseBeforeAttack) {
                // The enemy wants to attack check its position first : on tile or half tile only
                if (remainingMoves > 0
                        || Math.abs(LocationUtil.HALF_TILE_SIZE - LocationUtil.getDeltaX(enemy.x)) < EnemyService.ATTACK_TOLERANCE
                        || Math.abs(LocationUtil.HALF_TILE_SIZE - LocationUtil.getDeltaY(enemy.y)) < EnemyService.ATTACK_TOLERANCE) {
                    enemy.isAttacking = true;
                    remainingMoves = 0;
                }
            }
            if (remainingMoves > 0) {
                goToNextTile(enemy, remainingMoves);
            }
        }
    }

    /**
     * Handle when the enemy is moving
     */
    public void handleEnemyIsMoving(Enemy enemy, float deltaTime) {
        if (!enemy.isDead && enemy.isActive && !enemy.isStunned) {
            // The enemy moves
            float remainingMoves = deltaTime * enemy.speed;
            remainingMoves = goToNextTile(enemy, remainingMoves);
            while (remainingMoves > 0) {
                Destination destination = chooseNextTile(enemy.orientation, enemy.nextTileX, enemy.nextTileY);
                enemy.nextTileX = destination.x;
                enemy.nextTileY = destination.y;
                enemy.orientation = destination.orientation;
                enemy.currentAnimation = enemy.moveAnimations.get(enemy.orientation);
                remainingMoves = goToNextTile(enemy, remainingMoves);
            }
        }
    }

    /**
     * Handle when the enemy is moving with pause on tiles
     */
    public void handleEnemyIsMovingWithPause(Enemy enemy, float deltaTime) {
        if (!enemy.isDead && enemy.isActive) {
            if (enemy.pauseBeforeNextTile > 0) {
                enemy.pauseBeforeNextTile -= deltaTime;
            } else {
                // The enemy moves
                float remainingMoves = deltaTime * enemy.speed;
                remainingMoves = goToNextTile(enemy, remainingMoves);
                if (remainingMoves > 0) {
                    Destination destination = chooseNextTile(enemy.orientation, enemy.nextTileX, enemy.nextTileY);
                    enemy.pauseBeforeNextTile = choosePauseBeforeNextTile();
                    enemy.nextTileX = destination.x;
                    enemy.nextTileY = destination.y;
                    enemy.orientation = destination.orientation;
                    enemy.currentAnimation = enemy.moveAnimations.get(enemy.orientation);
                }
            }
        }
    }

    /**
     * Move until the enemy has arrived at the next tile or until remainingMoves is consumed
     */
    public float goToNextTile(Enemy enemy, float remainingMoves) {
        boolean nextTileIsReachable = false;
        switch (enemy.orientation) {
            case UP:
                nextTileIsReachable = (enemy.y - remainingMoves < enemy.nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.y - enemy.nextTileY);
                    enemy.y = enemy.nextTileY;
                } else {
                    enemy.y -= remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.y = enemy.y + enemy.hitbox.y_offset;
                break;
            case DOWN:
                nextTileIsReachable = (enemy.y + remainingMoves > enemy.nextTileY);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.nextTileY - enemy.y);
                    enemy.y = enemy.nextTileY;
                } else {
                    enemy.y += remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.y = enemy.y + enemy.hitbox.y_offset;
                break;
            case LEFT:
                nextTileIsReachable = (enemy.x - remainingMoves < enemy.nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.x - enemy.nextTileX);
                    enemy.x = enemy.nextTileX;
                } else {
                    enemy.x -= remainingMoves;
                    remainingMoves = 0;
                }
                enemy.hitbox.x = enemy.x + enemy.hitbox.x_offset;
                break;
            case RIGHT:
                nextTileIsReachable = (enemy.x + remainingMoves > enemy.nextTileX);
                if (nextTileIsReachable) {
                    remainingMoves -= (enemy.nextTileX - enemy.x);
                    enemy.x = enemy.nextTileX;
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
    public Destination chooseNextTile(Orientation orientation, float x, float y) {
        // Continue in the same direction if possible
        if (Math.random() < PROBABILITY_TO_KEEP_SAME_ORIENTATION) {
            Destination destination = tryToChooseThisOrientationForNextTile(orientation, x, y);
            if (destination.isValid) {
                return destination;
            }
        }
        // Either the enemy has chosen to change direction or continue is not possible
        int direction1 = (int) (Math.floor(3 * Math.random()));
        int direction2 = 10 + (int) (Math.floor(2 * Math.random()));
        int direction3 = 10 + direction2;
        Orientation orientation1 = directionTree.get(orientation)[direction1][0];
        Destination destination1 = tryToChooseThisOrientationForNextTile(orientation1, x, y);
        if (destination1.isValid) {
            return destination1;
        }
        Orientation orientation2 = directionTree.get(orientation)[direction1][direction2];
        Destination destination2 = tryToChooseThisOrientationForNextTile(orientation2, x, y);
        if (destination2.isValid) {
            return destination2;
        }
        Orientation orientation3 = directionTree.get(orientation)[direction1][direction3];
        Destination destination3 = tryToChooseThisOrientationForNextTile(orientation3, x, y);
        if (destination3.isValid) {
            return destination3;
        }
        return new Destination(x, y, Orientation.UP, false);
    }

    /**
     * Try to choose an orientation. +HALF_TILE_SIZE to be sure we are not at the boundaries of 2 tiles.
     */
    private Destination tryToChooseThisOrientationForNextTile(Orientation chosenOrientation, float x, float y) {
        float nextTileXCandidate = x;
        float nextTileYCandidate = y;
        switch (chosenOrientation) {
            case UP:
                nextTileYCandidate = y - LocationUtil.TILE_SIZE;
                break;
            case DOWN:
                nextTileYCandidate = y + LocationUtil.TILE_SIZE;
                break;
            case LEFT:
                nextTileXCandidate = x - LocationUtil.TILE_SIZE;
                break;
            case RIGHT:
                nextTileXCandidate = x + LocationUtil.TILE_SIZE;
        }
        switch (chosenOrientation) {
            case UP:
            case DOWN:
                if ((LocationUtil.isTileAtBorder(x + LocationUtil.HALF_TILE_SIZE, y + LocationUtil.HALF_TILE_SIZE)
                        || !LocationUtil.isTileAtBorder(x + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE))
                        && zoneManager.isTileWalkable(x + LocationUtil.HALF_TILE_SIZE, nextTileYCandidate + LocationUtil.HALF_TILE_SIZE)) {
                    return new Destination(x, nextTileYCandidate, chosenOrientation, true);
                }
                break;
            case LEFT:
            case RIGHT:
                if ((LocationUtil.isTileAtBorder(x + LocationUtil.HALF_TILE_SIZE, y + LocationUtil.HALF_TILE_SIZE)
                        || !LocationUtil.isTileAtBorder(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, y + LocationUtil.HALF_TILE_SIZE))
                        && zoneManager.isTileWalkable(nextTileXCandidate + LocationUtil.HALF_TILE_SIZE, y + LocationUtil.HALF_TILE_SIZE)) {
                    return new Destination(nextTileXCandidate, y, chosenOrientation, true);
                }
                break;
        }
        return new Destination(0, 0, Orientation.UP, false);
    }

    /**
     * Choose the orientation according to link position
     */
    public Orientation chooseTurretOrientation(Enemy enemy) {
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

    /**
     * One chance out of 2 to continue moving immediately
     */
    private float choosePauseBeforeNextTile() {
        boolean doNotStay = (Math.random() < Enemy.PROBABILITY_TO_CONTINUE_MOVING);
        return (doNotStay) ? 5f : Enemy.MIN_TIME_FOR_MOVING_PAUSE + ((Enemy.MAX_TIME_FOR_MOVING_PAUSE - Enemy.MIN_TIME_FOR_MOVING_PAUSE) * (float) Math.random());
    }
}
