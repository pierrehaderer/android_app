package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.EnemyUtil;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class LightGreenGel extends Gel {

    public LightGreenGel(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
    }

    /**
     * Init enemy animations
     */
    protected void initAnimations(Graphics g) {
        EnemyUtil enemyUtil = new EnemyUtil();
        initialAnimation = enemyUtil.getFastCloudAnimation(imagesEnemy, g);

        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemy.get("light_green_gel_1"), AllImages.COEF, 5);
        animation.addFrame(imagesEnemy.get("light_green_gel_2"), AllImages.COEF, 5);
        moveAnimations = new HashMap<>();
        moveAnimations.put(Orientation.UP, animation);
        moveAnimations.put(Orientation.DOWN, animation);
        moveAnimations.put(Orientation.LEFT, animation);
        moveAnimations.put(Orientation.RIGHT, animation);
    }
}
