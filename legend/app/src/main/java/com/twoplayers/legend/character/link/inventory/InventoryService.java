package com.twoplayers.legend.character.link.inventory;

import com.twoplayers.legend.character.link.Link;

public class InventoryService {

    public int findPickAnimation(String itemName) {
        if ("raft".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        if ("ladder".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        if ("triforce".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        return Link.PICK_ANIMATION_SMALL;
    }

}
