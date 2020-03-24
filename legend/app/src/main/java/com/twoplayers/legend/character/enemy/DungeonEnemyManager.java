package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.IImagesEnemy;
import com.twoplayers.legend.assets.image.ImagesEnemyDungeon;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.dungeon.DungeonManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DungeonEnemyManager implements IEnemyManager {

    private boolean initNotDone = true;

    private DungeonManager dungeonManager;
    private LinkManager linkManager;
    private ImagesEnemyDungeon imagesEnemyDungeon;
    private SoundEffectManager soundEffectManager;
    private EnemyService enemyService;

    private Map<String, EnemyToSpawn[]> dungeonEnemies;

    private boolean loadingEnemies;
    private List<Enemy> enemies;
    private int spawnCounter;

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
        spawnCounter = 0;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        dungeonManager = ((MainActivity) game).getDungeonManager();
        linkManager = ((MainActivity) game).getLinkManager();

        imagesEnemyDungeon = ((MainActivity) game).getAllImages().getImagesEnemyDungeon();
        imagesEnemyDungeon.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();

        enemyService = new EnemyService(dungeonManager);

        initWorldMapEnemies(game);
        enemyColorMatrix = new EnemyColorMatrix();
    }

    private void initWorldMapEnemies(Game game) {
        Map<String, Class<? extends Enemy>> enemyMap = new HashMap<>();
        enemyMap.put("Skeleton", Skeleton.class);

        dungeonEnemies = new HashMap<>();
        Properties enemiesProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "enemy/dungeon1_enemies.properties");
        Properties spawnEnemiesProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "enemy/dungeon1_spawn_enemies.properties");
        for (String key : enemiesProperties.stringPropertyNames()) {
            // Format is "EnemyClass1,EnemyClass2,EnemyClass3,..."
            String[] enemies = (enemiesProperties.getProperty(key).length() == 0) ? new String[0] : enemiesProperties.getProperty(key).split(",");

            EnemyToSpawn[] enemiesToSpawn = new EnemyToSpawn[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                enemiesToSpawn[i] = new EnemyToSpawn(enemies[i], enemyMap);
            }

            // Format is "SPAWN_MODE[|ORIENTATION,x1,y1,x2,y2,...]* "
            String[] spawnPossibilities = spawnEnemiesProperties.getProperty(key).split("\\|");
            SpawnMode spawnMode = SpawnMode.valueOf(spawnPossibilities[0]);
            for (int i = 0; i < enemies.length; i++) {
                enemiesToSpawn[i].mode = spawnMode;
            }
            switch (spawnMode) {
                case FIX:
                    for (int i = 1; i < spawnPossibilities.length; i++) {
                        String[] spawnPossibility = spawnPossibilities[i].split(",");
                        Orientation orientation = Orientation.valueOf(spawnPossibility[0]);
                        for (int j = 1; j < spawnPossibility.length - 1; j=j+2) {
                            if (enemiesToSpawn.length > j / 2) {
                                enemiesToSpawn[j/2].addSpawnPossibility(orientation, spawnPossibility[j], spawnPossibility[j+1]);
                            }
                        }
                    }
                    break;
            }
            dungeonEnemies.put(key, enemiesToSpawn);
        }
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (loadingEnemies) {
            int currentSpawnCounter = spawnCounter++;
            EnemyToSpawn[] enemiesToSpawn = dungeonEnemies.get(dungeonManager.getCoordinate());
            for (EnemyToSpawn enemyToSpawn : enemiesToSpawn) {
                try {
                    if (enemyToSpawn.enemyClass != null) {
                        Constructor<? extends Enemy> constructor = enemyToSpawn.enemyClass.getConstructor(IImagesEnemy.class,
                                SoundEffectManager.class, IZoneManager.class, LinkManager.class, IEnemyManager.class, EnemyService.class, Graphics.class);
                        Enemy enemy = constructor.newInstance(imagesEnemyDungeon, soundEffectManager, dungeonManager, linkManager, this, enemyService, g);
                        Coordinate spawnCoordinate = getSpawnPosition(enemyToSpawn, linkManager.getLink().orientation, currentSpawnCounter);
                        Logger.info("Spawning " + enemy.getClass().getSimpleName() + " at (" + spawnCoordinate.x + "," + spawnCoordinate.y + ").");
                        enemy.x = spawnCoordinate.x;
                        enemy.y = spawnCoordinate.y;
                        enemy.hitbox.relocate(enemy.x, enemy.y);
                        this.enemies.add(enemy);
                    } else {
                        Logger.error("Could not find the enemy type : " + enemyToSpawn.name);
                    }
                } catch (Exception e) {
                    Logger.error("Could not create enemy class with type " + enemyToSpawn.name + " : " + e.getMessage());
                }
            }
            loadingEnemies = false;
        }
        for (Enemy enemy : enemies) {
            if (enemy.isDead) {
                enemy.currentAnimation.update(deltaTime);
            } else {
                enemy.update(deltaTime, g);
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
                g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y));
            }
        }
    }

    /**
     * Get a spawn position
     */
    private Coordinate getSpawnPosition(EnemyToSpawn enemyToSpawn, Orientation orientation, int spawnCounter) {
        if (enemyToSpawn.mode == SpawnMode.RANDOM) {
            return dungeonManager.findSpawnableCoordinate();
        }
        if (enemyToSpawn.spawnPossibilities.containsKey(orientation)) {
            int index = (spawnCounter / 2) % enemyToSpawn.spawnPossibilities.get(orientation).size();
            return enemyToSpawn.spawnPossibilities.get(orientation).get(index);
        }
        return new Coordinate(LocationUtil.getXFromGrid(1), LocationUtil.getYFromGrid(1));
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
    public List<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public void damageEnemy(Enemy enemy, int damage) {
        enemy.isDamaged(damage);
    }

    @Override
    public void boomerangHits(Enemy enemy) {
        enemy.isHitByBoomerang();
    }

    @Override
    public void hasHitLink(Enemy enemy) {
        enemy.hasHitLink();
    }
}