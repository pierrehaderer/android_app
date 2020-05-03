package com.twoplayers.legend.character.enemy.missile;

import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.util.Orientation;

public class EmptyMissile extends Missile {

    public EmptyMissile(MissileService missileService) {
        super(missileService);
    }

    @Override
    public void init(IImagesEnemy imagesEnemyWorldMap, Graphics g) {
        orientation = Orientation.UP;
        hitbox = new Hitbox();
        isActive = false;
    }

    @Override
    public void update(float deltaTime, Graphics g) {
    }
}
