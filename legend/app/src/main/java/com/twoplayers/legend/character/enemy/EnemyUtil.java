package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;

public class EnemyUtil {

    public Animation getSlowCloudAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        return createCloudAnimation(imagesEnemy, g, 36);
    }

    public Animation getFastCloudAnimation(IImagesEnemy imagesEnemy, Graphics g) {
        return createCloudAnimation(imagesEnemy, g, 12);
    }

    private Animation createCloudAnimation(IImagesEnemy imagesEnemy, Graphics g, int firstStepDuration) {
        Animation animationCloud = g.newAnimation();
        animationCloud.addFrame(imagesEnemy.get("cloud_1"), AllImages.COEF, firstStepDuration);
        animationCloud.addFrame(imagesEnemy.get("cloud_2"), AllImages.COEF, 12);
        animationCloud.addFrame(imagesEnemy.get("cloud_3"), AllImages.COEF, 12);
        return animationCloud;
    }
}
