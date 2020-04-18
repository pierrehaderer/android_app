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
import com.twoplayers.legend.util.ColorMatrixCharacter;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.ItemService;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowType;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.bomb.BombCloud;
import com.twoplayers.legend.character.link.inventory.boomerang.Boomerang;
import com.twoplayers.legend.character.link.inventory.boomerang.BoomerangType;
import com.twoplayers.legend.character.link.inventory.arrow.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.light.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.Scepter;
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class LinkManager implements IManager {

    private boolean initNotDone = true;

    private ItemService itemService;
    private LinkService linkService;

    private SoundEffectManager soundEffectManager;
    private ImagesLink imagesLink;

    private Link link;
    private ColorMatrixCharacter colorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, int zone, Coordinate position) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        IZoneManager zoneManager = ((MainActivity) game).getZoneManager(zone);
        IEnemyManager enemyManager = ((MainActivity) game).getEnemyManager(zone);
        GuiManager guiManager = ((MainActivity) game).getGuiManager();
        MusicManager musicManager = ((MainActivity) game).getMusicManager();
        linkService = new LinkService(guiManager, zoneManager, this, enemyManager, musicManager, soundEffectManager);
        itemService = new ItemService(guiManager, zoneManager, enemyManager, soundEffectManager);

        link.x = position.x;
        link.y = position.y;
        link.underTheDoor = position;
        link.hitbox.relocate(link.x, link.y);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = (link.isExitingADoor) ? Orientation.DOWN : Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isAttacking = false;
        link.isInvincible = false;
        link.isEnteringADoor = false;
        link.enterSomewhereDistance = 0;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        imagesLink = ((MainActivity) game).getAllImages().getImagesLink();
        imagesLink.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();

        link = new Link(imagesLink, game.getGraphics());

        link.life = 3;
        link.lifeMax = 3;
        link.coins = 255;
        link.keys = 1;
        link.isExitingADoor = false;

        //TODO Change it when it can be collected
        link.boomerang = new Boomerang(imagesLink, game.getGraphics());
        link.boomerang.type = BoomerangType.NONE;
        link.bomb = new Bomb(imagesLink, game.getGraphics());
        link.bombCloud = new BombCloud(imagesLink, game.getGraphics());
        link.bombQuantity = 0;
        link.bombMax = 8;
        link.bow = Bow.NONE;
        link.arrow = new Arrow(imagesLink, game.getGraphics());
        link.arrow.type = ArrowType.NONE;
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

        colorMatrix = new ColorMatrixCharacter();
    }

    @Override
    public void update(float deltaTime, Graphics g) {

        // Link is entering somewhere
        linkService.handleLinkEnteringSomewhere(link, deltaTime);

        // Link is exiting somewhere
        linkService.handleLinkExitingSomewhere(link, deltaTime);

        // Movement of Link
        linkService.handleLinkMovement(link, deltaTime);

        // Link is picking an item
        itemService.handleLinkPickingItem(link, deltaTime);

        // Link is using the second object
        itemService.handleLinkUsingItem(link, deltaTime);

        // Link is wounded
        linkService.handleLinkInvincible(link, deltaTime, colorMatrix);
        linkService.handleLinkWounded(link, deltaTime);
        linkService.handleLinkPushed(link, deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw link
        if (link.isInvincible) {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y, colorMatrix.getMatrix());
        } else {
            g.drawAnimation(link.currentAnimation, (int) link.x, (int) link.y);
        }

        // Draw item picked
        if (link.isShowingItem) {
            g.drawScaledImage(link.itemToShow.image, (int) link.x - 8, (int) (link.y - LocationUtil.TILE_SIZE) + 2, AllImages.COEF);
        }

        // Draw empty tile
        if (link.isEnteringADoor || link.isExitingADoor) {
            g.drawScaledImage(imagesLink.get("empty_tile"), (int) link.underTheDoor.x, (int) link.underTheDoor.y, AllImages.COEF);
        }

        // Draw the sword
        if (link.isAttacking) {
            g.drawAnimation(link.sword.getAnimation(), (int) link.sword.x, (int) link.sword.y);
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
        // Draw the arrow
        if (link.arrow.isActive || link.arrow.isAnImpact) {
            g.drawAnimation(link.arrow.currentAnimation, (int) link.arrow.x, (int) link.arrow.y);
        }
        // Draw the bomb
        if (link.bomb.isActive) {
            g.drawAnimation(link.bomb.currentAnimation, (int) link.bomb.x, (int) link.bomb.y);
        }
        if (link.bombCloud.isActive) {
            for (int i = 0; i < 7; i++) {
                float x = link.bomb.x + link.bombCloud.animationPositions[i].x;
                float y = link.bomb.y + link.bombCloud.animationPositions[i].y;
                g.drawAnimation(link.bombCloud.animations[i], (int) x, (int) y);
            }
        }

        // Draw the hitboxes
        g.drawRect((int) link.hitbox.x, (int) link.hitbox.y, (int) link.hitbox.width, (int) link.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.sword.getHitbox().x, (int) link.sword.getHitbox().y, (int) link.sword.getHitbox().width, (int) link.sword.getHitbox().height, Hitbox.COLOR);
        g.drawRect((int) link.boomerang.hitbox.x, (int) link.boomerang.hitbox.y, (int) link.boomerang.hitbox.width, (int) link.boomerang.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.fire1.hitbox.x, (int) link.fire1.hitbox.y, (int) link.fire1.hitbox.width, (int) link.fire1.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.fire2.hitbox.x, (int) link.fire2.hitbox.y, (int) link.fire2.hitbox.width, (int) link.fire2.hitbox.height, Hitbox.COLOR);
        g.drawRect((int) link.arrow.hitbox.x, (int) link.arrow.hitbox.y, (int) link.arrow.hitbox.width, (int) link.arrow.hitbox.height, Hitbox.COLOR);
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
        link.isExitingADoor = true;
    }

    /**
     * Check if link has finished entering somewhere
     */
    public boolean hasFinishedEnteringSomewhere() {
        return link.enterSomewhereDistance < 0;
    }
}
