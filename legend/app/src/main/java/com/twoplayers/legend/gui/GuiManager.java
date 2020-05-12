package com.twoplayers.legend.gui;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImagesGui;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.arrow.ArrowType;
import com.twoplayers.legend.character.link.inventory.boomerang.BoomerangType;
import com.twoplayers.legend.character.link.inventory.arrow.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.light.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.rod.RodType;
import com.twoplayers.legend.character.link.inventory.sword.SwordType;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.TextUtil;

public class GuiManager implements IManager {

    private static final int LEFT_MINI_MAP = 7;
    private static final int TOP_MINI_MAP = 7;

    private static final int LEFT_ARROWS = 0;
    private static final int TOP_ARROWS = 0;
    private static final int WIDTH_ARROWS = 300;
    private static final int HEIGHT_ARROWS = 480;

    private static final int LEFT_UP = 0;
    private static final int TOP_UP = 275;
    private static final int LEFT_DOWN = 0;
    private static final int TOP_DOWN = 425;
    private static final int LEFT_LEFT = 0;
    private static final int TOP_LEFT = 325;
    private static final int LEFT_RIGHT = 100;
    private static final int TOP_RIGHT = 325;
    private static final int WIDTH_ARROW_UP = 150;
    private static final int HEIGHT_ARROW_UP = 100;
    private static final int WIDTH_ARROW_DOWN = 510;
    private static final int HEIGHT_ARROW_DOWN = 50;
    private static final int WIDTH_ARROW_LEFT = 50;
    private static final int HEIGHT_ARROW_LEFT = 150;
    private static final int WIDTH_ARROW_RIGHT = 100;
    private static final int HEIGHT_ARROW_RIGHT = 150;

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

    private static final int LEFT_CURSOR = 708;
    private static final int TOP_CURSOR = 26;
    private static final int WIDTH_ITEM = 22;
    private static final int HEIGHT_ITEM = 18;

    private static final int LEFT_SWORD = 460;
    private static final int TOP_SWORD = 28;
    private static final int LEFT_ITEM_B = 388;
    private static final int TOP_ITEM_B = 28;
    private static final float COEF_SELECTED_ITEMS = 3;

    private static final int LEFT_RUPEES = 305;
    private static final int TOP_RUPEES = 29;
    private static final int LEFT_KEYS = 305;
    private static final int TOP_KEYS = 57;
    private static final int LEFT_BOMBS = 305;
    private static final int TOP_BOMBS = 84;

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
    private static final int LEFT_ROD = 778;
    private static final int TOP_ROD = 44;

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

    private static final int DEBUGY = 112;
    private static final int DEBUGX = 8;

    private boolean initNotDone = true;

    private Game game;
    private ImagesGui imagesGui;

    private IZoneManager zoneManager;
    private LinkManager linkManager;
    private SaveManager saveManager;

    private boolean buttonsActivated;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private boolean aPressed;
    private boolean bPressed;
    private boolean cPressed;

    private int leftCursor;
    private int topCursor;

    private float time;
    /**
     * Load this manager
     */
    public void load(Game game, int zone) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        linkManager = ((MainActivity) game).getLinkManager();
        zoneManager = ((MainActivity) game).getZoneManager(zone);
        saveManager = ((MainActivity) game).getSaveManager();
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        this.game = game;

        imagesGui = ((MainActivity) game).getAllImages().getImagesGui();
        imagesGui.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        leftCursor = LEFT_CURSOR;
        topCursor = TOP_CURSOR;

        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        aPressed = false;
        bPressed = false;
        cPressed = false;

        buttonsActivated = true;
        time = 0;
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        time += deltaTime;

        for (Input.TouchEvent event : game.getInput().getTouchEvents()) {
            if (event.type == Input.TouchEvent.TOUCH_DOWN || event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                if (buttonsActivated) {
                    if (LocationUtil.inBounds(event, LEFT_ARROWS, TOP_ARROWS, WIDTH_ARROWS, HEIGHT_ARROWS)) {
                        upPressed = false;
                        downPressed = false;
                        leftPressed = false;
                        rightPressed = false;
                    }
                    if (LocationUtil.inBounds(event, LEFT_UP, TOP_UP, WIDTH_ARROW_UP, HEIGHT_ARROW_UP)) {
                        upPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_DOWN, TOP_DOWN, WIDTH_ARROW_DOWN, HEIGHT_ARROW_DOWN)) {
                        downPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_LEFT, TOP_LEFT, WIDTH_ARROW_LEFT, HEIGHT_ARROW_LEFT)) {
                        leftPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_RIGHT, TOP_RIGHT, WIDTH_ARROW_RIGHT, HEIGHT_ARROW_RIGHT)) {
                        rightPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_A, TOP_A, WIDTH_BUTTON, HEIGHT_BUTTON_A)) {
                        aPressed = true;
                    }
                    if (LocationUtil.inBounds(event, LEFT_B, TOP_B, WIDTH_BUTTON, HEIGHT_BUTTON)) {
                        bPressed = true;
                    }
                    logButtons(event);
                }
                if (LocationUtil.inBounds(event, LEFT_C, TOP_C, WIDTH_BUTTON, HEIGHT_BUTTON)) {
                    cPressed = true;
                }
                if (LocationUtil.inBounds(event, LEFT_HEARTS, TOP_HEARTS_UPPER_ROW, 8 * WIDTH_HEART, 3 * WIDTH_HEART) && event.type == Input.TouchEvent.TOUCH_DOWN) {
                    linkManager.increaseLinkLife();
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
                logButtons(event);
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        // Draw background GUI
        g.drawImage(imagesGui.get("gui"), 0, 0);

        // Draw mini map
        g.drawImage(zoneManager.getMiniMap(), LEFT_MINI_MAP, TOP_MINI_MAP);
        float miniX = LEFT_MINI_MAP + zoneManager.getCurrentMiniAbscissa() + 5;
        float miniY = TOP_MINI_MAP + zoneManager.getCurrentMiniOrdinate() + 3;
        g.drawRect((int) miniX, (int) miniY, 8, 7, Color.BLUE);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                if (!zoneManager.isExplored(i, j)) {
                    g.drawRect(LEFT_MINI_MAP + 16 * i, TOP_MINI_MAP + 11 * j, 18, 13, Color.DKGRAY);
                }
            }
        }

        Link link = linkManager.getLink();

        // Draw link life
        float linkLifeMax = link.getLifeMax();
        float linkLife = link.getLife();
        for (int i = 1; i <= Math.min(8, linkLifeMax); i++) {
            if (linkLife >= i) {
                g.drawImage(imagesGui.get("heart"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            } else if (linkLife > i - 1) {
                g.drawImage(imagesGui.get("heart_half"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            } else {
                g.drawImage(imagesGui.get("heart_empty"), LEFT_HEARTS + (i - 1) * WIDTH_HEART, TOP_HEARTS_LOWER_ROW);
            }
        }

        for (int i = 9; i <= Math.min(16, linkLifeMax); i++) {
            if (linkLife >= i) {
                g.drawImage(imagesGui.get("heart"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            } else if (linkLife > i - 1) {
                g.drawImage(imagesGui.get("heart_half"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            } else {
                g.drawImage(imagesGui.get("heart_empty"), LEFT_HEARTS + (i - 9) * WIDTH_HEART, TOP_HEARTS_UPPER_ROW);
            }
        }

        // Draw selected items
        if (link.getSword().getType() != SwordType.NONE) {
            g.drawScaledImage(imagesGui.get(link.getSword().getType().name),LEFT_SWORD, TOP_SWORD, COEF_SELECTED_ITEMS);
        }
        switch (link.getSecondItem()) {
            case 0:
            case 1:
                g.drawScaledImage(imagesGui.get(link.getBoomerang().getType().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 2:
                g.drawScaledImage(imagesGui.get("bomb"),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 3:
                g.drawScaledImage(imagesGui.get(link.getArrow().getType().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
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
                g.drawScaledImage(imagesGui.get(link.getRod().getType().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
        }

        // Draw inventory items
        if (link.getBombQuantity() > 0) {
            g.drawImage(imagesGui.get("bomb"), LEFT_BOMB, TOP_BOMB);
        }
        if (link.getArrow().getType() != ArrowType.NONE) {
            g.drawImage(imagesGui.get(link.getArrow().getType().name),LEFT_ARROW, TOP_ARROW);
        }
        if (link.getBoomerang().getType() != BoomerangType.NONE) {
            g.drawImage(imagesGui.get(link.getBoomerang().getType().name),LEFT_BOOMERANG, TOP_BOOMERANG);
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
        if (link.getRod().getType() != RodType.NONE) {
            g.drawImage(imagesGui.get(link.getRod().getType().name), LEFT_ROD, TOP_ROD);
        }
        if (link.getSpellBook() != SpellBook.NONE) {
            g.drawImage(imagesGui.get(link.getSpellBook().name),LEFT_SPELLBOOK, TOP_SPELLBOOK);
        }
        g.drawImage(imagesGui.get("cursor"), leftCursor, topCursor);

        // Draw resources
        int linkRupees = link.getRupees();
        String rupeesToDisplay = (linkRupees < 100) ? "x" + linkRupees : String.valueOf(linkRupees);
        g.drawString(rupeesToDisplay, LEFT_RUPEES, TOP_RUPEES, TextUtil.getPaint());
        g.drawString("x" + link.getKeys(), LEFT_KEYS, TOP_KEYS, TextUtil.getPaint());
        g.drawString("x" + link.getBombQuantity(), LEFT_BOMBS, TOP_BOMBS, TextUtil.getPaint());

        // Draw debug info
        g.drawString("Attempt: " + saveManager.getSave().getAttempt(), DEBUGX, DEBUGY, TextUtil.getDebugPaint());
        g.drawString("Time: " + (int) time, DEBUGX, DEBUGY + 20, TextUtil.getDebugPaint());
    }

    /**
     * Log pressed buttons properly
     */
    private void logButtons(Input.TouchEvent event) {
        String type;
        if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
            type = "DRAGGED";
        } else if (event.type == Input.TouchEvent.TOUCH_DOWN) {
            type = "DOWN";
        } else if (event.type == Input.TouchEvent.TOUCH_UP) {
            type = "UP";
        } else if (event.type == Input.TouchEvent.TOUCH_HOLD) {
            type = "HOLD";
        } else {
            type = "UNKNOWN";
        }
        String message = String.format("%1$" + 18 + "s", "Event : " + type + "-(" + event.x + "," + event.y + ")");
        message += "up=" + ((upPressed) ? "1" : "0") + ",down=" + ((downPressed) ? "1" : "0");
        message += ",left=" + ((leftPressed) ? "1" : "0") + ",right=" + ((rightPressed) ? "1" : "0");
        message += ",a=" + ((aPressed) ? "1" : "0") + ",b=" + ((bPressed) ? "1" : "0") + ",c=" + ((cPressed) ? "1" : "0");
        Logger.debug(message);
    }

    /**
     * Update cursor position
     */
    public void updateCursor(int secondItem) {
        int secondItemToUse = (secondItem == 0) ? 0 : secondItem - 1;
        leftCursor = LEFT_CURSOR + ((secondItemToUse) % 4) * WIDTH_ITEM;
        topCursor = TOP_CURSOR + (secondItemToUse / 4) * HEIGHT_ITEM;
    }

    /**
     * Activate the buttons
     */
    public void activateButtons() {
        buttonsActivated = true;
    }

    /**
     * Deactivate the buttons
     */
    public void deactivateButtons() {
        buttonsActivated = false;
    }

    public boolean areButtonsActivated() {
        return buttonsActivated;
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
