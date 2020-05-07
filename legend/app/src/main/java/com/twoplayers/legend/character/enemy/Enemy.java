package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.util.Logger;

import java.util.Map;

public abstract class Enemy {

    public static final float PAUSE_BEFORE_FIRST_MOVE = 300f;
    public static final float INITIAL_STUN_COUNTER = 300f;
    public static final float INITIAL_INVINCIBLE_COUNTER = 40f;
    public static final float MIN_TIME_BEFORE_ATTACK = 300.0f;
    public static final float MAX_TIME_BEFORE_ATTACK = 700.0f;
    public static final float PAUSE_BEFORE_ATTACK = 100f;
    public static final float MIN_TIME_FOR_MOVING_PAUSE = 20f;
    public static final float MAX_TIME_FOR_MOVING_PAUSE = 80f;
    public static final double PROBABILITY_TO_CONTINUE_MOVING = 0.5;

    protected IZoneManager zoneManager;
    protected LinkManager linkManager;
    protected IEnemyManager enemyManager;
    protected SoundEffectManager soundEffectManager;

    protected EnemyService enemyService;

    public float x;
    public float y;
    public Hitbox hitbox;
    protected float timeBeforeFirstMove;

    protected boolean isActive;
    protected boolean isLethal;
    protected boolean hasBeenHit;
    public boolean isDead;
    protected float damage;
    protected int life;

    // For enemies that can be hurt
    protected boolean isInvincible;
    protected float invicibleCounter;

    // For enemies that moves on tiles
    protected float speed;
    public Orientation orientation;
    protected float nextTileX;
    protected float nextTileY;
    protected float pauseBeforeNextTile;
    protected boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;

    // For enemies that are attacking
    protected float timeBeforeAttack;
    protected boolean isAttacking;

    // For enemies that can be stunned
    protected boolean hasBeenStunned;
    protected boolean isStunned;
    protected float stunCounter;

    // For enemies that are spawning
    protected boolean isSpawning;
    protected boolean hasSpawned;
    protected float spawnCounter;

    public Animation currentAnimation;
    protected Animation initialAnimation;
    protected Animation deathAnimation;
    protected Map<Orientation, Animation> moveAnimations;

    public Enemy(SoundEffectManager s, IZoneManager z, LinkManager l, IEnemyManager em, EnemyService es) {
        this.zoneManager = z;
        this.linkManager = l;
        this.enemyManager = em;
        this.soundEffectManager = s;
        this.enemyService = es;

        x = 0;
        y = 0;
        hitbox = new Hitbox();
        timeBeforeFirstMove = 0;

        isActive = false;
        isLethal = false;
        hasBeenHit = false;
        isDead = false;
        damage = 0;
        life = 0;

        isInvincible = true;
        invicibleCounter = 0;

        orientation = Orientation.UP;
        nextTileX = 0;
        nextTileY = 0;
        isPushed = false;
        pushX = 0;
        pushY = 0;
        pushCounter = 0;

        timeBeforeAttack = 0;
        isAttacking = false;

        hasBeenStunned = false;
        isStunned = false;
        stunCounter = 0;

        isSpawning = false;
        hasSpawned = false;
        spawnCounter = 0;


    }

    /**
     * Initialize enemy after spawn
     */
    public abstract void init(IImagesEnemy imagesEnemy, Graphics g);

    /**
     * Update the enemy has it is existing in the room
     */
    public abstract void update(float deltaTime, Graphics g);

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitByBoomerang() {
        hasBeenStunned = true;
    }

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitBySword(Sword sword) {
        enemyService.handleEnemyIsWounded(this, sword.getType().damage, sword.getHitbox(), sword.getOrientation());
    }

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitByThrowingSword(ThrowingSword throwingSword) {
        enemyService.handleEnemyIsWounded(this, throwingSword.getSword().getType().damage, throwingSword.getHitbox(), throwingSword.getOrientation());
    }

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitByArrow(Arrow arrow) {
        enemyService.handleEnemyIsWounded(this, arrow.getType().damage, arrow.getHitbox(), arrow.getOrientation());
    }

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitByBomb(Bomb bomb) {
        enemyService.handleEnemyIsWounded(this, Bomb.DAMAGE, bomb.getHitbox(), Orientation.ANY);
    }

    /**
     * This method is overridden if something special happens to the enemy
     */
    public void isHitByFire(Fire fire) {
        enemyService.handleEnemyIsWounded(this, Fire.DAMAGE_TO_ENEMY, fire.getHitbox(), fire.getOrientation());
    }

    /**
     * This method is overridden if something special happens to the enemy which is hitting link
     */
    public void hasHitLink() {
        Logger.info("This enemy has hit link.");
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public boolean isLethal() {
        // Enemy becomes shortly lethal when it has been hit by link
        return isLethal || hasBeenHit;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public boolean isActive() {
        return isActive;
    }
}
