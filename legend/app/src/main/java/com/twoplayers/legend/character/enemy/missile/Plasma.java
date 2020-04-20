package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.enemy.Missile;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class Plasma extends Missile {

    private static final float SPEED = 2f;
    private static final float DAMAGE = -0.5f;

    public Plasma(IImagesEnemy imagesEnemyWorldMap, IZoneManager zoneManager, Graphics g) {
        super(imagesEnemyWorldMap, zoneManager, g);
        damage = DAMAGE;
        speed = SPEED;
        isBlockedByObstacle = false;
    }

    @Override
    public void initAnimations(IImagesEnemy imagesEnemyWorldMap, Graphics g) {
        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("plasma_1"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("plasma_2"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("plasma_3"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("plasma_4"), AllImages.COEF, 5f);
        animations = new HashMap<>();
        for (Orientation orientation : Orientation.values()) {
            animations.put(orientation, animation);
        }
    }
}
