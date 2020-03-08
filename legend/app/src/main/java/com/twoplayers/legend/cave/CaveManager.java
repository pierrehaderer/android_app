package com.twoplayers.legend.cave;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesCave;
import com.twoplayers.legend.assets.image.ImagesItem;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.npc.Npc;
import com.twoplayers.legend.character.npc.NpcType;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.List;
import java.util.Properties;

public class CaveManager implements IZoneManager {

    private static final String DEFAULT_MESSAGE = "";
    private static final String DEFAULT_NPC = "empty";
    private static final String DEFAULT_LOCATION = "7|7";
    private static final String DEFAULT_ENTRANCE = "4|1";
    private static final String DEFAULT_ITEMS = "";

    private boolean initNotDone = true;

    private LinkManager linkManager;

    private ImagesCave imagesCave;
    private ImagesItem imagesItem;

    private CaveScreen caveScreen;
    private Cave cave;
    private boolean hasExitedZone;
    /**
     * Load this manager
     */
    public void load(Game game, String mapCoordinate) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }
        initCave(game, mapCoordinate);
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        linkManager = ((MainActivity) game).getLinkManager();

        imagesCave = ((MainActivity) game).getAllImages().getImagesCave();
        imagesCave.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        imagesItem = ((MainActivity) game).getAllImages().getImagesItem();
        imagesItem.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        CaveTile.initHashMap();
        NpcType.initHashMap();

        initCaveScreen(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "cave/cave.txt"));
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        cave.fireAnimation.update(deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(imagesCave.get("cave"), LocationUtil.LEFT_MAP, LocationUtil.TOP_MAP, AllImages.COEF);
        g.drawAnimation(cave.fireAnimation, (int) LocationUtil.getXFromGrid(5), (int) cave.npc.y);
        g.drawAnimation(cave.fireAnimation, (int) LocationUtil.getXFromGrid(10), (int) cave.npc.y);
        g.drawScaledImage(cave.npc.image, (int) cave.npc.x, (int) cave.npc.y, AllImages.COEF);
        if (cave.items.size() == 1) {
            Item item = cave.items.get(0);
            //int price = cave.itemPrices.get(0);
            g.drawScaledImage(item.image, (int) item.x, (int) item.y, AllImages.COEF);
        }
    }

    /**
     * Create all the caveScreen object from the cave file
     */
    private void initCaveScreen(List<String> caveFileContent) {
        caveScreen = new CaveScreen();

        for (int index = 0; index < 11; index++) {
            String line = caveFileContent.get(index);
            caveScreen.addALine(line);
        }
    }

    /**
     * Init the cave based on the file properties
     */
    private void initCave(Game game, String coordinate) {
        Properties caveProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "cave/" + coordinate + ".properties");
        cave = new Cave(imagesCave, game.getGraphics());
        hasExitedZone = false;

        cave.message = caveProperties.getProperty("message", DEFAULT_MESSAGE);

        String[] location = caveProperties.getProperty("location", DEFAULT_LOCATION).split("\\|");
        cave.location = new Coordinate(Float.valueOf(location[0]), Float.valueOf(location[1]));

        String[] entrance = caveProperties.getProperty("entrance", DEFAULT_ENTRANCE).split("\\|");
        cave.entrance = new Coordinate(Float.valueOf(entrance[0]), Float.valueOf(entrance[1]));

        cave.npc = new Npc();
        cave.npc.type = NpcType.getEnum(caveProperties.getProperty("npc", DEFAULT_NPC));
        Logger.info("Loading cave with NPC '" + cave.npc.type.name + "'");
        cave.npc.image = imagesCave.get(cave.npc.type.name);
        cave.npc.x = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
        cave.npc.y = LocationUtil.getYFromGrid(4);
        cave.npc.hitbox.relocate(cave.npc.x, cave.npc.y);

        String[] items = caveProperties.getProperty("items", DEFAULT_ITEMS).split("\\|");
        if (items.length > 0) {
            for (String itemAsString : items) {
                Logger.info("Loading item with '" + itemAsString + "'");
                String[] elements = itemAsString.split(",");
                Item item = new Item();
                item.name = elements[0];
                item.image = imagesItem.get(elements[0]);
                item.x = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
                item.y = LocationUtil.getYFromGrid(5) + LocationUtil.HALF_TILE_SIZE;
                item.hitbox.relocate(item.x, item.y);
                cave.addItem(item, Integer.valueOf(elements[1]));
            }
        }
    }

    @Override
    public void changeScreen(Orientation orientation) {
        Logger.info("Link has exited the cave.");
        hasExitedZone = true;
        linkManager.exitZone();
    }

    @Override
    public boolean isTileACave(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound) {
        int tileX = (int) ((x - LocationUtil.LEFT_MAP) / LocationUtil.TILE_SIZE);
        int tileY = (int) ((y - LocationUtil.TOP_MAP) / LocationUtil.TILE_SIZE);
        CaveTile tile = caveScreen.getTile(tileX, tileY);
        if (tile == CaveTile.OUT_OF_BOUNDS && authorizeOutOfBound) {
            return true;
        }
        return tile.walkable;
    }

    @Override
    public boolean isExplored(int x, int y) {
        return false;
    }

    @Override
    public float getCurrentMiniAbscissa() {
        return 0;
    }

    @Override
    public float getCurrentMiniOrdinate() {
        return 0;
    }

    public boolean hasExitedZone() {
        return hasExitedZone;
    }

    /**
     * Obtain the location of the cave
     */
    public Coordinate getCaveLocation() {
        return cave.location;
    }

    /**
     * Obtain the entrance of the cave
     */
    public Coordinate getCaveEntrance() {
        return cave.entrance;
    }
}
