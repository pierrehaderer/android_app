package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.enemy.Missile;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class EnemyArrow extends Missile {

    private static final float SPEED = 3f;
    private static final float DAMAGE = -0.5f;

    public EnemyArrow(IImagesEnemy imagesEnemyWorldMap, IZoneManager zoneManager, Graphics g) {
        super(imagesEnemyWorldMap, zoneManager, g);
        damage = DAMAGE;
        speed = SPEED;
        isBlockedByObstacle = false;
    }

    @Override
    public void initAnimations(IImagesEnemy imagesEnemyWorldMap, Graphics g) {
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animationUp.addFrame(imagesEnemyWorldMap.get("arrow_up"), AllImages.COEF, 20f);
        animationUp.setOccurrences(1);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animationDown.addFrame(imagesEnemyWorldMap.get("arrow_down"), AllImages.COEF, 20f);
        animationDown.setOccurrences(1);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animationLeft.addFrame(imagesEnemyWorldMap.get("arrow_left"), AllImages.COEF, 20f);
        animationLeft.setOccurrences(1);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animationRight.addFrame(imagesEnemyWorldMap.get("arrow_right"), AllImages.COEF, 20f);
        animationRight.setOccurrences(1);
        animations = new HashMap<>();
        animations.put(Orientation.UP, animationUp);
        animations.put(Orientation.DOWN, animationDown);
        animations.put(Orientation.LEFT, animationLeft);
        animations.put(Orientation.RIGHT, animationRight);

    }
}
