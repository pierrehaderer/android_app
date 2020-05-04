package com.twoplayers.legend.character.link.inventory.bomb;

import com.kilobolt.framework.Animation;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.inventory.ItemService;
import com.twoplayers.legend.map.EntranceInfo;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class BombService {

    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    private ItemService itemService;

    /**
     * Constructor
     */
    public BombService(IZoneManager zoneManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager, ItemService itemService) {
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
        this.zoneManager = zoneManager;
        this.itemService = itemService;
    }

    /**
     * Reset the bomb
     */
    public void reset(Link link) {
        link.bomb.isActive = false;
        link.bombCloud.isActive = false;
    }

    /**
     * Initiate bomb when link is throwing it
     */
    public void initiateBomb(Link link) {
        Bomb bomb = link.bomb;
        if (link.bombQuantity > 0 && !bomb.isActive) {
            Logger.info("Link is using a bomb.");
            link.isUsingSecondItem = true;
            link.switchToUseAnimation();
            link.bombQuantity--;
            if (link.bombQuantity == 0) {
                link.secondItem = 0;
                itemService.switchToNextItem(link);
            }
            soundEffectManager.play("bomb_placement");
            bomb.isActive = true;
            bomb.timeBeforeExplosion = Bomb.TIME_BEFORE_EXPLOSION;
            switch (link.orientation) {
                case UP:
                    bomb.x = link.x + LocationUtil.QUARTER_TILE_SIZE;
                    bomb.y = link.y - LocationUtil.TILE_SIZE;
                    break;
                case DOWN:
                    bomb.x = link.x + LocationUtil.QUARTER_TILE_SIZE;
                    bomb.y = link.y + LocationUtil.TILE_SIZE;
                    break;
                case LEFT:
                    bomb.x = link.x - LocationUtil.QUARTER_TILE_SIZE - LocationUtil.HALF_TILE_SIZE;
                    bomb.y = link.y;
                    break;
                case RIGHT:
                    bomb.x = link.x + LocationUtil.QUARTER_TILE_SIZE + LocationUtil.TILE_SIZE;
                    bomb.y = link.y;
                    break;
            }
            bomb.hitbox.relocate(bomb.x, bomb.y);
            Logger.info("Bomb is starting at position (" + bomb.x + "," + bomb.y + ")");
        }
    }

    /**
     * Handle bomb movements and interactions
     */
    public void handleBomb(Link link, float deltaTime) {
        Bomb bomb = link.bomb;
        BombCloud bombCloud = link.bombCloud;
        if (bomb.isActive) {
            bomb.timeBeforeExplosion -= deltaTime;
            if (bomb.timeBeforeExplosion < 0) {
                soundEffectManager.play("bomb");
                zoneManager.bombHasExploded(bomb);
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.bomb.hitbox, enemy.getHitbox())) {
                        Logger.info("Link has hit enemy with bomb.");
                        enemyManager.isHitByBomb(enemy, link.bomb);
                    }
                }
                bomb.isActive = false;
                bomb.hitbox.relocate(0, 0);
                for (Animation animation : bombCloud.animations) {
                    animation.reset();
                }
                bombCloud.isActive = true;
                bombCloud.x = bomb.x;
                bombCloud.y = bomb.y;
            }
        }

        if (bombCloud.isActive) {
            for (Animation animation : bombCloud.animations) {
                animation.update(deltaTime);
            }
            if (bombCloud.animations[4].isOver()) {
                bombCloud.isActive = false;
            }
        }
    }
}
