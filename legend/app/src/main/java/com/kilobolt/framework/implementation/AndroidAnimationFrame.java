package com.kilobolt.framework.implementation;

import com.kilobolt.framework.Image;

public class AndroidAnimationFrame {
    public Image image;
    public float endTime;
    public int leftOffset;
    public int topOffset;
    public int width;
    public int height;

    public AndroidAnimationFrame(Image image, float endTime) {
        this.image = image;
        this.leftOffset = 0;
        this.topOffset = 0;
        this.width = -1;
        this.height = -1;
        this.endTime = endTime;
    }

    public AndroidAnimationFrame(Image image, int width, int height, float endTime) {
        this.image = image;
        this.leftOffset = 0;
        this.topOffset = 0;
        this.width = width;
        this.height = height;
        this.endTime = endTime;
    }

    public AndroidAnimationFrame(Image image, int leftOffset, int topOffset, int width, int height, float endTime) {
        this.image = image;
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
        this.width = width;
        this.height = height;
        this.endTime = endTime;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
