package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.enemy.Missile;

public class Rock extends Missile {

    private static final float SPEED = 3f;
    private static final float DAMAGE = -0.5f;

    public Rock(IImagesEnemy imagesEnemyWorldMap, IZoneManager zoneManager, Graphics g) {
        super(imagesEnemyWorldMap, zoneManager, g);
        damage = DAMAGE;
        speed = SPEED;
        isBlockedByObstacle = true;
    }

    @Override
    public void initAnimations(IImagesEnemy imagesEnemyWorldMap, Graphics g) {
        animation = g.newAnimation();
        animation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("rock"), AllImages.COEF, 20f);
        animation.setOccurrences(1);
    }
}