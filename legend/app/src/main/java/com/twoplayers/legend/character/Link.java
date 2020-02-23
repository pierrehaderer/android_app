package com.twoplayers.legend.character;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageLink;
import com.twoplayers.legend.map.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Link {

    public static final float LINK_SPEED = 1.3f;

    private ImageLink imageLink;

    public float x;
    public float y;
    public Orientation orientation;

    public boolean isAttacking;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> moveAnimations;
    protected Map<Orientation, Animation> attackAnimations;

    public Link(ImageLink imageLink, Graphics g) {
        this.imageLink = imageLink;
        initMoveAnimation(g);
        initAttackAnimation(g);
    }

    /**
     * Initialise the move animations
     */
    private void initMoveAnimation(Graphics g) {
        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imageLink.get("link_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imageLink.get("link_up_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imageLink.get("link_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imageLink.get("link_down_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imageLink.get("link_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imageLink.get("link_left_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imageLink.get("link_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imageLink.get("link_right_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }

    /**
     * Initialise the attack animations
     */
    private void initAttackAnimation(Graphics g) {
        attackAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imageLink.get("link_wood_sword_up_1"), AllImages.COEF, 8);
        animationUp.addFrame(imageLink.get("link_wood_sword_up_2"), 0, Math.round(-12 * AllImages.COEF), AllImages.COEF, 25);
        animationUp.addFrame(imageLink.get("link_wood_sword_up_3"), 0, Math.round(-11 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.addFrame(imageLink.get("link_wood_sword_up_4"), 0, Math.round(-3 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.setOccurrences(1);
        attackAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imageLink.get("link_wood_sword_down_1"), AllImages.COEF, 8);
        animationDown.addFrame(imageLink.get("link_wood_sword_down_2"), AllImages.COEF, 25);
        animationDown.addFrame(imageLink.get("link_wood_sword_down_3"), AllImages.COEF, 4);
        animationDown.addFrame(imageLink.get("link_wood_sword_down_4"), AllImages.COEF, 4);
        animationDown.setOccurrences(1);
        attackAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imageLink.get("link_wood_sword_left_1"), AllImages.COEF, 8);
        animationLeft.addFrame(imageLink.get("link_wood_sword_left_2"), Math.round(-11 * AllImages.COEF), 0, AllImages.COEF, 25);
        animationLeft.addFrame(imageLink.get("link_wood_sword_left_3"), Math.round(-7 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.addFrame(imageLink.get("link_wood_sword_left_4"), Math.round(-3 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.setOccurrences(1);
        attackAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imageLink.get("link_wood_sword_right_1"), AllImages.COEF, 8);
        animationRight.addFrame(imageLink.get("link_wood_sword_right_2"), AllImages.COEF, 25);
        animationRight.addFrame(imageLink.get("link_wood_sword_right_3"), AllImages.COEF, 4);
        animationRight.addFrame(imageLink.get("link_wood_sword_right_4"), AllImages.COEF, 4);
        animationRight.setOccurrences(1);
        attackAnimations.put(Orientation.RIGHT, animationRight);
    }
}
