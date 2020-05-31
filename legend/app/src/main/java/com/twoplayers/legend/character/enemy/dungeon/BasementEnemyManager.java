package com.twoplayers.legend.character.enemy.dungeon;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesEnemyDungeon;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.EnemyService;
import com.twoplayers.legend.character.enemy.missile.Missile;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.rod.Rod;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.dungeon.DungeonManager;
import com.twoplayers.legend.util.ColorMatrixCharacter;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class BasementEnemyManager implements IEnemyManager {

    public static final int TIME_BEFORE_FIRST_MOVE = 36;

    private boolean initNotDone = true;

    private DungeonManager dungeonManager;
    private LinkManager linkManager;
    private ImagesEnemyDungeon imagesEnemyDungeon;
    private SoundEffectManager soundEffectManager;
    private EnemyService enemyService;
    private Graphics graphics;

    private List<Enemy> enemies;
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

        colorMatrix = new ColorMatrixCharacter();
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
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        for (Enemy enemy : enemies) {
            if (!enemy.isDead) {
                if (enemy.shouldBlink()) {
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
        int [] spawnLocationsX = new int[]{2, 6, 9, 13};
        for (int spawnLocationX : spawnLocationsX) {
            BlueKeeze enemy = new BlueKeeze(soundEffectManager, dungeonManager, linkManager, this, enemyService);
            Coordinate spawnCoordinate = new Coordinate(LocationUtil.getXFromGrid(spawnLocationX), LocationUtil.getYFromGrid(6));
            Logger.info("Spawning " + enemy.getClass().getSimpleName() + " at (" + spawnCoordinate.x + "," + spawnCoordinate.y + ").");
            enemy.x = spawnCoordinate.x;
            enemy.y = spawnCoordinate.y;
            enemy.init(imagesEnemyDungeon, graphics);
            this.enemies.add(enemy);
        }
    }

    @Override
    public void spawnMissile(Enemy enemy) {
    }

    @Override
    public void unloadEnemies() {
        enemies.clear();
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public List<Missile> getMissiles() {
        return new ArrayList<>();
    }

    @Override
    public void enemyHasDied(Enemy enemy) {
    }

    @Override
    public void isHitBySword(Enemy enemy, Sword sword) {
        enemy.isHitBySword(sword);
    }

    @Override
    public void isHitByThrowingSword(Enemy enemy, ThrowingSword throwingSword) {
        enemy.isHitByThrowingSword(throwingSword);
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
    public void isHitByRod(Enemy enemy, Rod rod) {
        enemy.isHitByRod(rod);

    }

    @Override
    public void isHitByRodWave(Enemy enemy, RodWave rodWave) {
        enemy.isHitByRodWave(rodWave);
    }

    @Override
    public void hasHitLink(Enemy enemy) {
        enemy.hasHitLink();
    }

    @Override
    public void hasHitLink(Missile missile) {
        missile.hasHitLink();
    }

    @Override
    public boolean noMoreEnemy() {
        return false;
    }
}
