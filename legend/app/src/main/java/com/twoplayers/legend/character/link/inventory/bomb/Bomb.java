package com.twoplayers.legend.character.link.inventory.bomb;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;

public class Bomb {

    protected static final float TIME_BEFORE_EXPLOSION = 100f;
    public static final int DAMAGE = 4;

    public float x;
    public float y;
    public float timeBeforeExplosion;
    public boolean isActive;

    public Hitbox hitbox;

    public Animation currentAnimation;
    /**
     * Constructor
     */
    public Bomb(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        hitbox = new Hitbox(0, 0, -18, -14, 44, 44);
        timeBeforeExplosion = 0;
        isActive = false;
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        currentAnimation = g.newAnimation();
        currentAnimation.addFrame(imagesLink.get("bomb"), AllImages.COEF, 10f);
        currentAnimation.setOccurrences(1);
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
