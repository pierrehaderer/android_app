package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public abstract class TurretEnemy extends Enemy {

    protected float timeBeforeAttack;
    protected boolean isAttacking;
    public Orientation orientation;

    public TurretEnemy(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        isAttacking = false;
    }

    /**
     * Choose the orinetation according to link position
     */
    protected Orientation chooseOrientation() {
        Orientation orientation = Orientation.UP;
        float deltaX = x - linkManager.getLink().x;
        float deltaY = y - linkManager.getLink().y;
        float ratio = 0;
        if (deltaX == 0) {
            if (deltaY > 0) {
                orientation = Orientation.UP;
            } else {
                orientation = Orientation.DOWN;
            }
        } else {
            ratio = deltaY / deltaX;
            if (deltaX < 0 && deltaY >= 0) {
                if (ratio > -1/4f) {
                    orientation = Orientation.RIGHT;
                } else if (ratio > -3/4f) {
                    orientation = Orientation.DEGREES_340;
                } else if (ratio > -4/3f) {
                    orientation = Orientation.DEGREES_315;
                } else if (ratio > -4f) {
                    orientation = Orientation.DEGREES_290;
                } else {
                    orientation = Orientation.UP;
                }
            } else if (deltaX > 0 && deltaY >= 0) {
                if (ratio < 1/4f) {
                    orientation = Orientation.LEFT;
                } else if (ratio < 3/4f) {
                    orientation = Orientation.DEGREES_200;
                } else if (ratio < 4/3f) {
                    orientation = Orientation.DEGREES_225;
                } else if (ratio < 4f) {
                    orientation = Orientation.DEGREES_250;
                } else {
                    orientation = Orientation.UP;
                }
            } else if (deltaX > 0 && deltaY < 0) {
                if (ratio > -1/4f) {
                    orientation = Orientation.LEFT;
                } else if (ratio > -3/4f) {
                    orientation = Orientation.DEGREES_160;
                } else if (ratio > -4/3f) {
                    orientation = Orientation.DEGREES_135;
                } else if (ratio > -4f) {
                    orientation = Orientation.DEGREES_110;
                } else {
                    orientation = Orientation.DOWN;
                }
            } else if (deltaX < 0 && deltaY < 0) {
                if (ratio < 1/4f) {
                    orientation = Orientation.RIGHT;
                } else if (ratio < 3/4f) {
                    orientation = Orientation.DEGREES_20;
                } else if (ratio < 4/3f) {
                    orientation = Orientation.DEGREES_45;
                } else if (ratio < 4f) {
                    orientation = Orientation.DEGREES_70;
                } else {
                    orientation = Orientation.DOWN;
                }
            }
        }
        Logger.info("deltaX=" + deltaX + ",deltaY=" + deltaY + ",ratio=" + ratio + ",orientation=" + orientation.toString());
        return orientation;
    }

}
