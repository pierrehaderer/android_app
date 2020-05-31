package com.twoplayers.legend.basement;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesDungeon;
import com.twoplayers.legend.assets.image.ImagesItem;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.enemy.dungeon.BasementEnemyManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.ItemService;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.dungeon.BasementInfo;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.ColorMatrixZone;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.Orientation;

import java.util.List;

public class BasementManager implements IZoneManager {

    private static final float INITIAL_STUN_COUNTER = 30f;
    private static final float TRANSITION_SPEED = 4.0f;
    private static final float TEXT_SPEED = 0.12f;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private BasementEnemyManager basementEnemyManager;
    private SaveManager saveManager;
    private ItemService itemService;

    private ImagesDungeon imagesDungeon;
    private ImagesItem imagesItem;
    private SoundEffectManager soundEffectManager;

    private BasementRoom basementRoom;
    private Basement basement;

    private boolean shouldInitialize = true;
    private float stunCounter;
    private boolean hasExitedZone;

    private ColorMatrixZone colorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, BasementInfo basementInfo) {
        if (shouldInitialize) {
            shouldInitialize = false;
            init(game);
        }
        stunCounter = INITIAL_STUN_COUNTER;

        initBasementRoom((MainActivity) game);
        initBasement(basementInfo);
        basementEnemyManager.spawnEnemies();

        hasExitedZone = false;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        linkManager = ((MainActivity) game).getLinkManager();
        basementEnemyManager = ((MainActivity) game).getBasementEnemyManager();
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();
        saveManager = ((MainActivity) game).getSaveManager();
        itemService = new ItemService(guiManager, this, linkManager, basementEnemyManager, soundEffectManager);

        imagesDungeon = ((MainActivity) game).getAllImages().getImagesDungeon();
        imagesDungeon.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        imagesItem = ((MainActivity) game).getAllImages().getImagesItem();
        imagesItem.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        BasementTile.initHashMap();

        colorMatrix = new ColorMatrixZone();
    }

    /**
     * Initialize the basement room
     */
    private void initBasementRoom(MainActivity game) {
        List<String> basementFileContent = FileUtil.extractLinesFromAsset(game.getAssetManager(), "other/basement_item.txt");
        basementRoom = new BasementRoom();
        for (int index = 0; index < 11; index++) {
            String line = basementFileContent.get(index);
            basementRoom.addALine(line);
        }
    }

    /**
     * Initialize the basement
     */
    private void initBasement(BasementInfo basementInfo) {
        basement = new Basement(imagesDungeon, basementInfo);
        int itemsTaken = saveManager.getSave().getDungeonSave(basement.dungeonInfo.dungeonId).getItemsTaken(basementInfo.basementLocationInTheDungeon);
        if (itemsTaken <= 1) {
            Item item1 = new Item();
            item1.name = basementInfo.item;
            item1.image = imagesItem.get(item1.name);
            item1.pickAnimation = itemService.findPickAnimation(item1.name);
            item1.x = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
            item1.y = LocationUtil.getYFromGrid(5);
            item1.hitbox.relocate(item1.x, item1.y);
            basement.addItem(item1);
        }
        if (itemsTaken == 0) {
            Item item2 = new Item();
            item2.name = basementInfo.item;
            item2.image = imagesItem.get(item2.name);
            item2.pickAnimation = itemService.findPickAnimation(item2.name);
            item2.x = LocationUtil.getXFromGrid(9);
            item2.y = LocationUtil.getYFromGrid(5);
            item2.hitbox.relocate(item2.x, item2.y);
            basement.addItem(item2);
        }
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (stunCounter > 0) {
            stunCounter -= deltaTime;
            if (stunCounter <= 0) {
                guiManager.activateButtons();
            }
        }

        colorMatrix.update(deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(basement.image, (int) LocationUtil.LEFT_MAP, (int) LocationUtil.TOP_MAP, AllImages.COEF, colorMatrix.getMatrix());
        for (Item item : basement.items) {
            if (!item.hidden) {
                g.drawScaledImage(item.image, (int) item.x, (int) item.y, AllImages.COEF, colorMatrix.getMatrix());
            }
        }
    }

    /**
     * Paint the doors cache after link to hide ihim when he goes through the doors
     */
    public void paintCache(Graphics g) {
        g.drawScaledImage(imagesDungeon.get("basement_cache"), (int) LocationUtil.LEFT_MAP, (int) LocationUtil.TOP_MAP, AllImages.COEF, colorMatrix.getMatrix());
    }

    @Override
    public void changeRoom(Orientation orientation) {
        Logger.info("Link has exited the basement.");
        guiManager.deactivateButtons();
        linkManager.exitZone();
        hasExitedZone = true;

    }

    @Override
    public boolean isTileADoor(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileStairs(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileABombHole(float x, float y) {
        return false;

    }

    @Override
    public boolean isTileWalkable(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileBlockingMissile(float x, float y) {
        return false;
    }

    @Override
    public boolean checkKeyDoor(Orientation orientation, float x, float y) {
        return false;
    }

    @Override
    public void openKeyDoor(Orientation orientation) {
    }

    @Override
    public boolean checkPushableBlock(Orientation orientation, float x, float y) {
        return false;
    }

    @Override
    public void pushBloc(Orientation orientation) {
    }

    @Override
    public Coordinate findSpawnableCoordinate() {
        return new Coordinate();
    }

    @Override
    public Coordinate findSpawnableCoordinateInWater() {
        return new Coordinate();
    }

    @Override
    public boolean isUpValid(float x, float y) {
        int tileUpX = LocationUtil.getTileXFromPositionX(x);
        int tileUpY = LocationUtil.getTileYFromPositionY(y);
        BasementTile tileUp = basementRoom.getTile(tileUpX, tileUpY);
        float deltaX = x - LocationUtil.getXFromGrid(tileUpX);
        return tileUp.walkable && deltaX < LocationUtil.HALF_TILE_SIZE;
    }

    @Override
    public boolean isDownValid(float x, float y) {
        int tileDownX = LocationUtil.getTileXFromPositionX(x);
        int tileDownY = LocationUtil.getTileYFromPositionY(y) + 1;
        BasementTile tileDown = basementRoom.getTile(tileDownX, tileDownY);
        float deltaX = x - LocationUtil.getXFromGrid(tileDownX);
        return tileDown.walkable && deltaX < LocationUtil.HALF_TILE_SIZE;
    }

    @Override
    public boolean isLeftValid(float x, float y) {
        int tileLeftX = LocationUtil.getTileXFromPositionX(x);
        int tileLeftY = LocationUtil.getTileYFromPositionY(y);
        BasementTile tileLeft = basementRoom.getTile(tileLeftX, tileLeftY);
        float deltaY = y - LocationUtil.getYFromGrid(tileLeftY);
        return tileLeft.walkable && deltaY < LocationUtil.HALF_TILE_SIZE;
    }

    @Override
    public boolean isRightValid(float x, float y) {
        int tileRightX = LocationUtil.getTileXFromPositionX(x) + 1;
        int tileRightY = LocationUtil.getTileYFromPositionY(y);
        BasementTile tileRight = basementRoom.getTile(tileRightX, tileRightY);
        float deltaY = y - LocationUtil.getYFromGrid(tileRightY);
        return tileRight.walkable && deltaY < LocationUtil.HALF_TILE_SIZE;
    }

    @Override
    public List<Item> getItems() {
        return basement.items;
    }

    @Override
    public void linkHasPickedItem(Item item) {
        item.hideItem();
        saveManager.increaseItemTaken(basement.dungeonInfo.dungeonId, basement.basementLocationInTheDungeon);
    }

    @Override
    public boolean isExplored(int x, int y) {
        return false;
    }

    @Override
    public String getDungeonId() {
        return "0";
    }

    @Override
    public boolean isARealRoom(int i, int j) {
        return true;
    }

    @Override
    public Location getTriforceLocation() {
        return new Location();
    }

    @Override
    public Image getMiniMap() {
        return imagesDungeon.get("mini_dungeon" + 1);
    }

    @Override
    public float getCurrentMiniAbscissa() {
        return 0;
    }

    @Override
    public float getCurrentMiniOrdinate() {
        return 0;
    }

    @Override
    public boolean isLinkFarEnoughFromBorderToAttack(Link link) {
        return !LocationUtil.isTileAtBorder(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE);
    }

    @Override
    public boolean hasThrowingSwordHitBorder(ThrowingSword throwingSword) {
        switch (throwingSword.orientation) {
            case UP:
                return throwingSword.y < LocationUtil.TOP_MAP + LocationUtil.HALF_TILE_SIZE;
            case DOWN:
                return throwingSword.y > LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - 3 * LocationUtil.HALF_TILE_SIZE;
            case LEFT:
                return throwingSword.x < LocationUtil.LEFT_MAP + LocationUtil.HALF_TILE_SIZE;
            case RIGHT:
                return throwingSword.x > LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - 3 * LocationUtil.HALF_TILE_SIZE;
        }
        return true;
    }

    @Override
    public boolean hasRodWaveHitBorder(RodWave rodWave) {
        switch (rodWave.orientation) {
            case UP:
                return rodWave.y < LocationUtil.TOP_MAP;
            case DOWN:
                return rodWave.y > LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - LocationUtil.TILE_SIZE;
            case LEFT:
                return rodWave.x < LocationUtil.LEFT_MAP;
            case RIGHT:
                return rodWave.x > LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - LocationUtil.TILE_SIZE;
        }
        return true;
    }

    @Override
    public boolean upAndDownAuthorized(Link link) {
        return true;
    }

    @Override
    public boolean leftAndRightAuthorized(Link link) {
        return true;
    }

    @Override
    public void fireHasJustFinished(Fire fire) {
    }

    @Override
    public void bombHasExploded(Bomb bomb) {
        colorMatrix.activate();
    }

    /**
     * True if link has exited the zone
     */
    public boolean hasExitedZone() {
        return hasExitedZone;
    }

    /**
     * Provides the dungeonInfo to return to the dungeon
     */
    public DungeonInfo getDungeonInfo() {
        return basement.dungeonInfo;
    }
}
