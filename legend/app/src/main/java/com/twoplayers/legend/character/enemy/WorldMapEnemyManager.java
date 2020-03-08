package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesEnemyWorldMap;
import com.twoplayers.legend.assets.sound.AllSoundEffects;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorldMapEnemyManager implements IEnemyManager {

    private boolean initNotDone = true;

    private WorldMapManager worldMapManager;
    private ImagesEnemyWorldMap imagesEnemyWorldMap;
    private AllSoundEffects allSoundEffects;
    private Properties enemiesProperties;

    private boolean loadingEnemies;
    private List<Enemy> enemies;

    private EnemyColorMatrix enemyColorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        loadingEnemies = false;
        enemies = new ArrayList<>();
    }

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
        enemyColorMatrix = new EnemyColorMatrix();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (loadingEnemies) {
            String enemiesAsString = enemiesProperties.getProperty(worldMapManager.getCoordinate());
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
            if (enemy.isDead) {
                enemy.currentAnimation.update(deltaTime);
            } else {
                enemy.update(deltaTime, g, worldMapManager);
            }
        }
        enemyColorMatrix.update(deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        for (Enemy enemy : enemies) {
            if (!enemy.isDead) {
                g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y));
                g.drawRect((int) enemy.hitbox.x, (int) enemy.hitbox.y, (int) enemy.hitbox.width, (int) enemy.hitbox.height, Hitbox.COLOR);
            } else if (!enemy.currentAnimation.isAnimationOver()) {
                g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y), enemyColorMatrix.getCurrentColorMatrix());
            }
        }
    }

    @Override
    public void requestEnemiesLoading() {
        loadingEnemies = true;
    }

    @Override
    public void unloadEnemies() {
        loadingEnemies = false;
        enemies.clear();
    }

    @Override
    public void damageEnemy(Enemy enemyDamaged, int damage) {
        enemyDamaged.life -= damage;
        if (enemyDamaged.life <= 0) {
            enemyDamaged.isDead = true;
            enemyDamaged.currentAnimation = enemyDamaged.deathAnimation;
        }
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }
}
