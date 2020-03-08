package com.twoplayers.legend.character.npc;

import com.kilobolt.framework.Image;
import com.twoplayers.legend.character.Hitbox;

public class Npc {

    public NpcType type;
    public Image image;

    public float x;
    public float y;
    public Hitbox hitbox;

    public Npc() {
        hitbox = new Hitbox(0, 0, 5, 5, 6, 6);
    }
}
