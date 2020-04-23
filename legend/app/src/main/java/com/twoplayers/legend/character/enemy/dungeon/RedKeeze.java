package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.EnemyUtil;
import com.twoplayers.legend.character.link.LinkManager;

public class RedKeeze extends Keese {

    public RedKeeze(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
    }

    @Override
    protected void initAnimations(Graphics g) {
        EnemyUtil enemyUtil = new EnemyUtil();
        initialAnimation = enemyUtil.getFastCloudAnimation(imagesEnemy, g);

        moveAnimation = g.newAnimation();
        moveAnimation.addFrame(imagesEnemy.get("red_keese_1"), AllImages.COEF, 6);
        moveAnimation.addFrame(imagesEnemy.get("red_keese_2"), AllImages.COEF, 6);
    }
}
