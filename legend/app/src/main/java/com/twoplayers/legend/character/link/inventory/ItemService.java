package com.twoplayers.legend.character.link.inventory;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowService;
import com.twoplayers.legend.character.link.inventory.boomerang.BoomerangService;
import com.twoplayers.legend.character.link.inventory.light.LightService;
import com.twoplayers.legend.character.link.inventory.sword.SwordService;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowType;
import com.twoplayers.legend.character.link.inventory.boomerang.BoomerangType;
import com.twoplayers.legend.character.link.inventory.arrow.Bow;
import com.twoplayers.legend.character.link.inventory.light.Light;
import com.twoplayers.legend.character.link.inventory.sword.SwordType;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.LocationUtil;

public class ItemService {

    private GuiManager guiManager;
    private IZoneManager zoneManager;
    private SoundEffectManager soundEffectManager;

    private SwordService swordService;
    private BoomerangService boomerangService;
    private ArrowService arrowService;
    private LightService lightService;

    /**
     * Constructor
     */
    public ItemService(GuiManager guiManager, IZoneManager zoneManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.guiManager = guiManager;
        this.zoneManager = zoneManager;
        this.soundEffectManager = soundEffectManager;

        swordService = new SwordService(enemyManager, soundEffectManager);
        boomerangService = new BoomerangService(guiManager, enemyManager, soundEffectManager);
        arrowService = new ArrowService(enemyManager, soundEffectManager);
        lightService = new LightService(zoneManager, enemyManager, soundEffectManager);
    }

    /**
     * Handle link use the second item
     */
    public void handleLinkUsingItem(Link link, float deltaTime) {
        if (!link.isAttacking && !link.isUsingSecondItem && guiManager.areButtonsActivated()
                && !link.isEnteringADoor && !link.isExitingADoor && !link.isShowingItem && zoneManager.isLinkFarEnoughFromBorderToAttack(link)) {

            if (guiManager.isaPressed() && guiManager.areButtonsActivated()) {
                swordService.initiateSword(link);
            }

            if (guiManager.isbPressed() && guiManager.areButtonsActivated()) {
                switch (link.secondItem) {
                    case 1:
                        boomerangService.initiateBoomerang(link);
                        break;
                    case 2:
                        break;
                    case 3:
                        arrowService.initiateArrow(link);
                        break;
                    case 4:
                        lightService.initiateFireFromLight(link);
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                }
            }
        }
        boomerangService.handleBoomerang(link, deltaTime);
        lightService.handleFire(link, deltaTime);
        arrowService.handleArrow(link, deltaTime);
        swordService.handleLinkAttack(link, deltaTime);
        if (link.isUsingSecondItem || link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            if (link.currentAnimation.isAnimationOver()) {
                link.isUsingSecondItem = false;
                link.isAttacking = false;
                link.switchToMoveAnimation(link.orientation);
            }
        }
    }

    /**
     * Handle when link is picking an item
     */
    public void handleLinkPickingItem(Link link, float deltaTime) {
        for (Item item : zoneManager.getItems()) {
            if (LocationUtil.areColliding(link.hitbox, item.hitbox) && link.coins - link.coinsToRemove >= item.price && !alreadyInInventory(link, item)) {
                link.isPushed = false;
                item.hideItemForTheZone();
                link.coinCounter = 0;
                link.coinsToRemove += item.price;
                link.itemToShow = item;
                link.isShowingItem = true;
                link.showItemCounter = Link.INITIAL_SHOW_COUNT;
                link.switchToPickAnimation(item.pickAnimation);
                soundEffectManager.play("collect_item");
                putItemInInventory(link, item);
            }
        }
        if (link.isShowingItem) {
            link.showItemCounter -= deltaTime;
            if (link.showItemCounter < 0) {
                link.isShowingItem = false;
                link.switchToMoveAnimation(Orientation.DOWN);
            }
        }
        if (link.coinsToRemove > 0) {
            link.coinCounter += deltaTime * Link.REMOVE_COINS_SPEED;
            if (link.coinCounter > 1) {
                int floor = (int) Math.min(Math.floor(link.coinCounter), link.coinsToRemove);
                link.coins -= floor;
                link.coinsToRemove -= floor;
                link.coinCounter -= floor;
                soundEffectManager.play("coin_payment");
            }
            if (link.coinsToRemove == 0) {
                soundEffectManager.play("coin_payment_end");
            }
        }
    }

    /**
     * Method to hide all items and their effects from screen
     */
    public void hideItemsAndEffects(Link link) {
        boomerangService.reset(link);
        arrowService.reset(link);
        lightService.reset(link);
    }

    /**
     * Get the picking animation for an item
     */
    public int findPickAnimation(String itemName) {
        if ("raft".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        if ("ladder".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        if ("triforce".equals(itemName)) {
            return Link.PICK_ANIMATION_BIG;
        }
        return Link.PICK_ANIMATION_SMALL;
    }

    /**
     * Check if link already has the item
     */
    public boolean alreadyInInventory(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                return link.arrow.type != ArrowType.NONE;
            case "white_arrow":
                return link.arrow.type == ArrowType.WHITE;
            case "wood_boomerang":
                return link.boomerang.type != BoomerangType.NONE;
            case "white_boomerang":
                return link.boomerang.type == BoomerangType.WHITE;
            case "bombs":
                return link.bomb < link.bombMax;
            case "bow":
                return link.bow == Bow.BOW;
            case "bracelet":
                return link.bracelet == Bracelet.BRACELET;
            case "compass":
                return link.compass == Compass.COMPASS;
            case "dungeon_map":
                return link.dungeonMap == DungeonMap.MAP;
            case "flute":
                return link.flute == Flute.FLUTE;
            case "key":
                return link.keys >= 99;
            case "infinite_key":
                return link.infiniteKey == InfiniteKey.KEY;
            case "ladder":
                return link.ladder == Ladder.LADDER;
            case "blue_light":
                return link.light != Light.NONE;
            case "red_light":
                return link.light == Light.RED;
            case "meat":
                return link.meat == Meat.MEAT;
            case "note":
                return link.potion != Potion.NONE;
            case "blue_potion":
                return link.potion == Potion.BLUE || link.potion == Potion.RED;
            case "red_potion":
                return link.potion == Potion.RED;
            case "raft":
                return link.raft == Raft.RAFT;
            case "blue_ring":
                return link.ring != Ring.NONE;
            case "red_ring":
                return link.ring == Ring.RED;
            case "scepter":
                return link.scepter == Scepter.SCEPTER;
            case "shield":
                return link.shield == Shield.BIG;
            case "spell_book":
                return link.spellBook == SpellBook.BOOK;
            case "wood_sword":
                return link.sword.type != SwordType.NONE;
            case "white_sword":
                return link.sword.type == SwordType.WHITE || link.sword.type == SwordType.MAGICAL;
            case "magical_sword":
                return link.sword.type == SwordType.MAGICAL;
        }
        return false;
    }

    /**
     * Give the corresponding item to link
     */
    public void putItemInInventory(Link link, Item item) {
        int possibleSecondItem = 0;
        switch (item.name) {
            case "wood_arrow":
                link.arrow.type = ArrowType.WOOD;
                if (link.bow != Bow.NONE) possibleSecondItem = 3;
                break;
            case "white_arrow":
                link.arrow.type = ArrowType.WHITE;
                if (link.bow != Bow.NONE) possibleSecondItem = 3;
                break;
            case "wood_boomerang":
                link.boomerang.type = BoomerangType.WOOD;
                possibleSecondItem = 1;
                break;
            case "white_boomerang":
                link.boomerang.type = BoomerangType.WHITE;
                possibleSecondItem = 1;
                break;
            case "bombs":
                link.bomb = Math.min(link.bomb + 4, link.bombMax);
                possibleSecondItem = 2;
                break;
            case "bow":
                link.bow = Bow.BOW;
                if (link.arrow.type != ArrowType.NONE) possibleSecondItem = 3;
                break;
            case "bracelet":
                link.bracelet = Bracelet.BRACELET;
                break;
            case "compass":
                link.compass = Compass.COMPASS;
                break;
            case "dungeon_map":
                link.dungeonMap = DungeonMap.MAP;
                break;
            case "flute":
                link.flute = Flute.FLUTE;
                possibleSecondItem = 5;
                break;
            case "infinite_key":
                link.infiniteKey = InfiniteKey.KEY;
                break;
            case "key":
                link.keys++;
                break;
            case "ladder":
                link.ladder = Ladder.LADDER;
                break;
            case "blue_light":
                link.light = Light.BLUE;
                possibleSecondItem = 4;
                break;
            case "red_light":
                link.light = Light.RED;
                possibleSecondItem = 4;
                break;
            case "meat":
                link.meat = Meat.MEAT;
                possibleSecondItem = 6;
                break;
            case "note":
                link.potion = Potion.NOTE;
                break;
            case "blue_potion":
                link.potion = Potion.BLUE;
                possibleSecondItem = 7;
                break;
            case "red_potion":
                link.potion = Potion.RED;
                possibleSecondItem = 7;
                break;
            case "raft":
                link.raft = Raft.RAFT;
                break;
            case "blue_ring":
                link.ring = Ring.BLUE;
                break;
            case "red_ring":
                link.ring = Ring.RED;
                break;
            case "scepter":
                link.scepter = Scepter.SCEPTER;
                possibleSecondItem = 8;
                break;
            case "spell_book":
                link.spellBook = SpellBook.BOOK;
                break;
            case "wood_sword":
                link.sword.type = SwordType.WOOD;
                break;
            case "white_sword":
                link.sword.type = SwordType.WHITE;
                break;
            case "magical_sword":
                link.sword.type = SwordType.MAGICAL;
                break;
        }
        if (link.secondItem == 0) {
            link.secondItem = possibleSecondItem;
            guiManager.updateCursor(link.secondItem);
        }
    }

}
