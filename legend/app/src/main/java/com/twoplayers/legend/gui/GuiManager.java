package com.twoplayers.legend.gui;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesGui;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.object.Arrow;
import com.twoplayers.legend.character.object.Boomerang;
import com.twoplayers.legend.character.object.Bow;
import com.twoplayers.legend.character.object.Bracelet;
import com.twoplayers.legend.character.object.Compass;
import com.twoplayers.legend.character.object.DungeonMap;
import com.twoplayers.legend.character.object.Flute;
import com.twoplayers.legend.character.object.InfiniteKey;
import com.twoplayers.legend.character.object.Ladder;
import com.twoplayers.legend.character.object.Light;
import com.twoplayers.legend.character.object.Meat;
import com.twoplayers.legend.character.object.Potion;
import com.twoplayers.legend.character.object.Raft;
import com.twoplayers.legend.character.object.Ring;
import com.twoplayers.legend.character.object.Scepter;
import com.twoplayers.legend.character.object.SpellBook;
import com.twoplayers.legend.character.object.Sword;
import com.twoplayers.legend.map.WorldMapManager;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class GuiManager implements IManager {

    private static final int LEFT_MINI_MAP = 7;
    private static final int TOP_MINI_MAP = 7;

    private static final int LEFT_ARROWS = 0;
    private static final int TOP_ARROWS = 240;
    private static final int WIDTH_ARROWS = 300;
    private static final int HEIGHT_ARROWS = 240;

    private static final int LEFT_UP = 50;
    private static final int TOP_UP = 325;
    private static final int LEFT_DOWN = 50;
    private static final int TOP_DOWN = 425;
    private static final int LEFT_LEFT = 0;
    private static final int TOP_LEFT = 375;
    private static final int LEFT_RIGHT = 100;
    private static final int TOP_RIGHT = 375;
    private static final int WIDTH_ARROW = 50;
    private static final int HEIGHT_ARROW = 50;

    private static final int LEFT_BUTTONS = 600;
    private static final int TOP_BUTTONS = 120;
    private static final int WIDTH_BUTTONS = 255;
    private static final int HEIGHT_BUTTONS = 365;

    private static final int LEFT_A = 705;
    private static final int TOP_A = 385;
    private static final int LEFT_B = 705;
    private static final int TOP_B = 295;
    private static final int LEFT_C = 705;
    private static final int TOP_C = 205;
    private static final int WIDTH_BUTTON = 150;
    private static final int HEIGHT_BUTTON = 90;
    private static final int HEIGHT_BUTTON_A = 100;

    private static final int TOP_HEARTS_UPPER_ROW = 44;
    private static final int TOP_HEARTS_LOWER_ROW = 68;
    private static final int LEFT_HEARTS = 528;
    private static final int WIDTH_HEART = 18;

    private static final int LEFT_SWORD = 460;
    private static final int TOP_SWORD = 28;
    private static final int LEFT_ITEM_B = 388;
    private static final int TOP_ITEM_B = 28;
    private static final float COEF_SELECTED_ITEMS = 3;

    private static final int LEFT_BOOMERANG = 712;
    private static final int TOP_BOOMERANG = 26;
    private static final int LEFT_BOMB = 734;
    private static final int TOP_BOMB = 26;
    private static final int LEFT_ARROW = 752;
    private static final int TOP_ARROW = 26;
    private static final int LEFT_BOW = 760;
    private static final int TOP_BOW = 26;
    private static final int LEFT_LIGHT = 778;
    private static final int TOP_LIGHT = 26;

    private static final int LEFT_FLUTE = 712;
    private static final int TOP_FLUTE = 44;
    private static final int LEFT_MEAT = 734;
    private static final int TOP_MEAT = 44;
    private static final int LEFT_POTION = 756;
    private static final int TOP_POTION = 44;
    private static final int LEFT_SCEPTER = 778;
    private static final int TOP_SCEPTER = 44;

    private static final int LEFT_LADDER = 705;
    private static final int TOP_LADDER = 3;
    private static final int LEFT_RAFT = 725;
    private static final int TOP_RAFT = 3;
    private static final int LEFT_BRACELET = 745;
    private static final int TOP_BRACELET = 3;
    private static final int LEFT_RING = 757;
    private static final int TOP_RING = 3;
    private static final int LEFT_INFINITEKEY = 770;
    private static final int TOP_INFINITEKEY = 3;
    private static final int LEFT_SPELLBOOK = 783;
    private static final int TOP_SPELLBOOK = 3;

    private static final int LEFT_COMPASS = 755;
    private static final int TOP_COMPASS = 80;
    private static final int LEFT_DUNGEONMAP = 712;
    private static final int TOP_DUNGEONMAP = 80;

    private Game game;
    private ImagesGui imagesGui;

    private WorldMapManager worldMapManager;
    private LinkManager linkManager;

    private boolean buttonActivated;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private boolean aPressed;
    private boolean bPressed;
    private boolean cPressed;

    private int cursor_position;
    private int left_cursor;
    private int top_cursor;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        this.game = game;

        worldMapManager = ((MainActivity) game).getWorldMapManager();
        linkManager = ((MainActivity) game).getLinkManager();

        imagesGui = ((MainActivity) game).getAllImages().getImagesGui();
        imagesGui.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        buttonActivated = true;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        aPressed = false;
        bPressed = false;
        cPressed = false;

        cursor_position = 1;
        left_cursor = 708;
        top_cursor = 26;
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (buttonActivated) {
            for (Input.TouchEvent event : game.getInput().getTouchEvents()) {
                Logger.debug("Touch event : " + event.type + "-" + event.x + "-" + event.y);
                if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                    if (LocationUtil.inBounds(event, LEFT_UP, TOP_UP, WIDTH_ARROW, HEIGHT_ARROW)) {
                        upPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_DOWN, TOP_DOWN, WIDTH_ARROW, HEIGHT_ARROW)) {
                        downPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_LEFT, TOP_LEFT, WIDTH_ARROW, HEIGHT_ARROW)) {
                        leftPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_RIGHT, TOP_RIGHT, WIDTH_ARROW, HEIGHT_ARROW)) {
                        rightPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_A, TOP_A, WIDTH_BUTTON, HEIGHT_BUTTON_A)) {
                        aPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_B, TOP_B, WIDTH_BUTTON, HEIGHT_BUTTON)) {
                        bPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_C, TOP_C, WIDTH_BUTTON, HEIGHT_BUTTON)) {
                        cPressed = true;
                    }
                }
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    if (LocationUtil.inBounds(event, LEFT_ARROWS, TOP_ARROWS, WIDTH_ARROWS, HEIGHT_ARROWS)) {
                        upPressed = false;
                        downPressed = false;
                        leftPressed = false;
                        rightPressed = false;
                    }
                    if (LocationUtil.inBounds(event, LEFT_BUTTONS, TOP_BUTTONS, WIDTH_BUTTONS, HEIGHT_BUTTONS)) {
                        aPressed = false;
                        bPressed = false;
                        cPressed = false;
                    }
                }
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw background GUI
        g.drawImage(imagesGui.get("gui"), 0, 0);

        // Draw mini map
        g.drawImage(imagesGui.get("mini_world_map"), LEFT_MINI_MAP, TOP_MINI_MAP);
        float miniX = LEFT_MINI_MAP + worldMapManager.getCurrentMiniAbsisse() + 5;
        float miniY = TOP_MINI_MAP + worldMapManager.getCurrentMiniOrdinate() + 3;
        g.drawRect((int) miniX, (int) miniY, 8, 7, Color.BLUE);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                if (!worldMapManager.isExplored(i, j)) {
                    g.drawRect(LEFT_MINI_MAP + 16 * i, TOP_MINI_MAP + 11 * j, 18, 13, Color.DKGRAY);
                }
            }
        }

        Link link = linkManager.getLink();

        // Draw link life
        for (int i = 1; i <= Math.min(8, link.getLifeMax()); i++) {
            if (link.getLife() >= i) {
                g.drawImage(imagesGui.get("heart"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            } else if (link.getLife() > i - 1) {
                g.drawImage(imagesGui.get("heart_half"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            } else {
                g.drawImage(imagesGui.get("heart_empty"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            }
        }

        for (int i = 9; i <= Math.min(16, link.getLifeMax()); i++) {
            if (link.getLife() >= i) {
                g.drawImage(imagesGui.get("heart"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            } else if (link.getLife() > i - 1) {
                g.drawImage(imagesGui.get("heart_half"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            } else {
                g.drawImage(imagesGui.get("heart_empty"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            }
        }

        // Draw selected items
        if (link.getSword() != Sword.NONE) {
            g.drawScaledImage(imagesGui.get(link.getSword().name),LEFT_SWORD, TOP_SWORD, COEF_SELECTED_ITEMS);
        }
        switch (cursor_position) {
            case 1:
                g.drawScaledImage(imagesGui.get(link.getBoomerang().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 2:
                g.drawScaledImage(imagesGui.get("bomb"),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 3:
                g.drawScaledImage(imagesGui.get(link.getBow().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 4:
                g.drawScaledImage(imagesGui.get(link.getLight().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 5:
                g.drawScaledImage(imagesGui.get(link.getFlute().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 6:
                g.drawScaledImage(imagesGui.get(link.getMeat().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 7:
                g.drawScaledImage(imagesGui.get(link.getPotion().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 8:
                g.drawScaledImage(imagesGui.get(link.getScepter().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
        }

        // Draw inventory items
        if (link.getBomb() > 0) {
            g.drawImage(imagesGui.get("bomb"), LEFT_BOMB, TOP_BOMB);
        }
        if (link.getArrow() != Arrow.NONE) {
            g.drawImage(imagesGui.get(link.getArrow().name),LEFT_ARROW, TOP_ARROW);
        }
        if (link.getBoomerang() != Boomerang.NONE) {
            g.drawImage(imagesGui.get(link.getBoomerang().name),LEFT_BOOMERANG, TOP_BOOMERANG);
        }
        if (link.getBow() != Bow.NONE) {
            g.drawImage(imagesGui.get(link.getBow().name),LEFT_BOW, TOP_BOW);
        }
        if (link.getBracelet() != Bracelet.NONE) {
            g.drawImage(imagesGui.get(link.getBracelet().name),LEFT_BRACELET, TOP_BRACELET);
        }
        if (link.getCompass() != Compass.NONE) {
            g.drawImage(imagesGui.get(link.getCompass().name),LEFT_COMPASS, TOP_COMPASS);
        }
        if (link.getDungeonMap() != DungeonMap.NONE) {
            g.drawImage(imagesGui.get(link.getDungeonMap().name),LEFT_DUNGEONMAP, TOP_DUNGEONMAP);
        }
        if (link.getFlute() != Flute.NONE) {
            g.drawImage(imagesGui.get(link.getFlute().name),LEFT_FLUTE, TOP_FLUTE);
        }
        if (link.getInfiniteKey() != InfiniteKey.NONE) {
            g.drawImage(imagesGui.get(link.getInfiniteKey().name),LEFT_INFINITEKEY, TOP_INFINITEKEY);
        }
        if (link.getLadder() != Ladder.NONE) {
            g.drawImage(imagesGui.get(link.getLadder().name),LEFT_LADDER, TOP_LADDER);
        }
        if (link.getLight() != Light.NONE) {
            g.drawImage(imagesGui.get(link.getLight().name),LEFT_LIGHT, TOP_LIGHT);
        }
        if (link.getMeat() != Meat.NONE) {
            g.drawImage(imagesGui.get(link.getMeat().name),LEFT_MEAT, TOP_MEAT);
        }
        if (link.getPotion() != Potion.NONE) {
            g.drawImage(imagesGui.get(link.getPotion().name),LEFT_POTION, TOP_POTION);
        }
        if (link.getRaft() != Raft.NONE) {
            g.drawImage(imagesGui.get(link.getRaft().name),LEFT_RAFT, TOP_RAFT);
        }
        if (link.getRing() != Ring.NONE) {
            g.drawImage(imagesGui.get(link.getRing().name),LEFT_RING, TOP_RING);
        }
        if (link.getScepter() != Scepter.NONE) {
            g.drawImage(imagesGui.get(link.getScepter().name),LEFT_SCEPTER, TOP_SCEPTER);
        }
        if (link.getSpellBook() != SpellBook.NONE) {
            g.drawImage(imagesGui.get(link.getSpellBook().name),LEFT_SPELLBOOK, TOP_SPELLBOOK);
        }
        g.drawImage(imagesGui.get("cursor"),left_cursor, top_cursor);
    }

    /**
     * Activate the buttons
     */
    public void activateButtons() {
        buttonActivated = true;
    }

    /**
     * Deactivate the buttons
     */
    public void deactivateButtons() {
        buttonActivated = false;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isaPressed() {
        return aPressed;
    }

    public boolean isbPressed() {
        return bPressed;
    }

    public boolean iscPressed() {
        return cPressed;
    }
}
