package com.twoplayers.legend.assets.image;

public class AllImages {

    /** 224 is the size of the nintendo resolution height, 480 is the size of this game resolution height */
    public static final float COEF = 480f / 224f;

    private ImageOther imageOther;
    private ImagesWorldMap imagesWorldMap;
    private ImagesCave imagesCave;
    private ImagesDungeon imagesDungeon;
    private ImagesEnemyWorldMap imagesEnemyWorldMap;
    private ImagesEnemyDungeon imagesEnemyDungeon;
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
        imagesDungeon = new ImagesDungeon();
        imagesEnemyWorldMap = new ImagesEnemyWorldMap();
        imagesEnemyDungeon = new ImagesEnemyDungeon();
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

    public ImagesDungeon getImagesDungeon() {
        return imagesDungeon;
    }

    public ImagesEnemyWorldMap getImagesEnemyWorldMap() {
        return imagesEnemyWorldMap;
    }

    public ImagesEnemyDungeon getImagesEnemyDungeon() {
        return imagesEnemyDungeon;
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
