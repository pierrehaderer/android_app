package com.twoplayers.legend.character.npc;

import java.util.HashMap;

public enum NpcType {
    NONE("empty"),
    OLD_MAN("old_man_1");

    public String name;

    private static HashMap<String, NpcType> hashMap;

    private NpcType(String name) {
        this.name = name;
    }


    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (NpcType npcType : NpcType.values()) {
            hashMap.put(npcType.name, npcType);
        }
    }

    public static NpcType getEnum(String name) {
        return hashMap.get(name);
    }
}
