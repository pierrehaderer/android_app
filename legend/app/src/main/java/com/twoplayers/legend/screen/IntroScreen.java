package com.twoplayers.legend.screen;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;

import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.ImageOther;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input.TouchEvent;
import com.kilobolt.framework.Screen;

import java.util.List;

public class IntroScreen extends Screen {

    public static final int LEFT_SCREEN = 150;
    public static final int TOP_SCREEN = 0;
    public static final int WIDTH_SCREEN = 548;
    public static final int HEIGHT_SCREEN = 480;

    private ImageOther imageOther;

    private float count;
    private ColorMatrix colorMatrix;
    private float[] colorTransform;

    public IntroScreen(Game game) {
        super(game);
        Logger.info("Entering IntroScreen.");
        imageOther = ((MainActivity) game).getAllImages().getImageOther();

        colorTransform = new float[20];
        colorTransform[0] = 0;
        colorTransform[1] = 0;
        colorTransform[2] = 0;
        colorTransform[3] = 0;
        colorTransform[4] = 0;
        colorTransform[5] = 0;
        colorTransform[6] = 0;
        colorTransform[7] = 0;
        colorTransform[8] = 0;
        colorTransform[9] = 0;
        colorTransform[10] = 0;
        colorTransform[11] = 0;
        colorTransform[12] = 0;
        colorTransform[13] = 0;
        colorTransform[14] = 0;
        colorTransform[15] = 0;
        colorTransform[16] = 0;
        colorTransform[17] = 0;
        colorTransform[18] = 1f;
        colorTransform[19] = 0;
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f); //Remove Colour
        colorMatrix.set(colorTransform); //Apply the Red

    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
//                if (LocationUtil.inBounds(event, LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN)) {
//                    game.setScreen(new WorldMapLoadingScreen(game));
//                    break;
//                }
                if (LocationUtil.inBounds(event, 100, 0, 100, 100)) {
                    colorTransform[0] = 1 - colorTransform[0];
                }
                if (LocationUtil.inBounds(event, 200, 0, 100, 100)) {
                    colorTransform[1] = 1 - colorTransform[1];
                }
                if (LocationUtil.inBounds(event, 300, 0, 100, 100)) {
                    colorTransform[2] = 1 - colorTransform[2];
                }
                if (LocationUtil.inBounds(event, 400, 0, 100, 100)) {
                    colorTransform[3] = 1 - colorTransform[3];
                }
                if (LocationUtil.inBounds(event, 500, 0, 100, 100)) {
                    colorTransform[4] = 1 - colorTransform[4];
                }

                if (LocationUtil.inBounds(event, 100, 100, 100, 100)) {
                    colorTransform[5] = 1 - colorTransform[5];
                }
                if (LocationUtil.inBounds(event, 200, 100, 100, 100)) {
                    colorTransform[6] = 1 - colorTransform[6];
                }
                if (LocationUtil.inBounds(event, 300, 100, 100, 100)) {
                    colorTransform[7] = 1 - colorTransform[7];
                }
                if (LocationUtil.inBounds(event, 400, 100, 100, 100)) {
                    colorTransform[8] = 1 - colorTransform[8];
                }
                if (LocationUtil.inBounds(event, 500, 100, 100, 100)) {
                    colorTransform[9] = 1 - colorTransform[9];
                }

                if (LocationUtil.inBounds(event, 100, 200, 100, 100)) {
                    colorTransform[10] = 1 - colorTransform[10];
                }
                if (LocationUtil.inBounds(event, 200, 200, 100, 100)) {
                    colorTransform[11] = 1 - colorTransform[11];
                }
                if (LocationUtil.inBounds(event, 300, 200, 100, 100)) {
                    colorTransform[12] = 1 - colorTransform[12];
                }
                if (LocationUtil.inBounds(event, 400, 200, 100, 100)) {
                    colorTransform[13] = 1 - colorTransform[13];
                }
                if (LocationUtil.inBounds(event, 500, 200, 100, 100)) {
                    colorTransform[14] = 1 - colorTransform[14];
                }

                if (LocationUtil.inBounds(event, 100, 300, 100, 100)) {
                    colorTransform[15] = 1 - colorTransform[15];
                }
                if (LocationUtil.inBounds(event, 200, 300, 100, 100)) {
                    colorTransform[16] = 1 - colorTransform[16];
                }
                if (LocationUtil.inBounds(event, 300, 300, 100, 100)) {
                    colorTransform[17] = 1 - colorTransform[17];
                }
                if (LocationUtil.inBounds(event, 400, 300, 100, 100)) {
                    colorTransform[18] = 1 - colorTransform[18];
                }
                if (LocationUtil.inBounds(event, 500, 300, 100, 100)) {
                    colorTransform[19] = 1 - colorTransform[19];
                }
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0f); //Remove Colour
                colorMatrix.set(colorTransform); //Apply the Red
                this.colorMatrix = colorMatrix;
            }
        }
        if (count > 0) {
            count -= deltaTime;
        } else {
            count = 200f;
        }
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN, Color.BLACK);
        //g.drawScaledImage(imageOther.get("intro_screen"), LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN);
        g.drawRect(100, 0, 100, 100, Color.GREEN);
        g.drawRect(200, 0, 100, 100, Color.BLUE);
        g.drawRect(300, 0, 100, 100, Color.GREEN);
        g.drawRect(400, 0, 100, 100, Color.BLUE);
        g.drawRect(500, 0, 100, 100, Color.GREEN);
        g.drawRect(100, 100, 100, 100, Color.BLUE);
        g.drawRect(200, 100, 100, 100, Color.GREEN);
        g.drawRect(300, 100, 100, 100, Color.BLUE);
        g.drawRect(400, 100, 100, 100, Color.GREEN);
        g.drawRect(500, 100, 100, 100, Color.BLUE);
        g.drawRect(100, 200, 100, 100, Color.GREEN);
        g.drawRect(200, 200, 100, 100, Color.BLUE);
        g.drawRect(300, 200, 100, 100, Color.GREEN);
        g.drawRect(400, 200, 100, 100, Color.BLUE);
        g.drawRect(500, 200, 100, 100, Color.GREEN);
        g.drawRect(100, 300, 100, 100, Color.BLUE);
        g.drawRect(200, 300, 100, 100, Color.GREEN);
        g.drawRect(300, 300, 100, 100, Color.BLUE);
        g.drawRect(400, 300, 100, 100, Color.GREEN);
        g.drawRect(500, 300, 100, 100, Color.BLUE);
        g.drawString(Float.toString(colorTransform[0]), 150, 50, new Paint());
        g.drawString(Float.toString(colorTransform[1]), 250, 50, new Paint());
        g.drawString(Float.toString(colorTransform[2]), 350, 50, new Paint());
        g.drawString(Float.toString(colorTransform[3]), 450, 50, new Paint());
        g.drawString(Float.toString(colorTransform[4]), 550, 50, new Paint());
        g.drawString(Float.toString(colorTransform[5]), 150, 150, new Paint());
        g.drawString(Float.toString(colorTransform[6]), 250, 150, new Paint());
        g.drawString(Float.toString(colorTransform[7]), 350, 150, new Paint());
        g.drawString(Float.toString(colorTransform[8]), 450, 150, new Paint());
        g.drawString(Float.toString(colorTransform[9]), 550, 150, new Paint());
        g.drawString(Float.toString(colorTransform[10]), 150, 250, new Paint());
        g.drawString(Float.toString(colorTransform[11]), 250, 250, new Paint());
        g.drawString(Float.toString(colorTransform[12]), 350, 250, new Paint());
        g.drawString(Float.toString(colorTransform[13]), 450, 250, new Paint());
        g.drawString(Float.toString(colorTransform[14]), 550, 250, new Paint());
        g.drawString(Float.toString(colorTransform[15]), 150, 350, new Paint());
        g.drawString(Float.toString(colorTransform[16]), 250, 350, new Paint());
        g.drawString(Float.toString(colorTransform[17]), 350, 350, new Paint());
        g.drawString(Float.toString(colorTransform[18]), 450, 350, new Paint());
        g.drawString(Float.toString(colorTransform[19]), 550, 350, new Paint());
        if (count > 100) {
            g.drawImage(imageOther.get("link_down_1"), 5, 5);
        } else {
            g.drawImage(imageOther.get("link_down_1"), 5, 5, colorMatrix);
        }
        g.drawImage(imageOther.get("link_down_1"), 5, 35);
        g.drawImage(imageOther.get("link_down_1"), 35, 35, colorMatrix);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void backButton() {
        android.os.Process.killProcess(android.os.Process.myPid());
        //imageOther.theme.stop();
    }
}
