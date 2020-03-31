package com.twoplayers.legend.character.enemy.cave;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.character.enemy.AttackingEnemy;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.Missile;
import com.twoplayers.legend.character.enemy.TurretEnemy;
import com.twoplayers.legend.character.link.Fire;
import com.twoplayers.legend.character.link.Sword;

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
    public void spawnEnemies() {
    }

    @Override
    public void spawnMissile(AttackingEnemy enemy) {
    }

    @Override
    public void spawnMissile(TurretEnemy enemy) {
    }

    @Override
    public void unloadEnemies() {
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
    public void isHitBySword(Enemy enemy, Sword sword) {
    }

    @Override
    public void isHitByBoomerang(Enemy enemy) {
    }

    @Override
    public void isHitByFire(Enemy enemy, Fire fire) {
    }

    @Override
    public void hasHitLink(Enemy enemy) {
    }

    @Override
    public void hasHitLink(Missile missile) {
    }
}