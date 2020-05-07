package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Sword {

    public float x;
    public float y;
    public Orientation orientation;

    protected Map<Orientation, Hitbox> hitboxes;

    public SwordType type;

    protected Map<SwordType, Map<Orientation, Animation>> animations;

    /**
     * Constructor
     */
    public Sword(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        orientation = Orientation.UP;
        hitboxes = new HashMap<>();
        hitboxes.put(Orientation.UP, new Hitbox(0, 0, 3, -11, 8, 18));
        hitboxes.put(Orientation.DOWN, new Hitbox(0, 0, 5, 9, 8, 18));
        hitboxes.put(Orientation.LEFT, new Hitbox(0, 0, -12, 6, 19, 8));
        hitboxes.put(Orientation.RIGHT, new Hitbox(0, 0, 10, 6, 19, 8));
    }

    /**
     * Initialise the attack animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new HashMap<>();

        animations.put(SwordType.NONE, new HashMap<Orientation, Animation>());
        Animation emptyAnimation = g.newAnimation();
        emptyAnimation.setOccurrences(1);
        animations.get(SwordType.NONE).put(Orientation.UP, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.DOWN, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.LEFT, emptyAnimation);
        animations.get(SwordType.NONE).put(Orientation.RIGHT, emptyAnimation);

        addSwordAnimations(imagesLink, g, SwordType.WOOD, "wood_sword");
        addSwordAnimations(imagesLink, g, SwordType.WHITE, "white_sword");
        addSwordAnimations(imagesLink, g, SwordType.MAGICAL, "magical_sword");
    }

    /**
     * Initiate one sword animation
     */
    private void addSwordAnimations(ImagesLink imagesLink, Graphics g, SwordType swordType, String swordName) {
        animations.put(swordType, new HashMap<Orientation, Animation>());
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("empty"), Link.STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get(swordName + "_up_2"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, Link.STEP_2_ATTACK_DURATION);
        animationUp.addFrame(imagesLink.get(swordName + "_up_3"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, Link.STEP_3_DURATION);
        animationUp.addFrame(imagesLink.get(swordName + "_up_4"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, Link.STEP_4_DURATION);
        animationUp.setOccurrences(1);
        animations.get(swordType).put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("empty"), Link.STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get(swordName + "_down_2"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, Link.STEP_2_ATTACK_DURATION);
        animationDown.addFrame(imagesLink.get(swordName + "_down_3"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, Link.STEP_3_DURATION);
        animationDown.addFrame(imagesLink.get(swordName + "_down_4"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, Link.STEP_4_DURATION);
        animationDown.setOccurrences(1);
        animations.get(swordType).put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("empty"), Link.STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get(swordName + "_left_2"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_2_ATTACK_DURATION);
        animationLeft.addFrame(imagesLink.get(swordName + "_left_3"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_3_DURATION);
        animationLeft.addFrame(imagesLink.get(swordName + "_left_4"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_4_DURATION);
        animationLeft.setOccurrences(1);
        animations.get(swordType).put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("empty"), Link.STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get(swordName + "_right_2"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_2_ATTACK_DURATION);
        animationRight.addFrame(imagesLink.get(swordName + "_right_3"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_3_DURATION);
        animationRight.addFrame(imagesLink.get(swordName + "_right_4"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, Link.STEP_4_DURATION);
        animationRight.setOccurrences(1);
        animations.get(swordType).put(Orientation.RIGHT, animationRight);
    }

    public SwordType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitboxes.get(orientation);
    }

    public Animation getAnimation() {
        return animations.get(type).get(orientation);
    }
}
