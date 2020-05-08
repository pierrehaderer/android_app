package com.twoplayers.legend.character.link.inventory.light;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;

public class Fire {

    public static final float INITIAL_TIME_BEFORE_DESPAWN = 150f;
    public static final float DAMAGE_TO_LINK = -0.5f;
    public static final int DAMAGE_TO_ENEMY = 1;

    protected static final float SPEED = 0.4f;

    public boolean isActive;
    public float remainingMoves;
    public float timeBeforeDespawn;

    protected Orientation orientation;
    public float x;
    public float y;
    public Hitbox hitbox;

    public Animation animation;

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

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
