package com.kilobolt.framework;

import android.graphics.ColorMatrix;
import android.graphics.Paint;

public interface Graphics {

    public Image newImage(String fileName, ImageFormat format);
    public Animation newAnimation();
    public void clearScreen(int color);
    public void drawLine(int x, int y, int x2, int y2, int color);
    public void drawRect(int x, int y, int width, int height, int color);
    public void drawCircle(int x, int y, int radius, int color);
    public void drawEmptyCircle(int x, int y, int radius, int strokeWidth, int color);
    public void drawEmptyRect(int x, int y, int width, int height, int strokeWidth, int color);
    public void drawImage(Image image, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight);
    public void drawImage(Image Image, int x, int y);
    public void drawAnimation(Animation animation, int x, int y);
    public void drawAnimation(Animation animation, int x, int y, ColorMatrix colorMatrix);
    public void drawScaledImage(Image Image, int x, int y, int width, int height);
    public void drawScaledImage(Image image, int x, int y, float coef);
    public void drawScaledImage(Image image, int x, int y, float coef, ColorMatrix colorMatrix);
    public void drawString(String text, int x, int y, Paint paint);
    public int getWidth();
    public int getHeight();
    public void drawARGB(int i, int j, int k, int l);

}
