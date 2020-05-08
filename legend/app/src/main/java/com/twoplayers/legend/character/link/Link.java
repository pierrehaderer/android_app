package com.twoplayers.legend.character.link;

import com.kilobolt.framework.Animation;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesLink;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.inventory.ItemService;
import com.twoplayers.legend.character.link.inventory.arrow.Arrow;
import com.twoplayers.legend.character.link.inventory.arrow.Bow;
import com.twoplayers.legend.character.link.inventory.Bracelet;
import com.twoplayers.legend.character.link.inventory.Compass;
import com.twoplayers.legend.character.link.inventory.DungeonMap;
import com.twoplayers.legend.character.link.inventory.Flute;
import com.twoplayers.legend.character.link.inventory.InfiniteKey;
import com.twoplayers.legend.character.link.inventory.Ladder;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.bomb.BombCloud;
import com.twoplayers.legend.character.link.inventory.boomerang.Boomerang;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.light.Light;
import com.twoplayers.legend.character.link.inventory.Meat;
import com.twoplayers.legend.character.link.inventory.Potion;
import com.twoplayers.legend.character.link.inventory.Raft;
import com.twoplayers.legend.character.link.inventory.Ring;
import com.twoplayers.legend.character.link.inventory.Scepter;
import com.twoplayers.legend.character.link.inventory.Shield;
import com.twoplayers.legend.character.link.inventory.SpellBook;
import com.twoplayers.legend.character.link.inventory.sword.Sword;
import com.twoplayers.legend.character.link.inventory.sword.SwordSplash;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class Link {

    public static final float SPEED = 1.5f;
    protected static final float PUSH_SPEED = 9f;
    protected static final float ENTER_DOOR_SPEED = 0.40f;
    public static final float REMOVE_RUPEES_SPEED = 0.3f;
    protected static final float INITIAL_PUSH_COUNT = 9f;
    protected static final float INITIAL_INVINCIBLE_COUNT = 100f;
    public static final float INITIAL_SHOW_COUNT = 150f;
    public static final float INITIAL_TIME_BEFORE_USE_LIGHT = 80f;
    public static final float CHANGE_ITEM_INITIAL_COUNT = 50f;

    public static final int PICK_ANIMATION_BIG = 0;
    public static final int PICK_ANIMATION_SMALL = 1;

    public Animation currentAnimation;
    protected Map<Orientation, Animation> moveAnimations;
    protected Map<Orientation, Animation> useAnimations;
    protected Animation[] pickAnimations;

    public float x;
    public float y;
    public Hitbox hitbox;
    public Orientation orientation;
    protected Coordinate underTheDoor;

    public boolean isPushed;
    protected float pushX;
    protected float pushY;
    protected float pushCounter;
    protected boolean isInvincible;
    protected float invicibleCounter;
    public boolean isEnteringADoor;
    protected float enterSomewhereDistance;
    public boolean isExitingADoor;
    protected boolean mustPlayExitSomewhereSound;
    protected float exitSomewhereDistance;
    public boolean isShowingItem;
    public float showItemCounter;
    public Item itemToShow;

    public float life;
    public float lifeMax;
    public int rupees;
    public float coinCounter;
    public int rupeesToRemove;
    public int keys;

    public Boomerang boomerang;
    public Bomb bomb;
    public BombCloud bombCloud;
    public int bombQuantity;
    public int bombMax;
    public Bow bow;
    public Arrow arrow;
    public Light light;
    public int lightCount;
    public float timeBeforeUseLight;
    public Fire fire1;
    public Fire fire2;
    public Flute flute;
    public Meat meat;
    public Potion potion;
    public Scepter scepter;

    public Bracelet bracelet;
    public Raft raft;
    public Ladder ladder;
    public InfiniteKey infiniteKey;
    public Ring ring;
    public SpellBook spellBook;

    public DungeonMap dungeonMap;
    public Compass compass;

    public Sword sword;
    public ThrowingSword throwingSword;
    public SwordSplash swordSplash;
    public Shield shield;
    public int secondItem;
    public float changeItemCount;
    public boolean isUsingItem;
    public int useItemStep;
    public boolean useItemStepHasChanged;
    public float useItemProgression;

    public Link(ImagesLink imagesLink, Graphics g) {
        initMoveAnimations(imagesLink, g);
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
     * Initialise the use animations
     */
    private void initUseAnimations(ImagesLink imagesLink, Graphics g) {
        useAnimations = new HashMap<>();
        Animation animationUp = g.newAnimation();
        animationUp.addFrame(imagesLink.get("link_use_up"), AllImages.COEF, ItemService.STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("link_up_2"), AllImages.COEF, ItemService.STEP_2_DURATION - ItemService.STEP_1_DURATION);
        animationUp.addFrame(imagesLink.get("link_up_1"), AllImages.COEF, ItemService.STEP_3_DURATION - ItemService.STEP_2_DURATION);
        animationUp.setOccurrences(1);
        useAnimations.put(Orientation.UP, animationUp);
        Animation animationDown = g.newAnimation();
        animationDown.addFrame(imagesLink.get("link_use_down"), AllImages.COEF, ItemService.STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("link_down_2"), AllImages.COEF, ItemService.STEP_2_DURATION - ItemService.STEP_1_DURATION);
        animationDown.addFrame(imagesLink.get("link_down_1"), AllImages.COEF, ItemService.STEP_3_DURATION - ItemService.STEP_2_DURATION);
        animationDown.setOccurrences(1);
        useAnimations.put(Orientation.DOWN, animationDown);
        Animation animationLeft = g.newAnimation();
        animationLeft.addFrame(imagesLink.get("link_use_left"), AllImages.COEF, ItemService.STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("link_left_2"), AllImages.COEF, ItemService.STEP_2_DURATION - ItemService.STEP_1_DURATION);
        animationLeft.addFrame(imagesLink.get("link_left_1"), AllImages.COEF, ItemService.STEP_3_DURATION - ItemService.STEP_2_DURATION);
        animationLeft.setOccurrences(1);
        useAnimations.put(Orientation.LEFT, animationLeft);
        Animation animationRight = g.newAnimation();
        animationRight.addFrame(imagesLink.get("link_use_right"), AllImages.COEF, ItemService.STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("link_right_2"), AllImages.COEF, ItemService.STEP_2_DURATION - ItemService.STEP_1_DURATION);
        animationRight.addFrame(imagesLink.get("link_right_1"), AllImages.COEF, ItemService.STEP_3_DURATION - ItemService.STEP_2_DURATION);
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

    /**
     * Switch Link animation to useAnimation
     */
    public void startToUseItem() {
        isUsingItem = true;
        useItemProgression = 0;
        useItemStep = 0;
        currentAnimation = useAnimations.get(orientation);
        currentAnimation.reset();
    }

    /**
     * Switch Link animation to pickAnimation
     */
    public void switchToPickAnimation(int pickAnimation) {
        currentAnimation = pickAnimations[pickAnimation];
        currentAnimation.reset();
    }

    /**
     * Switch Link animation to moveAnimation
     */
    public void switchToMoveAnimation(Orientation orientation) {
        currentAnimation = moveAnimations.get(orientation);
        currentAnimation.reset();
    }

    public float getLife() {
        return life;
    }

    public float getLifeMax() {
        return lifeMax;
    }

    public int getRupees() {
        return rupees;
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

    public int getBombQuantity() {
        return bombQuantity;
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
