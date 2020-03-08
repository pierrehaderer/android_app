package com.twoplayers.legend;

import com.kilobolt.framework.Graphics;

public interface ILocationManager extends IManager {
    /** Initiate the change of the screen */
    public void changeScreen(Orientation orientation);
    /** Check if a tile is a cave */
    public boolean isTileACave(float x, float y);
    /** Check if a tile is walkable */
    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound);
}
