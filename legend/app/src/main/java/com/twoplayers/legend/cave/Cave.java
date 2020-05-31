package com.twoplayers.legend.cave;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesCave;
import com.twoplayers.legend.assets.image.ImagesItem;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.npc.Npc;
import com.twoplayers.legend.map.CaveInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Cave {

    protected CaveType type;
    protected Npc npc;
    protected Location locationOnWorldMap;
    protected Coordinate exit;
    protected List<Item> items;

    protected Animation fireAnimation;
    protected Animation coinAnimation;

    public Cave(ImagesCave imagesCave, ImagesItem imagesItem, Graphics g, CaveInfo caveInfo) {
        type = caveInfo.type;
        locationOnWorldMap = caveInfo.entranceLocationOnTheWorldMap;
        exit = caveInfo.exitCoordinateOnTheWorldMap;

        items = new ArrayList<>();

        npc = new Npc(imagesCave, caveInfo.npcName, caveInfo.message1, caveInfo.message2, caveInfo.message3);
        npc.name = caveInfo.npcName;
        Logger.info("Loading cave with NPC '" + npc.name + "'");


        fireAnimation = g.newAnimation();
        fireAnimation.addFrame(imagesCave.get("fire_1"), AllImages.COEF, 10);
        fireAnimation.addFrame(imagesCave.get("fire_2"), AllImages.COEF, 10);
        coinAnimation = g.newAnimation();
        coinAnimation.addFrame(imagesItem.get("red_rupee"), AllImages.COEF, 15);
        coinAnimation.addFrame(imagesItem.get("blue_rupee"), AllImages.COEF, 15);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void updateAnimations(float deltaTime) {
        fireAnimation.update(deltaTime);
        coinAnimation.update(deltaTime);
        for (Item item : items) {
            if (item.animation != null) {
                item.animation.update(deltaTime);
            }
        }
    }
}
