package com.twoplayers.legend.character.enemy.dungeon;

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

public class BlueGoriya extends Goriya {

    private static final float SPEED = 0.6f;
    private static final int INITIAL_LIFE = 5;
    private static final float DAMAGE = -1f;

    /**
     * Constructor
     */
    public BlueGoriya(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
        life = INITIAL_LIFE;
        speed = SPEED;
        damage = DAMAGE;
    }

    @Override
    protected void initAnimations(IImages imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getFastCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemy.get("blue_goriya_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesEnemy.get("blue_goriya_up_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemy.get("blue_goriya_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesEnemy.get("blue_goriya_down_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemy.get("blue_goriya_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesEnemy.get("blue_goriya_left_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemy.get("blue_goriya_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesEnemy.get("blue_goriya_right_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }
}
