package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.Fire;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.Sword;
import com.twoplayers.legend.util.Logger;

public abstract class Enemy {

    public static final float INITIAL_IMMOBILISATION_COUNTER = 300f;
    protected static final float INITIAL_INVINCIBLE_COUNT = 100f;

    protected IZoneManager zoneManager;
    protected LinkManager linkManager;
    protected IEnemyManager enemyManager;
    protected IImagesEnemy imagesEnemy;
    protected SoundEffectManager soundEffectManager;

    protected EnemyService enemyService;

    public float x;
    public float y;
    public Hitbox hitbox;

    public Animation currentAnimation;
    protected Animation deathAnimation;

    protected boolean isContactLethal;
    protected float contactDamage;
    protected boolean isInvincible;
    protected float invicibleCounter;
    public boolean isDead;
    protected boolean isActive;

    protected int life;

    public Enemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager em, EnemyService es, Graphics g) {
        this.zoneManager = z;
        this.linkManager = l;
        this.enemyManager = em;
        this.imagesEnemy = i;
        this.soundEffectManager = s;
        this.enemyService = es;

        // Death animation is common to al enemies
        deathAnimation = g.newAnimation();
        deathAnimation.addFrame(imagesEnemy.get("enemy_death_1"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemy.get("enemy_death_2"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemy.get("enemy_death_3"), AllImages.COEF, 10);
        deathAnimation.addFrame(imagesEnemy.get("empty"), AllImages.COEF, 1);
        deathAnimation.setOccurrences(1);

        isInvincible = false;
    }

    /**
     * Update the enemy has it is existing in the room
     */
    public void update(float deltaTime, Graphics g) {
        if (invicibleCounter >= 0) {
            invicibleCounter -= deltaTime;
            if (invicibleCounter < 0) {
                isInvincible = false;
            }
        }
    }

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

    public boolean isActive() {
        return isActive;
    }

    public abstract void isHitByBoomerang();

    /**
     * This method is overridden if something special happens to the enemy which is hitting link
     */
    public void isHitBySword(Sword sword) {
        isWounded(sword.getType().damage, sword.getHitbox(), sword.getOrientation());
    }

    /**
     * This method is overridden if something special happens to the enemy which is hitting link
     */
    public void isHitByFire(Fire fire) {
        isWounded(Fire.DAMAGE_TO_ENEMY, fire.getHitbox(), fire.getOrientation());
    }

        /**
         * This method is overridden if something special happens to the enemy which is hitting link
         */
        protected void isWounded(int damage, Hitbox hitbox, Orientation orientation) {
            this.life -= damage;
            if (this.life <= 0) {
                this.isDead = true;
                // Move hitbox away when enemy is dead
                this.hitbox.x = 0;
                this.hitbox.y = 0;
                soundEffectManager.play("enemy_dies");
                this.currentAnimation = this.deathAnimation;
            } else {
                isInvincible = true;
                invicibleCounter = INITIAL_INVINCIBLE_COUNT;
                soundEffectManager.play("enemy_wounded");
            }
        }

    /**
     * This method is overridden if something special happens to the enemy which is hitting link
     */
    public void hasHitLink() {
        Logger.info("This enemy has hit link.");
    }
}
