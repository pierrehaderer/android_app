package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public abstract class AttackingEnemy extends MoveOnTileEnemy {

    protected static final float ATTACK_TOLERANCE = 2f;

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
        if (isAttacking && !isPushed) {
            float deltaX = x - LocationUtil.getXFromGrid(LocationUtil.getTileXFromPositionX(x));
            float deltaY = x - LocationUtil.getYFromGrid(LocationUtil.getTileYFromPositionY(y));
            if (deltaX < ATTACK_TOLERANCE && deltaY < ATTACK_TOLERANCE) {
                isPushed = true;
                pushCounter = INITIAL_PUSH_DISTANCE;
                Float[] pushDirections = LocationUtil.computePushDirections(hitbox, orientation, this.hitbox);
                pushX = pushDirections[0];
                pushY = pushDirections[1];
                Logger.info("Enemy push direction : " + pushX + ", " + pushY);
            }
        }
    }

}
