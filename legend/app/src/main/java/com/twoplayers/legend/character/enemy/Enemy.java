package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.map.Orientation;
import com.twoplayers.legend.map.WorldMapManager;

import java.util.Map;

public abstract class Enemy {

    protected ImagesEnemyWorldMap imagesEnemyWorldMap;

    public float x;
    public float y;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> animations;

    public Enemy(ImagesEnemyWorldMap imagesEnemyWorldMap, Graphics g) {
        this.imagesEnemyWorldMap = imagesEnemyWorldMap;
    }

    public abstract void update(float deltaTime, Graphics g, WorldMapManager worldMapManager);
}
