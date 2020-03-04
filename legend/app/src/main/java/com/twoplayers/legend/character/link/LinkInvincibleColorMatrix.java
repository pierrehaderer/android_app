package com.twoplayers.legend.character.link;

import android.graphics.ColorMatrix;

public class LinkInvincibleColorMatrix {

    private static final float ROTATION_SPEED = 0.15f;
    private ColorMatrix[] matrixList;

    private float count;

    public LinkInvincibleColorMatrix() {
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
        count += deltaTime * ROTATION_SPEED;
    }

    public ColorMatrix getCurrentColorMatrix() {
        return matrixList[(int) count % matrixList.length];
    }
}