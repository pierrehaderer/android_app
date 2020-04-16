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

    public static final int LEFT_SCREEN = 0;
    public static final int TOP_SCREEN = 0;
    public static final int WIDTH_SCREEN = 548;
    public static final int HEIGHT_SCREEN = 480;

    private ImageOther imageOther;
    private int offset = 0;
    private int coef1 = 0;
    private int coef2 = 0;
    private int coef3 = 0;
    private int coef4= 0;
    private int coef5 = 0;
    private int coef6 = 0;
    private int coef7 = 0;
    private int coef8 = 0;
    private int coef9 = 0;
    private int coef10 = 0;
    private int coef11 = 0;
    private int coef12 = 0;

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
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 100, TOP_SCREEN + 100, 100, 100)) {
                    coef1 = (coef1 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 200, TOP_SCREEN + 100, 100, 100)) {
                    coef2 = (coef2 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 300, TOP_SCREEN + 100, 100, 100)) {
                    coef3 = (coef3 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 400, TOP_SCREEN + 100, 100, 100)) {
                    coef4 = (coef4 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 100, TOP_SCREEN + 200, 100, 100)) {
                    coef5 = (coef5 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 200, TOP_SCREEN + 200, 100, 100)) {
                    coef6 = (coef6 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 300, TOP_SCREEN + 200, 100, 100)) {
                    coef7 = (coef7 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 400, TOP_SCREEN + 200, 100, 100)) {
                    coef8 = (coef8 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 100, TOP_SCREEN + 300, 100, 100)) {
                    coef9 = (coef9 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 200, TOP_SCREEN + 300, 100, 100)) {
                    coef10 = (coef10 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 300, TOP_SCREEN + 300, 100, 100)) {
                    coef11 = (coef11 + 1) % 11;
                    break;
                }
                if (LocationUtil.inBounds(event, LEFT_SCREEN + 400, TOP_SCREEN + 300, 100, 100)) {
                    coef12 = (coef12 + 1) % 11;
                    break;
                }
            }
        }
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(LEFT_SCREEN, TOP_SCREEN, WIDTH_SCREEN, HEIGHT_SCREEN, Color.BLACK);
        g.drawRect(LEFT_SCREEN + 110, TOP_SCREEN + 110, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 210, TOP_SCREEN + 110, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 310, TOP_SCREEN + 110, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 410, TOP_SCREEN + 110, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 110, TOP_SCREEN + 210, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 210, TOP_SCREEN + 210, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 310, TOP_SCREEN + 210, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 410, TOP_SCREEN + 210, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 110, TOP_SCREEN + 310, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 210, TOP_SCREEN + 310, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 310, TOP_SCREEN + 310, 80, 80, Color.WHITE);
        g.drawRect(LEFT_SCREEN + 410, TOP_SCREEN + 310, 80, 80, Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        g.drawString(String.valueOf(coef1), LEFT_SCREEN + 130, TOP_SCREEN + 130, paint);
        g.drawString(String.valueOf(coef2), LEFT_SCREEN + 230, TOP_SCREEN + 130, paint);
        g.drawString(String.valueOf(coef3), LEFT_SCREEN + 330, TOP_SCREEN + 130, paint);
        g.drawString(String.valueOf(coef4), LEFT_SCREEN + 430, TOP_SCREEN + 130, paint);
        g.drawString(String.valueOf(coef5), LEFT_SCREEN + 130, TOP_SCREEN + 230, paint);
        g.drawString(String.valueOf(coef6), LEFT_SCREEN + 230, TOP_SCREEN + 230, paint);
        g.drawString(String.valueOf(coef7), LEFT_SCREEN + 330, TOP_SCREEN + 230, paint);
        g.drawString(String.valueOf(coef8), LEFT_SCREEN + 430, TOP_SCREEN + 230, paint);
        g.drawString(String.valueOf(coef9), LEFT_SCREEN + 130, TOP_SCREEN + 330, paint);
        g.drawString(String.valueOf(coef10), LEFT_SCREEN + 230, TOP_SCREEN + 330, paint);
        g.drawString(String.valueOf(coef11), LEFT_SCREEN + 330, TOP_SCREEN + 330, paint);
        g.drawString(String.valueOf(coef12), LEFT_SCREEN + 430, TOP_SCREEN + 330, paint);

        float[] colorTransform = {
                coef1 / 10f, coef2 / 10f, coef3 / 10f, coef4 / 10f, 0,
                coef5 / 10f, coef6 / 10f, coef7 / 10f, coef8 / 10f, 0,
                coef9 / 10f, coef10 / 10f, coef11 / 10f, coef12 / 10f, 0,
                0, 0, 0, 1f, 0};
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        colorMatrix.set(colorTransform);
        g.drawImage(imageOther.get("map_g"), 500, 150, colorMatrix);
        g.drawImage(imageOther.get("map_d"), 628, 150);
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
