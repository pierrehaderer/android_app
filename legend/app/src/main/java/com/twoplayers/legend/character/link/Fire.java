package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.LocationUtil;

public class Fire {

    public static final float INITIAL_TIME_BEFORE_DESPAWN = 150f;
    public static final float DAMAGE_TO_LINK = -0.5f;
    public static final int DAMAGE_TO_ENEMY = 1;

    private static final float SPEED = 0.4f;

    protected boolean isActive;
    protected float remainingMoves;
    protected float timeBeforeDespawn;

    protected Orientation orientation;
    protected float x;
    protected float y;
    protected Hitbox hitbox;

    protected Animation animation;

    public Fire(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        isActive = false;
        remainingMoves = 0;
        timeBeforeDespawn = INITIAL_TIME_BEFORE_DESPAWN;
        orientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 2, 3, 12, 12);
    }

    /**
     * Initialise the move animations
     */
    protected void initAnimations(ImagesLink imagesLink, Graphics g) {
        animation = g.newAnimation();
        animation.addFrame(imagesLink.get("fire_1"), AllImages.COEF, 10);
        animation.addFrame(imagesLink.get("fire_2"), AllImages.COEF, 10);
    }

    /**
     * Init the fire when produced by link light
     */
    protected void initFromLight(Orientation orientation, float x, float y) {
        isActive = true;
        timeBeforeDespawn = Fire.INITIAL_TIME_BEFORE_DESPAWN;
        this.remainingMoves = LocationUtil.TILE_SIZE;
        this.orientation = orientation;
        this.x = x;
        this.y = y;
        hitbox.relocate(x, y);
    }

    /**
     * Update fire animation and position
     */
    protected void update(float deltaTime) {
        if (isActive) {
            animation.update(deltaTime);
            if (remainingMoves > 0) {
                float distance = Math.min(remainingMoves, deltaTime * SPEED);
                remainingMoves -= distance;
                switch (orientation) {
                    case UP:
                        y -= distance;
                        hitbox.y -= distance;
                        break;
                    case DOWN:
                        y += distance;
                        hitbox.y += distance;
                        break;
                    case LEFT:
                        x -= distance;
                        hitbox.x -= distance;
                        break;
                    case RIGHT:
                        x += distance;
                        hitbox.x += distance;
                        break;
                }
            } else {
                timeBeforeDespawn -= deltaTime;
                if (timeBeforeDespawn < 0) {
                    isActive = false;
                    hitbox.relocate(0, 0);
                }
            }
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
