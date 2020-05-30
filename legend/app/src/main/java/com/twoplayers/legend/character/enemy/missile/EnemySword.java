package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class EnemySword extends Missile {

    private static final float SPEED = 2.5f;
    private static final float DAMAGE = -2f;

    public EnemySword(MissileService missileService) {
        super(missileService);
    }

    @Override
    public void init(IImages imagesEnemyWorldMap, Graphics g) {
        initAnimations(imagesEnemyWorldMap, g);
        hitbox = new Hitbox(x,y,0,0,8,8);
        isActive = true;
        damage = DAMAGE;
        speed = SPEED;
        isBlockedByObstacle = false;
        currentAnimation = animations.get(orientation);
    }

    /**
     * Init animations
     */
    public void initAnimations(IImages imagesEnemyWorldMap, Graphics g) {
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesEnemyWorldMap.get("sword_up_1"), AllImages.COEF, 5f);
        animationUp.addFrame(imagesEnemyWorldMap.get("sword_up_2"), AllImages.COEF, 5f);
        animationUp.addFrame(imagesEnemyWorldMap.get("sword_up_3"), AllImages.COEF, 5f);
        animationUp.addFrame(imagesEnemyWorldMap.get("sword_up_4"), AllImages.COEF, 5f);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesEnemyWorldMap.get("sword_down_1"), AllImages.COEF, 5f);
        animationDown.addFrame(imagesEnemyWorldMap.get("sword_down_2"), AllImages.COEF, 5f);
        animationDown.addFrame(imagesEnemyWorldMap.get("sword_down_3"), AllImages.COEF, 5f);
        animationDown.addFrame(imagesEnemyWorldMap.get("sword_down_4"), AllImages.COEF, 5f);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesEnemyWorldMap.get("sword_left_1"), AllImages.COEF, 5f);
        animationLeft.addFrame(imagesEnemyWorldMap.get("sword_left_2"), AllImages.COEF, 5f);
        animationLeft.addFrame(imagesEnemyWorldMap.get("sword_left_3"), AllImages.COEF, 5f);
        animationLeft.addFrame(imagesEnemyWorldMap.get("sword_left_4"), AllImages.COEF, 5f);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesEnemyWorldMap.get("sword_right_1"), AllImages.COEF, 5f);
        animationRight.addFrame(imagesEnemyWorldMap.get("sword_right_2"), AllImages.COEF, 5f);
        animationRight.addFrame(imagesEnemyWorldMap.get("sword_right_3"), AllImages.COEF, 5f);
        animationRight.addFrame(imagesEnemyWorldMap.get("sword_right_4"), AllImages.COEF, 5f);
        animations = new HashMap<>();
        animations.put(Orientation.UP, animationUp);
        animations.put(Orientation.DOWN, animationDown);
        animations.put(Orientation.LEFT, animationLeft);
        animations.put(Orientation.RIGHT, animationRight);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        missileService.moveStraightMissile(this, deltaTime);
        missileService.handleStraightMissileHits(this);
    }
}
