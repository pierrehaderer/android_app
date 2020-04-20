package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.character.enemy.MoveOnTileEnemy;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.EnemyUtil;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.Logger;

import java.util.HashMap;

public class Stalfos extends MoveOnTileEnemy {

    private static final int TIME_BEFORE_FIRST_MOVE = 36;
    private static final float SPEED = 0.6f;

    private Animation initialAnimation;

    private boolean initNotDone;
    private float timeBeforeFirstMove;
    private float immobilisationCounter;

    public Stalfos(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        initNotDone = true;
        timeBeforeFirstMove = TIME_BEFORE_FIRST_MOVE;
        isLethal = false;
        isActive = false;
        life = 2;
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 3, 3, 11, 11);
        damage = -0.5f;
        immobilisationCounter = 0;
        currentAnimation = initialAnimation;
    }

    /**
     * Init enemy animations
     */
    protected void initAnimations(Graphics g) {
        EnemyUtil enemyUtil = new EnemyUtil();
        initialAnimation = enemyUtil.getFastCloudAnimation(imagesEnemy, g);

        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemy.get("skeleton_1"), AllImages.COEF, 15);
        animation.addFrame(imagesEnemy.get("skeleton_2"), AllImages.COEF, 15);
        moveAnimations = new HashMap<>();
        moveAnimations.put(Orientation.UP, animation);
        moveAnimations.put(Orientation.DOWN, animation);
        moveAnimations.put(Orientation.LEFT, animation);
        moveAnimations.put(Orientation.RIGHT, animation);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        super.update(deltaTime, g);
        // Init
        if (initNotDone) {
            initNotDone = false;
            nextTileX = x;
            nextTileY = y;
            Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
            nextNextTileX = destination.x;
            nextNextTileY = destination.y;
            nextOrientation = destination.orientation;
        }

        // Move hitbox away when enemy is dead
        if (isDead) {
            hitbox.x = 0;
            hitbox.y = 0;
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
        } else {
            if (immobilisationCounter > 0) {
                immobilisationCounter -= deltaTime;
                if (immobilisationCounter <= 0) {
                    isLethal = true;
                }
            } else {
                // The enemy moves
                float remainingMoves = deltaTime * SPEED;
                remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                while (remainingMoves > 0) {
                    //Logger.debug("Stalfos is on a new Tile (" + x + "," + y + ")");
                    nextTileX = nextNextTileX;
                    nextTileY = nextNextTileY;
                    orientation = nextOrientation;
                    Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
                    nextNextTileX = destination.x;
                    nextNextTileY = destination.y;
                    nextOrientation = destination.orientation;
                    remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                }
            }
            currentAnimation.update(deltaTime);
        }

    }

    @Override
    public void isHitByBoomerang() {
        soundEffectManager.play("enemy_wounded");
        if (isActive) {
            immobilisationCounter = Enemy.INITIAL_IMMOBILISATION_COUNTER;
            isLethal = false;
        }
    }
}
