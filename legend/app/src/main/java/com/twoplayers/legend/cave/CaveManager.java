package com.twoplayers.legend.cave;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesCave;
import com.twoplayers.legend.assets.image.ImagesItem;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.link.inventory.InventoryService;
import com.twoplayers.legend.character.npc.Npc;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.TextUtil;

import java.util.List;
import java.util.Properties;

public class CaveManager implements IZoneManager {

    private static final String DEFAULT_MESSAGE = "";
    private static final String DEFAULT_NPC = "empty";
    private static final String DEFAULT_LOCATION = "7|7";
    private static final String DEFAULT_ENTRANCE = "4|1";
    private static final String DEFAULT_ITEMS = "";

    private static final int PRICE_OFFSET_X_2DIGITS = -1;
    private static final int PRICE_OFFSET_X_3DIGITS = -11;
    private static final int PRICE_OFFSET_Y = 60;
    private static final float TEXT_SPEED = 0.12f;

    private boolean initNotDone = true;

    private LinkManager linkManager;
    private InventoryService inventoryService;

    private ImagesCave imagesCave;
    private ImagesItem imagesItem;
    private SoundEffectManager soundEffectManager;

    private CaveRoom caveRoom;
    private Cave cave;

    private float textCounter;
    private int textSoundCounter;
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
        textCounter = 0;
        textSoundCounter = 0;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        linkManager = ((MainActivity) game).getLinkManager();
        inventoryService = new InventoryService();

        imagesCave = ((MainActivity) game).getAllImages().getImagesCave();
        imagesCave.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        imagesItem = ((MainActivity) game).getAllImages().getImagesItem();
        imagesItem.load(((MainActivity) game).getAssetManager(), game.getGraphics());
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();

        CaveTile.initHashMap();
        List<String> caveFileContent = FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "cave/cave.txt");
        caveRoom = new CaveRoom();
        for (int index = 0; index < 11; index++) {
            String line = caveFileContent.get(index);
            caveRoom.addALine(line);
        }
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        textCounter += deltaTime * TEXT_SPEED;
        textCounter = Math.min(textCounter, cave.message1.length() + cave.message2.length());
        int end1 = (int) Math.min(textCounter, cave.message1.length());
        int end2 = (int) Math.max(0, Math.min(textCounter - cave.message1.length(), cave.message2.length()));
        cave.displayedMessage1 = cave.message1.substring(0, end1);
        cave.displayedMessage2 = cave.message2.substring(0, end2);
        if (textSoundCounter < (int) textCounter) {
            textSoundCounter = (int) textCounter;
            soundEffectManager.play("text");
        }

        cave.fireAnimation.update(deltaTime);
        cave.coinAnimation.update(deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(imagesCave.get("cave"), LocationUtil.LEFT_MAP, LocationUtil.TOP_MAP, AllImages.COEF);
        float message1X = LocationUtil.LEFT_MAP + 2.2f * LocationUtil.TILE_SIZE + 6.5f * (1f - cave.message1.length() / 22f) * LocationUtil.TILE_SIZE;
        float message2X = LocationUtil.LEFT_MAP + 2.2f * LocationUtil.TILE_SIZE + 6.5f * (1f - cave.message2.length() / 22f) * LocationUtil.TILE_SIZE;
        g.drawString(cave.displayedMessage1, (int) message1X, (int) LocationUtil.getYFromGrid(3), TextUtil.getPaint());
        g.drawString(cave.displayedMessage2, (int) message2X, (int) (LocationUtil.getYFromGrid(3) + LocationUtil.HALF_TILE_SIZE), TextUtil.getPaint());
        g.drawAnimation(cave.fireAnimation, (int) LocationUtil.getXFromGrid(5), (int) cave.npc.y);
        g.drawAnimation(cave.fireAnimation, (int) LocationUtil.getXFromGrid(10), (int) cave.npc.y);
        g.drawScaledImage(cave.npc.image, (int) cave.npc.x, (int) cave.npc.y, AllImages.COEF);
        boolean priceDisplayed = false;
        for(Item item : cave.items) {
            if (!item.hidden) {
                g.drawScaledImage(item.image, (int) item.x, (int) item.y, AllImages.COEF);
                if (item.price > 0) {
                    String text = String.valueOf(item.price);
                    int offsetX = (text.length() == 2) ? PRICE_OFFSET_X_2DIGITS : PRICE_OFFSET_X_3DIGITS;
                    g.drawString(text, (int) item.x + offsetX, (int) item.y + PRICE_OFFSET_Y, TextUtil.getPaint());
                    priceDisplayed = true;
                }
            }
        }
        if (priceDisplayed) {
            g.drawAnimation(cave.coinAnimation, (int) LocationUtil.getXFromGrid(3), (int) (LocationUtil.getYFromGrid(6) + LocationUtil.HALF_TILE_SIZE));
            g.drawString("x", (int) LocationUtil.getXFromGrid(4), (int) (LocationUtil.getYFromGrid(5) + LocationUtil.HALF_TILE_SIZE) + PRICE_OFFSET_Y, TextUtil.getPaint());
        }
    }

    /**
     * Init the cave based on the file properties
     */
    private void initCave(Game game, String coordinate) {
        Properties caveProperties = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "cave/" + coordinate + ".properties");
        cave = new Cave(imagesCave, imagesItem, game.getGraphics());
        hasExitedZone = false;

        cave.message1 = caveProperties.getProperty("message1", DEFAULT_MESSAGE);
        cave.message2 = caveProperties.getProperty("message2", DEFAULT_MESSAGE);

        String[] location = caveProperties.getProperty("location", DEFAULT_LOCATION).split("\\|");
        cave.location = new Coordinate(Float.valueOf(location[0]), Float.valueOf(location[1]));

        String[] entrance = caveProperties.getProperty("entrance", DEFAULT_ENTRANCE).split("\\|");
        cave.entrance = new Coordinate(Float.valueOf(entrance[0]), Float.valueOf(entrance[1]));

        cave.npc = new Npc();
        cave.npc.name = caveProperties.getProperty("npc", DEFAULT_NPC);
        Logger.info("Loading cave with NPC '" + cave.npc.name + "'");
        cave.npc.image = imagesCave.get(cave.npc.name);
        cave.npc.x = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
        cave.npc.y = LocationUtil.getYFromGrid(4);
        cave.npc.hitbox.relocate(cave.npc.x, cave.npc.y);

        String itemsFromFile = caveProperties.getProperty("items", DEFAULT_ITEMS);
        if (itemsFromFile.length() > 0) {
            String[] itemsSplitted = itemsFromFile.split("\\|");
            float[] itemPositionsX = new float[3];
            if (itemsSplitted.length == 1) {
                itemPositionsX[0] = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
            } else {
                itemPositionsX[0] = LocationUtil.getXFromGrid(5) + LocationUtil.HALF_TILE_SIZE;
                itemPositionsX[1] = LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE;
                itemPositionsX[2] = LocationUtil.getXFromGrid(9) + LocationUtil.HALF_TILE_SIZE;
            }
            for (int index = 0; index < itemsSplitted.length; index++) {
                String itemAndPrice = itemsSplitted[index];
                Logger.info("Loading item with '" + itemAndPrice + "'");
                String[] elements = itemAndPrice.split(",");
                Item item = new Item();
                item.name = elements[0];
                item.image = imagesItem.get(item.name);
                item.pickAnimation = inventoryService.findPickAnimation(item.name);
                item.x = itemPositionsX[index];
                item.y = LocationUtil.getYFromGrid(5) + LocationUtil.HALF_TILE_SIZE;
                item.hitbox.relocate(item.x, item.y);
                item.price = Integer.valueOf(elements[1]);
                cave.addItem(item);
            }
        }
    }

    @Override
    public void changeRoom(Orientation orientation) {
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
        CaveTile tile = caveRoom.getTile(tileX, tileY);
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

    @Override
    public List<Item> getItems() {
        return cave.items;
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

    /**
     * True if link has exited the zone
     */
    public boolean hasExitedZone() {
        return hasExitedZone;
    }
}
