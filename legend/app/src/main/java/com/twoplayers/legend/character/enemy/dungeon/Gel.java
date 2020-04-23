package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.MoveOnTileEnemy;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.Orientation;

public abstract class Gel extends MoveOnTileEnemy {

    private static final int TIME_BEFORE_FIRST_MOVE = 36;
    private static final float MIN_PAUSE = 20f;
    private static final float MAX_PAUSE = 80f;
    private static final float SPEED = 0.8f;

    protected Animation initialAnimation;

    private boolean shouldInitialize;
    private float timeBeforeFirstMove;

    private float pauseBeforeNextTile;

    public Gel(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        shouldInitialize = true;
        timeBeforeFirstMove = TIME_BEFORE_FIRST_MOVE;
        isLethal = false;
        isActive = false;
        life = 1;
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 3, 3, 11, 11);
        damage = -0.5f;
        currentAnimation = initialAnimation;
    }

    /**
     * Init enemy animations
     */
    protected abstract void initAnimations(Graphics g);

    @Override
    public void update(float deltaTime, Graphics g) {
        super.update(deltaTime, g);
        // Init
        if (shouldInitialize) {
            shouldInitialize = false;
            nextTileX = x;
            nextTileY = y;
            Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
            pauseBeforeNextTile = choosePauseBeforeNextTile();
            nextNextTileX = destination.x;
            nextNextTileY = destination.y;
            nextOrientation = destination.orientation;
        }

        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
            if (timeBeforeFirstMove <= 0) {
                isLethal = true;
                isActive = true;
                currentAnimation = moveAnimations.get(Orientation.UP);
            }
        }

        if (!isDead && isActive) {
            if (pauseBeforeNextTile > 0) {
                pauseBeforeNextTile -= deltaTime;
            } else {
                // The enemy moves
                float remainingMoves = deltaTime * SPEED;
                remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                if (remainingMoves > 0) {
                    nextTileX = nextNextTileX;
                    nextTileY = nextNextTileY;
                    orientation = nextOrientation;
                    Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
                    pauseBeforeNextTile = choosePauseBeforeNextTile();
                    nextNextTileX = destination.x;
                    nextNextTileY = destination.y;
                    nextOrientation = destination.orientation;
                }
            }
            currentAnimation.update(deltaTime);
        }

    }

    /**
     * One chance out of 2 to continue moving immediately
     */
    private float choosePauseBeforeNextTile() {
        boolean doNotStay = (Math.random() > 0.5);
        return (doNotStay) ? 5f : MIN_PAUSE + ((MAX_PAUSE - MIN_PAUSE) * (float) Math.random());
    }

    @Override
    public void isHitByBoomerang() {
        isWounded(1, new Hitbox(), Orientation.UP);
    }
}
