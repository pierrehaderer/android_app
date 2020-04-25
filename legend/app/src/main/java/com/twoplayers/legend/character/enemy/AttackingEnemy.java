package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Orientation;

public abstract class AttackingEnemy extends MoveOnTileEnemy {

    protected float timeBeforeAttack;
    protected boolean isAttacking;

    public AttackingEnemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        isAttacking = false;
    }

    /**
     * This enemy is pushed when it is hit
     */
    protected void isWounded(int damage, Hitbox hitbox, Orientation orientation) {
        super.isWounded(damage, hitbox, orientation);
        enemyService.handleEnemyIsWounded(this, damage, hitbox, orientation);
    }

    /**
     * Randomly choose a duration before the next attack
     */
    protected void chooseTimeBeforeAttack(float min, float max) {
        timeBeforeAttack = (float) ((max - min) * Math.random() + min);
    }
}
