package com.twoplayers.legend;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Orientation;

import java.util.List;

public interface IZoneManager extends IManager {
    /** Initiate the change of the screen */
    void changeRoom(Orientation orientation);
    /** Check if a tile is a door */
    boolean isTileADoor(float x, float y);
    /** Check if a tile is stairs */
    boolean isTileStairs(float x, float y);
    /** Check if a tile is a bomb hole */
    boolean isTileABombHole(float x, float y);
    /** Check if a tile is walkable */
    boolean isTileWalkable(float x, float y);
    /** Check if a tile is blocking missiles */
    boolean isTileBlockingMissile(float x, float y);
    /** Check if a door is in front of link */
    boolean checkKeyDoor(Orientation orientation, float x, float y);
    /** Open the door in front of link */
    void openKeyDoor(Orientation orientation);
    /** Check if a pushable bloc is in front of link */
    boolean checkPushableBlock(Orientation orientation, float x, float y);
    /** Try to push the block in front of link */
    void pushBloc(Orientation orientation);
    /** Return true if the mapScreen has been explored */
    boolean isExplored(int x, int y);
    /** Get the list of items on the screen */
    List<Item> getItems();
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
    /** has the throwing sword hit the border */
    boolean hasThrowingSwordHitBorder(ThrowingSword throwingSword);
    /** has the rod wave hit the border */
    boolean hasRodWaveHitBorder(RodWave rodWave);
    /** check if link can move up or down */
    boolean upAndDownAuthorized(Link link);
    /** check if link can move left or right */
    boolean leftAndRightAuthorized(Link link);
    /** Find spawnable corrdinate for an enemy */
    Coordinate findSpawnableCoordinate();
    /** Find spawnable corrdinate for an water enemy */
    Coordinate findSpawnableCoordinateInWater();
    /** Burn the bushes under the fire if any */
    void fireHasJustFinished(Fire fire);
    /** Inform that a bomb has exploded */
    void bombHasExploded(Bomb bomb);
}
