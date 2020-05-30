package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;

public class Rock extends Missile {

    private static final float SPEED = 3f;
    private static final float DAMAGE = -0.5f;

    /**
     * Constructor
     */
    public Rock(MissileService missileService) {
        super(missileService);
    }

    @Override
    public void init(IImages imagesEnemyWorldMap, Graphics g) {
        initAnimations(imagesEnemyWorldMap, g);
        hitbox = new Hitbox(x,y,0,0,8,8);
        isActive = true;
        damage = DAMAGE;
        speed = SPEED;
        isBlockedByObstacle = true;
        currentAnimation = animations.get(orientation);
    }

    /**
     * Init animations
     */
    public void initAnimations(IImages imagesEnemyWorldMap, Graphics g) {
        Animation animation = g.newAnimation();
        animation.addFrame(imagesEnemyWorldMap.get("empty"), AllImages.COEF, 5f);
        animation.addFrame(imagesEnemyWorldMap.get("rock"), AllImages.COEF, 20f);
        animation.setOccurrences(1);
        animations = new HashMap<>();
        animations.put(Orientation.UP, animation);
        animations.put(Orientation.DOWN, animation);
        animations.put(Orientation.LEFT, animation);
        animations.put(Orientation.RIGHT, animation);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        missileService.moveStraightMissile(this, deltaTime);
        missileService.handleStraightMissileHits(this);
    }
}
