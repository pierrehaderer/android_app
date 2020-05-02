package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;

public class Zora extends Enemy {

    private static final float TIME_BEFORE_SPAWN = 200f;
    private static final float SPAWNING_TIME = 25f;
    private static final float DESPAWNING_TIME = 150f;
    private static final float TIME_BEFORE_ATTACK = 75f;

    private Animation animationUp;
    private Animation animationDown;

    public Zora(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    public void init(IImagesEnemy imagesEnemy, Graphics g) {
        initAnimations(imagesEnemy, g);
        isSpawning = false;
        hasSpawned = false;
        spawnCounter = TIME_BEFORE_SPAWN;
        life = 2;
        hitbox = new Hitbox(x, y, 3, 3, 10, 10);
        damage = -0.5f;
        currentAnimation = initialAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(IImagesEnemy imagesEnemy, Graphics g) {
        initialAnimation = g.newAnimation();
        initialAnimation.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        initialAnimation.setOccurrences(1);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animationUp.addFrame(imagesEnemy.get("zora_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("zora_2"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("zora_up"), AllImages.COEF, 150);
        animationUp.addFrame(imagesEnemy.get("zora_2"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("zora_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animationUp.setOccurrences(1);
        animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animationDown.addFrame(imagesEnemy.get("zora_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("zora_2"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("zora_down"), AllImages.COEF, 150);
        animationDown.addFrame(imagesEnemy.get("zora_2"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("zora_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animationDown.setOccurrences(1);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        enemyService.handleEnemyHasBeenHit(this, deltaTime);

        // Spawn in water
        if (spawnCounter > 0) {
            currentAnimation.update(deltaTime);
            spawnCounter -= deltaTime;
        }
        if (spawnCounter <= 0 && !isSpawning && !hasSpawned && !isDead) {
            Coordinate spawnCoordinate = zoneManager.findSpawnableCoordinateInWater();
            x = spawnCoordinate.x;
            y = spawnCoordinate.y;
            hitbox.relocate(x, y);
            orientation = enemyService.chooseTurretOrientation(this);
            isSpawning = true;
            currentAnimation = (y > linkManager.getLink().y - LocationUtil.TILE_SIZE) ? animationUp : animationDown;
            currentAnimation.reset();
            spawnCounter = SPAWNING_TIME;
        }

        // The enemy appears
        if (isSpawning) {
            if (spawnCounter <= 0) {
                isActive = true;
                isLethal = true;
                isSpawning = false;
                hasSpawned = true;
                timeBeforeAttack = TIME_BEFORE_ATTACK;
                spawnCounter = DESPAWNING_TIME;
            }
        }

        if (hasSpawned) {
            if (timeBeforeAttack > 0) {
                timeBeforeAttack -= deltaTime;
                if (timeBeforeAttack <= 0) {
                    enemyManager.spawnMissile(this);
                }
            }
            if (spawnCounter <= 0) {
                isActive = false;
                isLethal = false;
                hasSpawned = false;
                spawnCounter = TIME_BEFORE_SPAWN;
            }
        }
    }

    @Override
    public void isHitByBoomerang() {
        soundEffectManager.play("enemy_wounded");
    }
}
