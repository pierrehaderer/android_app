package com.twoplayers.legend.util;

import android.graphics.ColorMatrix;

public class ColorMatrixZone {

    private static final float INITIAL_COUNTER = 4f;
    private static final float ROTATION_SPEED = 0.2f;
    private ColorMatrix[] matrixList;

    private float count;

    public ColorMatrixZone() {
        count = 0;
        matrixList = new ColorMatrix[2];
        float[] colorTransform0 = {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0};
        matrixList[0] = new ColorMatrix();
        matrixList[0].setSaturation(0f);
        matrixList[0].set(colorTransform0);
        float[] colorTransform1 = {
                0.6f, 0, 0, 0, 0,
                0, 0.6f, 0, 0, 0,
                0, 0, 0.6f, 0, 0,
                0, 0, 0, 1, 0};
        matrixList[1] = new ColorMatrix();
        matrixList[1].setSaturation(0f);
        matrixList[1].set(colorTransform1);

    }

    public void activate() {
        count = INITIAL_COUNTER;
    }

    public void update(float deltaTime) {
        if (count > 0) {
            count -= deltaTime * ROTATION_SPEED;
        }
    }

    public ColorMatrix getMatrix() {
        return (count > 0) ? matrixList[(int) (count % matrixList.length)] : null;
    }
}
