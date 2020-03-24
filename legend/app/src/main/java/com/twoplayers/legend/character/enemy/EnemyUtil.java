package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;

public class EnemyUtil {

    public Animation getCloudAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        Animation animationCloud = g.newAnimation();
        animationCloud.addFrame(imagesEnemy.get("cloud_1"), AllImages.COEF, 36);
        animationCloud.addFrame(imagesEnemy.get("cloud_2"), AllImages.COEF, 12);
        animationCloud.addFrame(imagesEnemy.get("cloud_3"), AllImages.COEF, 12);
        return animationCloud;
    }
}
