package com.twoplayers.legend.map;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesWorldMap;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.enemy.WorldMapEnemyManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorldMapManager implements IZoneManager {

    private static final float TRANSITION_SPEED = 4.0f;

    private boolean initNotDone = true;

    private ImagesWorldMap imagesWorldMap;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private WorldMapEnemyManager worldMapEnemyManager;
    private MusicManager musicManager;

    /** 16x8 MapScreens that represent the whole worldMap in this game */
    private MapRoom[][] worldMap;
    private Boolean[][] exploredWorldMap;

    private Properties worldMapCaves;

    private int currentAbscissa;
    private int currentOrdinate;
    private int nextAbscissa;
    private int nextOrdinate;
    private float currentMiniAbscissa;
    private float currentMiniOrdinate;

    private boolean transitionRunning;
    private float transitionCount;
    private Orientation transitionOrientation;
    private float leftCurrentMapScreen;
    private float topCurrentMapScreen;
    private Image imageCurrentMapScreen;
    private float leftNextMapScreen;
    private float topNextMapScreen;
    private Image imageNextMapScreen;

    /**
     * Load this manager
     */
    public void load(Game game, Coordinate location) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        musicManager.clear();
        musicManager.plan(100, "world_map_intro", false);
        musicManager.plan(0, "world_map_loop", true);

        currentAbscissa = (int) location.x;
        currentOrdinate = (int) location.y;
        nextAbscissa = currentAbscissa;
        nextOrdinate = currentOrdinate;
        currentMiniAbscissa = 16 * currentAbscissa;
        currentMiniOrdinate = 11 * currentOrdinate;

        transitionRunning = false;
        leftCurrentMapScreen = LocationUtil.LEFT_MAP;
        topCurrentMapScreen = LocationUtil.TOP_MAP;
        imageCurrentMapScreen = imagesWorldMap.get(getCoordinate());
        leftNextMapScreen = LocationUtil.LEFT_MAP;
        topNextMapScreen = LocationUtil.TOP_MAP;
        imageNextMapScreen = imagesWorldMap.get("empty");
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        linkManager = ((MainActivity) game).getLinkManager();
        worldMapEnemyManager = ((MainActivity) game).getWorldMapEnemyManager();
        musicManager = ((MainActivity) game).getMusicManager();

        imagesWorldMap = ((MainActivity) game).getAllImages().getImagesWorldMap();
        imagesWorldMap.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        MapTile.initHashMap();
        initWorldMap(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "map/world_map.txt"));
        worldMapCaves = FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "cave/world_map_caves.properties");
    }

    /**
     * Create all the MapRoom objects from the world_map file
     */
    private void initWorldMap(List<String> worldMapFileContent) {
        worldMap = new MapRoom[16][8];
        exploredWorldMap = new Boolean[16][8];

        // Initialise the mapScreens
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                worldMap[i][j] = (new MapRoom());
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

    @Override
    public void update(float deltaTime, Graphics g) {
        if (transitionRunning) {
            if (transitionOrientation == Orientation.UP) {
                float transitionDeltaY = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                topCurrentMapScreen += transitionDeltaY;
                topNextMapScreen += transitionDeltaY;
                currentMiniOrdinate -= transitionDeltaY * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkY(transitionDeltaY);
            }
            if (transitionOrientation == Orientation.DOWN) {
                float transitionDeltaY = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                topCurrentMapScreen -= transitionDeltaY;
                topNextMapScreen -= transitionDeltaY;
                currentMiniOrdinate += transitionDeltaY * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkY(-1 * transitionDeltaY);
            }
            if (transitionOrientation == Orientation.LEFT) {
                float transitionDeltaX = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                leftCurrentMapScreen += transitionDeltaX;
                leftNextMapScreen += transitionDeltaX;
                currentMiniAbscissa -= transitionDeltaX * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkX(transitionDeltaX);
            }
            if (transitionOrientation == Orientation.RIGHT) {
                float transitionDeltaX = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                leftCurrentMapScreen -= transitionDeltaX;
                leftNextMapScreen -= transitionDeltaX;
                currentMiniAbscissa += transitionDeltaX * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkX(-1 * transitionDeltaX);
            }
            transitionCount -= TRANSITION_SPEED * deltaTime;

            if (transitionCount < 0) {
                // End of the transition
                imageCurrentMapScreen = imageNextMapScreen;
                leftCurrentMapScreen = LocationUtil.LEFT_MAP;
                topCurrentMapScreen = LocationUtil.TOP_MAP;
                currentAbscissa = nextAbscissa;
                currentOrdinate = nextOrdinate;
                currentMiniAbscissa = 16 * currentAbscissa;
                currentMiniOrdinate = 11 * currentOrdinate;
                imageNextMapScreen = imagesWorldMap.get("empty");
                transitionRunning = false;
                worldMapEnemyManager.requestEnemiesLoading();
                guiManager.activateButtons();
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        g.drawScaledImage(imageNextMapScreen, (int) leftNextMapScreen, (int) topNextMapScreen, AllImages.COEF);
        g.drawScaledImage(imageCurrentMapScreen, (int) leftCurrentMapScreen, (int) topCurrentMapScreen, AllImages.COEF);
    }

    @Override
    public boolean isTileWalkable(float x, float y, boolean authorizeOutOfBound) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = (int) ((x - LocationUtil.LEFT_MAP) / LocationUtil.TILE_SIZE);
        int tileY = (int) ((y - LocationUtil.TOP_MAP) / LocationUtil.TILE_SIZE);
        MapTile tile = currentMapRoom.getTile(tileX, tileY);
        if (tile == MapTile.OUT_OF_BOUNDS && authorizeOutOfBound) {
            return true;
        }
        return tile.walkable;
    }

    @Override
    public boolean isTileACave(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = (int) ((x - LocationUtil.LEFT_MAP) / LocationUtil.TILE_SIZE);
        int tileY = (int) ((y - LocationUtil.TOP_MAP) / LocationUtil.TILE_SIZE);
        MapTile tile = currentMapRoom.getTile(tileX, tileY);
        return tile == MapTile.CAVE;
    }

    @Override
    public void changeRoom(Orientation orientation) {
        guiManager.deactivateButtons();
        worldMapEnemyManager.unloadEnemies();
        switch (orientation) {
            case UP :
                transitionCount = 176 * AllImages.COEF;
                leftNextMapScreen = LocationUtil.LEFT_MAP;
                topNextMapScreen = LocationUtil.TOP_MAP - LocationUtil.HEIGHT_MAP;
                nextAbscissa = currentAbscissa;
                nextOrdinate = currentOrdinate - 1;
                break;
            case DOWN :
                transitionCount = 176 * AllImages.COEF;
                leftNextMapScreen = LocationUtil.LEFT_MAP;
                topNextMapScreen = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
                nextAbscissa = currentAbscissa;
                nextOrdinate = currentOrdinate + 1;
                break;
            case LEFT :
                transitionCount = 256 * AllImages.COEF;
                leftNextMapScreen = LocationUtil.LEFT_MAP - LocationUtil.WIDTH_MAP;
                topNextMapScreen = LocationUtil.TOP_MAP;
                nextAbscissa = currentAbscissa - 1;
                nextOrdinate = currentOrdinate;
                break;
            case RIGHT :
                transitionCount = 256 * AllImages.COEF;
                leftNextMapScreen = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
                topNextMapScreen = LocationUtil.TOP_MAP;
                nextAbscissa = currentAbscissa + 1;
                nextOrdinate = currentOrdinate;
                break;
        }
        imageNextMapScreen = imagesWorldMap.get(String.valueOf(nextAbscissa) + nextOrdinate);
        Logger.info("Starting screen transition to " + nextAbscissa + nextOrdinate);
        exploredWorldMap[nextAbscissa][nextOrdinate] = true;
        transitionRunning = true;
        transitionOrientation = orientation;
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean isExplored(int x, int y) {
        return exploredWorldMap[x][y];
    }

    /**
     * Find a tile where a enemy can spawn avoid map borders
     */
    public Coordinate findSpawnableCoordinate() {
        /**  A counter to avoid infinite loop. */
        int counter = 50;
        float x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
        float y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
        Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        while (counter-- > 0 && !isTileWalkable(x, y, false)) {
            x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
            y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
            Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        }
        return new Coordinate(x, y);
    }

    @Override
    public boolean isUpValid(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        MapTile tileUpLeft = currentMapRoom.getTile(tileX, tileY);
        MapTile tileUpRight = currentMapRoom.getTile(tileX + 1, tileY);
        MapTile tileDownLeft = currentMapRoom.getTile(tileX, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpLeft) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case BLOC_TOP_LOWER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                if (tileDownLeft == MapTile.BLOC_TOP_UPPER) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileUpRight) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case BLOC_TOP_UPPER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isDownValid(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        MapTile tileDownLeft = currentMapRoom.getTile(tileX, tileY + 1);
        MapTile tileDownRight = currentMapRoom.getTile(tileX + 1, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileDownLeft) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_UPPER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
                if (deltaX < LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_BOT_LOWER:
                if (deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_LOWER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
                if (deltaX < LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_BOT_UPPER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE || deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isLeftValid(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        MapTile tileUpLeft = currentMapRoom.getTile(tileX, tileY);
        MapTile tileDownLeft = currentMapRoom.getTile(tileX, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpLeft) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
                if (deltaX < LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownLeft) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_TOP_UPPER:
            case BLOC_TOP_LOWER:
            case BLOC_BOT_UPPER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_BOT_LOWER:
                if ((deltaY > LocationUtil.OBSTACLE_TOLERANCE && deltaX < LocationUtil.HALF_TILE_SIZE) || deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isRightValid(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        MapTile tileUpRight = currentMapRoom.getTile(tileX + 1, tileY);
        MapTile tileDownRight = currentMapRoom.getTile(tileX + 1, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpRight) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
                if (deltaX < LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case WATER:
            case STATUE:
            case TOMB:
            case BLOC_TOP_UPPER:
            case BLOC_TOP_LOWER:
            case BLOC_BOT_LOWER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_BOT_UPPER:
                if ((deltaY > LocationUtil.OBSTACLE_TOLERANCE && deltaX > LocationUtil.HALF_TILE_SIZE) || deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case CAVE:
            case BRIDGE:
            case LADDER:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    /**
     * Obtain the current mapScreen coordinate
     */
    public String getCoordinate() {
        return String.valueOf(currentAbscissa) + currentOrdinate;
    }

    /**
     * Obtain the cave information on the current mapScreen
     */
    public String getCave() {
        return worldMapCaves.getProperty(getCoordinate(), "CAVE");
    }

    public float getCurrentMiniAbscissa() {
        return currentMiniAbscissa;
    }

    public float getCurrentMiniOrdinate() {
        return currentMiniOrdinate;
    }
}
