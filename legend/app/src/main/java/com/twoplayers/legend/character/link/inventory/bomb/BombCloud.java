package com.twoplayers.legend.character.link.inventory.bomb;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.util.Coordinate;

public class BombCloud {

    public float x;
    public float y;
    public boolean isActive;

    public Animation[] animations;
    public Coordinate[] animationPositions;

    /**
     * Constructor
     */
    public BombCloud(ImagesLink imagesLink, Graphics g) {
        initAnimations(imagesLink, g);
        isActive = false;
    }

    /**
     * Initialise the animations
     */
    private void initAnimations(ImagesLink imagesLink, Graphics g) {
        animations = new Animation[7];
        animations[0] = defineCloudAnimation(imagesLink, g, 5f, 2f);
        animations[1] = defineCloudAnimation(imagesLink, g, 6f, 3f);
        animations[2] = defineCloudAnimation(imagesLink, g, 5f, 4f);
        animations[3] = defineCloudAnimation(imagesLink, g, 7f, 2f);
        animations[4] = defineCloudAnimation(imagesLink, g, 6f, 4f);
        animations[5] = defineCloudAnimation(imagesLink, g, 5f, 3f);
        animations[6] = defineCloudAnimation(imagesLink, g, 6f, 2f);
        animationPositions = new Coordinate[7];
        animationPositions[0] = new Coordinate(-10f * AllImages.COEF, -14f * AllImages.COEF);
        animationPositions[1] = new Coordinate(2f * AllImages.COEF, -14f * AllImages.COEF);
        animationPositions[2] = new Coordinate(-18f * AllImages.COEF, 0);
        animationPositions[3] = new Coordinate(-4f * AllImages.COEF, 0);
        animationPositions[4] = new Coordinate(10f * AllImages.COEF, 0);
        animationPositions[5] = new Coordinate(-10f * AllImages.COEF, 14f * AllImages.COEF);
        animationPositions[6] = new Coordinate(2f * AllImages.COEF, 14f * AllImages.COEF);
    }

    private Animation defineCloudAnimation(ImagesLink imagesLink, Graphics g, float cloudTime, float emptyTime) {
        Animation animation = g.newAnimation();
        float sum = 0;
        while (sum < 70f) {
            animation.addFrame(imagesLink.get("cloud_1"), AllImages.COEF, cloudTime);
            animation.addFrame(imagesLink.get("empty"), AllImages.COEF, emptyTime);
            sum += cloudTime + emptyTime;
        }
        for (int i = 0; i < 2; i++) {
            animation.addFrame(imagesLink.get("cloud_2"), AllImages.COEF, cloudTime / 2);
            animation.addFrame(imagesLink.get("empty"), AllImages.COEF, emptyTime / 2);
        }
        for (int i = 0; i < 2; i++) {
            animation.addFrame(imagesLink.get("cloud_3"), AllImages.COEF, cloudTime / 2);
            animation.addFrame(imagesLink.get("empty"), AllImages.COEF, emptyTime / 2);
        }
        animation.setOccurrences(1);
        return animation;
    }
}
