package com.twoplayers.legend.dungeon;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.ImagesDungeon;

public class DoorCache {
    protected Image up;
    protected Image down;
    protected Image left;
    protected Image right;

    public DoorCache(ImagesDungeon imagesDungeon) {
        up = imagesDungeon.get("empty");
        down = imagesDungeon.get("empty");
        left = imagesDungeon.get("empty");
        right = imagesDungeon.get("empty");

    }
}
