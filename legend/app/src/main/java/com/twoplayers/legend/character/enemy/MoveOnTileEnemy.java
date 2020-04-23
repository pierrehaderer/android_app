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
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

import java.util.Map;

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

    protected Map<Orientation, Animation> moveAnimations;

    public MoveOnTileEnemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        orientation = Orientation.UP;
    }


    @Override
    public void update(float deltaTime, Graphics g) {
        super.update(deltaTime, g);

        // The enemy is pushed
        if (!isDead && isPushed) {
            Logger.info("Enemy is pushed, remaining counter : " + pushCounter);
            float distance = Math.min(deltaTime * PUSH_SPEED, pushCounter);
            pushCounter -= distance;

            float deltaY = pushY * distance;
            boolean pushed = false;
            if ((deltaY < 0 && zoneManager.isUpValid(x, y + deltaY)) || (deltaY > 0 && zoneManager.isDownValid(x, y + deltaY))) {
                pushed = true;
                y += deltaY;
                hitbox.y += deltaY;
            }
            float deltaX = pushX * distance;
            if ((deltaX < 0 && zoneManager.isLeftValid(x + deltaX, y)) || (deltaX > 0 && zoneManager.isRightValid(x + deltaX, y))) {
                pushed = true;
                x += deltaX;
                hitbox.x += deltaX;
            }
            // Stop pushing if there is an obstacle or if the counter is down to 0
            if (!pushed || pushCounter <= 0) {
                isPushed = false;
            }
        }
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

    protected Animation getMoveAnimation() {
        return moveAnimations.get(orientation);
    }
}
