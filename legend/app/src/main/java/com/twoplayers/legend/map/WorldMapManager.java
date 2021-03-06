package com.twoplayers.legend.map;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.cave.CaveType;
import com.twoplayers.legend.character.Hitbox;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesWorldMap;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.character.enemy.worldmap.WorldMapEnemyManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.ColorMatrixZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorldMapManager implements IZoneManager {

    private static final float TRANSITION_SPEED = 4.0f;

    private boolean shouldInitialize = true;

    private ImagesWorldMap imagesWorldMap;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private WorldMapEnemyManager worldMapEnemyManager;
    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;
    private SaveManager saveManager;

    /** 16x8 MapRooms that represent the whole worldMap in this game */
    private MapRoom[][] worldMap;
    private Boolean[][] exploredRooms;

    private EntranceInfo[][] worldMapEntrances;

    private int currentAbscissa;
    private int currentOrdinate;
    private int nextAbscissa;
    private int nextOrdinate;
    private float currentMiniAbscissa;
    private float currentMiniOrdinate;

    private boolean transitionRunning;
    private float transitionCount;
    private Orientation transitionOrientation;
    private float leftCurrentRoom;
    private float topCurrentRoom;
    private Image imageCurrentRoom;
    private float leftNextRoom;
    private float topNextRoom;
    private Image imageNextRoom;
    private List<Coordinate> waterCoordinate;

    private boolean mustReactivateButtons;
    private ColorMatrixZone colorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, Location location) {
        if (shouldInitialize) {
            shouldInitialize = false;
            init(game);
        }

        musicManager.clear();
        musicManager.plan(100, "world_map_intro", false);
        musicManager.plan(0, "world_map_loop", true);

        currentAbscissa = location.x;
        currentOrdinate = location.y;
        nextAbscissa = currentAbscissa;
        nextOrdinate = currentOrdinate;
        currentMiniAbscissa = 16 * currentAbscissa;
        currentMiniOrdinate = 11 * currentOrdinate;

        transitionRunning = false;
        leftCurrentRoom = LocationUtil.LEFT_MAP;
        topCurrentRoom = LocationUtil.TOP_MAP;
        imageCurrentRoom = imagesWorldMap.get(getCoordinate());
        leftNextRoom = LocationUtil.LEFT_MAP;
        topNextRoom = LocationUtil.TOP_MAP;
        imageNextRoom = imagesWorldMap.get("empty");

        mustReactivateButtons = true;
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        linkManager = ((MainActivity) game).getLinkManager();
        worldMapEnemyManager = ((MainActivity) game).getWorldMapEnemyManager();
        musicManager = ((MainActivity) game).getMusicManager();
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();
        saveManager = ((MainActivity) game).getSaveManager();

        imagesWorldMap = ((MainActivity) game).getAllImages().getImagesWorldMap();
        imagesWorldMap.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        MapTile.initHashMap();
        initWorldMap(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "other/world_map.txt"));
        initWorldMapCaves(FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/world_map_entrance.properties"));
        waterCoordinate = new ArrayList<>();

        colorMatrix = new ColorMatrixZone();
    }

    /**
     * Create all the MapRoom objects from the world_map file
     */
    private void initWorldMap(List<String> worldMapFileContent) {
        worldMap = new MapRoom[16][8];
        exploredRooms = new Boolean[16][8];
        Boolean[][] savedExploredRooms = saveManager.getSave().getWorldMapSave().getExploredRooms();

        // Initialise the mapRooms
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                worldMap[i][j] = (new MapRoom());
                exploredRooms[i][j] = savedExploredRooms[i][j];
            }
        }

        // Fill the mapRooms, line by line
        int indexLine = 0;
        for (int index1 = 0; index1 < 8; index1 = index1) {
            String line = worldMapFileContent.get(indexLine++);
            for (int index2 = 0; index2 < 16; index2++) {
                worldMap[index2][index1].addALine(line.substring(17 * index2, 17 * index2 + 16));
            }
            // Jump over the delimiter line and go to next line of mapRooms
            if (indexLine % 12 == 11) {
                indexLine++;
                index1++;
            }
        }
    }

    /**
     * Initiate all the caves from the world_map_caves file
     */
    private void initWorldMapCaves(Properties entranceProperties) {
        worldMapEntrances = new EntranceInfo[16][8];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                String key = String.valueOf(i) + j;
                if (entranceProperties.containsKey(key) && entranceProperties.getProperty(key).length() > 0) {
                    String[] entranceArray = entranceProperties.getProperty(key).split("\\|");
                    if ("DUNGEON".equals(entranceArray[0])) {
                        DungeonInfo dungeonInfo = new DungeonInfo();
                        dungeonInfo.hiddenStyle = EntranceInfo.getStyle(entranceArray[1]);
                        dungeonInfo.style = EntranceInfo.getStyle(entranceArray[2]);
                        dungeonInfo.hidden = (dungeonInfo.hiddenStyle != dungeonInfo.style);
                        dungeonInfo.entranceLocationOnTheWorldMap = new Location(i, j);
                        dungeonInfo.entranceCoordinateOnTheWorldMap = new Coordinate(entranceArray[3]);
                        dungeonInfo.exitCoordinateOnTheWorldMap = findExit(i, j, dungeonInfo);
                        dungeonInfo.hitbox.relocate(dungeonInfo.entranceCoordinateOnTheWorldMap.x, dungeonInfo.entranceCoordinateOnTheWorldMap.y);
                        dungeonInfo.dungeonId = entranceArray[4];
                        dungeonInfo.linkStartLocationInTheDungeon = new Location(entranceArray[5]);
                        dungeonInfo.linkStartCoordinateInTheDungeon = new Coordinate(LocationUtil.getXFromGrid(7) + LocationUtil.HALF_TILE_SIZE, LocationUtil.getYFromGrid(9));
                        dungeonInfo.startMusic = true;
                        worldMapEntrances[i][j] = dungeonInfo;
                    } else {
                        CaveInfo caveInfo = new CaveInfo();
                        caveInfo.type = CaveType.valueOf(entranceArray[0]);
                        caveInfo.hiddenStyle = EntranceInfo.getStyle(entranceArray[1]);
                        caveInfo.style = EntranceInfo.getStyle(entranceArray[2]);
                        caveInfo.hidden = (caveInfo.hiddenStyle != caveInfo.style);
                        caveInfo.entranceLocationOnTheWorldMap = new Location(i, j);
                        caveInfo.entranceCoordinateOnTheWorldMap = new Coordinate(entranceArray[3]);
                        caveInfo.exitCoordinateOnTheWorldMap = findExit(i, j, caveInfo);
                        caveInfo.hitbox.relocate(caveInfo.entranceCoordinateOnTheWorldMap.x, caveInfo.entranceCoordinateOnTheWorldMap.y);
                        caveInfo.message1 = entranceArray[4];
                        caveInfo.message2 = entranceArray[5];
                        caveInfo.message3 = "";
                        caveInfo.npcName = (entranceArray[6].length() > 0) ? entranceArray[6] : CaveInfo.DEFAULT_NPC;
                        for (int index = 7; index < entranceArray.length; index++) {
                            caveInfo.itemsAndPrices.add(entranceArray[index]);
                        }
                        worldMapEntrances[i][j] = caveInfo;
                    }
                } else {
                    worldMapEntrances[i][j] = new CaveInfo();
                }
            }
        }

        Boolean[][] savedOpenedEntrances = saveManager.getSave().getWorldMapSave().getOpenedEntrances();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                // Update the info if the entrance has already been opened
                if (savedOpenedEntrances[i][j]) {
                    worldMapEntrances[i][j].hidden = false;
                    int tileX = LocationUtil.getTileXFromPositionX(worldMapEntrances[i][j].entranceCoordinateOnTheWorldMap.x);
                    int tileY = LocationUtil.getTileYFromPositionY(worldMapEntrances[i][j].entranceCoordinateOnTheWorldMap.y);
                    MapTile mapTile = (worldMapEntrances[i][j].style == EntranceInfo.STAIRS) ? MapTile.STAIRS : MapTile.DOOR;
                    worldMap[i][j].changeTile(tileX, tileY, mapTile);
                }
            }
        }
    }

    /**
     * Find the exit for the cave.
     */
    private Coordinate findExit(int i, int j, EntranceInfo entranceInfo) {
        if (entranceInfo.style == EntranceInfo.DOOR) {
            return new Coordinate(entranceInfo.entranceCoordinateOnTheWorldMap.x, entranceInfo.entranceCoordinateOnTheWorldMap.y + LocationUtil.TILE_SIZE + 1);
        }
        int tileX = LocationUtil.getTileXFromPositionX(entranceInfo.entranceCoordinateOnTheWorldMap.x);
        int tileY = LocationUtil.getTileYFromPositionY(entranceInfo.entranceCoordinateOnTheWorldMap.y);
        MapRoom mapRoom = worldMap[i][j];
        if (mapRoom.getTile(tileX - 1, tileY).walkable) {
            return entranceInfo.exitCoordinateOnTheWorldMap = new Coordinate(entranceInfo.entranceCoordinateOnTheWorldMap.x - LocationUtil.TILE_SIZE, entranceInfo.entranceCoordinateOnTheWorldMap.y);
        }
        if (mapRoom.getTile(tileX - 1, tileY + 1).walkable) {
            return entranceInfo.exitCoordinateOnTheWorldMap = new Coordinate(entranceInfo.entranceCoordinateOnTheWorldMap.x - LocationUtil.TILE_SIZE, entranceInfo.entranceCoordinateOnTheWorldMap.y + LocationUtil.TILE_SIZE);
        }
        return entranceInfo.exitCoordinateOnTheWorldMap = new Coordinate(entranceInfo.entranceCoordinateOnTheWorldMap.x - LocationUtil.TILE_SIZE, entranceInfo.entranceCoordinateOnTheWorldMap.y - LocationUtil.TILE_SIZE);
    }

    @Override
    public void update(float deltaTime, Graphics g) {
        if (mustReactivateButtons) {
            mustReactivateButtons = false;
            guiManager.activateButtons();
        }
        if (transitionRunning) {
            if (transitionOrientation == Orientation.UP) {
                float transitionDeltaY = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                topCurrentRoom += transitionDeltaY;
                topNextRoom += transitionDeltaY;
                currentMiniOrdinate -= transitionDeltaY * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkY(transitionDeltaY, false);
            }
            if (transitionOrientation == Orientation.DOWN) {
                float transitionDeltaY = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                topCurrentRoom -= transitionDeltaY;
                topNextRoom -= transitionDeltaY;
                currentMiniOrdinate += transitionDeltaY * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkY(-1 * transitionDeltaY, false);
            }
            if (transitionOrientation == Orientation.LEFT) {
                float transitionDeltaX = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                leftCurrentRoom += transitionDeltaX;
                leftNextRoom += transitionDeltaX;
                currentMiniAbscissa -= transitionDeltaX * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkX(transitionDeltaX, false);
            }
            if (transitionOrientation == Orientation.RIGHT) {
                float transitionDeltaX = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                leftCurrentRoom -= transitionDeltaX;
                leftNextRoom -= transitionDeltaX;
                currentMiniAbscissa += transitionDeltaX * 16 / LocationUtil.WIDTH_MAP;
                linkManager.moveLinkX(-1 * transitionDeltaX, false);
            }
            transitionCount -= TRANSITION_SPEED * deltaTime;

            if (transitionCount < 0) {
                // End of the transition
                imageCurrentRoom = imageNextRoom;
                leftCurrentRoom = LocationUtil.LEFT_MAP;
                topCurrentRoom = LocationUtil.TOP_MAP;
                currentAbscissa = nextAbscissa;
                currentOrdinate = nextOrdinate;
                currentMiniAbscissa = 16 * currentAbscissa;
                currentMiniOrdinate = 11 * currentOrdinate;
                imageNextRoom = imagesWorldMap.get("empty");
                transitionRunning = false;
                listWaterCoordinates();
                worldMapEnemyManager.spawnEnemies();
                guiManager.activateButtons();
            }
        }
        colorMatrix.update(deltaTime);
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        if (transitionRunning) {
            g.drawScaledImage(imageNextRoom, (int) leftNextRoom, (int) topNextRoom, AllImages.COEF);
        }
        g.drawScaledImage(imageCurrentRoom, (int) leftCurrentRoom, (int) topCurrentRoom, AllImages.COEF, colorMatrix.getMatrix());

        EntranceInfo entranceInfo = worldMapEntrances[currentAbscissa][currentOrdinate];
        if (!entranceInfo.hidden) {
            float x = entranceInfo.entranceCoordinateOnTheWorldMap.x + leftCurrentRoom - LocationUtil.LEFT_MAP;
            float y = entranceInfo.entranceCoordinateOnTheWorldMap.y + topCurrentRoom - LocationUtil.TOP_MAP;
            if (entranceInfo.style == EntranceInfo.DOOR) {
                g.drawScaledImage(imagesWorldMap.get("door"), (int) x, (int) y, AllImages.COEF);
            } else if (entranceInfo.style == EntranceInfo.STAIRS) {
                // +2 to hide the bush behind
                g.drawScaledImage(imagesWorldMap.get("stairs"), (int) x, (int) y + 2, AllImages.COEF);
            }
        }
    }

    @Override
    public void changeRoom(Orientation orientation) {
        guiManager.deactivateButtons();
        worldMapEnemyManager.unloadEnemies();
        switch (orientation) {
            case UP :
                transitionCount = 176 * AllImages.COEF;
                leftNextRoom = LocationUtil.LEFT_MAP;
                topNextRoom = LocationUtil.TOP_MAP - LocationUtil.HEIGHT_MAP;
                nextAbscissa = currentAbscissa;
                nextOrdinate = currentOrdinate - 1;
                break;
            case DOWN :
                transitionCount = 176 * AllImages.COEF;
                leftNextRoom = LocationUtil.LEFT_MAP;
                topNextRoom = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
                nextAbscissa = currentAbscissa;
                nextOrdinate = currentOrdinate + 1;
                break;
            case LEFT :
                transitionCount = 256 * AllImages.COEF;
                leftNextRoom = LocationUtil.LEFT_MAP - LocationUtil.WIDTH_MAP;
                topNextRoom = LocationUtil.TOP_MAP;
                nextAbscissa = currentAbscissa - 1;
                nextOrdinate = currentOrdinate;
                break;
            case RIGHT :
                transitionCount = 256 * AllImages.COEF;
                leftNextRoom = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
                topNextRoom = LocationUtil.TOP_MAP;
                nextAbscissa = currentAbscissa + 1;
                nextOrdinate = currentOrdinate;
                break;
        }
        imageNextRoom = imagesWorldMap.get(String.valueOf(nextAbscissa) + nextOrdinate);
        Logger.info("Starting room transition to " + nextAbscissa + nextOrdinate);
        exploredRooms[nextAbscissa][nextOrdinate] = true;
        saveManager.updateWorldMapExploredRooms(nextAbscissa, nextOrdinate);
        transitionRunning = true;
        transitionOrientation = orientation;
    }

    @Override
    public boolean isTileADoor(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        return currentMapRoom.getTile(tileX, tileY) == MapTile.DOOR;
    }

    @Override
    public boolean isTileStairs(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        return currentMapRoom.getTile(tileX, tileY) == MapTile.STAIRS;
    }

    @Override
    public boolean isTileABombHole(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileWalkable(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        MapTile tile = currentMapRoom.getTile(tileX, tileY);
        return tile.walkable;
    }

    @Override
    public boolean isTileBlockingMissile(float x, float y) {
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        MapTile tile = currentMapRoom.getTile(tileX, tileY);
        return tile.isblockingMissile;
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

    /**
     * Find all the water coordinates in this room
     */
    private void listWaterCoordinates() {
        waterCoordinate.clear();
        MapRoom currentMapRoom = worldMap[currentAbscissa][currentOrdinate];
        for (int i = 1; i < 15; i++) {
            for(int j = 1; j < 10; j++) {
                if (currentMapRoom.getTile(i, j) == MapTile.WATER) {
                    waterCoordinate.add(new Coordinate(LocationUtil.getXFromGrid(i), LocationUtil.getYFromGrid(j)));
                }
            }
        }
    }

    /**
     * Find a tile where a enemy can spawn avoid map borders
     */
    public Coordinate findSpawnableCoordinate() {
        // A counter to avoid infinite loop.
        int counter = 50;
        float x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
        float y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
        Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        while (counter-- > 0 && !isTileWalkable(x, y)) {
            x = (float) (Math.floor(1 + Math.random() * 14) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
            y = (float) (Math.floor(1 + Math.random() * 9) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
            Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        }
        return new Coordinate(x, y);
    }

    @Override
    public Coordinate findSpawnableCoordinateInWater() {
        return waterCoordinate.isEmpty() ? new Coordinate() : waterCoordinate.get((int) (Math.random() * waterCoordinate.size()));
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case BLOC_TOP_LOWER:
            case TREE_LEFT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                if (tileDownLeft == MapTile.BLOC_TOP_UPPER) {
                    return false;
                }
                break;
            case TREE_RIGHT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case BLOC_TOP_UPPER:
            case TREE_RIGHT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_LOWER:
                if (deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case TREE_LEFT:
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_UPPER:
            case TREE_LEFT:
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
            case TREE_RIGHT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_LOWER:
            case TREE_RIGHT:
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
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case TREE_LEFT:
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case TREE_LEFT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_TOP_UPPER:
            case TREE_RIGHT:
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
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_TOP_UPPER:
            case BLOC_TOP_LOWER:
            case BLOC_BOT_UPPER:
            case TREE_LEFT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case BLOC_BOT_LOWER:
                if ((deltaY > LocationUtil.OBSTACLE_TOLERANCE && deltaX < LocationUtil.HALF_TILE_SIZE) || deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case TREE_RIGHT:
                if (deltaY > LocationUtil.OBSTACLE_TOLERANCE && deltaX < LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
            case TOMB:
            case BLOC_BOT_UPPER:
            case BLOC_BOT_LOWER:
            case TREE_RIGHT:
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
            case TREE_LEFT:
                if (deltaX > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case DOOR:
            case STAIRS:
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
            case ARMOS:
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
            case TREE_LEFT:
                if (deltaX > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case TREE_RIGHT:
            case PATH:
            case DOOR:
            case STAIRS:
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
    public List<Item> getItems() {
        return new ArrayList<>();
    }

    @Override
    public void linkHasPickedItem(Item item) {
        item.hideItem();
    }

    @Override
    public boolean isExplored(int x, int y) {
        return exploredRooms[x][y];
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
        return imagesWorldMap.get("mini_world_map");
    }

    @Override
    public float getCurrentMiniAbscissa() {
        return currentMiniAbscissa;
    }

    @Override
    public float getCurrentMiniOrdinate() {
        return currentMiniOrdinate;
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
    public void bombHasExploded(Bomb bomb) {
        colorMatrix.activate();
        openHiddenEntrance(bomb.hitbox, EntranceInfo.WALL);
    }

    @Override
    public void fireHasJustFinished(Fire fire) {
        openHiddenEntrance(fire.hitbox, EntranceInfo.BUSH);
    }

    public void openHiddenEntrance(Hitbox hitox, int entranceType) {
        EntranceInfo entranceInfo = worldMapEntrances[currentAbscissa][currentOrdinate];
        if (entranceInfo.hidden && entranceInfo.hiddenStyle == entranceType) {
            if (LocationUtil.areColliding(entranceInfo.hitbox, hitox)) {
                soundEffectManager.play("find_secret");
                // Enable walking on the tile
                MapTile mapTile = (entranceInfo.style == EntranceInfo.STAIRS) ? MapTile.STAIRS : MapTile.DOOR;
                int tileX = LocationUtil.getTileXFromPositionX(entranceInfo.entranceCoordinateOnTheWorldMap.x);
                int tileY = LocationUtil.getTileYFromPositionY(entranceInfo.entranceCoordinateOnTheWorldMap.y);
                worldMap[currentAbscissa][currentOrdinate].changeTile(tileX, tileY, mapTile);
                // Add the entrance in the list of the entrances opened
                entranceInfo.hidden = false;
                saveManager.updateOpenedEntrances(currentAbscissa, currentOrdinate);
            }
        }
    }

    /**
     * Obtain the current mapRoom coordinate
     */
    public String getCoordinate() {
        return String.valueOf(currentAbscissa) + currentOrdinate;
    }

    /**
     * Obtain the cave information on the current mapRoom
     */
    public EntranceInfo getCave() {
        return worldMapEntrances[currentAbscissa][currentOrdinate];
    }

}
