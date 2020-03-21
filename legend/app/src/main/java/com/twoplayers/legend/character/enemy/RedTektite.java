package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.link.LinkManager;

public class RedTektite extends Tektite {

    public RedTektite(ImagesEnemyWorldMap i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
    }

    @Override
    protected void initAnimations(Graphics g) {
        EnemyUtil enemyUtil = new EnemyUtil();
        initAnimation = enemyUtil.getCloudAnimation(imagesEnemyWorldMap, g);

        waitAnimation = g.newAnimation();
        waitAnimation.addFrame(imagesEnemyWorldMap.get("red_tektite_1"), AllImages.COEF, 26);
        waitAnimation.addFrame(imagesEnemyWorldMap.get("red_tektite_2"), AllImages.COEF, 26);

        prepareAnimation = g.newAnimation();
        prepareAnimation.addFrame(imagesEnemyWorldMap.get("red_tektite_1"), AllImages.COEF, 1000);

        jumpAnimation = g.newAnimation();
        jumpAnimation.addFrame(imagesEnemyWorldMap.get("red_tektite_2"), AllImages.COEF, 1000);
    }
}
