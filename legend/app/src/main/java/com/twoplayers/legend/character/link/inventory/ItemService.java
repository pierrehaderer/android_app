package com.twoplayers.legend.character.link.inventory;

import com.twoplayers.legend.IEnemyManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowService;
import com.twoplayers.legend.character.link.inventory.bomb.BombService;
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
    private LinkManager linkManager;
    private SoundEffectManager soundEffectManager;

    private SwordService swordService;
    private BoomerangService boomerangService;
    private BombService bombService;
    private ArrowService arrowService;
    private LightService lightService;

    /**
     * Constructor
     */
    public ItemService(GuiManager guiManager, IZoneManager zoneManager, LinkManager linkManager, IEnemyManager enemyManager, SoundEffectManager soundEffectManager) {
        this.guiManager = guiManager;
        this.zoneManager = zoneManager;
        this.linkManager = linkManager;
        this.soundEffectManager = soundEffectManager;

        swordService = new SwordService(enemyManager, zoneManager, soundEffectManager);
        boomerangService = new BoomerangService(guiManager, enemyManager, soundEffectManager);
        arrowService = new ArrowService(enemyManager, soundEffectManager);
        lightService = new LightService(zoneManager, enemyManager, soundEffectManager);
        bombService = new BombService(zoneManager, enemyManager, soundEffectManager, this);
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
                        bombService.initiateBomb(link);
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
        swordService.handleLinkAttack(link, deltaTime);
        swordService.handleLinkThrowingSword(link, deltaTime);
        boomerangService.handleBoomerang(link, deltaTime);
        bombService.handleBomb(link, deltaTime);
        lightService.handleFire(link, deltaTime);
        arrowService.handleArrow(link, deltaTime);
        if (link.isUsingSecondItem || link.isAttacking) {
            link.currentAnimation.update(deltaTime);
            if (link.currentAnimation.isOver()) {
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
            if (!link.isAttacking && !link.isUsingSecondItem && !link.isEnteringADoor && !link.isExitingADoor && !link.isShowingItem) {
                if (LocationUtil.areColliding(link.hitbox, item.hitbox) && link.rupees - link.rupeesToRemove >= item.price && linkCanPickItem(link, item)) {
                    link.isPushed = false;
                    item.hideItemForTheZone();
                    linkManager.removeRupees(item.price);
                    link.itemToShow = item;
                    link.isShowingItem = true;
                    link.showItemCounter = Link.INITIAL_SHOW_COUNT;
                    link.switchToPickAnimation(item.pickAnimation);
                    soundEffectManager.play("collect_item");
                    putItemInInventory(link, item);
                }
            }
        }
        if (link.isShowingItem) {
            link.showItemCounter -= deltaTime;
            if (link.showItemCounter < 0) {
                link.isShowingItem = false;
                link.switchToMoveAnimation(Orientation.DOWN);
            }
        }
    }

    /**
     * Removes rupees from link when he is paying somthing
     */
    public void handleLinkPayment(Link link, float deltaTime) {
        if (link.rupeesToRemove > 0) {
            link.coinCounter += deltaTime * Link.REMOVE_RUPEES_SPEED;
            if (link.coinCounter > 1) {
                int floor = (int) Math.min(Math.floor(link.coinCounter), link.rupeesToRemove);
                if (floor < link.rupees) {
                    link.rupees -= floor;
                    link.rupeesToRemove -= floor;
                    link.coinCounter -= floor;
                    soundEffectManager.play("coin_payment");
                } else {
                    link.rupees = 0;
                    link.rupeesToRemove = 0;
                    link.coinCounter = 0;
                }
            }
            if (link.rupeesToRemove == 0) {
                soundEffectManager.play("coin_payment_end");
            }
        }
    }

    /**
     * Method to hide all items and their effects from screen
     */
    public void hideItemsAndEffects(Link link) {
        swordService.reset(link);
        boomerangService.reset(link);
        bombService.reset(link);
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
    public boolean linkCanPickItem(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                return link.arrow.type == ArrowType.NONE;
            case "white_arrow":
                return link.arrow.type != ArrowType.WHITE;
            case "wood_boomerang":
                return link.boomerang.type == BoomerangType.NONE;
            case "white_boomerang":
                return link.boomerang.type != BoomerangType.WHITE;
            case "bomb":
                return link.bombQuantity < link.bombMax;
            case "bow":
                return link.bow == Bow.NONE;
            case "bracelet":
                return link.bracelet == Bracelet.NONE;
            case "compass":
                return link.compass == Compass.NONE;
            case "dungeon_map":
                return link.dungeonMap == DungeonMap.NONE;
            case "flute":
                return link.flute == Flute.NONE;
            case "key":
                return link.keys < 99;
            case "infinite_key":
                return link.infiniteKey == InfiniteKey.NONE;
            case "ladder":
                return link.ladder == Ladder.NONE;
            case "blue_light":
                return link.light == Light.NONE;
            case "red_light":
                return link.light != Light.RED;
            case "meat":
                return link.meat == Meat.NONE;
            case "note":
                return link.potion == Potion.NONE;
            case "blue_potion":
                return link.potion != Potion.BLUE && link.potion != Potion.RED;
            case "red_potion":
                return link.potion != Potion.RED;
            case "raft":
                return link.raft == Raft.NONE;
            case "blue_ring":
                return link.ring == Ring.NONE;
            case "red_ring":
                return link.ring != Ring.RED;
            case "scepter":
                return link.scepter == Scepter.NONE;
            case "shield":
                return link.shield == Shield.SMALL;
            case "spell_book":
                return link.spellBook == SpellBook.NONE;
            case "wood_sword":
                return link.sword.type == SwordType.NONE;
            case "white_sword":
                return link.sword.type != SwordType.WHITE && link.sword.type != SwordType.MAGICAL;
            case "magical_sword":
                return link.sword.type != SwordType.MAGICAL;
            case "big_heart":
                return link.lifeMax < 16;
            case "taken_rupees":
                return false;
        }
        return false;
    }

    /**
     * Give the corresponding item to link
     */
    public void putItemInInventory(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                link.arrow.type = ArrowType.WOOD;
                break;
            case "white_arrow":
                link.arrow.type = ArrowType.WHITE;
                break;
            case "wood_boomerang":
                link.boomerang.type = BoomerangType.WOOD;
                break;
            case "white_boomerang":
                link.boomerang.type = BoomerangType.WHITE;
                break;
            case "bomb":
                link.bombQuantity = Math.min(link.bombQuantity + 4, link.bombMax);
                break;
            case "bow":
                link.bow = Bow.BOW;
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
                break;
            case "red_light":
                link.light = Light.RED;
                break;
            case "meat":
                link.meat = Meat.MEAT;
                break;
            case "note":
                link.potion = Potion.NOTE;
                break;
            case "blue_potion":
                link.potion = Potion.BLUE;
                break;
            case "red_potion":
                link.potion = Potion.RED;
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
            switchToNextItem(link);
        }
    }

    /**
     * Switch to the next item in the inventory
     */
    public void switchToNextItem(Link link) {
        int initialSecondItem = link.secondItem;
        if (link.secondItem < 2 && link.bombQuantity > 0) {
            link.secondItem = 2;
        } else if (link.secondItem < 3 && link.bow != Bow.NONE && link.arrow.type != ArrowType.NONE) {
            link.secondItem = 3;
        } else if (link.secondItem < 4 && link.light != Light.NONE) {
            link.secondItem = 4;
        } else if (link.secondItem < 5 && link.flute != Flute.NONE) {
            link.secondItem = 5;
        } else if (link.secondItem < 6 && link.meat != Meat.NONE) {
            link.secondItem = 6;
        } else if (link.secondItem < 7 && link.potion != Potion.NONE) {
            link.secondItem = 7;
        } else if (link.secondItem < 8 && link.scepter != Scepter.NONE) {
            link.secondItem = 8;
        }

        // Item has not changed yet, try again from the beginning
        if (initialSecondItem == link.secondItem) {
            link.secondItem = 0;
            if (link.boomerang.type != BoomerangType.NONE) {
                link.secondItem = 1;
            } else if (link.bombQuantity > 0) {
                link.secondItem = 2;
            } else if (link.bow != Bow.NONE && link.arrow.type != ArrowType.NONE) {
                link.secondItem = 3;
            } else if (link.light != Light.NONE) {
                link.secondItem = 4;
            } else if (link.flute != Flute.NONE) {
                link.secondItem = 5;
            } else if (link.meat != Meat.NONE) {
                link.secondItem = 6;
            } else if (link.potion != Potion.NONE) {
                link.secondItem = 7;
            } else if (link.scepter != Scepter.NONE) {
                link.secondItem = 8;
            }
        }

        guiManager.updateCursor(link.secondItem);
    }
}
