package com.kilobolt.framework.implementation;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.kilobolt.framework.ImageFormat;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.io.InputStream;

public class AndroidGraphics implements Graphics {
    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
    }

    @Override
    public Image newImage(String fileName, ImageFormat format) {
        Config config = null;
        if (format == ImageFormat.RGB565)
            config = Config.RGB_565;
        else if (format == ImageFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;

        Options options = new Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        if (bitmap.getConfig() == Config.RGB_565)
            format = ImageFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = ImageFormat.ARGB4444;
        else
            format = ImageFormat.ARGB8888;

        return new AndroidImage(bitmap, format);
    }

    @Override
    public Animation newAnimation() {
        return new AndroidAnimation();
    }

    @Override
    public void clearScreen(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
    }

    @Override
    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    @Override
    public void drawCircle(int x, int y, int radius, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public void drawEmptyCircle(int x, int y, int radius, int strokeWidth, int color) {
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public void drawEmptyRect(int x, int y, int width, int height, int strokeWidth, int color) {
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    @Override
    public void drawARGB(int a, int r, int g, int b) {
        paint.setStyle(Style.FILL);
        canvas.drawARGB(a, r, g, b);
    }

    @Override
    public void drawString(String text, int x, int y, Paint paint) {
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public void drawImage(Image image, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        canvas.drawBitmap(((AndroidImage) image).bitmap, srcRect, dstRect, null);
    }

    @Override
    public void drawImage(Image image, int x, int y) {
        canvas.drawBitmap(((AndroidImage) image).bitmap, x, y, null);
    }

    @Override
    public void drawAnimation(Animation animation, int x, int y) {
        AndroidAnimationFrame frame = ((AndroidAnimation) animation).getFrame();
        int left = x + frame.leftOffset;
        int top = y + frame.topOffset;
        if (frame.width <= 0 || frame.height <= 0) {
            canvas.drawBitmap(((AndroidImage) frame.image).bitmap, left, top, null);
        } else {
            canvas.drawBitmap(Bitmap.createScaledBitmap(((AndroidImage) frame.image).bitmap, frame.width, frame.height, true), left, top, null);
        }
    }

    @Override
    public void drawAnimation(Animation animation, int x, int y, ColorMatrix colorMatrix) {
        AndroidAnimationFrame frame = ((AndroidAnimation) animation).getFrame();
        int left = x + frame.leftOffset;
        int top = y + frame.topOffset;
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        if (frame.width <= 0 || frame.height <= 0) {
            canvas.drawBitmap(((AndroidImage) frame.image).bitmap, left, top, paint);
        } else {
            canvas.drawBitmap(Bitmap.createScaledBitmap(((AndroidImage) frame.image).bitmap, frame.width, frame.height, true), left, top, paint);
        }
    }

    @Override
    public void drawScaledImage(Image image, int x, int y, int width, int height) {
        Bitmap bitmap = Bitmap.createScaledBitmap(((AndroidImage) image).bitmap, width, height, true);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    @Override
    public void drawScaledImage(Image image, int x, int y, float coef) {
        int width = Math.round(image.getWidth() * coef);
        int height = Math.round(image.getHeight() * coef);
        Bitmap bitmap = Bitmap.createScaledBitmap(((AndroidImage) image).bitmap, width, height, true);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    @Override
    public void drawScaledImage(Image image, int x, int y, float coef, ColorMatrix colorMatrix) {
        int width = Math.round(image.getWidth() * coef);
        int height = Math.round(image.getHeight() * coef);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Bitmap bitmap = Bitmap.createScaledBitmap(((AndroidImage) image).bitmap, width, height, true);
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void drawScaledImage(Image image, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + width;
        dstRect.bottom = y + height;

        canvas.drawBitmap(((AndroidImage) image).bitmap, srcRect, dstRect, null);

    }

    @Override
    public int getWidth() {
        return frameBuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return frameBuffer.getHeight();
    }
}
