package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Orientation;

import java.util.Map;

public abstract class Missile {

    private final IZoneManager zoneManager;

    protected float damage;
    protected float speed;
    public Map<Orientation, Animation> animations;
    public Animation currentAnimation;

    public Orientation orientation;
    public float x;
    public float y;
    public Hitbox hitbox;

    public boolean isActive;
    protected boolean isBlockedByObstacle;

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

    /**
     * Update the missile position and animation check despawn condition
     */
    public void update(float deltaTime, Graphics g) {
        if (isActive) {
            currentAnimation.update(deltaTime);
            switch (orientation) {
                case UP:
                    y -= deltaTime * speed;
                    hitbox.y -= deltaTime * speed;
                    break;
                case DOWN:
                    y += deltaTime * speed;
                    hitbox.y += deltaTime * speed;
                    break;
                case LEFT:
                    x -= deltaTime * speed;
                    hitbox.x -= deltaTime * speed;
                    break;
                case RIGHT:
                    x += deltaTime * speed;
                    hitbox.x += deltaTime * speed;
                    break;
                default:
                    y += deltaTime * speed * Math.sin(orientation.angle);
                    x += deltaTime * speed * Math.cos(orientation.angle);
                    hitbox.relocate(x, y);
                    break;
            }
            if ((zoneManager.isTileBlockingMissile(x, y) && isBlockedByObstacle)
                    || LocationUtil.isTileAtBorder(x, y)|| LocationUtil.isTileAtBorder(x + LocationUtil.QUARTER_TILE_SIZE, y + LocationUtil.QUARTER_TILE_SIZE)) {
                hitbox.relocate(0, 0);
                isActive = false;
            }
        }
    }

    /**
     * Define the orientation and choose the animation associated with it
     */
    public void defineOrientation(Orientation orientation) {
        this.orientation = orientation;
        currentAnimation = animations.get(orientation);
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
