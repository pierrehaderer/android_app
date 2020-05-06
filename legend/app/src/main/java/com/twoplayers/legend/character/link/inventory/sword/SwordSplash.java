package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.util.Coordinate;

public class SwordSplash {

    public static final float INITIAL_COUNT = 40f;
    public static final float SPEED = 0.6f;

    public float x;
    public float y;
    public boolean isActive;

    public float count;

    public Animation[] animations;
    public Coordinate[] animationPositions;

    /**
     * Constructor
     */
    public SwordSplash(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        isActive = false;
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new Animation[4];
        animations[0] = g.newAnimation();
        animations[0].addFrame(imagesLink.get("sword_splash_ul_1"), AllImages.COEF, 3f);
        animations[0].addFrame(imagesLink.get("sword_splash_ul_2"), AllImages.COEF, 3f);
        animations[0].addFrame(imagesLink.get("sword_splash_ul_3"), AllImages.COEF, 3f);
        animations[0].addFrame(imagesLink.get("sword_splash_ul_4"), AllImages.COEF, 3f);
        animations[1] = g.newAnimation();
        animations[1].addFrame(imagesLink.get("sword_splash_ur_1"), AllImages.COEF, 3f);
        animations[1].addFrame(imagesLink.get("sword_splash_ur_2"), AllImages.COEF, 3f);
        animations[1].addFrame(imagesLink.get("sword_splash_ur_3"), AllImages.COEF, 3f);
        animations[1].addFrame(imagesLink.get("sword_splash_ur_4"), AllImages.COEF, 3f);
        animations[2] = g.newAnimation();
        animations[2].addFrame(imagesLink.get("sword_splash_dl_1"), AllImages.COEF, 3f);
        animations[2].addFrame(imagesLink.get("sword_splash_dl_2"), AllImages.COEF, 3f);
        animations[2].addFrame(imagesLink.get("sword_splash_dl_3"), AllImages.COEF, 3f);
        animations[2].addFrame(imagesLink.get("sword_splash_dl_4"), AllImages.COEF, 3f);
        animations[3] = g.newAnimation();
        animations[3].addFrame(imagesLink.get("sword_splash_dr_1"), AllImages.COEF, 3f);
        animations[3].addFrame(imagesLink.get("sword_splash_dr_2"), AllImages.COEF, 3f);
        animations[3].addFrame(imagesLink.get("sword_splash_dr_3"), AllImages.COEF, 3f);
        animations[3].addFrame(imagesLink.get("sword_splash_dr_4"), AllImages.COEF, 3f);
        animationPositions = new Coordinate[4];
        animationPositions[0] = new Coordinate();
        animationPositions[1] = new Coordinate();
        animationPositions[2] = new Coordinate();
        animationPositions[3] = new Coordinate();
    }
}
