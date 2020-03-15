package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;

public class BlueTektite extends Tektite {

    public BlueTektite(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        super(imagesEnemyWorldMap, g);
    }

    @Override
    protected void initAnimations(Graphics g) {
        EnemyUtil enemyUtil = new EnemyUtil();
        initAnimation = enemyUtil.getCloudAnimation(imagesEnemyWorldMap, g);

        waitAnimation = g.newAnimation();
        waitAnimation.addFrame(imagesEnemyWorldMap.get("blue_tektite_1"), AllImages.COEF, 26);
        waitAnimation.addFrame(imagesEnemyWorldMap.get("blue_tektite_2"), AllImages.COEF, 26);

        prepareAnimation = g.newAnimation();
        prepareAnimation.addFrame(imagesEnemyWorldMap.get("blue_tektite_1"), AllImages.COEF, 1000);

        jumpAnimation = g.newAnimation();
        jumpAnimation.addFrame(imagesEnemyWorldMap.get("blue_tektite_2"), AllImages.COEF, 1000);
    }
}
