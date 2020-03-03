package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.object.Arrow;
import com.twoplayers.legend.character.object.Boomerang;
import com.twoplayers.legend.character.object.Bow;
import com.twoplayers.legend.character.object.Bracelet;
import com.twoplayers.legend.character.object.Compass;
import com.twoplayers.legend.character.object.DungeonMap;
import com.twoplayers.legend.character.object.Flute;
import com.twoplayers.legend.character.object.InfiniteKey;
import com.twoplayers.legend.character.object.Ladder;
import com.twoplayers.legend.character.object.Light;
import com.twoplayers.legend.character.object.Meat;
import com.twoplayers.legend.character.object.Potion;
import com.twoplayers.legend.character.object.Raft;
import com.twoplayers.legend.character.object.Ring;
import com.twoplayers.legend.character.object.Scepter;
import com.twoplayers.legend.character.object.SpellBook;
import com.twoplayers.legend.character.object.Sword;
import com.twoplayers.legend.map.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Link {

    public static final float LINK_SPEED = 1.3f;
    public static final float PUSH_SPEED = 9f;
    public static final float INITIAL_PUSH_COUNT = 9f;
    public static final float INITIAL_INVINCIBLE_COUNT = 150f;

    private ImagesLink imagesLink;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> moveAnimations;
    protected Map<Sword, Map<Orientation, Animation>> attackAnimations;

    public float x;
    public float y;
    protected Hitbox hitbox;
    public Orientation orientation;

    public boolean isAttacking;
    protected boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;
    protected boolean isInvincible;
    protected float invicibleCounter;

    protected float life;
    protected float lifeMax;
    protected Arrow arrow;
    protected int bomb;
    protected Boomerang boomerang;
    protected Bow bow;
    protected Bracelet bracelet;
    protected Compass compass;
    protected DungeonMap dungeonMap;
    protected Flute flute;
    protected InfiniteKey infiniteKey;
    protected Ladder ladder;
    protected Light light;
    protected Meat meat;
    protected Potion potion;
    protected Raft raft;
    protected Ring ring;
    protected Scepter scepter;
    protected SpellBook spellBook;
    protected Sword sword;

    public Link(ImagesLink imagesLink, Graphics g) {
        this.imagesLink = imagesLink;
        initMoveAnimation(g);
        initAttackAnimation(g);
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
    }

    /**
     * Initialise the move animations
     */
    private void initMoveAnimation(Graphics g) {
        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_up_1"), AllImages.COEF, 15);
        animationUp.addFrame(imagesLink.get("link_up_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_down_1"), AllImages.COEF, 15);
        animationDown.addFrame(imagesLink.get("link_down_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_left_1"), AllImages.COEF, 15);
        animationLeft.addFrame(imagesLink.get("link_left_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_right_1"), AllImages.COEF, 15);
        animationRight.addFrame(imagesLink.get("link_right_2"), AllImages.COEF, 15);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }

    /**
     * Initialise the attack animations
     */
    private void initAttackAnimation(Graphics g) {
        attackAnimations = new HashMap<>();

        attackAnimations.put(Sword.NONE, new HashMap<Orientation, Animation>());
        Animation emptyAnimation = g.newAnimation();
        emptyAnimation.setOccurrences(1);
        attackAnimations.get(Sword.NONE).put(Orientation.UP, emptyAnimation);
        attackAnimations.get(Sword.NONE).put(Orientation.DOWN, emptyAnimation);
        attackAnimations.get(Sword.NONE).put(Orientation.LEFT, emptyAnimation);
        attackAnimations.get(Sword.NONE).put(Orientation.RIGHT, emptyAnimation);

        attackAnimations.put(Sword.WOOD, new HashMap<Orientation, Animation>());
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_wood_sword_up_1"), AllImages.COEF, 8);
        animationUp.addFrame(imagesLink.get("link_wood_sword_up_2"), 0, Math.round(-12 * AllImages.COEF), AllImages.COEF, 25);
        animationUp.addFrame(imagesLink.get("link_wood_sword_up_3"), 0, Math.round(-11 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.addFrame(imagesLink.get("link_wood_sword_up_4"), 0, Math.round(-3 * AllImages.COEF), AllImages.COEF, 4);
        animationUp.setOccurrences(1);
        attackAnimations.get(Sword.WOOD).put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_wood_sword_down_1"), AllImages.COEF, 8);
        animationDown.addFrame(imagesLink.get("link_wood_sword_down_2"), AllImages.COEF, 25);
        animationDown.addFrame(imagesLink.get("link_wood_sword_down_3"), AllImages.COEF, 4);
        animationDown.addFrame(imagesLink.get("link_wood_sword_down_4"), AllImages.COEF, 4);
        animationDown.setOccurrences(1);
        attackAnimations.get(Sword.WOOD).put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_wood_sword_left_1"), AllImages.COEF, 8);
        animationLeft.addFrame(imagesLink.get("link_wood_sword_left_2"), Math.round(-11 * AllImages.COEF), 0, AllImages.COEF, 25);
        animationLeft.addFrame(imagesLink.get("link_wood_sword_left_3"), Math.round(-7 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.addFrame(imagesLink.get("link_wood_sword_left_4"), Math.round(-3 * AllImages.COEF), 0, AllImages.COEF, 4);
        animationLeft.setOccurrences(1);
        attackAnimations.get(Sword.WOOD).put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_wood_sword_right_1"), AllImages.COEF, 8);
        animationRight.addFrame(imagesLink.get("link_wood_sword_right_2"), AllImages.COEF, 25);
        animationRight.addFrame(imagesLink.get("link_wood_sword_right_3"), AllImages.COEF, 4);
        animationRight.addFrame(imagesLink.get("link_wood_sword_right_4"), AllImages.COEF, 4);
        animationRight.setOccurrences(1);
        attackAnimations.get(Sword.WOOD).put(Orientation.RIGHT, animationRight);
    }

    public float getLife() {
        return life;
    }

    public float getLifeMax() {
        return lifeMax;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public int getBomb() {
        return bomb;
    }

    public Boomerang getBoomerang() {
        return boomerang;
    }

    public Bow getBow() {
        return bow;
    }

    public Bracelet getBracelet() {
        return bracelet;
    }

    public Compass getCompass() {
        return compass;
    }

    public DungeonMap getDungeonMap() {
        return dungeonMap;
    }

    public Flute getFlute() {
        return flute;
    }

    public InfiniteKey getInfiniteKey() {
        return infiniteKey;
    }

    public Ladder getLadder() {
        return ladder;
    }

    public Light getLight() {
        return light;
    }

    public Meat getMeat() {
        return meat;
    }

    public Potion getPotion() {
        return potion;
    }

    public Raft getRaft() {
        return raft;
    }

    public Ring getRing() {
        return ring;
    }

    public Scepter getScepter() {
        return scepter;
    }

    public SpellBook getSpellBook() {
        return spellBook;
    }

    public Sword getSword() {
        return sword;
    }
}
