package com.twoplayers.legend.gui;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageGui;
import com.twoplayers.legend.character.LinkManager;
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
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

public class GuiManager implements IManager {

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
    private ImageGui imageGui;

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
        imageGui = ((MainActivity) game).getAllImages().getImageGui();
        imageGui.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        linkManager = ((MainActivity) game).getLinkManager();

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
        g.drawImage(imageGui.get("gui"), 0, 0);

        // Draw selected items
        if (linkManager.getLink().getSword() != Sword.NONE) {
            g.drawScaledImage(imageGui.get(linkManager.getLink().getSword().name),LEFT_SWORD, TOP_SWORD, COEF_SELECTED_ITEMS);
        }
        switch (cursor_position) {
            case 1:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getBoomerang().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 2:
                g.drawScaledImage(imageGui.get("bomb"),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 3:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getBow().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 4:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getLight().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 5:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getFlute().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 6:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getMeat().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 7:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getPotion().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
            case 8:
                g.drawScaledImage(imageGui.get(linkManager.getLink().getScepter().name),LEFT_ITEM_B, TOP_ITEM_B, COEF_SELECTED_ITEMS);
                break;
        }

        // Draw inventory items
        if (linkManager.getLink().getBomb() > 0) {
            g.drawImage(imageGui.get("bomb"), LEFT_BOMB, TOP_BOMB);
        }
        if (linkManager.getLink().getArrow() != Arrow.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getArrow().name),LEFT_ARROW, TOP_ARROW);
        }
        if (linkManager.getLink().getBoomerang() != Boomerang.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getBoomerang().name),LEFT_BOOMERANG, TOP_BOOMERANG);
        }
        if (linkManager.getLink().getBow() != Bow.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getBow().name),LEFT_BOW, TOP_BOW);
        }
        if (linkManager.getLink().getBracelet() != Bracelet.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getBracelet().name),LEFT_BRACELET, TOP_BRACELET);
        }
        if (linkManager.getLink().getCompass() != Compass.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getCompass().name),LEFT_COMPASS, TOP_COMPASS);
        }
        if (linkManager.getLink().getDungeonMap() != DungeonMap.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getDungeonMap().name),LEFT_DUNGEONMAP, TOP_DUNGEONMAP);
        }
        if (linkManager.getLink().getFlute() != Flute.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getFlute().name),LEFT_FLUTE, TOP_FLUTE);
        }
        if (linkManager.getLink().getInfiniteKey() != InfiniteKey.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getInfiniteKey().name),LEFT_INFINITEKEY, TOP_INFINITEKEY);
        }
        if (linkManager.getLink().getLadder() != Ladder.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getLadder().name),LEFT_LADDER, TOP_LADDER);
        }
        if (linkManager.getLink().getLight() != Light.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getLight().name),LEFT_LIGHT, TOP_LIGHT);
        }
        if (linkManager.getLink().getMeat() != Meat.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getMeat().name),LEFT_MEAT, TOP_MEAT);
        }
        if (linkManager.getLink().getPotion() != Potion.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getPotion().name),LEFT_POTION, TOP_POTION);
        }
        if (linkManager.getLink().getRaft() != Raft.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getRaft().name),LEFT_RAFT, TOP_RAFT);
        }
        if (linkManager.getLink().getRing() != Ring.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getRing().name),LEFT_RING, TOP_RING);
        }
        if (linkManager.getLink().getScepter() != Scepter.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getScepter().name),LEFT_SCEPTER, TOP_SCEPTER);
        }
        if (linkManager.getLink().getSpellBook() != SpellBook.NONE) {
            g.drawImage(imageGui.get(linkManager.getLink().getSpellBook().name),LEFT_SPELLBOOK, TOP_SPELLBOOK);
        }
        g.drawImage(imageGui.get("cursor"),left_cursor, top_cursor);
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
