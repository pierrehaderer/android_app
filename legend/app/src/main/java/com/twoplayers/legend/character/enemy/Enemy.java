package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.WorldMapManager;

public abstract class Enemy {

    protected ImagesEnemyWorldMap imagesEnemyWorldMap;

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

    public Enemy(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        this.imagesEnemyWorldMap = imagesEnemyWorldMap;

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
}
