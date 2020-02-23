package com.twoplayers.legend.gui;

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
    public static final int TOP_ARROWS = 330;
    public static final int WIDTH_ARROWS = 150;
    public static final int HEIGHT_ARROWS = 150;

    public static final int LEFT_UP = 50;
    public static final int TOP_UP = 330;
    public static final int LEFT_DOWN = 50;
    public static final int TOP_DOWN = 430;
    public static final int LEFT_LEFT = 0;
    public static final int TOP_LEFT = 380;
    public static final int LEFT_RIGHT = 100;
    public static final int TOP_RIGHT = 380;
    public static final int WIDTH_ARROW = 50;
    public static final int HEIGHT_ARROW = 50;

    private Game game;
    private ImageGui imageGui;

    private boolean buttonActivated;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        this.game = game;
        imageGui = ((MainActivity) game).getAllImages().getImageGui();
        imageGui.load(game.getGraphics());

        buttonActivated = true;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
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
                }
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    if (LocationUtil.inBounds(event, LEFT_ARROWS, TOP_ARROWS, WIDTH_ARROWS, HEIGHT_ARROWS)) {
                        upPressed = false;
                        downPressed = false;
                        leftPressed = false;
                        rightPressed = false;
                    }
                }
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawImage(imageGui.get("button_arrows"), LEFT_ARROWS, TOP_ARROWS);
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
}
