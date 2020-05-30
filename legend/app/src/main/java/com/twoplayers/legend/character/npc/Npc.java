package com.twoplayers.legend.character.npc;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.IImages;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.LocationUtil;

public class Npc {

    public String name;
    public Image image;

    public float x;
    public float y;
    public Hitbox hitbox;

    public String message1;
    public String message2;
    public String message3;
    public String displayedMessage1;
    public String displayedMessage2;
    public String displayedMessage3;

    public Npc(IImages images,  String name, String message1, String message2, String message3) {
        image = images.get(name);
        x = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
        y = LocationUtil.getYFromGrid(4);
        hitbox = new Hitbox(x, y, 5, 5, 6, 6);
        this.message1 = message1;
        this.message2 = message2;
        this.message3 = message3;
        reset();
    }

    public void reset() {
        displayedMessage1 = "";
        displayedMessage2 = "";
        displayedMessage3 = "";
    }
}
