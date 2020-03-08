package com.twoplayers.legend.cave;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesCave;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.npc.Npc;
import com.twoplayers.legend.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Cave {

    protected String message;
    protected Npc npc;
    protected Coordinate location;
    protected Coordinate entrance;
    protected List<Item> items;
    protected List<Integer> itemPrices;

    protected Animation fireAnimation;

    public Cave(ImagesCave imagesCave, Graphics g) {
        items = new ArrayList<>();
        itemPrices = new ArrayList<>();

        fireAnimation = g.newAnimation();
        fireAnimation.addFrame(imagesCave.get("fire_1"), AllImages.COEF, 5);
        fireAnimation.addFrame(imagesCave.get("fire_2"), AllImages.COEF, 5);
    }

    public void addItem(Item item, Integer price) {
        this.items.add(item);
        this.itemPrices.add(price);
    }
}
