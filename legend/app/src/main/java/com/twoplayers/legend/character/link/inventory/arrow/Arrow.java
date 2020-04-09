package com.twoplayers.legend.character.link.inventory.arrow;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowType;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Arrow {

    protected static final float SPEED = 4f;

    public ArrowType type;

    public float x;
    public float y;
    protected Orientation orientation;
    public boolean isActive;
    public boolean isAnImpact;

    protected Map<Orientation, Hitbox> hitboxes;
    public Hitbox hitbox;

    protected Map<ArrowType, Map<Orientation, Animation>> animations;
    protected Animation deathAnimation;
    public Animation currentAnimation;

    /**
     * Constructor
     */
    public Arrow(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        hitboxes = new HashMap<>();
        hitboxes.put(Orientation.UP, new Hitbox(0, 0, 4, -4, 8, 8));
        hitboxes.put(Orientation.DOWN, new Hitbox(0, 0, 4, 12, 8, 8));
        hitboxes.put(Orientation.LEFT, new Hitbox(0, 0, -4, 4, 8, 8));
        hitboxes.put(Orientation.RIGHT, new Hitbox(0, 0, 12, 4, 8, 8));
        hitbox = hitboxes.get(Orientation.UP);
        isActive = false;
        isAnImpact = false;
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new HashMap<>();

        Map<Orientation, Animation> animationWood = new HashMap<>();
        Animation animationWoodUp = g.newAnimation();
        animationWoodUp.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWoodUp.addFrame(imagesLink.get("wood_arrow_up"), AllImages.COEF, 20f);
        animationWoodUp.setOccurrences(1);
        animationWood.put(Orientation.UP, animationWoodUp);
        Animation animationWoodDown = g.newAnimation();
        animationWoodDown.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWoodDown.addFrame(imagesLink.get("wood_arrow_down"), AllImages.COEF, 20f);
        animationWoodDown.setOccurrences(1);
        animationWood.put(Orientation.DOWN, animationWoodDown);
        Animation animationWoodLeft = g.newAnimation();
        animationWoodLeft.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWoodLeft.addFrame(imagesLink.get("wood_arrow_left"), AllImages.COEF, 20f);
        animationWoodLeft.setOccurrences(1);
        animationWood.put(Orientation.LEFT, animationWoodLeft);
        Animation animationWoodRight = g.newAnimation();
        animationWoodRight.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWoodRight.addFrame(imagesLink.get("wood_arrow_right"), AllImages.COEF, 20f);
        animationWoodRight.setOccurrences(1);
        animationWood.put(Orientation.RIGHT, animationWoodRight);
        animations.put(ArrowType.WOOD, animationWood);

            Map<Orientation, Animation> animationWhite = new HashMap<>();
        Animation animationWhiteUp = g.newAnimation();
        animationWhiteUp.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWhiteUp.addFrame(imagesLink.get("white_arrow_up"), AllImages.COEF, 20f);
        animationWhiteUp.setOccurrences(1);
        animationWhite.put(Orientation.UP, animationWhiteUp);
        Animation animationWhiteDown = g.newAnimation();
        animationWhiteDown.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWhiteDown.addFrame(imagesLink.get("white_arrow_down"), AllImages.COEF, 20f);
        animationWhiteDown.setOccurrences(1);
        animationWhite.put(Orientation.DOWN, animationWhiteDown);
        Animation animationWhiteLeft = g.newAnimation();
        animationWhiteLeft.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWhiteLeft.addFrame(imagesLink.get("white_arrow_left"), AllImages.COEF, 20f);
        animationWhiteLeft.setOccurrences(1);
        animationWhite.put(Orientation.LEFT, animationWhiteLeft);
        Animation animationWhiteRight = g.newAnimation();
        animationWhiteRight.addFrame(imagesLink.get("empty"), AllImages.COEF, 5f);
        animationWhiteRight.addFrame(imagesLink.get("white_arrow_right"), AllImages.COEF, 20f);
        animationWhiteRight.setOccurrences(1);
        animationWhite.put(Orientation.RIGHT, animationWhiteRight);
        animations.put(ArrowType.WHITE, animationWhite);

        deathAnimation = g.newAnimation();
        deathAnimation.addFrame(imagesLink.get("impact"), AllImages.COEF, 15f);
        deathAnimation.setOccurrences(1);
    }

    public void selectCurrentAnimation() {
        currentAnimation = animations.get(type).get(orientation);
    }

    public ArrowType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
