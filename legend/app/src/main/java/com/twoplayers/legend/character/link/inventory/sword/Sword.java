package com.twoplayers.legend.character.link.inventory.sword;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Sword {

    public float x;
    public float y;
    public Orientation orientation;

    protected Map<Orientation, Hitbox> hitboxes;

    public SwordType type;
    public boolean isActive;

    public Image image;
    public Image emptyImage;
    protected Map<SwordType, Map<Orientation, Image>> images;
    protected Map<Orientation, float[]> positionDeltaX;
    protected Map<Orientation, float[]> positionDeltaY;

    /**
     * Constructor
     */
    public Sword(ImagesLink imagesLink, Graphics g) {
        initImages(imagesLink, g);
        isActive = false;
        orientation = Orientation.UP;
        hitboxes = new HashMap<>();
        hitboxes.put(Orientation.UP, new Hitbox(0, 0, 1, -11, 14, 18));
        hitboxes.put(Orientation.DOWN, new Hitbox(0, 0, 1, 9, 14, 18));
        hitboxes.put(Orientation.LEFT, new Hitbox(0, 0, -12, 1, 19, 14));
        hitboxes.put(Orientation.RIGHT, new Hitbox(0, 0, 10, 1, 19, 14));
        positionDeltaX = new HashMap<>();
        positionDeltaX.put(Orientation.UP, new float[]{0,3 * AllImages.COEF,3 * AllImages.COEF,3 * AllImages.COEF,0});
        positionDeltaX.put(Orientation.DOWN, new float[]{0,5 * AllImages.COEF,5 * AllImages.COEF,5 * AllImages.COEF,0});
        positionDeltaX.put(Orientation.LEFT, new float[]{0,-11 * AllImages.COEF,-7 * AllImages.COEF,-3 * AllImages.COEF,0});
        positionDeltaX.put(Orientation.RIGHT, new float[]{0,11 * AllImages.COEF,7 * AllImages.COEF,3 * AllImages.COEF,0});
        positionDeltaY = new HashMap<>();
        positionDeltaY.put(Orientation.UP, new float[]{0,-12 * AllImages.COEF,-11 * AllImages.COEF,-3 * AllImages.COEF,0});
        positionDeltaY.put(Orientation.DOWN, new float[]{0,11 * AllImages.COEF,7 * AllImages.COEF,3 * AllImages.COEF,0});
        positionDeltaY.put(Orientation.LEFT, new float[]{0,5 * AllImages.COEF,5 * AllImages.COEF,5 * AllImages.COEF,0});
        positionDeltaY.put(Orientation.RIGHT, new float[]{0,6 * AllImages.COEF,6 * AllImages.COEF,6 * AllImages.COEF,0});
    }

    /**
     * Initialise the attack animations
     */
    private void initImages(ImagesLink imagesLink, Graphics g) {
        emptyImage = imagesLink.get("empty");
        image = emptyImage;

        images = new HashMap<>();

        HashMap<Orientation, Image> noneImages = new HashMap<>();
        noneImages.put(Orientation.UP, imagesLink.get("empty"));
        noneImages.put(Orientation.DOWN, imagesLink.get("empty"));
        noneImages.put(Orientation.LEFT, imagesLink.get("empty"));
        noneImages.put(Orientation.RIGHT, imagesLink.get("empty"));
        images.put(SwordType.NONE, noneImages);

        HashMap<Orientation, Image> woodImages = new HashMap<>();
        woodImages.put(Orientation.UP, imagesLink.get("wood_sword_up"));
        woodImages.put(Orientation.DOWN, imagesLink.get("wood_sword_down"));
        woodImages.put(Orientation.LEFT, imagesLink.get("wood_sword_left"));
        woodImages.put(Orientation.RIGHT, imagesLink.get("wood_sword_right"));
        images.put(SwordType.WOOD, woodImages);

        HashMap<Orientation, Image> whiteImages = new HashMap<>();
        whiteImages.put(Orientation.UP, imagesLink.get("white_sword_up"));
        whiteImages.put(Orientation.DOWN, imagesLink.get("white_sword_down"));
        whiteImages.put(Orientation.LEFT, imagesLink.get("white_sword_left"));
        whiteImages.put(Orientation.RIGHT, imagesLink.get("white_sword_right"));
        images.put(SwordType.WHITE, whiteImages);

        HashMap<Orientation, Image> magicalImages = new HashMap<>();
        magicalImages.put(Orientation.UP, imagesLink.get("magical_sword_up"));
        magicalImages.put(Orientation.DOWN, imagesLink.get("magical_sword_down"));
        magicalImages.put(Orientation.LEFT, imagesLink.get("magical_sword_left"));
        magicalImages.put(Orientation.RIGHT, imagesLink.get("magical_sword_right"));
        images.put(SwordType.MAGICAL, magicalImages);
    }

    public SwordType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitboxes.get(orientation);
    }

    public Image getImage() {
        return images.get(type).get(orientation);
    }
}
