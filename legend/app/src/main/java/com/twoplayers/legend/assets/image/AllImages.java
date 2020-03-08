package com.twoplayers.legend.assets.image;

public class AllImages {

    /** 224 is the size of the nintendo resolution height, 480 is the size of this game resolution height */
    public static final float COEF = 480f / 224f;

    private ImageOther imageOther;
    private ImagesWorldMap imagesWorldMap;
    private ImagesCave imagesCave;
    private ImagesEnemyWorldMap imagesEnemyWorldMap;
    private ImagesLink imagesLink;
    private ImagesItem imagesItem;
    private ImagesGui imagesGui;

    /**
     * Constructor
     */
    public AllImages() {
        imageOther = new ImageOther();
        imagesWorldMap = new ImagesWorldMap();
        imagesCave = new ImagesCave();
        imagesEnemyWorldMap = new ImagesEnemyWorldMap();
        imagesLink = new ImagesLink();
        imagesItem = new ImagesItem();
        imagesGui = new ImagesGui();
    }

    public ImageOther getImageOther() {
        return imageOther;
    }

    public ImagesWorldMap getImagesWorldMap() {
        return imagesWorldMap;
    }

    public ImagesCave getImagesCave() {
        return imagesCave;
    }

    public ImagesEnemyWorldMap getImagesEnemyWorldMap() {
        return imagesEnemyWorldMap;
    }

    public ImagesLink getImagesLink() {
        return imagesLink;
    }

    public ImagesItem getImagesItem() {
        return imagesItem;
    }

    public ImagesGui getImagesGui() {
        return imagesGui;
    }
}
