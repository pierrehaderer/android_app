package com.twoplayers.legend.dungeon;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

public class Dungeon {

    protected String id;
    protected Location location;
    protected Coordinate exit;

    protected Animation fireAnimation;

    public Dungeon(IImages imagesDungeon, Graphics g, DungeonInfo dungeonInfo) {
        id = dungeonInfo.dungeonId;
        location = dungeonInfo.entranceLocationOnTheWorldMap;
        exit = dungeonInfo.exitCoordinateOnTheWorldMap;

        fireAnimation = g.newAnimation();
        fireAnimation.addFrame(imagesDungeon.get("fire_1"), AllImages.COEF, 10);
        fireAnimation.addFrame(imagesDungeon.get("fire_2"), AllImages.COEF, 10);
    }
}
