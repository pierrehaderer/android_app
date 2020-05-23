package com.twoplayers.legend.gui;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.ImagesGui;

public class GuiElements {

    protected float time;
    protected Animation triforceAnimation;

    public GuiElements(ImagesGui imagesGui, Graphics g) {
        time = 0;
        triforceAnimation = g.newAnimation();
        triforceAnimation.addFrame(imagesGui.get("triforce_1"), 20);
        triforceAnimation.addFrame(imagesGui.get("triforce_2"), 20);
    }
}
