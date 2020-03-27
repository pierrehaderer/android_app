package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Orientation;

public abstract class Missile {

    public static final float SPEED = 3f;

    private final IZoneManager zoneManager;

    protected float damage;
    public Animation animation;

    public Orientation orientation;
    public float x;
    public float y;
    public Hitbox hitbox;

    public boolean isActive;

    /**
     * Constructor
     */
    public Missile(IImagesEnemy imagesEnemyWorldMap, IZoneManager zoneManager, Graphics g) {
        this.zoneManager = zoneManager;
        initAnimations(imagesEnemyWorldMap, g);

        orientation = Orientation.UP;
        hitbox = new Hitbox(0,0,0,0,8,8);
        isActive = true;
    }

    /**
     * Init animations
     */
    public abstract void initAnimations(IImagesEnemy imagesEnemyWorldMap, Graphics g);

    public void update(float deltaTime, Graphics g) {
        if (isActive) {
            animation.update(deltaTime);
            switch (orientation) {
                case UP:
                    y -= deltaTime * SPEED;
                    hitbox.y -= deltaTime * SPEED;
                    break;
                case DOWN:
                    y += deltaTime * SPEED;
                    hitbox.y += deltaTime * SPEED;
                    break;
                case LEFT:
                    x -= deltaTime * SPEED;
                    hitbox.x -= deltaTime * SPEED;
                    break;
                case RIGHT:
                    x += deltaTime * SPEED;
                    hitbox.x += deltaTime * SPEED;
                    break;
            }
            if (zoneManager.isTileBlockingMissile(x, y)) {
                hitbox.relocate(0, 0);
                isActive = false;
            }
        }
    }

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
