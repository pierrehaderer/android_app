package com.twoplayers.legend;

import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.enemy.missile.Missile;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;

import java.util.List;

public interface IEnemyManager extends IManager {
    /** Ask for the loading of enemies */
    void spawnEnemies();
    /** Enemy is throwing a missile */
    void spawnMissile(Enemy enemy);
    /** Unload enemies */
    void unloadEnemies();
    /** Get enemies */
    List<Enemy> getEnemies();
    /** Get missiles */
    List<Missile> getMissiles();
    /** Inform that an enemy has died */
    void enemyHasDied(Enemy enemy);
    /** Enemy has been hit by sword */
    void isHitBySword(Enemy enemy, Sword sword);
    /** Enemy has been hit by throwing sword */
    void isHitBySword(Enemy enemy, ThrowingSword throwingSword);
    /** Enemy has been hit by boomerang */
    void isHitByBoomerang(Enemy enemy);
    /** Enemy has been hit by fire */
    void isHitByFire(Enemy enemy, Fire fire);
    /** Enemy has has been hit by arrow */
    void isHitByArrow(Enemy enemy, Arrow arrow);
    /** Enemy has has been hit by bombx */
    void isHitByBomb(Enemy enemy, Bomb bomb);
    /** Enemy has hit link */
    void hasHitLink(Enemy enemy);
    /** Missile has hit link */
    void hasHitLink(Missile missile);
}
