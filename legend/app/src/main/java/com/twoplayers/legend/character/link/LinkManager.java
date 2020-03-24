package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.Arrow;
import com.twoplayers.legend.character.link.inventory.BoomerangType;
import com.twoplayers.legend.character.link.inventory.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.Scepter;
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LinkManager implements IManager {

    private boolean initNotDone = true;

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private IEnemyManager enemyManager;
    private ItemService itemService;
    private LinkService linkService;

    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;
    private ImagesLink imagesLink;

    private Link link;
    private Coordinate cavePosition;
    private LinkInvincibleColorMatrix invincibleColorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, int zone, Coordinate position) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        zoneManager = ((MainActivity) game).getZoneManager(zone);
        enemyManager = ((MainActivity) game).getEnemyManager(zone);
        linkService = new LinkService(guiManager, zoneManager, this, enemyManager, musicManager, soundEffectManager);
        itemService = new ItemService(guiManager, zoneManager, enemyManager, soundEffectManager);

        link.x = position.x;
        link.y = (link.isExitingSomewhere) ? position.y + LocationUtil.TILE_SIZE : position.y;
        link.cavePosition = position;
        link.hitbox.relocate(link.x, link.y);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = (link.isExitingSomewhere) ? Orientation.DOWN : Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;
        link.isInvincible = false;
        link.isEnteringSomewhere = false;
        link.enterSomewhereCounter = 0;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        musicManager = ((MainActivity) game).getMusicManager();

        imagesLink = ((MainActivity) game).getAllImages().getImagesLink();
        imagesLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();

        link = new Link(imagesLink, game.getGraphics());

        link.life = 3;
        link.lifeMax = 3;
        link.coins = 255;
        link.keys = 1;
        link.isExitingSomewhere = false;

        //TODO Change it when it can be collected
        link.boomerang = new Boomerang(imagesLink, game.getGraphics());
        link.boomerang.type = BoomerangType.NONE;
        link.boomerang.isMovingForward = false;
        link.boomerang.isMovingBackward = false;
        link.boomerang.counter = 0;
        link.bomb = 0;
        link.bombMax = 8;
        link.bow = Bow.NONE;
        link.arrow = Arrow.NONE;
        link.light = Light.NONE;
        link.lightCount = 0;
        link.timeBeforeUseLight = 0;
        link.fire1 = new Fire(imagesLink, game.getGraphics());
        link.fire2 = new Fire(imagesLink, game.getGraphics());
        link.flute = Flute.NONE;
        link.meat = Meat.NONE;
        link.potion = Potion.NONE;
        link.scepter = Scepter.NONE;

        link.bracelet = Bracelet.NONE;
        link.raft = Raft.NONE;
        link.ladder = Ladder.NONE;
        link.spellBook = SpellBook.NONE;
        link.ring = Ring.NONE;
        link.infiniteKey = InfiniteKey.NONE;

        link.compass = Compass.COMPASS;
        link.dungeonMap = DungeonMap.MAP;

        link.sword = new Sword(imagesLink, game.getGraphics());
        link.sword.type = SwordType.NONE;
        link.shield = Shield.SMALL;
        link.secondItem = (link.boomerang.type == BoomerangType.NONE) ? 0 : 1;
        link.isUsingSecondItem = false;

        invincibleColorMatrix = new LinkInvincibleColorMatrix();
    }

    @Override
    public void update(float deltaTime, Graphics g) {

        // Movement of Link
        linkService.handleLinkMovement(link, deltaTime);

        // Attack of link
        linkService.handleLinkAttack(link, deltaTime);

        // Link is picking an item
        itemService.handleLinkPickingItem(link, deltaTime);

        // Link is using the second object
        itemService.handleLinkUsingSecondItem(link, deltaTime);

        // Link is wounded
        linkService.handleLinkWounded(link, deltaTime, invincibleColorMatrix);
        linkService.handleLinkPushed(link, deltaTime);

        // Link is entering somewhere
        linkService.handleLinkEnteringSomewhere(link, deltaTime);

        // Link is exiting somewhere
        linkService.handleLinkExitingSomewhere(link, deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw link
        if (link.isShowingItem) {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y);
            g.drawScaledImage(link.itemToShow.image, (int) link.x - 8, (int) (link.y - LocationUtil.TILE_SIZE) + 2, AllImages.COEF);
        } else if (link.isInvincible) {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y, invincibleColorMatrix.getCurrentColorMatrix());
        } else if (link.isEnteringSomewhere || link.isExitingSomewhere) {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y);
            g.drawScaledImage(imagesLink.get("empty_tile"), (int) link.cavePosition.x, (int) (link.cavePosition.y + LocationUtil.TILE_SIZE + 1), AllImages.COEF);
        } else {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y);
        }

        // Draw the sword
        if (link.isAttacking) {
            g.drawAnimation(link.sword.getAnimation(link.orientation), (int) link.sword.x, (int) link.sword.y);
        }

        // Draw the boomerang
        if (link.boomerang.isMovingForward || link.boomerang.isMovingBackward) {
            g.drawAnimation(link.boomerang.getAnimation(), (int) link.boomerang.x, (int) link.boomerang.y);
        }
        // Draw the fires from light
        if (link.fire1.isActive) {
            g.drawAnimation(link.fire1.animation, (int) link.fire1.x, (int) link.fire1.y);
        }
        if (link.fire2.isActive) {
            g.drawAnimation(link.fire2.animation, (int) link.fire2.x, (int) link.fire2.y);
        }

        // Draw the hitboxes
        g.drawRect((int) link.hitbox.x, (int) link.hitbox.y, (int) link.hitbox.width, (int) link.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.sword.hitbox.x, (int) link.sword.hitbox.y, (int) link.sword.hitbox.width, (int) link.sword.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.boomerang.hitbox.x, (int) link.boomerang.hitbox.y, (int) link.boomerang.hitbox.width, (int) link.boomerang.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.fire1.hitbox.x, (int) link.fire1.hitbox.y, (int) link.fire1.hitbox.width, (int) link.fire1.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.fire2.hitbox.x, (int) link.fire2.hitbox.y, (int) link.fire2.hitbox.width, (int) link.fire2.hitbox.height, Hitbox.COLOR);
    }

    /**
     * Remove all items and effects displayed
     */
    public void hideItemsAndEffects() {
        itemService.hideItemsAndEffects(link);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkX(float deltaX) {
        linkService.moveLinkX(link, deltaX);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkY(float deltaY) {
        linkService.moveLinkY(link, deltaY);
    }

    /**
     * Provide link to access his info
     */
    public Link getLink() {
        return link;
    }

    /**
     * Increase Link life max by 1
     */
    public void increaseLinkLifeMax() {
        if (link.lifeMax < 16) {
            link.lifeMax++;
        }
    }

    /**
     * Prepare link to re-enter the world map
     */
    public void exitZone() {
        link.isExitingSomewhere = true;
        link.exitSomewhereDistance = LocationUtil.TILE_SIZE;
    }

    /**
     * Check if link has finished entering somewhere
     */
    public boolean hasFinishedEnteringSomewhere() {
        return link.enterSomewhereCounter < 0;
    }
}
