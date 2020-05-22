package com.twoplayers.legend.dungeon;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.ImagesDungeon;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Orientation;

public class DungeonBloc {

    public static final float BLOC_SPEED = 1.3f;

    public boolean hasBeenPushed;
    public float count;
    public float x;
    public float y;
    public Orientation orientation;
    public Location initialLocation;
    public Location newLocation;
    public Image blocImage;
    public Image floorImage;


    public DungeonBloc(ImagesDungeon imagesDungeon, Location location, String dungeonId) {
        this.hasBeenPushed = false;
        this.count = 0;
        this.orientation = Orientation.UP;
        this.initialLocation = location;
        this.newLocation = location;
        this.x = this.initialLocation.x * LocationUtil.TILE_SIZE;
        this.y = this.initialLocation.y * LocationUtil.TILE_SIZE;
        this.blocImage = imagesDungeon.get("bloc_" + dungeonId);
        this.floorImage = imagesDungeon.get("floor_" + dungeonId);
    }

    /**
     * Reset the bloc when link leaves the room
     */
    public void reset() {
        this.hasBeenPushed = false;
        this.count = 0;
        this.newLocation = this.initialLocation;
        this.x = this.initialLocation.x * LocationUtil.TILE_SIZE;
        this.y = this.initialLocation.y * LocationUtil.TILE_SIZE;
    }
}
