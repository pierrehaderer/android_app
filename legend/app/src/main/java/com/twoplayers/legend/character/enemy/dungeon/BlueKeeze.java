package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;

public class BlueKeeze extends Keese {

    public BlueKeeze(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es) {
        super(s, z, l, e, es);
    }

    @Override
    protected void initAnimations(IImages imagesEnemy, Graphics g) {
        initialAnimation = enemyService.getFastCloudAnimation(imagesEnemy, g);
        deathAnimation = enemyService.getDeathAnimation(imagesEnemy, g);

        moveAnimation = g.newAnimation();
        moveAnimation.addFrame(imagesEnemy.get("blue_keese_1"), AllImages.COEF, 6);
        moveAnimation.addFrame(imagesEnemy.get("blue_keese_2"), AllImages.COEF, 6);
    }
}
