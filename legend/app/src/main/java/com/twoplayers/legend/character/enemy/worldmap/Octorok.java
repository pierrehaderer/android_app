package com.twoplayers.legend.character.enemy.worldmap;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.AttackingEnemy;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.util.Destination;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.Map;

public abstract class Octorok extends AttackingEnemy {

    private static final float PAUSE_BEFORE_FIRST_MOVE = 300f;
    private static final float PAUSE_BEFORE_ATTACK = 100f;
    private static final float MIN_TIME_BEFORE_ATTACK = 300.0f;
    private static final float MAX_TIME_BEFORE_ATTACK = 700.0f;

    protected Map<Orientation, Animation> animations;

    private boolean initNotDone;
    private float timeBeforeFirstMove;
    private boolean isImmobilised;
    private float immobilisationCounter;

    protected float speed;


    public Octorok(IImagesEnemy i, SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager e, EnemyService es, Graphics g) {
        super(i, s, z, l, e, es, g);
        initAnimations(g);
        initNotDone = true;
        timeBeforeFirstMove = (float) Math.random() * PAUSE_BEFORE_FIRST_MOVE;
        isContactLethal = false;
        isActive = false;
        isInvincible = true;
        chooseTimeBeforeAttack();
        orientation = Orientation.UP;
        nextOrientation = Orientation.UP;
        hitbox = new Hitbox(0, 0, 3, 3, 11, 11);
        contactDamage = -0.5f;
        immobilisationCounter = 0;
        currentAnimation = animations.get(Orientation.INIT);
    }

    /**
     * Initialise the move animations
     */
    protected abstract void initAnimations(Graphics g);

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

        // The enemy appears
        if (timeBeforeFirstMove > 0) {
            timeBeforeFirstMove -= deltaTime;
            if (timeBeforeFirstMove <= 60) {
                currentAnimation.update(deltaTime);
            }
            if (timeBeforeFirstMove <= 0) {
                isContactLethal = true;
                isInvincible = false;
                isActive = true;
            }
        }

        // The enemy is immobilized
        if (isImmobilised) {
            immobilisationCounter -= deltaTime;
            if (immobilisationCounter <= 0) {
                isImmobilised = false;
                isContactLethal = true;
            }
        }

        if (isPushed) {
            Logger.info("Enemy is pushed, remaining counter : " + pushCounter);
            float distance = Math.min(deltaTime * PUSH_SPEED, pushCounter);
            pushCounter -= distance;

            float deltaY = pushY * distance;
            boolean pushed = false;
            if ((deltaY < 0 && zoneManager.isUpValid(x, y + deltaY)) || (deltaY > 0 && zoneManager.isDownValid(x, y + deltaY))){
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
            if (!pushed || pushCounter == 0) {
                isPushed = false;
            }
        }

        if (isAttacking && !isImmobilised) {
            // The enemy attacks
            timeBeforeAttack -= deltaTime;
            if (timeBeforeAttack < 0) {
                Logger.info("Octorok is attacking (" + x + "," + y + ")");
                isAttacking = false;
                // TODO ATTACK !!!!!
                chooseTimeBeforeAttack();
            }
        }

        // The enemy moves
        if (isActive && !isAttacking && !isImmobilised) {
            float remainingMoves = deltaTime * speed;
            remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
            while (remainingMoves > 0) {
                Logger.debug("Octorok is on a new Tile (" + x + "," + y + ")");
                nextTileX = nextNextTileX;
                nextTileY = nextNextTileY;
                orientation = nextOrientation;
                currentAnimation = animations.get(orientation);
                Destination destination = enemyService.chooseNextNextTile(orientation, nextTileX, nextTileY);
                nextNextTileX = destination.x;
                nextNextTileY = destination.y;
                nextOrientation = destination.orientation;
                remainingMoves = enemyService.goToNextTile(orientation, this, remainingMoves, nextTileX, nextTileY);
                // The enemy wants to attack check its position first : on tile or half tile only
                if (timeBeforeAttack > PAUSE_BEFORE_ATTACK) {
                    timeBeforeAttack -= deltaTime;
                } else {
                    float deltaX = x - LocationUtil.getXFromGrid(LocationUtil.getTileXFromPositionX(x));
                    float deltaY = x - LocationUtil.getYFromGrid(LocationUtil.getTileYFromPositionY(y));
                    if (remainingMoves > 0
                            || Math.abs(LocationUtil.HALF_TILE_SIZE - deltaX) < ATTACK_TOLERANCE
                            || Math.abs(LocationUtil.HALF_TILE_SIZE - deltaY) < ATTACK_TOLERANCE) {
                        isAttacking = true;
                        remainingMoves = 0;
                    }
                }
            }
        }

        if (timeBeforeFirstMove <= 0) {
            currentAnimation.update(deltaTime);
        }

    }

    /**
     * Randomly choose a duration before the next attack
     */
    private void chooseTimeBeforeAttack() {
        timeBeforeAttack = (float) ((MAX_TIME_BEFORE_ATTACK - MIN_TIME_BEFORE_ATTACK) * Math.random() + MIN_TIME_BEFORE_ATTACK);
    }

    @Override
    public void isHitByBoomerang() {
        soundEffectManager.play("enemy_wounded");
        if (isActive) {
            isImmobilised = true;
            immobilisationCounter = Enemy.INITIAL_IMMOBILISATION_COUNTER;
            isContactLethal = false;
        }
    }
}
