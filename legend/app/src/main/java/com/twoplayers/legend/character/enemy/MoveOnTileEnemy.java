package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Orientation;

import java.util.Map;

public abstract class MoveOnTileEnemy extends Enemy {

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

    protected Map<Orientation, Animation> moveAnimations;

    public MoveOnTileEnemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        orientation = Orientation.UP;
    }


    @Override
    public void update(float deltaTime, Graphics g) {
        super.update(deltaTime, g);
        enemyService.handleEnemyIsPushed(this, deltaTime);
    }

    /**
     * This enemy is pushed when it is hit
     */
    protected void isWounded(int damage, Hitbox hitbox, Orientation orientation) {
        super.isWounded(damage, hitbox, orientation);
        enemyService.handleEnemyIsWounded(this, damage, hitbox, orientation);
    }

    protected Animation getMoveAnimation() {
        return moveAnimations.get(orientation);
    }
}
