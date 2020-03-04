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
    private int offset = 0;

    public IntroScreen(Game game) {
        super(game);
        Logger.info("Entering IntroScreen.");
        imageOther = ((MainActivity) game).getAllImages().getImageOther();


    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (LocationUtil.inBounds(event, LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN)) {
                    offset = offset + 4096;
                    break;
                }

            }
        }
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN, Color.BLACK);
        //g.drawScaledImage(imageOther.get("intro_screen"), LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN);
        float coef = 0.1f  + (offset % 40960) / 40960f;
        for (int i = 0; i < 512; i++) {
            char[] chars = Integer.toBinaryString(4096 + i).toCharArray();
            float[] colorTransform = {
                    0, 0f, 0, 0, 0,
                    0, 0, 0f, 0, 0,
                    0, 0, 0, 0f, 0,
                    0, 0, 0, 1f, 0};
            colorTransform[0] = Float.parseFloat(Character.toString(chars[12]))*coef;
            colorTransform[1] = Float.parseFloat(Character.toString(chars[11]))*coef;
            colorTransform[2] = Float.parseFloat(Character.toString(chars[10]))*coef;
            colorTransform[3] = Float.parseFloat(Character.toString(chars[9]))*coef;

            colorTransform[5] = Float.parseFloat(Character.toString(chars[8]))*coef;
            colorTransform[6] = Float.parseFloat(Character.toString(chars[7]))*coef;
            colorTransform[7] = Float.parseFloat(Character.toString(chars[6]))*coef;
            colorTransform[8] = Float.parseFloat(Character.toString(chars[5]))*coef;

            colorTransform[10] = Float.parseFloat(Character.toString(chars[4]))*coef;
            colorTransform[11] = Float.parseFloat(Character.toString(chars[3]))*coef;
            colorTransform[12] = Float.parseFloat(Character.toString(chars[2]))*coef;
            colorTransform[13] = Float.parseFloat(Character.toString(chars[1]))*coef;
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0f);
            colorMatrix.set(colorTransform);
            g.drawImage(imageOther.get("link_down_1"), 5 + (i % 32) * 16, 5 + (i / 32) * 16, colorMatrix);
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            g.drawRect(100, 380, 100, 100, Color.DKGRAY);
            g.drawString(Integer.toString(offset), 100, 400, paint);
        }
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
