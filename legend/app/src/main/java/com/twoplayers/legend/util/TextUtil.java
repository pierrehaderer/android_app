package com.twoplayers.legend.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.kilobolt.framework.Game;
import com.twoplayers.legend.MainActivity;

public class TextUtil {

    private static Paint paint;
    private static Paint debugPaint;

    public synchronized static void initPaint(Game game) {
        Typeface tf = Typeface.createFromAsset(((MainActivity) game).getAssetManager(), "other/the-legend-of-zelda-nes.ttf");

        paint = new Paint();
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        // Changer la "fonts" du texte
        paint.setTypeface(tf);

        debugPaint = new Paint();
        debugPaint.setTextSize(10);
        debugPaint.setTextAlign(Paint.Align.LEFT);
        debugPaint.setAntiAlias(true);
        debugPaint.setColor(Color.WHITE);
        // Changer la "fonts" du texte
        debugPaint.setTypeface(tf);
    }

    public static Paint getPaint() {
        return paint;
    }

    public static Paint getDebugPaint() {
        return debugPaint;
    }
}
