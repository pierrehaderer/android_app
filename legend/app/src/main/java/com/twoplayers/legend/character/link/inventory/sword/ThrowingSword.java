package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;
import java.util.Map;

public class ThrowingSword {

    public static final float SPEED = 4f;
    public static final float INITIAL_DELAY = 15f;

    public Sword sword;

    public float x;
    public float y;
    public Orientation orientation;

    public Hitbox hitbox;

    protected Map<Orientation, Animation> animations;

    public float delayBeforeActive;
    public boolean isActive;

    /**
     * Constructor
     */
    public ThrowingSword(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        orientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 2, 2, 12, 12);
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new HashMap<>();

        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("sword_up_1"), AllImages.COEF, 3);
        animationUp.addFrame(imagesLink.get("sword_up_2"), AllImages.COEF, 3);
        animationUp.addFrame(imagesLink.get("sword_up_3"), AllImages.COEF, 3);
        animationUp.addFrame(imagesLink.get("sword_up_4"), AllImages.COEF, 3);
        animations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("sword_down_1"), AllImages.COEF, 3);
        animationDown.addFrame(imagesLink.get("sword_down_2"), AllImages.COEF, 3);
        animationDown.addFrame(imagesLink.get("sword_down_3"), AllImages.COEF, 3);
        animationDown.addFrame(imagesLink.get("sword_down_4"), AllImages.COEF, 3);
        animations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("sword_left_1"), AllImages.COEF, 3);
        animationLeft.addFrame(imagesLink.get("sword_left_2"), AllImages.COEF, 3);
        animationLeft.addFrame(imagesLink.get("sword_left_3"), AllImages.COEF, 3);
        animationLeft.addFrame(imagesLink.get("sword_left_4"), AllImages.COEF, 3);
        animations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("sword_right_1"), AllImages.COEF, 3);
        animationRight.addFrame(imagesLink.get("sword_right_2"), AllImages.COEF, 3);
        animationRight.addFrame(imagesLink.get("sword_right_3"), AllImages.COEF, 3);
        animationRight.addFrame(imagesLink.get("sword_right_4"), AllImages.COEF, 3);
        animations.put(Orientation.RIGHT, animationRight);
    }

    public Animation getAnimation() {
        return animations.get(orientation);
    }

    public Sword getSword() {
        return sword;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
