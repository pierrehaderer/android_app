package com.kilobolt.framework;

public interface Animation {

    public void reset();
    public void setOccurrences(int occurences);
    public void addFrame(Image image, float duration);
    public void addFrame(Image image, float coef, float duration);
    public void addFrame(Image image, int leftOffset, int topOffset, float coef, float duration);
    public void addFrame(Image image, int width, int height, float duration);
    public void addFrame(Image image, int leftOffset, int topOffset, int width, int height, float duration);
    public void replaceImage(int frameIndex, Image image);
    public void update(float deltaTime);
    public boolean isOver();
}
