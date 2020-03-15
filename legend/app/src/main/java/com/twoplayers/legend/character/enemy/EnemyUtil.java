package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;

public class EnemyUtil {

    public Animation getCloudAnimation(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        Animation animationCloud = g.newAnimation();
        animationCloud.addFrame(imagesEnemyWorldMap.get("cloud_1"), AllImages.COEF, 36);
        animationCloud.addFrame(imagesEnemyWorldMap.get("cloud_2"), AllImages.COEF, 12);
        animationCloud.addFrame(imagesEnemyWorldMap.get("cloud_3"), AllImages.COEF, 12);
        return animationCloud;
    }
}
