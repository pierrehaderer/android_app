package com.twoplayers.legend.character.enemy;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;

import java.util.ArrayList;
import java.util.List;

public class CaveEnemyManager implements IEnemyManager {

    private List<Enemy> enemies;

    /**
     * Load this manager
     */
    public void load(Game game) {
        init(game);
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        enemies = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime, Graphics g) {
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
    }

    @Override
    public void requestEnemiesLoading() {
    }

    @Override
    public void unloadEnemies() {
    }

    @Override
    public void damageEnemy(Enemy enemyDamaged, int damage) {
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }
}
