package com.twoplayers.legend.basement;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.ImagesDungeon;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.dungeon.BasementInfo;
import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;

import java.util.ArrayList;
import java.util.List;

public class Basement {

    public Image image;
    protected Location basementLocationInTheDungeon;
    protected Coordinate linkExitCoordinateInTheDungeon;
    protected List<Item> items;

    /** Will be used when re-entering the dungeon */
    public DungeonInfo dungeonInfo;

    public Basement(ImagesDungeon imagesDungeon, BasementInfo basementInfo) {
        basementLocationInTheDungeon = basementInfo.basementLocationInTheDungeon;
        linkExitCoordinateInTheDungeon = basementInfo.linkExitCoordinateInTheDungeon;
        image = imagesDungeon.get("basement_item");
        items = new ArrayList<>();
        dungeonInfo = basementInfo.dungeonInfo;
        dungeonInfo.linkStartLocationInTheDungeon = basementInfo.basementLocationInTheDungeon;
        dungeonInfo.linkStartCoordinateInTheDungeon = basementInfo.linkExitCoordinateInTheDungeon;
        dungeonInfo.startMusic = false;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }
}
