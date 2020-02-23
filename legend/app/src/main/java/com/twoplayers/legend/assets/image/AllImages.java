package com.twoplayers.legend.assets.image;

public class AllImages {

    /** 224 is the size of the nintendo resolution height, 480 is the size of this game resolution height */
    public static final float COEF = 480f / 224f;

    private ImageOthers imageOthers;
    private ImageWorldMaps imageWorldMaps;
    private ImageLink imageLink;
    private ImageGui imageGui;

    /**
     * Constructor
     */
    public AllImages() {
        imageOthers = new ImageOthers();
        imageWorldMaps = new ImageWorldMaps();
        imageLink = new ImageLink();
        imageGui = new ImageGui();
    }

    public ImageOthers getImageOthers() {
        return imageOthers;
    }

    public ImageWorldMaps getImageWorldMaps() {
        return imageWorldMaps;
    }

    public ImageLink getImageLink() {
        return imageLink;
    }

    public ImageGui getImageGui() {
        return imageGui;
    }
}
