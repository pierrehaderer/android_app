package com.twoplayers.legend.character;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageLink;

import java.util.HashMap;
import java.util.Map;

public class Link {

    private static final float LINK_SPEED = 1.3f;

    private ImageLink imageLink;

    public float x;
    public float y;
    public float speed = LINK_SPEED;
    public Orientation orientation;

    private Map<Orientation, Animation> moveAnimations;

    public Link(ImageLink imageLink, Graphics g) {
        this.imageLink = imageLink;
        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imageLink.get("link_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imageLink.get("link_up_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imageLink.get("link_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imageLink.get("link_down_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imageLink.get("link_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imageLink.get("link_left_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imageLink.get("link_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imageLink.get("link_right_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }

    public void paint(float deltaTime, Graphics g) {
        g.drawAnimation(moveAnimations.get(orientation), Math.round(x), Math.round(y));
    }

    public Map<Orientation, Animation> getMoveAnimations() {
        return moveAnimations;
    }
}
