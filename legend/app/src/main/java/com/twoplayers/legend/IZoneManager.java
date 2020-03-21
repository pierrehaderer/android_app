package com.twoplayers.legend;

import com.twoplayers.legend.character.Item;

import java.util.List;

public interface IZoneManager extends IManager {
    /** Initiate the change of the screen */
    public void changeRoom(Orientation orientation);
    /** Check if a tile is a cave */
    public boolean isTileACave(float x, float y);
    /** Return true if the mapScreen has been explored */
    public boolean isExplored(int x, int y);
    /** Get abscisse for the mini map */
    public float getCurrentMiniAbscissa();
    /** Get ordinate for the mini map */
    public float getCurrentMiniOrdinate();
    /** Get the list of items on the screen */
    public List<Item> getItems();
    /** Check if a tile is walkable */
    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound);
    /** Check if a position is valid */
    public boolean isUpValid(float x, float y);
    /** Check if a position is valid */
    public boolean isDownValid(float x, float y);
    /** Check if a position is valid */
    public boolean isLeftValid(float x, float y);
    /** Check if a position is valid */
    public boolean isRightValid(float x, float y);
}
