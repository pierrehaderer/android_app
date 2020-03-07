package com.twoplayers.legend.character;

import android.graphics.Color;

import com.twoplayers.legend.assets.image.AllImages;

public class Hitbox {

    public static final int COLOR = Color.TRANSPARENT;

    public float x;
    public float y;
    public float x_offset;
    public float y_offset;
    public float width;
    public float height;

    /**
     * Constructor
     */
    public Hitbox(float x, float y, float x_offset, float y_offset, float width, float height) {
        this.x = x + x_offset * AllImages.COEF;
        this.y = y + y_offset * AllImages.COEF;
        this.x_offset = x_offset * AllImages.COEF;
        this.y_offset = y_offset * AllImages.COEF;
        this.width = width * AllImages.COEF;
        this.height = height * AllImages.COEF;
    }

    /**
     * Relocate hitbox with x, y provided
     */
    public void relocate(float x, float y) {
        this.x = x + x_offset;
        this.y = y + y_offset;
    }
}
