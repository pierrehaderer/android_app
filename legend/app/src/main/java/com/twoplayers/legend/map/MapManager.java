package com.twoplayers.legend.map;

import android.graphics.Color;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImageWorldMaps;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements IManager {

    public static final int LEFT_MAP = 150;
    public static final int TOP_MAP = 103;
    public static final int WIDTH_MAP = 548;
    public static final int HEIGHT_MAP = 377;
    public static final int WIDTH_PHONE_SCREEN = 800;
    public static final int HEIGHT_PHONE_SCREEN = 480;

    private ImageWorldMaps imageWorldMaps;

    /** 8x16 MapScreens that represent the whole worldMap in this game */
    private List<List<MapScreen>> worldMap;

    private int currentAbsisse;
    private int currentOrdinate;

    public void init(Game game) {
        imageWorldMaps = ((MainActivity) game).getAllImages().getImageWorldMaps();
        imageWorldMaps.load(game.getGraphics());
        MapTile.initHashMap();
        List<String> worldMapFileContent = FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "map/world_map.txt");
        processWorldMapFileContent(worldMapFileContent);
        currentAbsisse = 8;
        currentOrdinate = 8;
    }

    @Override
    public void update(float deltaTime, Graphics g) {

    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(imageWorldMaps.get(currentAbsisse + "_" + currentOrdinate), LEFT_MAP, TOP_MAP, AllImages.COEF);
        g.drawRect(0, 0, LEFT_MAP, HEIGHT_PHONE_SCREEN, Color.BLACK);
        g.drawRect(0, 0, WIDTH_PHONE_SCREEN, TOP_MAP, Color.BLACK);
        g.drawRect(LEFT_MAP + WIDTH_MAP, 0, WIDTH_PHONE_SCREEN - LEFT_MAP - WIDTH_MAP + 1, HEIGHT_PHONE_SCREEN, Color.BLACK);
    }

    /**
     * Create all the MapScreen objects from the world_map file
     */
    private void processWorldMapFileContent(List<String> worldMapFileContent) {
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

    public boolean isTileWalkable(float x, float y) {
        MapScreen currentMapScreen = worldMap.get(currentOrdinate).get(currentAbsisse);
        int tileX = (int) Math.ceil((x - LEFT_MAP) / AllImages.COEF / 16f);
        int tileY = (int) Math.ceil((y - TOP_MAP) / AllImages.COEF / 16f);
        //Logger.debug("Tile checked (" + tileX + ", " + tileY + ")");
        return currentMapScreen.getTile(tileX, tileY).walkable;
    }
}
