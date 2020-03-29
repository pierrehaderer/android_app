package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.TurretEnemy;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Orientation;

public class Zora extends TurretEnemy {

    private static final float TIME_BEFORE_SPAWN = 200f;
    private static final float SPAWNING_TIME = 25f;
    public static final float DESPAWNING_TIME = 150f;
    private static final float TIME_BEFORE_ATTACK = 75f;

    private boolean isSpawning;
    private boolean hasSpawned;
    private float spawnCounter;

    private Animation animationInit;
    private Animation animationUp;
    private Animation animationDown;

    public Zora(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        isActive = false;
        isSpawning = false;
        hasSpawned = false;
        isLethal = false;
        spawnCounter = TIME_BEFORE_SPAWN;
        life = 2;
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
        damage = -0.5f;
        currentAnimation = animationInit;
        orientation = Orientation.UP;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(Graphics g) {
        animationInit = g.newAnimation();
        animationInit.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 10);
        animationInit.setOccurrences(1);
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
        super.update(deltaTime, g);
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
            orientation = chooseOrientation();
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

    @Override
    public void isWounded(int damage, Hitbox hitbox, Orientation orientation) {
        super.isWounded(damage, hitbox, orientation);
        if (life <= 0) {
            isSpawning = false;
            hasSpawned = false;
        }
    }
}
