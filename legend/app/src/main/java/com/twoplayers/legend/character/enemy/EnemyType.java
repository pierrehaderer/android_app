package com.twoplayers.legend.character.enemy;

import java.util.HashMap;

public enum EnemyType {
    OCTOROK("Octorok", Octorok.class);

    public Class<? extends Enemy> clazz;
    public String name;

    private static HashMap<String, EnemyType> hashMap;

    private EnemyType(String name, Class<? extends Enemy> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public static void initHashMap() {
        hashMap = new HashMap<>();
        for (EnemyType enemyType : EnemyType.values()) {
            hashMap.put(enemyType.name, enemyType);
        }
    }

    public static EnemyType getEnum(String c) {
        return hashMap.get(c);
    }
}
