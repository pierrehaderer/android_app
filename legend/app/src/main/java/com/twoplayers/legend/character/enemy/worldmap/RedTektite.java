package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;

public class RedTektite extends Tektite {

    public RedTektite(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    protected void initAnimations(IImagesEnemy imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getSlowCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        waitAnimation = g.newAnimation();
        waitAnimation.addFrame(imagesEnemy.get("red_tektite_1"), AllImages.COEF, 26);
        waitAnimation.addFrame(imagesEnemy.get("red_tektite_2"), AllImages.COEF, 26);

        prepareAnimation = g.newAnimation();
        prepareAnimation.addFrame(imagesEnemy.get("red_tektite_1"), AllImages.COEF, 1000);

        jumpAnimation = g.newAnimation();
        jumpAnimation.addFrame(imagesEnemy.get("red_tektite_2"), AllImages.COEF, 1000);
    }
}
