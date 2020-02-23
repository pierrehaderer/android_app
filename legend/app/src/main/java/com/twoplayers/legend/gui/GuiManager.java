package com.twoplayers.legend.gui;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageGui;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.List;

public class GuiManager implements IManager {

    public static final int LEFT_ARROWS = 0;
    public static final int TOP_ARROWS = 240;
    public static final int WIDTH_ARROWS = 300;
    public static final int HEIGHT_ARROWS = 240;

    public static final int LEFT_UP = 50;
    public static final int TOP_UP = 325;
    public static final int LEFT_DOWN = 50;
    public static final int TOP_DOWN = 425;
    public static final int LEFT_LEFT = 0;
    public static final int TOP_LEFT = 375;
    public static final int LEFT_RIGHT = 100;
    public static final int TOP_RIGHT = 375;
    public static final int WIDTH_ARROW = 50;
    public static final int HEIGHT_ARROW = 50;

    public static final int LEFT_BUTTONS = 600;
    public static final int TOP_BUTTONS = 120;
    public static final int WIDTH_BUTTONS = 255;
    public static final int HEIGHT_BUTTONS = 365;

    public static final int LEFT_A = 705;
    public static final int TOP_A = 385;
    public static final int LEFT_B = 705;
    public static final int TOP_B = 295;
    public static final int LEFT_C = 705;
    public static final int TOP_C = 205;
    public static final int WIDTH_BUTTON = 150;
    public static final int HEIGHT_BUTTON = 90;
    public static final int HEIGHT_BUTTON_A = 100;

    private Game game;
    private ImageGui imageGui;

    private boolean buttonActivated;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private boolean aPressed;
    private boolean bPressed;
    private boolean cPressed;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        this.game = game;
        imageGui = ((MainActivity) game).getAllImages().getImageGui();
        imageGui.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        buttonActivated = true;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        aPressed = false;
        bPressed = false;
        cPressed = false;
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
        g.drawImage(imageGui.get("gui"), 0, 0);
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
