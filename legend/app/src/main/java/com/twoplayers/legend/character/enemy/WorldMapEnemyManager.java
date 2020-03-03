package com.twoplayers.legend.character.enemy;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.AllSoundEffects;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorldMapEnemyManager implements IManager {

    private WorldMapManager worldMapManager;
    private ImagesEnemyWorldMap imagesEnemyWorldMap;
    private AllSoundEffects allSoundEffects;
    private Properties enemiesProperties;

    private boolean loadingEnemies;
    private List<Enemy> enemies;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        worldMapManager = ((MainActivity) game).getWorldMapManager();

        imagesEnemyWorldMap = ((MainActivity) game).getAllImages().getImagesEnemyWorldMap();
        imagesEnemyWorldMap.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        allSoundEffects = ((MainActivity) game).getAllSoundEffects();

        EnemyType.initHashMap();
        enemiesProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "enemy/world_map_enemies.properties");

        loadingEnemies = false;
        enemies = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (loadingEnemies) {
            String enemiesAsString = enemiesProperties.getProperty(String.valueOf(worldMapManager.getCurrentAbsisse()) + worldMapManager.getCurrentOrdinate());
            Logger.info("Enemies in properties : " + enemiesAsString);
            if (enemiesAsString != null) {
                for (String enemyAsString : enemiesAsString.split("\\|")) {
                    try {
                        EnemyType enemyType = EnemyType.getEnum(enemyAsString);
                        if (enemyType != null) {
                            Enemy enemy = enemyType.clazz.getConstructor(ImagesEnemyWorldMap.class, Graphics.class).newInstance(imagesEnemyWorldMap, g);
                            Coordinate coordinate = worldMapManager.findSpawnableCoordinate();
                            Logger.debug("Spawning " + enemyAsString + " at (" + coordinate.x + "," + coordinate.y + ")");
                            enemy.x = coordinate.x;
                            enemy.y = coordinate.y;
                            enemy.hitbox.relocate(enemy.x, enemy.y);
                            enemies.add(enemy);
                        } else {
                            Logger.error("Could not find the enemy type : " + enemyAsString);
                        }
                    } catch (Exception e) {
                        Logger.error("Could not create enemy class with type : " + enemyAsString);
                    }
                }
            }
            loadingEnemies = false;
        }
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, g, worldMapManager);
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        for (Enemy enemy : enemies) {
            g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y));
            //g.drawRect((int) enemy.hitbox.x, (int) enemy.hitbox.y, (int) enemy.hitbox.width, (int) enemy.hitbox.height, Color.GREEN);
        }
    }

    /**
     * Ask for the loading of enemies
     */
    public void willLoadEnemies() {
        loadingEnemies = true;
    }

    /**
     * Unload enemies
     */
    public void unloadEnemies() {
        loadingEnemies = false;
        enemies.clear();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
