package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.SwordType;
import com.twoplayers.legend.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Sword {

    public static final float STEP_1_DURATION = 8;
    public static final float STEP_2_DURATION = 25;
    public static final float STEP_3_DURATION = 3;
    public static final float STEP_4_DURATION = 3;

    protected SwordType type;
    protected float damage;

    public float x;
    public float y;

    protected Hitbox hitbox;
    protected Map<Orientation, Hitbox> hitboxes;

    protected Animation currentAnimation;
    protected Map<SwordType, Map<Orientation, Animation>> animations;

    /**
     * Constructor
     */
    public Sword(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        hitboxes = new HashMap<>();
        hitboxes.put(Orientation.UP, new Hitbox(0, 0, 5, -11, 4, 12));
        hitboxes.put(Orientation.DOWN, new Hitbox(0, 0, 7, 15, 4, 12));
        hitboxes.put(Orientation.LEFT, new Hitbox(0, 0, -12, 8, 13, 4));
        hitboxes.put(Orientation.RIGHT, new Hitbox(0, 0, 16, 8, 13, 4));
        hitbox = hitboxes.get(Orientation.UP);
    }

    /**
     * Initialise the attack animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new HashMap<>();

        animations.put(SwordType.NONE, new HashMap<Orientation, Animation>());
        Animation emptyAnimation = g.newAnimation();
        emptyAnimation.setOccurrences(1);
        currentAnimation = emptyAnimation;
        animations.get(SwordType.NONE).put(Orientation.UP, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.DOWN, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.LEFT, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.RIGHT, emptyAnimation);

        animations.put(SwordType.WOOD, new HashMap<Orientation, Animation>());
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("empty"), STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("wood_sword_up_2"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, STEP_2_DURATION);
        animationUp.addFrame(imagesLink.get("wood_sword_up_3"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, STEP_3_DURATION);
        animationUp.addFrame(imagesLink.get("wood_sword_up_4"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, STEP_4_DURATION);
        animationUp.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("empty"), STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("wood_sword_down_2"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, STEP_2_DURATION);
        animationDown.addFrame(imagesLink.get("wood_sword_down_3"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, STEP_3_DURATION);
        animationDown.addFrame(imagesLink.get("wood_sword_down_4"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, STEP_4_DURATION);
        animationDown.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("empty"), STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_2"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, STEP_2_DURATION);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_3"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, STEP_3_DURATION);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_4"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, STEP_4_DURATION);
        animationLeft.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("empty"), STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("wood_sword_right_2"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, STEP_2_DURATION);
        animationRight.addFrame(imagesLink.get("wood_sword_right_3"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, STEP_3_DURATION);
        animationRight.addFrame(imagesLink.get("wood_sword_right_4"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, STEP_4_DURATION);
        animationRight.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.RIGHT, animationRight);
    }

    public SwordType getType() {
        return type;
    }

    public Animation getAnimation(Orientation orientation) {
        return animations.get(type).get(orientation);
    }
}
