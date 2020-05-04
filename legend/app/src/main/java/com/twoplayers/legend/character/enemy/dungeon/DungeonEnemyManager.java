package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.character.enemy.MissileService;
import com.twoplayers.legend.character.enemy.missile.EnemyBoomerang;
import com.twoplayers.legend.util.ColorMatrixCharacter;
import com.twoplayers.legend.character.enemy.missile.Missile;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.ImagesEnemyDungeon;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.EnemyToSpawn;
import com.twoplayers.legend.character.enemy.SpawnMode;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
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

    public static final int TIME_BEFORE_FIRST_MOVE = 36;

    private boolean initNotDone = true;

    private DungeonManager dungeonManager;
    private LinkManager linkManager;
    private ImagesEnemyDungeon imagesEnemyDungeon;
    private SoundEffectManager soundEffectManager;
    private EnemyService enemyService;
    private MissileService missileService;
    private Graphics graphics;

    private Map<String, EnemyToSpawn[]> dungeonEnemies;
    private Map<String, Class<? extends Enemy>> enemyMap;
    private Map<Class<? extends Enemy>, Class<? extends Missile>> missileMap;

    private List<Enemy> enemies;
    private List<Missile> missiles;
    private int spawnCounter;

    private ColorMatrixCharacter colorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        enemies = new ArrayList<>();
        missiles = new ArrayList<>();
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
        graphics = game.getGraphics();

        enemyService = new EnemyService(dungeonManager, linkManager, this, soundEffectManager);
        missileService = new MissileService(dungeonManager);

        initEnemyMap();
        initMissileMap();
        initDungeonEnemies(game);
        colorMatrix = new ColorMatrixCharacter();
    }

    /**
     * Init the enemy map
     */
    private void initEnemyMap() {
        enemyMap = new HashMap<>();
        enemyMap.put("Stalfos", Stalfos.class);
        enemyMap.put("RedKeeze", RedKeeze.class);
        enemyMap.put("BlueKeeze", BlueKeeze.class);
        enemyMap.put("DarkBlueGel", DarkBlueGel.class);
        enemyMap.put("LightBlueGel", LightBlueGel.class);
        enemyMap.put("LightGreenGel", LightGreenGel.class);
        enemyMap.put("DarkGreenGel", DarkGreenGel.class);
        enemyMap.put("BrownGel", BrownGel.class);
        enemyMap.put("GreyGel", GreyGel.class);
        enemyMap.put("BlackGel", BlackGel.class);
        enemyMap.put("RedGoriya", RedGoriya.class);
        enemyMap.put("BlueGoriya", BlueGoriya.class);
    }

    /**
     * Init the missile map
     */
    private void initMissileMap() {
        missileMap = new HashMap<>();
        missileMap.put(RedGoriya.class, EnemyBoomerang.class);
        missileMap.put(BlueGoriya.class, EnemyBoomerang.class);
    }

    /**
     * Init the enemies position in the dungeon
     */
    private void initDungeonEnemies(Game game) {
        dungeonEnemies = new HashMap<>();
        Properties enemiesProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon1_enemies.properties");
        Properties spawnEnemiesProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon1_spawn_enemies.properties");
        for (String key : enemiesProperties.stringPropertyNames()) {
            // Format is "EnemyClass1,EnemyClass2,EnemyClass3,..."
            String[] enemies = (enemiesProperties.getProperty(key).length() == 0) ? new String[0] : enemiesProperties.getProperty(key).split(",");

            EnemyToSpawn[] enemiesToSpawn = new EnemyToSpawn[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                enemiesToSpawn[i] = new EnemyToSpawn(i, enemies[i], enemyMap);
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
        colorMatrix.update(deltaTime);
        for (Enemy enemy : enemies) {
            if (enemy.isDead) {
                if (!enemy.currentAnimation.isOver()) {
                    enemy.currentAnimation.update(deltaTime);
                }
            } else {
                enemy.update(deltaTime, g);
            }
        }
        boolean cleanRequired = false;
        for (Missile missile : missiles) {
            if (missile.isActive) {
                missile.update(deltaTime, g);
            } else {
                cleanRequired = true;
            }
        }
        missiles = missileService.cleanMissiles(missiles, cleanRequired);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        for (Missile missile: missiles) {
            if (missile.isActive) {
                g.drawAnimation(missile.currentAnimation, (int) missile.x, (int) missile.y);
                g.drawRect((int) missile.hitbox.x, (int) missile.hitbox.y, (int) missile.hitbox.width, (int) missile.hitbox.height, Hitbox.COLOR);
            }
        }
        for (Enemy enemy : enemies) {
            if (!enemy.isDead) {
                if (enemy.isInvincible()) {
                    g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y), colorMatrix.getMatrix());
                } else {
                    g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y));
                }
                g.drawRect((int) enemy.hitbox.x, (int) enemy.hitbox.y, (int) enemy.hitbox.width, (int) enemy.hitbox.height, Hitbox.COLOR);
            } else if (!enemy.currentAnimation.isOver()) {
                g.drawAnimation(enemy.currentAnimation, Math.round(enemy.x), Math.round(enemy.y));
            }
        }
    }

    @Override
    public void spawnEnemies() {
        int currentSpawnCounter = spawnCounter++;
        EnemyToSpawn[] enemiesToSpawn = dungeonEnemies.get(dungeonManager.getCoordinate());
        for (EnemyToSpawn enemyToSpawn : enemiesToSpawn) {
            try {
                if (enemyToSpawn.enemyClass != null) {
                    Constructor<? extends Enemy> constructor = enemyToSpawn.enemyClass.getConstructor(SoundEffectManager.class, IZoneManager.class, LinkManager.class, IEnemyManager.class, EnemyService.class);
                    Enemy enemy = constructor.newInstance(soundEffectManager, dungeonManager, linkManager, this, enemyService);
                    Coordinate spawnCoordinate = enemyService.getSpawnPosition(enemyToSpawn, linkManager.getLink().orientation, currentSpawnCounter);
                    Logger.info("Spawning " + enemy.getClass().getSimpleName() + " at (" + spawnCoordinate.x + "," + spawnCoordinate.y + ").");
                    enemy.x = spawnCoordinate.x;
                    enemy.y = spawnCoordinate.y;
                    enemy.init(imagesEnemyDungeon, graphics);
                    this.enemies.add(enemy);
                } else {
                    Logger.error("Could not find the enemy type : " + enemyToSpawn.name);
                }
            } catch (Exception e) {
                Logger.error("Could not create enemy class with type " + enemyToSpawn.name + " : " + e.getMessage());
            }
        }
    }

    @Override
    public void spawnMissile(Enemy enemy) {
        missiles.add(missileService.spawnMissile(imagesEnemyDungeon, graphics, enemy, missileMap.get(enemy.getClass())));
    }

    @Override
    public void unloadEnemies() {
        enemies.clear();
        missiles.clear();
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public List<Missile> getMissiles() {
        return missiles;
    }

    @Override
    public void enemyHasDied(Enemy enemy) {
        for (Missile missile : missiles) {
            if (missile instanceof EnemyBoomerang && missile.creator == enemy) {
                missile.isActive = false;
            }
        }
    }

    @Override
    public void isHitBySword(Enemy enemy, Sword sword) {
        enemy.isHitBySword(sword);
    }

    @Override
    public void isHitByBoomerang(Enemy enemy) {
        enemy.isHitByBoomerang();
    }

    @Override
    public void isHitByFire(Enemy enemy, Fire fire) {
        enemy.isHitByFire(fire);
    }

    @Override
    public void isHitByArrow(Enemy enemy, Arrow arrow) {
        enemy.isHitByArrow(arrow);
    }

    @Override
    public void isHitByBomb(Enemy enemy, Bomb bomb) {
        enemy.isHitByBomb(bomb);
    }

    @Override
    public void hasHitLink(Enemy enemy) {
        enemy.hasHitLink();
    }

    @Override
    public void hasHitLink(Missile missile) {
        missile.hasHitLink();
    }

}
