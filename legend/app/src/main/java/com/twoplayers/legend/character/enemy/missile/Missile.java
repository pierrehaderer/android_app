package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.util.Orientation;

import java.util.Map;

public abstract class Missile {

    protected final MissileService missileService;

    protected float damage;
    public float speed;
    public Map<Orientation, Animation> animations;
    public Animation currentAnimation;

    public Orientation orientation;
    public float x;
    public float y;
    public Hitbox hitbox;

    public boolean isActive;
    public boolean isBlockedByObstacle;

    public Enemy creator;

    /**
     * Constructor
     */
    public Missile(MissileService missileService) {
        this.missileService = missileService;
    }

    /**
     * Initialize missile
     */
    public abstract void init(IImagesEnemy imagesEnemyWorldMap, Graphics g);

    /**
     * Update the missile position and animation, check despawn condition
     */
    public abstract void update(float deltaTime, Graphics g);

    public void hasHitLink() {
        isActive = false;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public boolean isActive() {
        return isActive;
    }

    public float getDamage() {
        return damage;
    }
}
