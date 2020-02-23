package com.kilobolt.framework.implementation;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Image;

import java.util.ArrayList;

public class AndroidAnimation implements Animation {

    private int maxOccurrences;

    private ArrayList<AndroidAnimationFrame> frames;
    private float totalDuration;

    private float animationTime;
    private int currentOccurrence;
    private int currentFrame;
    private boolean isAnimationOver;

    public AndroidAnimation() {
        maxOccurrences = 0;
        frames = new ArrayList<>();
        totalDuration = 0;
        animationTime = 0;
        currentOccurrence = 1;
        currentFrame = 0;
        isAnimationOver = false;
    }

    @Override
    public void reset() {
        synchronized (this) {
            animationTime = 0;
            currentFrame = 0;
            currentOccurrence = 1;
            isAnimationOver = false;
        }
    }

    @Override
    public void setOccurrences(int occurrences) {
        maxOccurrences = occurrences;
    }

    @Override
    public synchronized void addFrame(Image image, float duration) {
        totalDuration += duration;
        frames.add(new AndroidAnimationFrame(image, totalDuration));
    }

    @Override
    public synchronized void addFrame(Image image, float coef, float duration) {
        totalDuration += duration;
        int width = Math.round(image.getWidth() * coef);
        int height = Math.round(image.getHeight() * coef);
        frames.add(new AndroidAnimationFrame(image, width, height, totalDuration));
    }

    @Override
    public synchronized void addFrame(Image image, int leftOffset, int topOffset, float coef, float duration) {
        totalDuration += duration;
        int width = Math.round(image.getWidth() * coef);
        int height = Math.round(image.getHeight() * coef);
        frames.add(new AndroidAnimationFrame(image, leftOffset, topOffset, width, height, totalDuration));
    }

    @Override
    public synchronized void addFrame(Image image, int width, int height, float duration) {
        totalDuration += duration;
        frames.add(new AndroidAnimationFrame(image, width, height, totalDuration));
    }

    @Override
    public synchronized void addFrame(Image image, int leftOffset, int topOffset, int width, int height, float duration) {
        totalDuration += duration;
        frames.add(new AndroidAnimationFrame(image, leftOffset, topOffset, width, height, totalDuration));
    }

    @Override
    public synchronized void replaceImage(int frameIndex, Image image) {
        frames.get(frameIndex).setImage(image);
    }

    @Override
    public synchronized void update(float deltaTime) {
        if (frames.isEmpty()) {
            return;
        }

        animationTime += deltaTime;

        // Handle the end of the occurrence of an animation
        if (animationTime >= totalDuration) {
            if (maxOccurrences == 0 || currentOccurrence < maxOccurrences) {
                currentOccurrence++;
                animationTime = animationTime % totalDuration;
                currentFrame = 0;
            } else {
                isAnimationOver = true;
            }
        }

        // Increment currentFrame until the frame corresponding to the animationTime is found
        while (!isAnimationOver && animationTime > frames.get(currentFrame).endTime) {
            currentFrame++;
        }
    }

    @Override
    public boolean isAnimationOver() {
        return isAnimationOver;
    }

    public synchronized AndroidAnimationFrame getFrame() {
        if (frames.size() == 0) {
            return null;
        } else {
            return frames.get(currentFrame);
        }
    }
}
