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
import com.twoplayers.legend.character.link.inventory.rod.Rod;
import com.twoplayers.legend.character.link.inventory.rod.RodType;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.SwordSplash;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
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
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.Arrays;

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
        itemService = new ItemService(guiManager, zoneManager, this, enemyManager, soundEffectManager);

        link.x = position.x;
        link.y = position.y;
        link.underTheDoor = position;
        link.hitbox.relocate(link.x, link.y);
        Logger.debug("Spawning link at (" + link.x + "," + link.y + ")");
        link.orientation = (link.isExitingADoor) ? Orientation.DOWN : Orientation.UP;
        link.currentAnimation = link.moveAnimations.get(link.orientation);
        link.isUsingItem = false;
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
        link.rupees = 255;
        link.keys = 5;
        link.isExitingADoor = false;

        //TODO Change it when it can be collected
        link.boomerang = new Boomerang(imagesLink, game.getGraphics());
        link.boomerang.type = BoomerangType.NONE;
        link.bomb = new Bomb(imagesLink, game.getGraphics());
        link.bombCloud = new BombCloud(imagesLink, game.getGraphics());
        link.bombQuantity = 4;
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
        link.rod = new Rod(imagesLink, game.getGraphics());
        link.rod.type = RodType.ROD;
        link.rodWave = new RodWave(imagesLink, game.getGraphics());
        link.rodFire = new Fire(imagesLink, game.getGraphics());

        link.bracelet = Bracelet.NONE;
        link.raft = Raft.NONE;
        link.ladder = Ladder.NONE;
        link.spellBook = SpellBook.NONE;
        link.ring = Ring.NONE;
        link.infiniteKey = InfiniteKey.NONE;

        link.compasses = new Compass[10];
        Arrays.fill(link.compasses, Compass.NONE);
        link.compasses[1] = Compass.COMPASS;
        link.dungeonMaps = new DungeonMap[10];
        Arrays.fill(link.dungeonMaps, DungeonMap.NONE);
        link.dungeonMaps[1] = DungeonMap.MAP;

        link.sword = new Sword(imagesLink, game.getGraphics());
        link.sword.type = SwordType.WOOD;
        link.throwingSword = new ThrowingSword(imagesLink, game.getGraphics());
        link.throwingSword.sword = link.sword;
        link.swordSplash = new SwordSplash(imagesLink, game.getGraphics());
        link.shield = Shield.SMALL;
        link.secondItem = (link.boomerang.type == BoomerangType.NONE) ? 0 : 1;
        link.changeItemCount = 0;
        link.isUsingItem = false;
        link.useItemStep = 0;
        link.useItemStepHasChanged = false;
        link.useItemProgression = 0;

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

        // Link is paying something
        itemService.handleLinkPayment(link, deltaTime);

        // Link is using the items
        itemService.handleLinkUsingItem(link, deltaTime);

        // Link is changing the second item
        itemService.handleLinkChangingSecondItem(link, deltaTime);

        // Link is wounded
        linkService.handleLinkInvincible(link, deltaTime, colorMatrix);
        linkService.handleLinkWounded(link, deltaTime);
        linkService.handleLinkPushed(link, deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw the sword
        if (link.sword.isActive) {
            g.drawScaledImage(link.sword.image, (int) link.sword.x, (int) link.sword.y, AllImages.COEF);
        }
        // Draw the throwing sword
        if (link.throwingSword.isActive) {
            g.drawAnimation(link.throwingSword.getAnimation(), (int) link.throwingSword.x, (int) link.throwingSword.y);
        }
        // Draw the boomerang
        if (link.boomerang.isMovingForward || link.boomerang.isMovingBackward) {
            g.drawAnimation(link.boomerang.getAnimation(), (int) link.boomerang.x, (int) link.boomerang.y);
        }
        // Draw the bomb
        if (link.bomb.isActive) {
            g.drawAnimation(link.bomb.currentAnimation, (int) link.bomb.x, (int) link.bomb.y);
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
        // Draw the rod
        if (link.rod.isActive) {
            g.drawScaledImage(link.rod.image, (int) link.rod.x, (int) link.rod.y, AllImages.COEF);
        }
        // Draw the rod wave
        if (link.rodWave.isActive) {
            g.drawAnimation(link.rodWave.getAnimation(), (int) link.rodWave.x, (int) link.rodWave.y);
        }
        // Draw the rod fire
        if (link.rodFire.isActive) {
            g.drawAnimation(link.rodFire.animation, (int) link.rodFire.x, (int) link.rodFire.y);
        }

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

        // Draw the bomb clouds
        if (link.bombCloud.isActive) {
            for (int i = 0; i < 7; i++) {
                float x = link.bombCloud.x + link.bombCloud.animationPositions[i].x;
                float y = link.bombCloud.y + link.bombCloud.animationPositions[i].y;
                g.drawAnimation(link.bombCloud.animations[i], (int) x, (int) y);
            }
        }

        // Draw the sword splash
        if (link.swordSplash.isActive) {
            g.drawAnimation(link.swordSplash.animations[0], (int) link.swordSplash.animationPositions[0].x, (int) link.swordSplash.animationPositions[0].y);
            g.drawAnimation(link.swordSplash.animations[1], (int) link.swordSplash.animationPositions[1].x, (int) link.swordSplash.animationPositions[1].y);
            g.drawAnimation(link.swordSplash.animations[2], (int) link.swordSplash.animationPositions[2].x, (int) link.swordSplash.animationPositions[2].y);
            g.drawAnimation(link.swordSplash.animations[3], (int) link.swordSplash.animationPositions[3].x, (int) link.swordSplash.animationPositions[3].y);
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
    public void moveLinkX(float deltaX, boolean withAnimation) {
        linkService.moveLinkX(link, deltaX);
        if (withAnimation) link.currentAnimation.update(Math.abs(deltaX) / Link.SPEED);
    }

    /**
     * Ask LinkManager to move link
     */
    public void moveLinkY(float deltaY, boolean withAnimation) {
        linkService.moveLinkY(link, deltaY);
        if (withAnimation) link.currentAnimation.update(Math.abs(deltaY) / Link.SPEED);
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
     * Increase link life by 1
     */
    public void increaseLinkLife() {
        if (link.life < link.lifeMax) {
            link.life = Math.min(link.life + 1, link.lifeMax);
        }
    }

    public void removeRupees(int rupees) {
        link.coinCounter = 0;
        link.rupeesToRemove += rupees;
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
