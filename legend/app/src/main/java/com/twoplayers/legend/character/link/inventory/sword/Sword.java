package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.map.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Sword {

    public SwordType type;

    public float x;
    public float y;

    public Animation currentAnimation;
    public Map<SwordType, Map<Orientation, Animation>> animations;

    /**
     * Constructor
     */
    public Sword(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
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

        animations.put(SwordType.WOOD, new HashMap<Orientation, Animation>());
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("empty"), 8);
        animationUp.addFrame(imagesLink.get("wood_sword_up_2"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, 25);
        animationUp.addFrame(imagesLink.get("wood_sword_up_3"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.addFrame(imagesLink.get("wood_sword_up_4"), 0, Math.round(-14 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("empty"), 8);
        animationDown.addFrame(imagesLink.get("wood_sword_down_2"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, 25);
        animationDown.addFrame(imagesLink.get("wood_sword_down_3"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, 4);
        animationDown.addFrame(imagesLink.get("wood_sword_down_4"), 0, Math.round(14 * AllImages.COEF), AllImages.COEF, 4);
        animationDown.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("empty"), 8);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_2"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, 25);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_3"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.addFrame(imagesLink.get("wood_sword_left_4"), Math.round(-14 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.setOccurrences(1);
        animations.get(SwordType.WOOD).put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("empty"), 8);
        animationRight.addFrame(imagesLink.get("wood_sword_right_2"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, 25);
        animationRight.addFrame(imagesLink.get("wood_sword_right_3"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationRight.addFrame(imagesLink.get("wood_sword_right_4"), Math.round(14 * AllImages.COEF), 0, AllImages.COEF, 4);
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
