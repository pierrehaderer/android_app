package com.twoplayers.legend;

import com.twoplayers.legend.character.enemy.AttackingEnemy;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.Missile;
import com.twoplayers.legend.character.enemy.TurretEnemy;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.sword.Sword;

import java.util.List;

public interface IEnemyManager extends IManager {
    /** Ask for the loading of enemies */
    void spawnEnemies();
    /** Enemy is throwing a missile */
    void spawnMissile(AttackingEnemy enemy);
    /** Enemy is throwing a missile */
    void spawnMissile(TurretEnemy enemy);
    /** Unload enemies */
    void unloadEnemies();
    /** Get enemies */
    List<Enemy> getEnemies();
    /** Get missiles */
    List<Missile> getMissiles();
    /** Enemy has been damaged */
    void isHitBySword(Enemy enemy, Sword sword);
    /** Enemy has been hit by boomerang */
    void isHitByBoomerang(Enemy enemy);
    /** Enemy has been hit by fire */
    void isHitByFire(Enemy enemy, Fire fire);
    /** Enemy has has been hit by arrow */
    void isHitByArrow(Enemy enemy, Arrow arrow);
    /** Enemy has hit link */
    void hasHitLink(Enemy enemy);
    /** Missile has hit link */
    void hasHitLink(Missile missile);
}
