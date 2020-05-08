package com.twoplayers.legend.character.link.inventory.rod;

import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.util.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Rod {

    public static final int DAMAGE_TO_ENEMY = 1;
    public float x;
    public float y;
    public Orientation orientation;

    protected Map<Orientation, Hitbox> hitboxes;

    public RodType type;
    public boolean isActive;

    public Image image;
    public Image emptyImage;
    protected Map<Orientation, Image> images;
    protected Map<Orientation, float[]> positionDeltaX;
    protected Map<Orientation, float[]> positionDeltaY;

    /**
     * Constructor
     */
    public Rod(ImagesLink imagesLink, Graphics g) {
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
        images.put(Orientation.UP, imagesLink.get("rod_up"));
        images.put(Orientation.DOWN, imagesLink.get("rod_down"));
        images.put(Orientation.LEFT, imagesLink.get("rod_left"));
        images.put(Orientation.RIGHT, imagesLink.get("rod_right"));
    }

    public RodType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Hitbox getHitbox() {
        return hitboxes.get(orientation);
    }
}
