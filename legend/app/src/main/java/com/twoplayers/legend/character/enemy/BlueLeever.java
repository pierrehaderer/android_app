package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.Logger;

public class BlueLeever extends Enemy {

    private static final float INITIAL_TIME_BEFORE_SPAWN = 100f;
    private static final float TIME_BEFORE_RESPAWN = 250f;
    private static final float TIME_BEFORE_DESPAWN = 600f;
    private static final float SPAWNING_SPEED = 0.2f;
    private static final float SPEED = 0.8f;

    protected Animation spawnAnimation;
    protected Animation moveAnimation;
    protected Animation despawnAnimation;

    private boolean initNotDone;
    private boolean isActive;
    private boolean isSpawning;
    private boolean hasSpawned;
    private float timeBeforeSpawn;
    private float timeBeforeDespawn;
    protected float immobilisationCounter;

    private float nextTileX;
    private float nextTileY;
    private float nextNextTileX;
    private float nextNextTileY;
    private Orientation orientation;
    private Orientation nextOrientation;

    /**
     * Constructor
     */
    public BlueLeever(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        initNotDone = true;
        isActive = false;
        isSpawning = false;
        hasSpawned = false;
        isInvincible = true;
        isContactLethal = false;
        orientation = Orientation.UP;
        timeBeforeSpawn = INITIAL_TIME_BEFORE_SPAWN;
        timeBeforeDespawn = 0;
        immobilisationCounter = 0;
        life = 2;
        hitbox = new Hitbox(0, 0, 3, 3, 11, 11);
        contactDamage = -0.5f;
        currentAnimation = spawnAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(Graphics g) {
        spawnAnimation = g.newAnimation();
        spawnAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 10);
        spawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_1"), AllImages.COEF, 15);
        spawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_2"), AllImages.COEF, 15);
        spawnAnimation.addFrame(imagesEnemyWorldMap.get("blue_leevers_3"), AllImages.COEF, 15);
        spawnAnimation.setOccurrences(1);

        moveAnimation = g.newAnimation();
        moveAnimation.addFrame(imagesEnemyWorldMap.get("blue_leevers_4"), AllImages.COEF, 15);
        moveAnimation.addFrame(imagesEnemyWorldMap.get("blue_leevers_5"), AllImages.COEF, 15);

        despawnAnimation = g.newAnimation();
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("blue_leevers_3"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_2"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_1"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_2"), AllImages.COEF, 20);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_1"), AllImages.COEF, 20);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_2"), AllImages.COEF, 25);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("leevers_1"), AllImages.COEF, 25);
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 10);
        despawnAnimation.setOccurrences(1);
    }

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

        if (timeBeforeSpawn > 0) {
            timeBeforeSpawn -= deltaTime;
            moveEnemy(deltaTime * SPAWNING_SPEED);
        }

        if (isPossibleToSpawn()) {
            isSpawning = true;
            spawnAnimation.reset();
            currentAnimation = spawnAnimation;
        }

        // The enemy moves
        if (isSpawning || hasSpawned) {
            currentAnimation.update(deltaTime);
            if (!isActive) {
                moveEnemy(deltaTime * SPAWNING_SPEED);
                // The enemy is appearing or disappearing
                if (currentAnimation.isAnimationOver()) {
                    if (isSpawning) {
                        moveAnimation.reset();
                        currentAnimation = moveAnimation;
                        isActive = true;
                        isContactLethal = true;
                        isInvincible = false;
                        isSpawning = false;
                        hasSpawned = true;
                        timeBeforeDespawn = TIME_BEFORE_DESPAWN;
                    } else {
                        hasSpawned = false;
                        timeBeforeSpawn = TIME_BEFORE_RESPAWN;
                    }
                }
            }
            if (isActive) {
                if (immobilisationCounter > 0) {
                    immobilisationCounter -= deltaTime;
                    if (immobilisationCounter <= 0) {
                        isContactLethal = true;
                    }
                } else {
                    moveEnemy(deltaTime * SPEED);
                    timeBeforeDespawn -= deltaTime;
                    if (timeBeforeDespawn < 0) {
                        despawnAnimation.reset();
                        currentAnimation = despawnAnimation;
                        isActive = false;
                        isContactLethal = false;
                        isInvincible = true;
                    }
                }
            }
        }
    }

    /**
     * Move the enemy and find next tiles if needed
     */
    private void moveEnemy(float remainingMoves) {
        remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
        while (remainingMoves > 0) {
            Logger.debug("BlueLeever is on a new Tile (" + x + "," + y + ")");
            nextTileX = nextNextTileX;
            nextTileY = nextNextTileY;
            orientation = nextOrientation;
            Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
            nextNextTileX = destination.x;
            nextNextTileY = destination.y;
            nextOrientation = destination.orientation;
            remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
        }
    }

    /**
     * Check if a blue leever can spawn
     */
    private boolean isPossibleToSpawn() {
        if (timeBeforeSpawn > 0 || isSpawning || hasSpawned || isDead) {
            return false;
        }
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.getClass() == BlueLeever.class) {
                BlueLeever blueLeever = (BlueLeever) enemy;
                if (blueLeever.isSpawning) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void isHitByBoomerang() {
        soundEffectManager.play("enemy_wounded");
        if (isActive) {
            immobilisationCounter = Enemy.INITIAL_IMMOBILISATION_COUNTER;
            isContactLethal = false;
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

}
