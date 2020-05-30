package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class EnemyBoomerang extends Missile {

    public static final float INITIAL_BOOMERANG_COUNTER = 85f;

    public static final float INITIAL_SPEED = 4f;
    private static final float DAMAGE = -1f;

    public float counter;
    public boolean isMovingForward;

    public EnemyBoomerang(MissileService missileService) {
        super(missileService);
    }

    @Override
    public void init(IImages imagesEnemyWorldMap, Graphics g) {
        initAnimations(imagesEnemyWorldMap, g);
        hitbox = new Hitbox(x,y,0,0,8,8);
        isActive = true;
        damage = DAMAGE;
        speed = INITIAL_SPEED;
        isBlockedByObstacle = true;
        counter = INITIAL_BOOMERANG_COUNTER;
        isMovingForward = true;
        currentAnimation = animations.get(orientation);
    }

    /**
     * Init animations
     */
    public void initAnimations(IImages imagesEnemyWorldMap, Graphics g) {
        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_1"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_2"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_3"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_4"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_5"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_6"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_7"), AllImages.COEF, 4f);
        animation.addFrame(imagesEnemyWorldMap.get("wood_boomerang_8"), AllImages.COEF, 4f);
        animations = new HashMap<>();
        animations.put(Orientation.UP, animation);
        animations.put(Orientation.DOWN, animation);
        animations.put(Orientation.LEFT, animation);
        animations.put(Orientation.RIGHT, animation);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        missileService.moveBoomerang(this, deltaTime);
        missileService.handleBoomerangHits(this);
        missileService.handleBoomerangBackToSender(this);
    }

    @Override
    public void hasHitLink() {
        isMovingForward = false;
    }
}
