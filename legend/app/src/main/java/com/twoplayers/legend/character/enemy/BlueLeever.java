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

public class BlueLeever extends Enemy {

    private static final float INITIAL_TIME_BEFORE_SPAWN = 100f;
    private static final float TIME_BEFORE_RESPAWN = 60f;
    private static final float SPAWNING_SPEED = 0.5f;
    private static final float SPEED = 1f;

    private boolean isActive;
    private boolean isSpawning;
    private boolean hasSpawned;
    private float timeBeforeSpawn;

    private Orientation orientation;

    protected Animation spawnAnimation;
    protected Animation moveAnimation;
    protected Animation despawnAnimation;
    protected float immobilisationCounter;

    /**
     * Constructor
     */
    public BlueLeever(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, Graphics g) {
        super(i, s, z, l, e, g);
        initAnimations(g);
        isActive = false;
        isSpawning = false;
        hasSpawned = false;
        isInvincible = true;
        isContactLethal = false;
        orientation = Orientation.UP;
        timeBeforeSpawn = INITIAL_TIME_BEFORE_SPAWN;
        life = 2;
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
        contactDamage = -0.5f;
        currentAnimation = spawnAnimation;
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(Graphics g) {

        spawnAnimation = g.newAnimation();
        spawnAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 1);
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
        despawnAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 1);
        despawnAnimation.setOccurrences(1);
    }

    @Override
    public void update(float deltaTime, Graphics g) {

        // Move hitbox away when enemy is dead
        if (isDead) {
            hitbox.x = 0;
            hitbox.y = 0;
        }

        // Spawn in front of link
        if (timeBeforeSpawn > 0) {
            timeBeforeSpawn -= deltaTime;
        }
        if (timeBeforeSpawn < 0 && !isSpawning && !hasSpawned) {

            if (isSpawning) {
                spawnAnimation.reset();
                currentAnimation = spawnAnimation;
            }
        }

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
