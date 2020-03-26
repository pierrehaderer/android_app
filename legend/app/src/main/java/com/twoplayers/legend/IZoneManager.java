package com.twoplayers.legend;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.util.Orientation;

import java.util.List;

public interface IZoneManager extends IManager {
    /** Initiate the change of the screen */
    void changeRoom(Orientation orientation);
    /** Check if a tile is a cave */
    boolean isTileACave(float x, float y);
    /** Return true if the mapScreen has been explored */
    boolean isExplored(int x, int y);
    /** Get the list of items on the screen */
    List<Item> getItems();
    /** Check if a tile is walkable */
    boolean isTileWalkable(float x, float y);
    /** Check if a position is valid */
    boolean isUpValid(float x, float y);
    /** Check if a position is valid */
    boolean isDownValid(float x, float y);
    /** Check if a position is valid */
    boolean isLeftValid(float x, float y);
    /** Check if a position is valid */
    boolean isRightValid(float x, float y);
    /** Get the image of the mini map */
    Image getMiniMap();
    /** Get abscisse for the mini map */
    float getCurrentMiniAbscissa();
    /** Get ordinate for the mini map */
    float getCurrentMiniOrdinate();
    /** is link too close to border to attack */
    boolean isLinkFarEnoughFromBorderToAttack(Link link);
    /** check if link can move up or down */
    boolean upAndDownAuthorized(Link link);
    /** check if link can move left or right */
    boolean leftAndRightAuthorized(Link link);
}
