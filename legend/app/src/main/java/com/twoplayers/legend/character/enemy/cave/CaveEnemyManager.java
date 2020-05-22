package com.twoplayers.legend.character.enemy.cave;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.missile.Missile;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.rod.Rod;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;

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
    public void spawnMissile(Enemy enemy) {
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
    public void enemyHasDied(Enemy enemy) {
    }

    @Override
    public void isHitBySword(Enemy enemy, Sword sword) {
    }

    @Override
    public void isHitByThrowingSword(Enemy enemy, ThrowingSword throwingSword) {
    }

    @Override
    public void isHitByBoomerang(Enemy enemy) {
    }

    @Override
    public void isHitByFire(Enemy enemy, Fire fire) {
    }

    @Override
    public void isHitByArrow(Enemy enemy, Arrow arrow) {
    }

    @Override
    public void isHitByBomb(Enemy enemy, Bomb bomb) {
    }

    @Override
    public void isHitByRod(Enemy enemy, Rod rod) {
    }

    @Override
    public void isHitByRodWave(Enemy enemy, RodWave rodWave) {
    }

    @Override
    public void hasHitLink(Enemy enemy) {
    }

    @Override
    public void hasHitLink(Missile missile) {
    }

    @Override
    public boolean noMoreEnemy() {
        return false;
    }

}
