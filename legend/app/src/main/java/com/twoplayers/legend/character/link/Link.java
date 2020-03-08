package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.Arrow;
import com.twoplayers.legend.character.link.inventory.Boomerang;
import com.twoplayers.legend.character.link.inventory.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.Scepter;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Link {

    public static final float LINK_SPEED = 1.5f;
    public static final float PUSH_SPEED = 9f;
    public static final float LINK_SPEED_ENTERING_CAVE = 0.40f;
    public static final float INITIAL_PUSH_COUNT = 9f;
    public static final float INITIAL_INVINCIBLE_COUNT = 100f;
    public static final float INITIAL_CAVE_COUNT = 80f;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> moveAnimations;
    protected Map<Orientation, Animation> attackAnimations;

    public float x;
    public float y;
    protected Hitbox hitbox;
    public Orientation orientation;

    public boolean isAttacking;
    public float attackProgression;
    protected boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;
    protected boolean isInvincible;
    protected float invicibleCounter;
    protected boolean isEnteringSomewhere;
    protected float enterSomewhereCounter;

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
        initMoveAnimations(imagesLink, g);
        initAttackAnimations(imagesLink, g);
        hitbox = new Hitbox(0, 0, 3, 3, 10, 10);
    }

    /**
     * Initialise the move animations
     */
    private void initMoveAnimations(ImagesLink imagesLink, Graphics g) {
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
    private void initAttackAnimations(ImagesLink imagesLink, Graphics g) {
        attackAnimations = new HashMap<>();

        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_sword_up_1"), AllImages.COEF, Sword.STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_2"), AllImages.COEF, Sword.STEP_2_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_3"), AllImages.COEF, Sword.STEP_3_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_4"), AllImages.COEF, Sword.STEP_4_DURATION);
        animationUp.setOccurrences(1);
        attackAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_sword_down_1"), AllImages.COEF, Sword.STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_2"), AllImages.COEF, Sword.STEP_2_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_3"), AllImages.COEF, Sword.STEP_3_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_4"), AllImages.COEF, Sword.STEP_4_DURATION);
        animationDown.setOccurrences(1);
        attackAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_sword_left_1"), AllImages.COEF, Sword.STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_2"), AllImages.COEF, Sword.STEP_2_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_3"), AllImages.COEF, Sword.STEP_3_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_4"), AllImages.COEF, Sword.STEP_4_DURATION);
        animationLeft.setOccurrences(1);
        attackAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_sword_right_1"), AllImages.COEF, Sword.STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_2"), AllImages.COEF, Sword.STEP_2_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_3"), AllImages.COEF, Sword.STEP_3_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_4"), AllImages.COEF, Sword.STEP_4_DURATION);
        animationRight.setOccurrences(1);
        attackAnimations.put(Orientation.RIGHT, animationRight);
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
