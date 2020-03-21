package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.link.LinkManager;

import java.util.HashMap;

public class BlueFastOctorok extends Octorok {

    public static final float HIGH_SPEED = 1.1f;

    /**
     * Constructor
     */
    public BlueFastOctorok(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, Graphics g) {
        super(i, s, z, l, e, g);
    }

    @Override
    protected void initAnimations(Graphics g) {
        animations = new HashMap<>();

        EnemyUtil enemyUtil = new EnemyUtil();
        animations.put(Orientation.INIT, enemyUtil.getCloudAnimation(imagesEnemyWorldMap, g));

        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemyWorldMap.get("blue_octorok_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemyWorldMap.get("blue_octorok_up_2"), AllImages.COEF, 15);
        animations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemyWorldMap.get("blue_octorok_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemyWorldMap.get("blue_octorok_down_2"), AllImages.COEF, 15);
        animations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemyWorldMap.get("blue_octorok_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesEnemyWorldMap.get("blue_octorok_left_2"), AllImages.COEF, 15);
        animations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemyWorldMap.get("blue_octorok_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesEnemyWorldMap.get("blue_octorok_right_2"), AllImages.COEF, 15);
        animations.put(Orientation.RIGHT, animationRight);
    }

    @Override
    protected float getSpeed() {
        return HIGH_SPEED;
    }

    @Override
    protected int getInitialLife() {
        return 2;
    }
}
