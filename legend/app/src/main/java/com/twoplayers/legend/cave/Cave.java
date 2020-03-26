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

import java.util.ArrayList;
import java.util.List;

public class Cave {

    protected String message1;
    protected String message2;
    protected String displayedMessage1;
    protected String displayedMessage2;
    protected Npc npc;
    protected Location location;
    protected Coordinate entrance;
    protected List<Item> items;

    protected Animation fireAnimation;
    protected Animation coinAnimation;

    public Cave(ImagesCave imagesCave, ImagesItem imagesItem, Graphics g, CaveInfo caveInfo) {
        message1 = caveInfo.message1;
        message2 = caveInfo.message2;
        location = caveInfo.location;
        entrance = caveInfo.entrance;

        items = new ArrayList<>();
        displayedMessage1 = "";
        displayedMessage2 = "";

        fireAnimation = g.newAnimation();
        fireAnimation.addFrame(imagesCave.get("fire_1"), AllImages.COEF, 10);
        fireAnimation.addFrame(imagesCave.get("fire_2"), AllImages.COEF, 10);
        coinAnimation = g.newAnimation();
        coinAnimation.addFrame(imagesItem.get("coin_orange"), AllImages.COEF, 15);
        coinAnimation.addFrame(imagesItem.get("coin_blue"), AllImages.COEF, 15);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }
}
