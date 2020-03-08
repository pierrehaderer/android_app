package com.twoplayers.legend.character;

import com.kilobolt.framework.Image;

public class Item {

    public String name;
    public Image image;

    public float x;
    public float y;
    public Hitbox hitbox;

    public Item() {
        hitbox = new Hitbox(0, 0, 5, 5, 6, 6);
    }
}
