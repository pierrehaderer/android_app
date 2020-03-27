package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public abstract class MoveOnTileEnemy extends Enemy {

    protected static final float PUSH_SPEED = 9f;
    protected static final float INITIAL_PUSH_DISTANCE = 4 * LocationUtil.TILE_SIZE;

    public Orientation orientation;
    protected Orientation nextOrientation;

    protected float nextTileX;
    protected float nextTileY;
    protected float nextNextTileX;
    protected float nextNextTileY;

    protected boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;

    public MoveOnTileEnemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        orientation = Orientation.UP;
    }

    /**
     * This enemy is pushed when it is hit
     */
    protected void isWounded(int damage, Hitbox hitbox, Orientation orientation) {
        super.isWounded(damage, hitbox, orientation);
        if (this.orientation.isSameAs(orientation)) {
            isPushed = true;
            pushCounter = INITIAL_PUSH_DISTANCE;
            Float[] pushDirections = LocationUtil.computePushDirections(hitbox, this.hitbox, this.orientation);
            pushX = pushDirections[0];
            pushY = pushDirections[1];
            Logger.info("Enemy push direction : " + pushX + ", " + pushY);
        }
    }
}
