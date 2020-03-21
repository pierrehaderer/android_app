package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Logger;

public abstract class Enemy {

    public static final float INITIAL_IMMOBILISATION_COUNTER = 300f;

    protected IZoneManager zoneManager;
    protected LinkManager linkManager;
    protected IEnemyManager enemyManager;
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

    public Enemy(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, Graphics g) {
        this.zoneManager = z;
        this.linkManager = l;
        this.enemyManager = e;
        this.imagesEnemyWorldMap = i;
        this.soundEffectManager = s;

        // Death animation is common to al enemies
        deathAnimation = g.newAnimation();
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_1"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_2"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("enemy_death_3"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 1);
        deathAnimation.setOccurrences(1);

    }

    public abstract void update(float deltaTime, Graphics g);

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

    /**
     * This method is overridden if something special happens to the enemy which is hitting link
     */
    protected void hasHitLink() {
        Logger.info("This enemy has hit link.");
    }

    /**
     * This method can be overridden if something special happens to enemy when it is damaged
     */
    protected void isDamaged(int damage) {
        this.life -= damage;
        if (this.life <= 0) {
            this.isDead = true;
            // Move hitbox away when enemy is dead
            this.hitbox.x = 0;
            this.hitbox.y = 0;
            soundEffectManager.play("enemy_dies");
            this.currentAnimation = this.deathAnimation;
        } else {
            soundEffectManager.play("enemy_wounded");
        }

    }
}
