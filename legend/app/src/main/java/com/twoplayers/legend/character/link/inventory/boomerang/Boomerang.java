package com.twoplayers.legend.character.link.inventory.boomerang;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;

import java.util.HashMap;
import java.util.Map;

public class Boomerang {

    protected static final float INITIAL_SPEED = 4f;
    protected static final float INITIAL_WOOD_BOOMERANG_COUNTER = 85f;
    protected static final float INITIAL_SOUND_COUNTER = 18f;

    public BoomerangType type;

    public float x;
    public float y;
    protected Orientation orientation;

    protected Map<Orientation, Hitbox> hitboxes;
    public Hitbox hitbox;

    protected Map<BoomerangType, Animation> animations;

    public boolean isMovingForward;
    public boolean isMovingBackward;
    protected float counter;
    protected float soundCounter;

    /**
     * Constructor
     */
    public Boomerang(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        hitboxes = new HashMap<>();
        hitboxes.put(Orientation.UP, new Hitbox(0, 0, 4, -4, 8, 8));
        hitboxes.put(Orientation.DOWN, new Hitbox(0, 0, 4, 12, 8, 8));
        hitboxes.put(Orientation.LEFT, new Hitbox(0, 0, -4, 4, 8, 8));
        hitboxes.put(Orientation.RIGHT, new Hitbox(0, 0, 12, 4, 8, 8));
        hitbox = hitboxes.get(Orientation.UP);
        isMovingForward = false;
        isMovingBackward = false;
        counter = 0;
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new HashMap<>();

        Animation animationWood = g.newAnimation();
        animationWood.addFrame(imagesLink.get("wood_boomerang_1"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_2"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_3"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_4"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_5"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_6"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_7"), AllImages.COEF, 4);
        animationWood.addFrame(imagesLink.get("wood_boomerang_8"), AllImages.COEF, 4);
        animations.put(BoomerangType.WOOD, animationWood);
        Animation animationWhite = g.newAnimation();
        animationWhite.addFrame(imagesLink.get("white_boomerang_1"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_2"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_3"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_4"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_5"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_6"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_7"), AllImages.COEF, 4);
        animationWhite.addFrame(imagesLink.get("white_boomerang_8"), AllImages.COEF, 4);
        animations.put(BoomerangType.WHITE, animationWhite);
    }

    public BoomerangType getType() {
        return type;
    }

    public Animation getAnimation() {
        return animations.get(type);
    }
}
