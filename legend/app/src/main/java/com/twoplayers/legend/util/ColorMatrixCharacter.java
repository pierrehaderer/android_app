package com.twoplayers.legend.util;

import android.graphics.ColorMatrix;

public class ColorMatrixCharacter {

    private static final float ROTATION_SPEED = 0.15f;
    private ColorMatrix[] matrixList;

    private float count;

    public ColorMatrixCharacter() {
        count = 0;
        matrixList = new ColorMatrix[8];
        matrixList[0] = createColorMatrix(2); // Rouge sombre
        matrixList[1] = createColorMatrix(34); // Vert delave
        matrixList[2] = createColorMatrix(288); // Bleu Leger
        matrixList[3] = createColorMatrix(99); // Jaune fort
        matrixList[4] = createColorMatrix(65); // Rouge appuye
        matrixList[5] = createColorMatrix(32); // Vert fluo
        matrixList[6] = createColorMatrix(272); // Bleu clair
        matrixList[7] = createColorMatrix(546); // Gris - 273 - 277
    }

    /**
     * Only 12 values are important in this matrix. Use numbers from 0 to 4095 written in binary to define these 12 values.
     */
    private ColorMatrix createColorMatrix(int value) {
        char[] chars = Integer.toBinaryString(4096 + value).toCharArray();
        float[] colorTransform = {
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0};
        colorTransform[0] = Float.parseFloat(Character.toString(chars[12]));
        colorTransform[1] = Float.parseFloat(Character.toString(chars[11]));
        colorTransform[2] = Float.parseFloat(Character.toString(chars[10]));
        colorTransform[3] = Float.parseFloat(Character.toString(chars[9]));

        colorTransform[5] = Float.parseFloat(Character.toString(chars[8]));
        colorTransform[6] = Float.parseFloat(Character.toString(chars[7]));
        colorTransform[7] = Float.parseFloat(Character.toString(chars[6]));
        colorTransform[8] = Float.parseFloat(Character.toString(chars[5]));

        colorTransform[10] = Float.parseFloat(Character.toString(chars[4]));
        colorTransform[11] = Float.parseFloat(Character.toString(chars[3]));
        colorTransform[12] = Float.parseFloat(Character.toString(chars[2]));
        colorTransform[13] = Float.parseFloat(Character.toString(chars[1]));
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        colorMatrix.set(colorTransform);
        return colorMatrix;
    }

    public void update(float deltaTime) {
        float nextCount = count + deltaTime * ROTATION_SPEED;
        count = (nextCount < matrixList.length) ? nextCount : nextCount % matrixList.length;
    }

    public ColorMatrix getMatrix() {
        return matrixList[(int) count];
    }
}
