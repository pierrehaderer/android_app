package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.WorldMapManager;

public abstract class Enemy {

    public static final float INITIAL_IMMOBILISATION_COUNTER = 300f;

    protected ImagesEnemyWorldMap imagesEnemyWorldMap;
    protected SoundEffectManager soundEffectManager;

    public float x;
    public float y;
    protected Hitbox hitbox;

    protected Animation currentAnimation;
    protected Animation deathAnimation;

    protected boolean isContactLethal;
    protected float contactDamage;

    protected int life;
    protected boolean isInvincible;
    protected boolean isDead;

    public Enemy(ImagesEnemyWorldMap imagesEnemyWorldMap, SoundEffectManager soundEffectManager, Graphics g) {
        this.imagesEnemyWorldMap = imagesEnemyWorldMap;
        this.soundEffectManager = soundEffectManager;

        // Death animation is common to al enemies
        deathAnimation = g.newAnimation();
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_1"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_2"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_3"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 1);
        deathAnimation.setOccurrences(1);

    }

    public abstract void update(float deltaTime, Graphics g, WorldMapManager worldMapManager);

    public Hitbox getHitbox() {
        return hitbox;
    }

    public boolean isContactLethal() {
        return isContactLethal;
    }

    public float getContactDamage() {
        return contactDamage;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public abstract void isHitByBoomerang();

    public abstract boolean isActive();
}
