package com.twoplayers.legend.map;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesWorldMap;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.enemy.WorldMapEnemyManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.List;

public class WorldMapManager implements IManager {

    public static final int LEFT_MAP = 150;
    public static final int TOP_MAP = 103;
    public static final int WIDTH_MAP = 548;
    public static final int HEIGHT_MAP = 377;
    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    public static final float TRANSITION_SPEED = 4.0f;

    private ImagesWorldMap imagesWorldMap;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private WorldMapEnemyManager worldMapEnemyManager;

    /** 16x8 MapScreens that represent the whole worldMap in this game */
    private MapScreen[][] worldMap;
    private Boolean[][] exploredWorldMap;

    private int currentAbsisse;
    private int currentOrdinate;
    private int nextAbsisse;
    private int nextOrdinate;
    private float currentMiniAbsisse;
    private float currentMiniOrdinate;

    private boolean transitionRunning;
    private float transitionCount;
    private float transitionSpeedX;
    private float transitionSpeedY;
    private float leftCurrentMapScreen;
    private float topCurrentMapScreen;
    private Image imageCurrentMapScreen;
    private float leftNextMapScreen;
    private float topNextMapScreen;
    private Image imageNextMapScreen;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        linkManager = ((MainActivity) game).getLinkManager();
        worldMapEnemyManager = ((MainActivity) game).getWorldMapEnemyManager();

        imagesWorldMap = ((MainActivity) game).getAllImages().getImagesWorldMap();
        imagesWorldMap.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        MapTile.initHashMap();
        initWorldMap(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "map/world_map.txt"));

        currentAbsisse = 7;
        currentOrdinate = 7;
        nextAbsisse = 7;
        nextOrdinate = 7;
        currentMiniAbsisse = 16 * currentAbsisse;
        currentMiniOrdinate = 11 * currentOrdinate;

        transitionRunning = false;
        leftCurrentMapScreen = LEFT_MAP;
        topCurrentMapScreen = TOP_MAP;
        imageCurrentMapScreen = imagesWorldMap.get(String.valueOf(currentAbsisse) + currentOrdinate);
        leftNextMapScreen = LEFT_MAP;
        topNextMapScreen = TOP_MAP;
        imageNextMapScreen = imagesWorldMap.get("empty");
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (transitionRunning) {
            float transitionDeltaX = transitionSpeedX * deltaTime;
            float transitionDeltaY = transitionSpeedY * deltaTime;
            transitionCount -= Math.abs(transitionDeltaX + transitionDeltaY);
            leftCurrentMapScreen += transitionDeltaX;
            topCurrentMapScreen += transitionDeltaY;
            leftNextMapScreen += transitionDeltaX;
            topNextMapScreen += transitionDeltaY;
            currentMiniAbsisse -= transitionDeltaX * 16 / WIDTH_MAP;
            currentMiniOrdinate -= transitionDeltaY * 16 / WIDTH_MAP;

            linkManager.moveLinkX(transitionDeltaX);
            linkManager.moveLinkY(transitionDeltaY);

            if (transitionCount < 0) {
                // End of the transition
                imageCurrentMapScreen = imageNextMapScreen;
                leftCurrentMapScreen = LEFT_MAP;
                topCurrentMapScreen = TOP_MAP;
                currentAbsisse = nextAbsisse;
                currentOrdinate = nextOrdinate;
                currentMiniAbsisse = 16 * currentAbsisse;
                currentMiniOrdinate = 11 * currentOrdinate;
                imageNextMapScreen = imagesWorldMap.get("empty");
                transitionRunning = false;
                worldMapEnemyManager.willLoadEnemies();
                guiManager.activateButtons();
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(imageNextMapScreen, (int) leftNextMapScreen, (int) topNextMapScreen, AllImages.COEF);
        g.drawScaledImage(imageCurrentMapScreen, (int) leftCurrentMapScreen, (int) topCurrentMapScreen, AllImages.COEF);
        g.drawRect(0, 0, LEFT_MAP, HEIGHT_PHONE_SCREEN, Color.BLACK);
        g.drawRect(0, 0, WIDTH_PHONE_SCREEN, TOP_MAP, Color.BLACK);
        g.drawRect(LEFT_MAP + WIDTH_MAP, 0, WIDTH_PHONE_SCREEN - LEFT_MAP - WIDTH_MAP + 1, HEIGHT_PHONE_SCREEN, Color.BLACK);
    }

    /**
     * Create all the MapScreen objects from the world_map file
     */
    private void initWorldMap(List<String> worldMapFileContent) {
        worldMap = new MapScreen[16][8];
        exploredWorldMap = new Boolean[16][8];

        // Initialise the mapScreens
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                worldMap[i][j] = (new MapScreen());
                exploredWorldMap[i][j] = false;
            }
        }
        exploredWorldMap[7][7] = true;

        // Fill the mapScreens, line by line
        int indexLine = 0;
        for (int index1 = 0; index1 < 8; index1 = index1) {
            String line = worldMapFileContent.get(indexLine++);
            for (int index2 = 0; index2 < 16; index2++) {
                worldMap[index2][index1].addALine(line.substring(17 * index2, 17 * index2 + 16));
            }
            // Jump over the delimiter line and go to next line of mapScreens
            if (indexLine % 12 == 11) {
                indexLine++;
                index1++;
            }
        }
    }

    /**
     * Check if a tile is walkable
     */
    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound) {
        MapScreen currentMapScreen = worldMap[currentAbsisse][currentOrdinate];
        int tileX = (int) Math.ceil((x - LEFT_MAP) / LocationUtil.TILE_SIZE);
        int tileY = (int) Math.ceil((y - TOP_MAP) / LocationUtil.TILE_SIZE);
        //Logger.debug("Tile checked (" + tileX + ", " + tileY + ")");
        MapTile tile = currentMapScreen.getTile(tileX, tileY);
        if (tile == MapTile.OUT_OF_BOUNDS && authorizeOutOfBound) {
            return true;
        }
        return tile.walkable;
    }

    /**
     * Check if a tile is on the border of the map
     */
    public boolean isTileAtBorder(float x, float y) {
        int tileX = (int) Math.ceil((x - LEFT_MAP) / LocationUtil.TILE_SIZE);
        if (tileX == 1 || tileX == 16) return true;
        int tileY = (int) Math.ceil((y - TOP_MAP) / LocationUtil.TILE_SIZE);
        if (tileY == 1 || tileY == 11) return true;
        return false;
    }

    /**
     * Ask for a transition between two mapScreens
     */
    public void changeMapScreeen(Orientation orientation) {
        guiManager.deactivateButtons();
        worldMapEnemyManager.unloadEnemies();
        switch (orientation) {
            case UP :
                transitionCount = 176 * AllImages.COEF;
                leftNextMapScreen = LEFT_MAP;
                topNextMapScreen = TOP_MAP - HEIGHT_MAP;
                nextAbsisse = currentAbsisse;
                nextOrdinate = currentOrdinate - 1;
                transitionSpeedX = 0;
                transitionSpeedY = TRANSITION_SPEED;
                break;
            case DOWN :
                transitionCount = 176 * AllImages.COEF;
                leftNextMapScreen = LEFT_MAP;
                topNextMapScreen = TOP_MAP + HEIGHT_MAP;
                nextAbsisse = currentAbsisse;
                nextOrdinate = currentOrdinate + 1;
                transitionSpeedX = 0;
                transitionSpeedY = -1 * TRANSITION_SPEED;
                break;
            case LEFT :
                transitionCount = 256 * AllImages.COEF;
                leftNextMapScreen = LEFT_MAP - WIDTH_MAP;
                topNextMapScreen = TOP_MAP;
                nextAbsisse = currentAbsisse - 1;
                nextOrdinate = currentOrdinate;
                transitionSpeedX = TRANSITION_SPEED;
                transitionSpeedY = 0;
                break;
            case RIGHT :
                transitionCount = 256 * AllImages.COEF;
                leftNextMapScreen = LEFT_MAP + WIDTH_MAP;
                topNextMapScreen = TOP_MAP;
                nextAbsisse = currentAbsisse + 1;
                nextOrdinate = currentOrdinate;
                transitionSpeedX = -1 * TRANSITION_SPEED;
                transitionSpeedY = 0;
                break;
        }
        imageNextMapScreen = imagesWorldMap.get(String.valueOf(nextAbsisse) + nextOrdinate);
        Logger.info("Starting screen transition to " + nextAbsisse + nextOrdinate);
        exploredWorldMap[nextAbsisse][nextOrdinate] = true;
        transitionRunning = true;
    }

    /**
     * Find a tile where a enemy can spawn avoid map borders
     */
    public Coordinate findSpawnableCoordinate() {
        /**  A counter to avoid infinite loop. */
        int counter = 50;
        float x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LEFT_MAP + 1);
        float y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + TOP_MAP + 1);
        Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        while (counter-- > 0 && !isTileWalkable(x, y, false)) {
            x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LEFT_MAP + 1);
            y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + TOP_MAP + 1);
            Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        }
        return new Coordinate(x, y);
    }

    /**
     * Return true if the mapScreen has been explored
     */
    public boolean isExplored(int x, int y) {
        return exploredWorldMap[x][y];
    }

    public int getCurrentAbsisse() {
        return currentAbsisse;
    }

    public int getCurrentOrdinate() {
        return currentOrdinate;
    }

    public float getCurrentMiniAbsisse() {
        return currentMiniAbsisse;
    }

    public float getCurrentMiniOrdinate() {
        return currentMiniOrdinate;
    }
}
