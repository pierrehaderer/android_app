package com.twoplayers.legend.character.link;

import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.inventory.Arrow;
import com.twoplayers.legend.character.link.inventory.Boomerang;
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

public class LinkService {

    /**
     * Check if link already has the item
     */
    public boolean alreadyInInventory(Link link, Item item) {
        switch (item.name) {
            case "wood_arrow":
                return link.arrow != Arrow.NONE;
            case "white_arrow":
                return link.arrow == Arrow.WHITE;
            case "wood_boomerang":
                return link.boomerang != Boomerang.NONE;
            case "white_boomerang":
                return link.boomerang == Boomerang.WHITE;
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
        switch (item.name) {
            case "wood_arrow":
                link.arrow = Arrow.WOOD;
                break;
            case "white_arrow":
                link.arrow = Arrow.WHITE;
                break;
            case "wood_boomerang":
                link.boomerang = Boomerang.WOOD;
                break;
            case "white_boomerang":
                link.boomerang = Boomerang.WHITE;
                break;
            case "bombs":
                link.bomb = Math.min(link.bomb + 4, link.bombMax);
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
    }
}
