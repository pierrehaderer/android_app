package com.twoplayers.legend.map;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesWorldMap;
import com.twoplayers.legend.character.LinkManager;
import com.twoplayers.legend.character.enemy.WorldMapEnemyManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
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

    /** 8x16 MapScreens that represent the whole worldMap in this game */
    private List<List<MapScreen>> worldMap;

    private int currentAbsisse;
    private int currentOrdinate;
    private int nextAbsisse;
    private int nextOrdinate;

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
        processWorldMapFile(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "map/world_map.txt"));

        currentAbsisse = 8;
        currentOrdinate = 8;
        nextAbsisse = 8;
        nextOrdinate = 8;

        transitionRunning = false;
        leftCurrentMapScreen = LEFT_MAP;
        topCurrentMapScreen = TOP_MAP;
        imageCurrentMapScreen = imagesWorldMap.get(currentAbsisse + "_" + currentOrdinate);
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
            this.leftCurrentMapScreen += transitionDeltaX;
            leftNextMapScreen += transitionDeltaX;
            topCurrentMapScreen += transitionDeltaY;
            topNextMapScreen += transitionDeltaY;

            linkManager.moveLink(transitionDeltaX, transitionDeltaY);

            if (transitionCount < 0) {
                // End of the transition
                imageCurrentMapScreen = imageNextMapScreen;
                leftCurrentMapScreen = LEFT_MAP;
                topCurrentMapScreen = TOP_MAP;
                currentAbsisse = nextAbsisse;
                currentOrdinate = nextOrdinate;
                imageNextMapScreen = imagesWorldMap.get("empty");
                leftNextMapScreen = LEFT_MAP;
                topNextMapScreen = TOP_MAP;
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
    private void processWorldMapFile(List<String> worldMapFileContent) {
        worldMap = new ArrayList<>();

        // Initialise the mapScreens
        for (int index1 = 0; index1 <= 8; index1++) {
            List<MapScreen> mapScreens = new ArrayList<>();
            for (int index2 = 0; index2 <= 16; index2++) {
                mapScreens.add(new MapScreen());
            }
            worldMap.add(mapScreens);
        }

        // Fill the mapScreens, line by line
        int indexLine = 0;
        for (int index1 = 0; index1 < 8; index1 = index1) {
            String line = worldMapFileContent.get(indexLine++);
            for (int index2 = 0; index2 < 16; index2++) {
                // +1 are added to avoid using the index 0.
                worldMap.get(index1 + 1).get(index2 + 1).addALine(line.substring(17 * index2, 17 * index2 + 16));
            }
            // Jump over the delimiter line and go to next line of mapScreens
            if (indexLine % 12 == 11) {
                indexLine++;
                index1++;
            }
        }

// Log the content of a mapScreen if necessary
//        for (int i = 1; i <= 11; i++) {
//            String line = "";
//            for (int j = 1; j <= 16; j++) {
//                line += getMapScreen(8, 8).getContent().get(i).get(j).character;
//            }
//            Logger.debug(line);
//        }
    }

    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound) {
        MapScreen currentMapScreen = worldMap.get(currentOrdinate).get(currentAbsisse);
        int tileX = (int) Math.ceil((x - LEFT_MAP) / AllImages.COEF / 16f);
        int tileY = (int) Math.ceil((y - TOP_MAP) / AllImages.COEF / 16f);
        //Logger.debug("Tile checked (" + tileX + ", " + tileY + ")");
        MapTile tile = currentMapScreen.getTile(tileX, tileY);
        if (tile == MapTile.OUT_OF_BOUNDS && authorizeOutOfBound) {
            return true;
        }
        return tile.walkable;
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
        imageNextMapScreen = imagesWorldMap.get(nextAbsisse + "_" + nextOrdinate);
        Logger.info("Starting screen transition to " + nextAbsisse + "_" + nextOrdinate);
        transitionRunning = true;
    }

    /**
     * Find a tile where a enemy can spawn
     */
    public Coordinate findSpawnableCoordinate() {
        float x = (float) (Math.floor(Math.random() * 16) * LocationUtil.TILE_SIZE + LEFT_MAP);
        float y = (float) (Math.floor(Math.random() * 11) * LocationUtil.TILE_SIZE + TOP_MAP);
        while (!isTileWalkable(x, y, false)) {
            x = (float) (Math.floor(Math.random() * 16) * LocationUtil.TILE_SIZE + LEFT_MAP);
            y = (float) (Math.floor(Math.random() * 11) * LocationUtil.TILE_SIZE + TOP_MAP);
        }
        return new Coordinate(x, y);
    }

    public int getCurrentAbsisse() {
        return currentAbsisse;
    }

    public int getCurrentOrdinate() {
        return currentOrdinate;
    }
}
