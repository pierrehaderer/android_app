package com.twoplayers.legend.map;

import java.util.ArrayList;
import java.util.List;

public class CaveInfo extends EntranceInfo {

    public static final String DEFAULT_NPC = "empty";

    public String message1;
    public String message2;
    public String npcName;
    public List<String> itemsAndPrices;

    public CaveInfo() {
        super();
        itemsAndPrices = new ArrayList<>();
    }
}
