package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.map.WorldMapManager;

import java.util.Map;

public abstract class Enemy {

    protected ImagesEnemyWorldMap imagesEnemyWorldMap;

    public float x;
    public float y;
    protected Hitbox hitbox;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> animations;
    protected Animation deathAnimation;

    protected boolean isContactLethal;
    protected float contactDamage;

    protected int life;
    protected boolean isInvincible;
    protected boolean isDead;

    public Enemy(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        this.imagesEnemyWorldMap = imagesEnemyWorldMap;
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
