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

public class BlackGel extends Gel {

    public BlackGel(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    /**
     * Init enemy animations
     */
    protected void initAnimations(IImages imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getFastCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemy.get("black_gel_1"), AllImages.COEF, 5);
        animation.addFrame(imagesEnemy.get("black_gel_2"), AllImages.COEF, 5);
        moveAnimations = new HashMap<>();
        moveAnimations.put(Orientation.UP, animation);
        moveAnimations.put(Orientation.DOWN, animation);
        moveAnimations.put(Orientation.LEFT, animation);
        moveAnimations.put(Orientation.RIGHT, animation);
    }
}
