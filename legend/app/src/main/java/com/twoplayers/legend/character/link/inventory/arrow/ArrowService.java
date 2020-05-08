package com.twoplayers.legend.character.link.inventory.arrow;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

public class ArrowService {

    private IEnemyManager enemyManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public ArrowService(IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.enemyManager = enemyManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Reset the arrow
     */
    public void reset(Link link) {
        link.arrow.isActive = false;
        link.arrow.isAnImpact = false;
    }

    /**
     * Initiate arrow when link is throwing it
     */
    public void initiateArrow(Link link) {
        Arrow arrow = link.arrow;
        if (!arrow.isActive && !arrow.isAnImpact && link.rupees > 0) {
            Logger.info("Link is using bow and arrow.");
            link.startToUseItem();
            link.rupees--;
            soundEffectManager.play("coin_remove");
            arrow.isActive = true;
            arrow.hitbox = arrow.hitboxes.get(link.orientation);
            arrow.hitbox.relocate(link.x, link.y);
            arrow.x = (link.orientation == Orientation.RIGHT) ? arrow.hitbox.x - 8 * AllImages.COEF : arrow.hitbox.x;
            arrow.y = (link.orientation == Orientation.DOWN) ? arrow.hitbox.y - 8 * AllImages.COEF : arrow.hitbox.y;
            Logger.info("Arrow is starting at position (" + arrow.x + "," + arrow.y + ")");
            arrow.orientation = link.orientation;
            arrow.selectCurrentAnimation();
            arrow.currentAnimation.reset();
        }
    }

    /**
     * Handle arrow movements and interactions
     */
    public void handleArrow(Link link, float deltaTime) {
        Arrow arrow = link.arrow;
        if (arrow.isActive) {
            arrow.currentAnimation.update(deltaTime);
            boolean removeArrow = false;
            switch (arrow.orientation) {
                case UP:
                    arrow.y -= Arrow.SPEED * deltaTime;
                    arrow.hitbox.y -= Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
                case DOWN:
                    arrow.y += Arrow.SPEED * deltaTime;
                    arrow.hitbox.y += Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y + arrow.hitbox.height)) {
                        removeArrow = true;
                    }
                    break;
                case LEFT:
                    arrow.x -= Arrow.SPEED * deltaTime;
                    arrow.hitbox.x -= Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
                case RIGHT:
                    arrow.x += Arrow.SPEED * deltaTime;
                    arrow.hitbox.x += Arrow.SPEED * deltaTime;
                    if (LocationUtil.isTileAtBorder(arrow.x + arrow.hitbox.width, arrow.y)) {
                        removeArrow = true;
                    }
                    break;
            }
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.arrow.hitbox, enemy.getHitbox())) {
                    Logger.info("Link has hit enemy with arrow.");
                    enemyManager.isHitByArrow(enemy, link.arrow);
                    removeArrow = true;
                    break;
                }
            }
            if (removeArrow) {
                arrow.currentAnimation = arrow.deathAnimation;
                arrow.currentAnimation.reset();
                arrow.isActive = false;
                arrow.isAnImpact = true;
            }
        }
        if (arrow.isAnImpact) {
            arrow.currentAnimation.update(deltaTime);
            if (arrow.currentAnimation.isOver()) {
                arrow.isAnImpact = false;
            }
        }
    }
}
