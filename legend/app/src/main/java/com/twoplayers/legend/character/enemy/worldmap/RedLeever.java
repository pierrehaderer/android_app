package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class RedLeever extends Enemy {

    private static final float INITIAL_TIME_BEFORE_SPAWN = 100f;
    private static final float SPEED = 0.8f;

    private Animation spawnAnimation;
    private Animation moveAnimation;
    private Animation despawnAnimation;

    public RedLeever(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImagesEnemy imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        nextTileX = x;
        nextTileY = y;
        isSpawning = false;
        hasSpawned = false;
        spawnCounter = INITIAL_TIME_BEFORE_SPAWN;
        life = 2;
        hitbox = new Hitbox(x, y, 3, 3, 10, 10);
        damage = -0.5f;
        currentAnimation = spawnAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(IImagesEnemy imagesEnemy, Graphics g) {
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        spawnAnimation = g.newAnimation();
        spawnAnimation.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        spawnAnimation.addFrame(imagesEnemy.get("leevers_1"), AllImages.COEF, 15);
        spawnAnimation.addFrame(imagesEnemy.get("leevers_2"), AllImages.COEF, 15);
        spawnAnimation.addFrame(imagesEnemy.get("red_leevers_3"), AllImages.COEF, 15);
        spawnAnimation.setOccurrences(1);

        moveAnimation = g.newAnimation();
        moveAnimation.addFrame(imagesEnemy.get("red_leevers_4"), AllImages.COEF, 15);
        moveAnimation.addFrame(imagesEnemy.get("red_leevers_5"), AllImages.COEF, 15);

        despawnAnimation = g.newAnimation();
        despawnAnimation.addFrame(imagesEnemy.get("red_leevers_3"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemy.get("leevers_2"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemy.get("leevers_1"), AllImages.COEF, 15);
        despawnAnimation.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        despawnAnimation.setOccurrences(1);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyHasBeenHit(this, deltaTime);
        enemyService.handleEnemyIsPushed(this, deltaTime);
        enemyService.handleEnemyIsStunned(this, deltaTime);

        // Spawn in front of link
        if (spawnCounter > 0) {
            spawnCounter -= deltaTime;
        }
        if (isPossibleToSpawn()) {
            Link link = linkManager.getLink();
            int tileX = LocationUtil.getTileXFromPositionX(link.x);
            int tileY = LocationUtil.getTileYFromPositionY(link.y);
            switch (link.orientation) {
                case UP:
                    spawnUpIfPossible(link, tileY);
                    spawnDownIfPossible(link, tileY);
                    break;
                case DOWN:
                    spawnDownIfPossible(link, tileY);
                    spawnUpIfPossible(link, tileY);
                    break;
                case LEFT:
                    spawnLeftIfPossible(link, tileX);
                    spawnRightIfPossible(link, tileX);
                    break;
                case RIGHT:
                    spawnRightIfPossible(link, tileX);
                    spawnLeftIfPossible(link, tileX);
                    break;
            }
            if (isSpawning) {
                spawnAnimation.reset();
                currentAnimation = spawnAnimation;
            }
        }

        // The enemy moves
        if (isSpawning || hasSpawned) {
            currentAnimation.update(deltaTime);
            if (!isActive) {
                // The enemy is appearing or disappearing
                if (currentAnimation.isOver()) {
                    if (isSpawning) {
                        moveAnimation.reset();
                        currentAnimation = moveAnimation;
                        isActive = true;
                        isLethal = true;
                        isSpawning = false;
                        hasSpawned = true;
                    } else {
                        hasSpawned = false;
                    }
                }
            }
            if (isActive && !isStunned) {
                moveEnemy(deltaTime * SPEED);
            }
        }
    }

    /**
     * Check if a red leever can spawn
     */
    private boolean isPossibleToSpawn() {
        if (spawnCounter > 0 || isSpawning || hasSpawned || isDead) {
            return false;
        }
        int leeversCount = 0;
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.getClass() == RedLeever.class) {
                RedLeever redLeever = (RedLeever) enemy;
                if (redLeever.isSpawning) {
                    return false;
                }
                if (redLeever.hasSpawned) {
                    leeversCount++;
                }
            }
        }
        return leeversCount < 2;
    }

    /**
     * Move the enemy and check if it must desapwn
     */
    private void moveEnemy(float speed) {
        boolean mustDespawn = false;
        switch (orientation) {
            case UP:
                y -= speed;
                hitbox.y  -= speed;
                if (!zoneManager.isUpValid(x, y) || LocationUtil.isTileAtBorder(x, y)) {
                    mustDespawn = true;
                }
                break;
            case DOWN:
                y += speed;
                hitbox.y += speed;
                if (!zoneManager.isDownValid(x, y) || LocationUtil.isTileAtBorder(x, y + LocationUtil.TILE_SIZE)) {
                    mustDespawn = true;
                }
                break;
            case LEFT:
                x -= speed;
                hitbox.x -= speed;
                if (!zoneManager.isLeftValid(x, y) || LocationUtil.isTileAtBorder(x, y)) {
                    mustDespawn = true;
                }
                break;
            case RIGHT:
                x += speed;
                hitbox.x += speed;
                if (!zoneManager.isRightValid(x, y) || LocationUtil.isTileAtBorder(x + LocationUtil.TILE_SIZE, y)) {
                    mustDespawn = true;
                }
                break;
        }
        if (mustDespawn) {
            isActive = false;
            isLethal = false;
            despawnAnimation.reset();
            currentAnimation = despawnAnimation;
        }
    }

    /**
     * Spawn up from link if the tile is available
     */
    private void spawnUpIfPossible(Link link, int tileY) {
        if (!isSpawning) {
            float spawnY = LocationUtil.getYFromGrid(tileY - 2);
            if (zoneManager.isTileWalkable(link.x, spawnY)) {
                x = link.x;
                y = spawnY;
                hitbox.relocate(x, y);
                orientation = Orientation.DOWN;
                isSpawning = true;
            }
        }
    }

    /**
     * Spawn down from link if the tile is available
     */
    private void spawnDownIfPossible(Link link, int tileY) {
        if (!isSpawning) {
            float spawnY;
            spawnY = LocationUtil.getYFromGrid(tileY + 3);
            if (zoneManager.isTileWalkable(link.x, spawnY)) {
                x = link.x;
                y = spawnY;
                hitbox.relocate(x, y);
                orientation = Orientation.UP;
                isSpawning = true;
            }
        }
    }

    /**
     * Spawn left from link if the tile is available
     */
    private void spawnLeftIfPossible(Link link, int tileX) {
        if (!isSpawning) {
            float spawnX = LocationUtil.getXFromGrid(tileX - 2);
            if (zoneManager.isTileWalkable(spawnX, link.y)) {
                x = spawnX;
                y = link.y;
                hitbox.relocate(x, y);
                orientation = Orientation.RIGHT;
                isSpawning = true;
            }
        }
    }

    /**
     * Spawn right from link if the tile is available
     */
    private void spawnRightIfPossible(Link link, int tileX) {
        if (!isSpawning) {
            float spawnX = LocationUtil.getXFromGrid(tileX + 3);
            if (zoneManager.isTileWalkable(spawnX, link.y)) {
                x = spawnX;
                y = link.y;
                hitbox.relocate(x, y);
                orientation = Orientation.LEFT;
                isSpawning = true;
            }
        }
    }

    @Override
    public void hasHitLink() {
        Logger.info("RedLeever has hit link, change its direction.");
        orientation = orientation.reverseOrientation();
    }
}
