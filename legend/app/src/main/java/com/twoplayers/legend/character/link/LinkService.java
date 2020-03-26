package com.twoplayers.legend.character.link;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.character.MyColorMatrix;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.enemy.Enemy;
import com.twoplayers.legend.character.link.inventory.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LinkService {

    public static final float A_TINY_BIT_MORE = 0.01f;

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private LinkManager linkManager;
    private IEnemyManager enemyManager;

    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;

    /**
     * Constructor
     */
    public LinkService(GuiManager guiManager, IZoneManager zoneManager, LinkManager linkManager, IEnemyManager enemyManager, MusicManager musicManager, SoundEffectManager soundEffectManager) {
        this.guiManager = guiManager;
        this.zoneManager = zoneManager;
        this.linkManager = linkManager;
        this.enemyManager = enemyManager;
        this.musicManager = musicManager;
        this.soundEffectManager = soundEffectManager;
    }

    /**
     * Handle link movement based on the arrows pressed. Return true if link change the room
     */
    public void handleLinkMovement(Link link, float deltaTime) {
        if (!link.isAttacking && !link.isUsingSecondItem && !link.isPushed && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
            // Movement of Link
            boolean linkHasNotMovedYet = true;
            if (guiManager.isUpPressed() && guiManager.areButtonsActivated() && zoneManager.upAndDownAuthorized(link)) {
                link.orientation = Orientation.UP;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = -1 * Link.SPEED * deltaTime;
                float nextX = evaluateXAfterShift(link.x);
                if (zoneManager.isUpValid(nextX, link.y + deltaY)) {
                    linkHasNotMovedYet = false;
                    shiftLinkX(link, nextX);
                    moveLinkY(link, deltaY);
                }
                if (LocationUtil.isUpOutOfMap(link.y)) {
                    linkManager.hideItemsAndEffects();
                    zoneManager.changeRoom(Orientation.UP);
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering(link);
            }
            if (guiManager.isDownPressed() && guiManager.areButtonsActivated() && zoneManager.upAndDownAuthorized(link)) {
                link.orientation = Orientation.DOWN;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaY = Link.SPEED * deltaTime;
                float nextX = evaluateXAfterShift(link.x);
                if (zoneManager.isDownValid(nextX, link.y + deltaY)) {
                    linkHasNotMovedYet = false;
                    shiftLinkX(link, nextX);
                    moveLinkY(link, deltaY);
                }
                if (LocationUtil.isDownOutOfMap(link.y + LocationUtil.TILE_SIZE)) {
                    linkManager.hideItemsAndEffects();
                    zoneManager.changeRoom(Orientation.DOWN);
                }
            }
            if (guiManager.isLeftPressed() && guiManager.areButtonsActivated() && zoneManager.leftAndRightAuthorized(link) && linkHasNotMovedYet) {
                link.orientation = Orientation.LEFT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = -1 * Link.SPEED * deltaTime;
                float nextY = evaluateYAfterShift(link.y);
                if (zoneManager.isLeftValid(link.x + deltaX, nextY)) {
                    shiftLinkY(link, nextY);
                    moveLinkX(link, deltaX);
                }
                if (LocationUtil.isLeftOutOfMap(link.x)) {
                    linkManager.hideItemsAndEffects();
                    zoneManager.changeRoom(Orientation.LEFT);
                }
            }
            if (guiManager.isRightPressed() && guiManager.areButtonsActivated() && zoneManager.leftAndRightAuthorized(link) && linkHasNotMovedYet) {
                link.orientation = Orientation.RIGHT;
                link.currentAnimation = link.moveAnimations.get(link.orientation);
                link.currentAnimation.update(deltaTime);
                float deltaX = Link.SPEED * deltaTime;
                float nextY = evaluateYAfterShift(link.y);
                if (zoneManager.isRightValid(link.x + deltaX, nextY)) {
                    shiftLinkY(link, nextY);
                    moveLinkX(link, deltaX);
                }
                if (LocationUtil.isRightOutOfMap(link.x + LocationUtil.TILE_SIZE)) {
                    linkManager.hideItemsAndEffects();
                    zoneManager.changeRoom(Orientation.RIGHT);
                }
            }
        }
    }

    /**
     * Handle link attack
     */
    public void handleLinkAttack(Link link, float deltaTime) {
        if (!link.isAttacking && !link.isUsingSecondItem && !link.isEnteringSomewhere && !link.isExitingSomewhere && !link.isShowingItem) {
            // Start of link's attack
            if (guiManager.isaPressed() && guiManager.areButtonsActivated() && link.sword.type != SwordType.NONE && zoneManager.isLinkFarEnoughFromBorderToAttack(link)) {
                link.currentAnimation = link.attackAnimations.get(link.orientation);
                link.currentAnimation.reset();
                link.sword.x = link.x;
                link.sword.y = link.y;
                link.sword.orientation = link.orientation;
                link.sword.getAnimation().reset();
                link.sword.getHitbox().relocate(link.x, link.y);
                soundEffectManager.play("sword");
                link.isAttacking = true;
                link.attackProgression = 0;
            }
        }
        if (link.isAttacking) {
            link.sword.getAnimation().update(deltaTime);
            link.attackProgression += deltaTime;
            if (link.attackProgression > Link.STEP_1_DURATION && link.attackProgression < Link.STEP_1_DURATION + Link.STEP_2_ATTACK_DURATION) {
                // Sword hitbox is active
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (enemy.isActive() && !enemy.isDead() && !enemy.isInvincible() && LocationUtil.areColliding(link.sword.getHitbox(), enemy.getHitbox())) {
                        Logger.info("Enemy " + enemy.getClass().getSimpleName() + " has been hit by link sword.");
                        if (!link.isInvincible && LocationUtil.areColliding(link.hitbox, enemy.getHitbox())) {
                            Logger.info("Link has collided with enemy : " + enemy.getClass());
                            soundEffectManager.play("link_wounded");
                            updateLinkLife(link, enemy.getContactDamage());
                            link.isInvincible = true;
                            link.invicibleCounter = Link.INITIAL_INVINCIBLE_COUNT;
                            link.isPushed = true;
                            link.pushCounter = Link.INITIAL_PUSH_COUNT;
                            Float[] pushDirections = LocationUtil.computePushDirections(enemy.getHitbox(), link.hitbox);
                            link.pushX = pushDirections[0];
                            link.pushY = pushDirections[1];
                            Logger.info("Link push direction : " + link.pushX + ", " + link.pushY);
                        }
                        enemyManager.isHitBySword(enemy, link.sword);
                    }
                }
            }
        }
    }

    /**
     * Handle when link is wounded
     */
    public void handleLinkWounded(Link link, float deltaTime, MyColorMatrix colorMatrix) {
        if (link.isInvincible) {
            Logger.info("Link is invincible, remaining counter : " + link.invicibleCounter);
            link.invicibleCounter -= deltaTime;
            if (link.invicibleCounter < 0) {
                link.isInvincible = false;
            }
            colorMatrix.update(deltaTime);
        } else {
            if (!link.isShowingItem) {
                Hitbox woundingHitbox = null;
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (woundingHitbox == null && !enemy.isDead() && enemy.isContactLethal() && LocationUtil.areColliding(link.hitbox, enemy.getHitbox())) {
                        Logger.info("Link has collided with enemy : " + enemy.getClass());
                        enemyManager.hasHitLink(enemy);
                        updateLinkLife(link, enemy.getContactDamage());
                        woundingHitbox = enemy.getHitbox();
                    }
                }
                Fire[] fireList = new Fire[] {link.fire1, link.fire2};
                for (Fire fire : fireList) {
                    if (woundingHitbox == null && fire.isActive && LocationUtil.areColliding(link.hitbox, fire.hitbox)) {
                        Logger.info("Link has collided with fire.");
                        updateLinkLife(link, Fire.DAMAGE_TO_LINK);
                        woundingHitbox = fire.hitbox;
                    }
                }
                if (woundingHitbox != null) {
                    soundEffectManager.play("link_wounded");
                    link.isInvincible = true;
                    link.invicibleCounter = Link.INITIAL_INVINCIBLE_COUNT;
                    link.isPushed = true;
                    link.pushCounter = Link.INITIAL_PUSH_COUNT;
                    Float[] pushDirections = LocationUtil.computePushDirections(woundingHitbox, link.hitbox);
                    link.pushX = pushDirections[0];
                    link.pushY = pushDirections[1];
                    Logger.info("Link push direction : " + link.pushX + ", " + link.pushY);
                }
            }
        }
    }

    /**
     * Handle when link is pushed
     */
    public void handleLinkPushed(Link link, float deltaTime) {
        if (link.isPushed) {
            Logger.info("Link is pushed, remaining counter : " + link.pushCounter);
            if (link.orientation == Orientation.UP || link.orientation == Orientation.DOWN) {
                float deltaY = Link.PUSH_SPEED * link.pushY * deltaTime;
                if (deltaY < 0) {
                    if (zoneManager.isUpValid(link.x, link.y + deltaY)) {
                        moveLinkY(link, deltaY);
                    }
                }
                if (deltaY > 0) {
                    if (zoneManager.isDownValid(link.x, link.y + deltaY)) {
                        moveLinkY(link, deltaY);
                    }
                }
                // Check if link is entering a cave
                checkAndInitCaveEntering(link);
            }
            if (link.orientation == Orientation.LEFT || link.orientation == Orientation.RIGHT) {
                float deltaX = Link.PUSH_SPEED * link.pushX * deltaTime;
                if (deltaX < 0) {
                    if (zoneManager.isLeftValid(link.x + deltaX, link.y)) {
                        moveLinkX(link, deltaX);
                    }
                }
                if (deltaX > 0) {
                    if (zoneManager.isRightValid(link.x + deltaX, link.y)) {
                        moveLinkX(link, deltaX);
                    }
                }
            }
            link.pushCounter -= deltaTime;
            if (link.pushCounter < 0) {
                link.isPushed = false;
            }
        }
    }

    /**
     * Handle when link is entering a case or a dungeon
     */
    public void handleLinkEnteringSomewhere(Link link, float deltaTime) {
        if (link.isEnteringSomewhere) {
            link.currentAnimation.update(deltaTime);
            float distance = deltaTime * Link.ENTER_CAVE_SPEED;
            link.enterSomewhereCounter -= distance;
            moveLinkY(link, distance);
        }
    }

    /**
     * Handle when link is exiting a case or a dungeon
     */
    public void handleLinkExitingSomewhere(Link link, float deltaTime) {
        if (link.isExitingSomewhere) {
            if (link.exitSomewhereDistance == LocationUtil.TILE_SIZE) {
                soundEffectManager.play("cave");
            }
            link.currentAnimation.update(deltaTime);
            float distance = Math.min(deltaTime * Link.ENTER_CAVE_SPEED, link.exitSomewhereDistance);
            link.exitSomewhereDistance -= distance;
            moveLinkY(link, -1 * distance);
            if (link.exitSomewhereDistance <= 0) {
                link.isExitingSomewhere = false;
                enemyManager.requestEnemiesLoading();
            }
        }
    }


    /**
     * Check if entering a cave and set the variables to make link enter a cave
     */
    private void checkAndInitCaveEntering(Link link) {
        if (zoneManager.isTileACave(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.TILE_SIZE)) {
            Logger.info("Link is entering a cave.");
            musicManager.stop();
            soundEffectManager.play("cave");
            enemyManager.unloadEnemies();
            linkManager.hideItemsAndEffects();
            link.isEnteringSomewhere = true;
            link.enterSomewhereCounter = LocationUtil.TILE_SIZE - 4; // Minus 4 to avoid link on the other side of the hiding tile
            link.isPushed = false;
            link.isAttacking = false;
            link.isInvincible = false;
            link.currentAnimation = link.moveAnimations.get(Orientation.UP);
            int tileX = LocationUtil.getTileXFromPositionX(link.x + LocationUtil.HALF_TILE_SIZE);
            int tileY = LocationUtil.getTileYFromPositionY(link.y + LocationUtil.TILE_SIZE);
            link.cavePosition = new Coordinate(LocationUtil.getXFromGrid(tileX), LocationUtil.getYFromGrid(tileY));
        }
    }

    /**
     * If Link is almost at the border of a tile put him on a border
     */
    private float evaluateXAfterShift(float x) {
        int tileX = LocationUtil.getTileXFromPositionX(x);
        float tilePositionX = LocationUtil.getXFromGrid(tileX);
        float deltaX = x - tilePositionX;
        if (deltaX > 2 * A_TINY_BIT_MORE && deltaX < LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionX + A_TINY_BIT_MORE;
        }
        if (deltaX >= LocationUtil.QUARTER_TILE_SIZE && deltaX < LocationUtil.HALF_TILE_SIZE) {
            return tilePositionX + LocationUtil.HALF_TILE_SIZE + A_TINY_BIT_MORE;
        }
        if (deltaX > LocationUtil.HALF_TILE_SIZE + 2 * A_TINY_BIT_MORE && deltaX < 3 * LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionX + LocationUtil.HALF_TILE_SIZE + A_TINY_BIT_MORE;
        }
        if (deltaX >= 3 * LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionX + LocationUtil.TILE_SIZE + A_TINY_BIT_MORE;
        }
        return x;
    }

    /**
     * If Link is almost at the border of a tile put him on a border
     */
    private float evaluateYAfterShift(float y) {
        int tileY = LocationUtil.getTileYFromPositionY(y);
        float tilePositionY = LocationUtil.getYFromGrid(tileY);
        float deltaY = y - tilePositionY;
        if (deltaY > 2 * A_TINY_BIT_MORE && deltaY < LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionY + A_TINY_BIT_MORE;
        }
        if (deltaY > LocationUtil.QUARTER_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE) {
            return tilePositionY + LocationUtil.HALF_TILE_SIZE + A_TINY_BIT_MORE;
        }
        if (deltaY > LocationUtil.HALF_TILE_SIZE + 2 * A_TINY_BIT_MORE && deltaY < 3 * LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionY + LocationUtil.HALF_TILE_SIZE + A_TINY_BIT_MORE;
        }
        if (deltaY >= 3 * LocationUtil.QUARTER_TILE_SIZE) {
            return tilePositionY + LocationUtil.TILE_SIZE + A_TINY_BIT_MORE;
        }
        return y;
    }

    /**
     * Move link to a position
     */
    private void shiftLinkX(Link link, float nextX) {
        if (nextX != link.x) {
            if (LocationUtil.isLeftOutOfMap(nextX)) {
                link.x = LocationUtil.LEFT_MAP;
            } else if (LocationUtil.isRightOutOfMap(nextX + LocationUtil.TILE_SIZE)) {
                link.x = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - LocationUtil.TILE_SIZE;
            } else {
                link.x = nextX;
            }
            link.hitbox.x = link.x + link.hitbox.x_offset;
            link.sword.x = link.x;
            link.sword.getHitbox().x = link.x + link.sword.getHitbox().x_offset;
        }
    }

    /**
     * Move link to an position
     */
    private void shiftLinkY(Link link, float nextY) {
        if (nextY != link.y) {
            if (LocationUtil.isUpOutOfMap(nextY)) {
                link.y = LocationUtil.TOP_MAP;
            } else if (LocationUtil.isDownOutOfMap(nextY + LocationUtil.TILE_SIZE)) {
                link.y = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - LocationUtil.TILE_SIZE;
            } else {
                link.y = nextY;
            }
            link.hitbox.y = link.y + link.hitbox.y_offset;
            link.sword.y = link.y;
            link.sword.getHitbox().y = link.y + link.sword.getHitbox().y_offset;
        }
    }

    /**
     * Move link by a delta
     */
    public void moveLinkX(Link link, float deltaX) {
        shiftLinkX(link, link.x + deltaX);
    }

    /**
     * Move link by a delta
     */
    public void moveLinkY(Link link, float deltaY) {
        shiftLinkY(link, link.y + deltaY);
    }

    /**
     * Update Link life
     */
    private void updateLinkLife(Link link, float value) {
        link.life = Math.min(link.lifeMax, link.life + value);
    }

}
