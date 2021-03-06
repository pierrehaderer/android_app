package com.twoplayers.legend.character;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Image;

public class Item {

    public String name;
    public Image image;
    public Animation animation;
    public int pickAnimation;
    public boolean hidden;

    public float x;
    public float y;
    public Hitbox hitbox;

    public int price;

    public Item() {
        hidden = false;
        hitbox = new Hitbox(0, 0, 5, 5, 6, 6);
    }

    public void hideItem() {
        hidden = true;
        hitbox = new Hitbox();
    }
}
