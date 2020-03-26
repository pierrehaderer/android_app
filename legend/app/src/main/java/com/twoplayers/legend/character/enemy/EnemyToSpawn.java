package com.twoplayers.legend.character.enemy;

import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnemyToSpawn {

    public String name;
    public Class<? extends Enemy> enemyClass;
    public SpawnMode mode;
    public Map<Orientation, List<Coordinate>> spawnPossibilities;

    /**
     * Constructor
     */
    public EnemyToSpawn(String name, Map<String, Class<? extends Enemy>> enemyMap) {
        this.name = name;
        this.enemyClass = enemyMap.get(name);
        spawnPossibilities = new HashMap<>();
    }

    /**
     * Add a spawn possibility
     */
    public void addSpawnPossibility(Orientation orientation, String x, String y) {
        if (!spawnPossibilities.containsKey(orientation)) {
            spawnPossibilities.put(orientation, new ArrayList<Coordinate>());
        }
        float spawnX = LocationUtil.getXFromGrid(Integer.parseInt(x));
        float spawnY = LocationUtil.getYFromGrid(Integer.parseInt(y));
        spawnPossibilities.get(orientation).add(new Coordinate(spawnX, spawnY));
    }
}
