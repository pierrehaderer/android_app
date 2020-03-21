package com.twoplayers.legend;

import com.twoplayers.legend.character.enemy.Enemy;

import java.util.List;

public interface IEnemyManager extends IManager {
    /** Ask for the loading of enemies */
    public void requestEnemiesLoading();
    /** Unload enemies */
    public void unloadEnemies();
    /** Enemy has been damaged */
    public void damageEnemy(Enemy enemyDamaged, int damage);
    /** Get enemies */
    public List<Enemy> getEnemies();
    /** Enemy has been hit by boomerang */
    void boomerangHits(Enemy enemy);
    /** Enemy has hit link */
    void hasHitLink(Enemy enemy);
}
