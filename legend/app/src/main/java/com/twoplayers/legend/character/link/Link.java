package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.inventory.Arrow;
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
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.Orientation;

import java.util.HashMap;
import java.util.Map;

public class Link {

    protected static final float LINK_SPEED = 1.5f;
    protected static final float PUSH_SPEED = 9f;
    protected static final float ENTER_CAVE_SPEED = 0.40f;
    protected static final float REMOVE_COINS_SPEED = 0.3f;
    protected static final float INITIAL_PUSH_COUNT = 9f;
    protected static final float INITIAL_INVINCIBLE_COUNT = 100f;
    protected static final float INITIAL_ENTER_COUNT = 75f;
    protected static final float INITIAL_SHOW_COUNT = 150f;
    protected static final float INITIAL_TIME_BEFORE_USE_LIGHT = 80f;

    public static final float STEP_1_DURATION = 8;
    public static final float STEP_2_ATTACK_DURATION = 25;
    public static final float STEP_2_USE_DURATION = 12;
    public static final float STEP_3_DURATION = 3;
    public static final float STEP_4_DURATION = 3;

    public static final int PICK_ANIMATION_BIG = 0;
    public static final int PICK_ANIMATION_SMALL = 1;

    protected Animation currentAnimation;
    protected Map<Orientation, Animation> moveAnimations;
    protected Map<Orientation, Animation> attackAnimations;
    protected Map<Orientation, Animation> useAnimations;
    protected Animation[] pickAnimations;

    public float x;
    public float y;
    protected Hitbox hitbox;
    public Orientation orientation;

    protected boolean isAttacking;
    protected float attackProgression;
    protected boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;
    protected boolean isInvincible;
    protected float invicibleCounter;
    protected boolean isEnteringSomewhere;
    protected float enterSomewhereCounter;
    protected boolean isExitingSomewhere;
    protected float exitSomewhereCounter;
    protected boolean isShowingItem;
    protected float showItemCounter;
    protected Item itemToShow;

    protected float life;
    protected float lifeMax;
    protected int coins;
    protected float coinCounter;
    protected int coinsToRemove;
    protected int keys;

    protected Boomerang boomerang;
    protected int bomb;
    protected int bombMax;
    protected Bow bow;
    protected Arrow arrow;
    protected Light light;
    protected int lightCount;
    protected float timeBeforeUseLight;
    protected Fire fire1;
    protected Fire fire2;
    protected Flute flute;
    protected Meat meat;
    protected Potion potion;
    protected Scepter scepter;

    protected Bracelet bracelet;
    protected Raft raft;
    protected Ladder ladder;
    protected InfiniteKey infiniteKey;
    protected Ring ring;
    protected SpellBook spellBook;

    protected DungeonMap dungeonMap;
    protected Compass compass;

    protected Sword sword;
    protected Shield shield;
    protected int secondItem;
    public boolean isUsingSecondItem;

    public Link(ImagesLink imagesLink, Graphics g) {
        initMoveAnimations(imagesLink, g);
        initAttackAnimations(imagesLink, g);
        initUseAnimations(imagesLink, g);
        initPickAnimations(imagesLink, g);
        hitbox = new Hitbox(0, 0, 3, 3, 11, 12);
    }

    /**
     * Initialise the move animations
     */
    private void initMoveAnimations(ImagesLink imagesLink, Graphics g) {
        moveAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_up_1"), AllImages.COEF, 10);
        animationUp.addFrame(imagesLink.get("link_up_2"), AllImages.COEF, 10);
        moveAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_down_1"), AllImages.COEF, 10);
        animationDown.addFrame(imagesLink.get("link_down_2"), AllImages.COEF, 10);
        moveAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_left_1"), AllImages.COEF, 10);
        animationLeft.addFrame(imagesLink.get("link_left_2"), AllImages.COEF, 10);
        moveAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_right_1"), AllImages.COEF, 10);
        animationRight.addFrame(imagesLink.get("link_right_2"), AllImages.COEF, 10);
        moveAnimations.put(Orientation.RIGHT, animationRight);
    }

    /**
     * Initialise the attack animations
     */
    private void initAttackAnimations(ImagesLink imagesLink, Graphics g) {
        attackAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_sword_up_1"), AllImages.COEF, STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_2"), AllImages.COEF, STEP_2_ATTACK_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_3"), AllImages.COEF, STEP_3_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_4"), AllImages.COEF, STEP_4_DURATION);
        animationUp.setOccurrences(1);
        attackAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_sword_down_1"), AllImages.COEF, STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_2"), AllImages.COEF, STEP_2_ATTACK_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_3"), AllImages.COEF, STEP_3_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_4"), AllImages.COEF, STEP_4_DURATION);
        animationDown.setOccurrences(1);
        attackAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_sword_left_1"), AllImages.COEF, STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_2"), AllImages.COEF, STEP_2_ATTACK_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_3"), AllImages.COEF, STEP_3_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_4"), AllImages.COEF, STEP_4_DURATION);
        animationLeft.setOccurrences(1);
        attackAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_sword_right_1"), AllImages.COEF, STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_2"), AllImages.COEF, STEP_2_ATTACK_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_3"), AllImages.COEF, STEP_3_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_4"), AllImages.COEF, STEP_4_DURATION);
        animationRight.setOccurrences(1);
        attackAnimations.put(Orientation.RIGHT, animationRight);
    }

    /**
     * Initialise the use animations
     */
    private void initUseAnimations(ImagesLink imagesLink, Graphics g) {
        useAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_sword_up_1"), AllImages.COEF, STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_2"), AllImages.COEF, STEP_2_USE_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_3"), AllImages.COEF, STEP_3_DURATION);
        animationUp.addFrame(imagesLink.get("link_sword_up_4"), AllImages.COEF, STEP_4_DURATION);
        animationUp.setOccurrences(1);
        useAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_sword_down_1"), AllImages.COEF, STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_2"), AllImages.COEF, STEP_2_USE_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_3"), AllImages.COEF, STEP_3_DURATION);
        animationDown.addFrame(imagesLink.get("link_sword_down_4"), AllImages.COEF, STEP_4_DURATION);
        animationDown.setOccurrences(1);
        useAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_sword_left_1"), AllImages.COEF, STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_2"), AllImages.COEF, STEP_2_USE_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_3"), AllImages.COEF, STEP_3_DURATION);
        animationLeft.addFrame(imagesLink.get("link_sword_left_4"), AllImages.COEF, STEP_4_DURATION);
        animationLeft.setOccurrences(1);
        useAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_sword_right_1"), AllImages.COEF, STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_2"), AllImages.COEF, STEP_2_USE_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_3"), AllImages.COEF, STEP_3_DURATION);
        animationRight.addFrame(imagesLink.get("link_sword_right_4"), AllImages.COEF, STEP_4_DURATION);
        animationRight.setOccurrences(1);
        useAnimations.put(Orientation.RIGHT, animationRight);
    }

    /**
     * Initialise the pick animations
     */
    private void initPickAnimations(ImagesLink imagesLink, Graphics g) {
        pickAnimations = new Animation[2];
        Animation animationSmall = g.newAnimation();
        animationSmall.addFrame(imagesLink.get("link_pick_item_big"), AllImages.COEF, 100);
        pickAnimations[PICK_ANIMATION_BIG] = animationSmall;
        Animation animationBig = g.newAnimation();
        animationBig.addFrame(imagesLink.get("link_pick_item_small"), AllImages.COEF, 100);
        pickAnimations[PICK_ANIMATION_SMALL] = animationBig;
    }

    public float getLife() {
        return life;
    }

    public float getLifeMax() {
        return lifeMax;
    }

    public int getCoins() {
        return coins;
    }

    public int getKeys() {
        return keys;
    }

    public int getSecondItem() {
        return secondItem;
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
