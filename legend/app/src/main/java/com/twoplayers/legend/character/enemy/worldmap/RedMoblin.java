package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class RedMoblin extends Moblin {

    private static final int INITIAL_LIFE = 2;

    /**
     * Constructor
     */
    public RedMoblin(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
        life = INITIAL_LIFE;
    }

    @Override
    protected void initAnimations(IImages imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getSlowCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemy.get("red_moblin_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("red_moblin_up_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemy.get("red_moblin_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("red_moblin_down_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemy.get("red_moblin_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesEnemy.get("red_moblin_left_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemy.get("red_moblin_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesEnemy.get("red_moblin_right_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }
}
