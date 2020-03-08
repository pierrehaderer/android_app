package com.twoplayers.legend.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.kilobolt.framework.Game;
import com.twoplayers.legend.MainActivity;

public class TextUtil {

    private static Paint paint;

    public synchronized static void initPaint(Game game) {
        paint = new Paint();
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        // Changer la "fonts" du texte
        Typeface tf = Typeface.createFromAsset(((MainActivity) game).getAssetManager(), "other/the-legend-of-zelda-nes.ttf");
        paint.setTypeface(tf);
    }

    public static Paint getPaint() {
        return paint;
    }
}
