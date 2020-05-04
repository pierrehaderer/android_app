package com.twoplayers.legend.dungeon;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.assets.save.SaveManager;
import com.twoplayers.legend.assets.sound.SoundEffectManager;
import com.twoplayers.legend.character.link.inventory.bomb.Bomb;
import com.twoplayers.legend.character.link.inventory.light.Fire;
import com.twoplayers.legend.util.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesDungeon;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.enemy.dungeon.DungeonEnemyManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.map.DungeonInfo;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;
import com.twoplayers.legend.util.ColorMatrixZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DungeonManager implements IZoneManager {

    private static final float INITIAL_IMMOBILISATION_COUNTER = 50f;
    private static final float TRANSITION_SPEED = 4.0f;
    private static final float INITIAL_BLINK_COUNTER = 20f;

    private ImagesDungeon imagesDungeon;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private DungeonEnemyManager dungeonEnemyManager;
    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;
    private SaveManager saveManager;

    /** dungeonRooms that represent the whole dungeon in this game */
    private DungeonRoom[][] dungeonRooms;
    private Boolean[][] exploredRooms;
    private Map<DungeonDoorPlacement, DungeonDoor>[][] dungeonDoors;

    private boolean shouldInitialize = true;
    private float stunCounter;
    private Dungeon dungeon;
    private boolean hasExitedZone;

    private int currentAbscissa;
    private int currentOrdinate;
    private int nextAbscissa;
    private int nextOrdinate;
    private float currentMiniAbscissa;
    private float currentMiniOrdinate;

    private boolean transitionRunning;
    private float transitionCount;
    private float transitionSteps;
    private Orientation transitionOrientation;
    private float leftCurrentRoom;
    private float topCurrentRoom;
    private Image imageCurrentRoom;
    private float leftNextRoom;
    private float topNextRoom;
    private Image imageNextRoom;
    private DoorCache currentDoorCache;
    private DoorCache nextDoorCache;

    private ColorMatrixZone colorMatrix;

    /**
     * Load this manager
     */
    public void load(Game game, DungeonInfo dungeonInfo) {
        if (shouldInitialize) {
            shouldInitialize = false;
            init(game);
        }
        stunCounter = INITIAL_IMMOBILISATION_COUNTER;

        dungeon = new Dungeon(dungeonInfo);
        Location start = dungeonInfo.startLocation;
        hasExitedZone = false;

        saveManager.updateDungeonExploredRooms(dungeon.id, start.x, start.y);
        initDungeon(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon" + dungeon.id + ".txt"));
        initDungeonDoors(FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon" + dungeon.id + "_doors.properties"));

        musicManager.clear();
        musicManager.plan(100, "dungeon_loop", true);

        currentAbscissa = start.x;
        currentOrdinate = start.y;
        nextAbscissa = currentAbscissa;
        nextOrdinate = currentOrdinate;
        currentMiniAbscissa = 16 * currentAbscissa;
        currentMiniOrdinate = 11 * currentOrdinate;

        transitionRunning = false;
        leftCurrentRoom = LocationUtil.LEFT_MAP;
        topCurrentRoom = LocationUtil.TOP_MAP;
        imageCurrentRoom = imagesDungeon.get(getCoordinate());
        currentDoorCache = new DoorCache(imagesDungeon);
        updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
        leftNextRoom = LocationUtil.LEFT_MAP;
        topNextRoom = LocationUtil.TOP_MAP;
        imageNextRoom = imagesDungeon.get("empty");
        nextDoorCache = new DoorCache(imagesDungeon);
    }

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        guiManager = ((MainActivity) game).getGuiManager();
        linkManager = ((MainActivity) game).getLinkManager();
        dungeonEnemyManager = ((MainActivity) game).getDungeonEnemyManager();
        musicManager = ((MainActivity) game).getMusicManager();
        soundEffectManager = ((MainActivity) game).getSoundEffectManager();
        saveManager = ((MainActivity) game).getSaveManager();

        imagesDungeon = ((MainActivity) game).getAllImages().getImagesDungeon();
        imagesDungeon.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        DungeonTile.initHashMap();

        colorMatrix = new ColorMatrixZone();
    }

    /**
     * Create all the DungeonRoom objects from the world_map file
     */
    private void initDungeon(List<String> dungeonFileContent) {
        dungeonRooms = new DungeonRoom[8][8];
        exploredRooms = new Boolean[8][8];
        Boolean[][] savedExploredRooms = saveManager.getSave().getDungeonSave(dungeon.id).getExploredRooms();

        // Initialise the mapRooms
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                dungeonRooms[i][j] = new DungeonRoom();
                exploredRooms[i][j] = savedExploredRooms[i][j];
            }
        }

        // Fill the mapRooms, line by line
        int indexLine = 0;
        for (int index1 = 0; index1 < 8; index1 = index1) {
            String line = dungeonFileContent.get(indexLine++);
            for (int index2 = 0; index2 < 8; index2++) {
                dungeonRooms[index2][index1].addALine(line.substring(17 * index2, 17 * index2 + 16));
            }
            // Jump over the delimiter line and go to next line of mapRooms
            if (indexLine % 12 == 11) {
                indexLine++;
                index1++;
            }
        }
    }

    /**
     * Init the doors of the dungeon
     */
    private void initDungeonDoors(Properties doorsProperties) {
        dungeonDoors = new Map[8][8];
        List<String> openedDoors = saveManager.getSave().getDungeonSave(dungeon.id).getOpenedDoors();

        // Init dungeonDoors
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                dungeonDoors[i][j] = new HashMap<>();
            }
        }

        // Parse the properties to add all the doors of this dungeon
        for (String key : doorsProperties.stringPropertyNames()) {
            String doorProperty = ((String) doorsProperties.get(key)).trim();
            if (doorProperty.length() > 0) {
                int abscissa = Integer.parseInt(key.substring(1,2));
                int ordinate = Integer.parseInt(key.substring(2,3));
                String[] dungeonDoorsAsString = doorProperty.split("\\|");
                for (String dungeonDoorAsString : dungeonDoorsAsString) {
                    String[] dungeonDoorInfo = dungeonDoorAsString.split(";");
                    DungeonDoorPlacement placement = DungeonDoorPlacement.valueOf(dungeonDoorInfo[0]);
                    DungeonDoorType type = DungeonDoorType.valueOf(dungeonDoorInfo[1]);
                    Logger.info("Adding door (" + abscissa + "," + ordinate + ") : " + dungeonDoorAsString);
                    Location location = (type == DungeonDoorType.PUSH) ? new Location(dungeonDoorInfo[2]) : new Location();
                    DungeonDoor dungeonDoor = new DungeonDoor(placement, type, location);
                    if (openedDoors.contains(generateOpenedDoorKey(abscissa, ordinate, dungeonDoorInfo[0]))) {
                        // Open the door, it has already been opened
                        dungeonDoor.isOpen = true;
                    } else {
                        // Make the door not walkable
                        updateDungeonRoomsWithDoorClosed(abscissa, ordinate, placement, type);
                    }
                    dungeonDoors[abscissa][ordinate].put(placement, dungeonDoor);
                }
            }
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
        if (transitionRunning) {
            if (transitionSteps > 2 * LocationUtil.TILE_SIZE) {
                // This transition is through a bomb hole so first, link is moving into the hole
                float transitionStep = Math.min(deltaTime * Link.SPEED, transitionSteps - 2 * LocationUtil.TILE_SIZE);
                transitionSteps -= transitionStep;
                switch (transitionOrientation) {
                    case UP:
                        linkManager.moveLinkY(-1 * transitionStep, true);
                        break;
                    case DOWN:
                        linkManager.moveLinkY(transitionStep, true);
                        break;
                    case LEFT:
                        linkManager.moveLinkX(-1 * transitionStep, true);
                        break;
                    case RIGHT:
                        linkManager.moveLinkX(transitionStep, true);
                        break;
                }
            } else if (transitionCount > 0) {
                float transitionDelta = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                transitionCount -= transitionDelta;
                switch (transitionOrientation) {
                    case UP:
                        topCurrentRoom += transitionDelta;
                        topNextRoom += transitionDelta;
                        currentMiniOrdinate -= transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkY(transitionDelta, false);
                        break;
                    case DOWN:
                        topCurrentRoom -= transitionDelta;
                        topNextRoom -= transitionDelta;
                        currentMiniOrdinate += transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkY(-1 * transitionDelta, false);
                    break;
                    case LEFT:
                        leftCurrentRoom += transitionDelta;
                        leftNextRoom += transitionDelta;
                        currentMiniAbscissa -= transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkX(transitionDelta, false);
                    break;
                    case RIGHT:
                        leftCurrentRoom -= transitionDelta;
                        leftNextRoom -= transitionDelta;
                        currentMiniAbscissa += transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkX(-1 * transitionDelta, false);
                    break;
                }
            } else if (transitionSteps > 0) {
                // After room transition, link is entering one tile in the door
                float transitionStep = Math.min(deltaTime * Link.SPEED, transitionSteps);
                transitionSteps -= transitionStep;
                switch (transitionOrientation) {
                    case UP:
                        linkManager.moveLinkY(-1 * transitionStep, true);
                        break;
                    case DOWN:
                        linkManager.moveLinkY(transitionStep, true);
                        break;
                    case LEFT:
                        linkManager.moveLinkX(-1 * transitionStep, true);
                        break;
                    case RIGHT:
                        linkManager.moveLinkX(transitionStep, true);
                        break;
                }
            } else {
                // End of the transition
                imageCurrentRoom = imageNextRoom;
                leftCurrentRoom = LocationUtil.LEFT_MAP;
                topCurrentRoom = LocationUtil.TOP_MAP;
                currentAbscissa = nextAbscissa;
                currentOrdinate = nextOrdinate;
                updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
                currentMiniAbscissa = 16 * currentAbscissa;
                currentMiniOrdinate = 11 * currentOrdinate;
                imageNextRoom = imagesDungeon.get("empty");
                transitionRunning = false;
                dungeonEnemyManager.spawnEnemies();
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
        paintDoors(g);
    }

    /**
     * Paint the doors of the dungeon
     */
    private void paintDoors( Graphics g) {
        if (transitionRunning) {
            for (DungeonDoor dungeonDoor : dungeonDoors[nextAbscissa][nextOrdinate].values()) {
                if (dungeonDoor.type == DungeonDoorType.BOMB) {
                    if (dungeonDoor.isOpen) {
                        Image doorImage = imagesDungeon.get("bomb_hole_" + dungeonDoor.placement.imagePrefix);
                        g.drawImage(doorImage, (int) (leftNextRoom + dungeonDoor.placement.x), (int) (topNextRoom + dungeonDoor.placement.y), colorMatrix.getMatrix());
                    }
                } else {
                    if (!dungeonDoor.isOpen) {
                        Image doorImage = imagesDungeon.get(dungeonDoor.type.imagePrefix + "_" + dungeonDoor.placement.imagePrefix + "_" + dungeon.id);
                        g.drawImage(doorImage, (int) (leftNextRoom + dungeonDoor.placement.x), (int) (topNextRoom + dungeonDoor.placement.y), colorMatrix.getMatrix());
                    }
                }
            }
        }
        for (DungeonDoor dungeonDoor : dungeonDoors[currentAbscissa][currentOrdinate].values()) {
            if (dungeonDoor.type == DungeonDoorType.BOMB) {
                if (dungeonDoor.isOpen) {
                    Image doorImage = imagesDungeon.get("bomb_hole_" + dungeonDoor.placement.imagePrefix);
                    g.drawImage(doorImage, (int) (leftCurrentRoom + dungeonDoor.placement.x), (int) (topCurrentRoom + dungeonDoor.placement.y), colorMatrix.getMatrix());
                }
            } else {
                if (!dungeonDoor.isOpen) {
                    Image doorImage = imagesDungeon.get(dungeonDoor.type.imagePrefix + "_" + dungeonDoor.placement.imagePrefix + "_" + dungeon.id);
                    g.drawImage(doorImage, (int) (leftCurrentRoom + dungeonDoor.placement.x), (int) (topCurrentRoom + dungeonDoor.placement.y), colorMatrix.getMatrix());
                }
            }
        }
    }

    /**
     * Paint the doors cache after link to hide ihim when he goes through the doors
     */
    public void paintDoorCache(Graphics g) {
        if (transitionRunning) {
            g.drawScaledImage(nextDoorCache.up, (int) leftNextRoom, (int) topNextRoom, AllImages.COEF);
            g.drawScaledImage(nextDoorCache.down, (int) leftNextRoom, (int) (topNextRoom + LocationUtil.HEIGHT_MAP - 24 * AllImages.COEF), AllImages.COEF);
            g.drawScaledImage(nextDoorCache.left, (int) leftNextRoom, (int) topNextRoom, AllImages.COEF);
            g.drawScaledImage(nextDoorCache.right, (int) (leftNextRoom + LocationUtil.WIDTH_MAP - 24 * AllImages.COEF), (int) topNextRoom, AllImages.COEF);
        }
        g.drawScaledImage(currentDoorCache.up, (int) leftCurrentRoom, (int) topCurrentRoom, AllImages.COEF, colorMatrix.getMatrix());
        g.drawScaledImage(currentDoorCache.down, (int) leftCurrentRoom, (int) (topCurrentRoom + LocationUtil.HEIGHT_MAP - 24 * AllImages.COEF), AllImages.COEF, colorMatrix.getMatrix());
        g.drawScaledImage(currentDoorCache.left, (int) leftCurrentRoom, (int) topCurrentRoom, AllImages.COEF, colorMatrix.getMatrix());
        g.drawScaledImage(currentDoorCache.right, (int) (leftCurrentRoom + LocationUtil.WIDTH_MAP - 24 * AllImages.COEF), (int) topCurrentRoom, AllImages.COEF, colorMatrix.getMatrix());
    }

    @Override
    public void changeRoom(Orientation orientation) {
        guiManager.deactivateButtons();
        dungeonEnemyManager.unloadEnemies();
        if (currentOrdinate == 7 && orientation == Orientation.DOWN) {
            Logger.info("Link has exited the dungeon.");
            linkManager.exitZone();
            hasExitedZone = true;
        } else {
            switch (orientation) {
                case UP:
                    transitionCount = 176 * AllImages.COEF;
                    leftNextRoom = LocationUtil.LEFT_MAP;
                    topNextRoom = LocationUtil.TOP_MAP - LocationUtil.HEIGHT_MAP;
                    nextAbscissa = currentAbscissa;
                    nextOrdinate = currentOrdinate - 1;
                    break;
                case DOWN:
                    transitionCount = 176 * AllImages.COEF;
                    leftNextRoom = LocationUtil.LEFT_MAP;
                    topNextRoom = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
                    nextAbscissa = currentAbscissa;
                    nextOrdinate = currentOrdinate + 1;
                    break;
                case LEFT:
                    transitionCount = 256 * AllImages.COEF;
                    leftNextRoom = LocationUtil.LEFT_MAP - LocationUtil.WIDTH_MAP;
                    topNextRoom = LocationUtil.TOP_MAP;
                    nextAbscissa = currentAbscissa - 1;
                    nextOrdinate = currentOrdinate;
                    break;
                case RIGHT:
                    transitionCount = 256 * AllImages.COEF;
                    leftNextRoom = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
                    topNextRoom = LocationUtil.TOP_MAP;
                    nextAbscissa = currentAbscissa + 1;
                    nextOrdinate = currentOrdinate;
                    break;
            }
            imageNextRoom = imagesDungeon.get(dungeon.id + String.valueOf(nextAbscissa) + nextOrdinate);
            updateDoorCache(nextDoorCache, nextAbscissa, nextOrdinate);
            Logger.info("Starting room transition to " + nextAbscissa + nextOrdinate);
            exploredRooms[nextAbscissa][nextOrdinate] = true;
            saveManager.updateDungeonExploredRooms(dungeon.id, nextAbscissa, nextOrdinate);
            transitionRunning = true;
            transitionOrientation = orientation;
            DungeonDoor dungeonDoor = dungeonDoors[currentAbscissa][currentOrdinate].get(DungeonDoorPlacement.valueOf(orientation.name()));
            transitionSteps = (dungeonDoor != null && dungeonDoor.type == DungeonDoorType.BOMB) ? 4 * LocationUtil.TILE_SIZE : LocationUtil.TILE_SIZE;
        }
    }

    @Override
    public boolean isTileWalkable(float x, float y) {
        DungeonRoom currentMapRoom = dungeonRooms[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        DungeonTile tile = currentMapRoom.getTile(tileX, tileY);
        return tile.walkable;
    }

    @Override
    public boolean isTileBlockingMissile(float x, float y) {
        return false;
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
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        DungeonTile tile = dungeonRooms[currentAbscissa][currentOrdinate].getTile(tileX, tileY);
        return tile == DungeonTile.BOMB_HOLE || tile == DungeonTile.BOMB_HOLE_LEFT || tile == DungeonTile.BOMB_HOLE_RIGHT;

    }

    @Override
    public boolean checkKeyDoor(Orientation orientation, float x, float y) {
        DungeonDoor dungeonDoor = dungeonDoors[currentAbscissa][currentOrdinate].get(DungeonDoorPlacement.valueOf(orientation.name()));
        // If the door is not special or if the door is not a key door or if it has already been opened
        if (dungeonDoor == null || dungeonDoor.type != DungeonDoorType.KEY || dungeonDoor.isOpen) {
            return false;
        }
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        float deltaX = LocationUtil.getDeltaX(x);
        float deltaY = LocationUtil.getDeltaY(y);
        switch (orientation) {
            case UP:
                return tileX == 7 && tileY == 1 && deltaX > LocationUtil.HALF_TILE_SIZE && deltaY > LocationUtil.HALF_TILE_SIZE;
            case DOWN:
                return tileX == 7 && tileY == 8 && deltaX > LocationUtil.HALF_TILE_SIZE;
            case LEFT:
                return tileX == 1 && tileY == 5 && deltaY < LocationUtil.HALF_TILE_SIZE;
            case RIGHT:
                return tileX == 13 && tileY == 5 && deltaY < LocationUtil.HALF_TILE_SIZE;
        }
        return false;
    }

    @Override
    public void openKeyDoor(Orientation orientation) {
        Location nextRoom = LocationUtil.findNextRoomLocation(currentAbscissa, currentOrdinate, orientation);
        DungeonDoorPlacement placement = DungeonDoorPlacement.valueOf(orientation.name());
        DungeonDoorPlacement oppositePlacement = placement.reversePlacement();
        updateDungeonRoomsWithDoorOpen(currentAbscissa, currentOrdinate, placement, DungeonDoorType.KEY);
        updateDungeonRoomsWithDoorOpen(nextRoom.x, nextRoom.y, oppositePlacement, DungeonDoorType.KEY);
        dungeonDoors[currentAbscissa][currentOrdinate].get(placement).isOpen = true;
        dungeonDoors[nextRoom.x][nextRoom.y].get(oppositePlacement).isOpen = true;
        String openedDoorKey1 = generateOpenedDoorKey(currentAbscissa, currentOrdinate, placement.name());
        String openedDoorKey2 = generateOpenedDoorKey(nextRoom.x, nextRoom.y, oppositePlacement.name());
        saveManager.updateOpenedDoors(dungeon.id, openedDoorKey1, openedDoorKey2);
    }

    @Override
    public Coordinate findSpawnableCoordinate() {
        // A counter to avoid infinite loop.
        int counter = 50;
        float x = (float) (Math.floor(2 + Math.random() * 12) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
        float y = (float) (Math.floor(2 + Math.random() * 7) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
        Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        while (counter-- > 0 && !isTileWalkable(x, y)) {
            x = (float) (Math.floor(2 + Math.random() * 12) * LocationUtil.TILE_SIZE + LocationUtil.LEFT_MAP + 1);
            y = (float) (Math.floor(2 + Math.random() * 7) * LocationUtil.TILE_SIZE + LocationUtil.TOP_MAP + 1);
            Logger.info("Checking if (" + x + "," + y + ") is a spawnable coordinate.");
        }
        return new Coordinate(x, y);
    }

    @Override
    public Coordinate findSpawnableCoordinateInWater() {
        return new Coordinate();
    }

    /**
     * Update the door cache of the current room or the next one.
     */
    private void updateDoorCache(DoorCache doorCache, int abscissa, int ordinate) {
        DungeonRoom dungeonRoom = dungeonRooms[abscissa][ordinate];
        DungeonTile tileUp = dungeonRoom.getTile(7, 1);
        if (tileUp == DungeonTile.DOOR_LEFT || tileUp == DungeonTile.CLOSED_DOOR_LEFT) {
            doorCache.up = imagesDungeon.get("door_cache_up_" + dungeon.id);
        } else if (tileUp == DungeonTile.BOMB_HOLE_LEFT) {
            doorCache.up = imagesDungeon.get("bomb_cache_up_" + dungeon.id);
        } else {
            doorCache.up = imagesDungeon.get("empty");
        }
        DungeonTile tileDown = dungeonRoom.getTile(7, 9);
        if (tileDown == DungeonTile.DOOR_LEFT || tileDown == DungeonTile.CLOSED_DOOR_LEFT) {
            doorCache.down = imagesDungeon.get("door_cache_down_" + dungeon.id);
        } else if (tileDown == DungeonTile.BOMB_HOLE_LEFT) {
            doorCache.down = imagesDungeon.get("bomb_cache_down_" + dungeon.id);
        } else {
            doorCache.down = imagesDungeon.get("empty");
        }
        DungeonTile tileLeft = dungeonRoom.getTile(1, 5);
        if (tileLeft == DungeonTile.PATH || tileLeft == DungeonTile.CLOSED_DOOR) {
            doorCache.left = imagesDungeon.get("door_cache_left_" + dungeon.id);
        } else if (tileLeft == DungeonTile.BOMB_HOLE) {
            doorCache.left = imagesDungeon.get("bomb_cache_left_" + dungeon.id);
        } else {
            doorCache.left = imagesDungeon.get("empty");
        }
        DungeonTile tileRight = dungeonRoom.getTile(14, 5);
        if (tileRight == DungeonTile.PATH || tileRight == DungeonTile.CLOSED_DOOR) {
            doorCache.right = imagesDungeon.get("door_cache_right_" + dungeon.id);
        } else if (tileRight == DungeonTile.BOMB_HOLE) {
            doorCache.right = imagesDungeon.get("bomb_cache_right_" + dungeon.id);
        } else {
            doorCache.right = imagesDungeon.get("empty");
        }
    }

    @Override
    public boolean isUpValid(float x, float y) {
        DungeonRoom currentMapRoom = dungeonRooms[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        DungeonTile tileUpLeft = currentMapRoom.getTile(tileX, tileY);
        DungeonTile tileUpRight = currentMapRoom.getTile(tileX + 1, tileY);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpLeft) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_RIGHT:
            case BOMB_HOLE_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case BOMB_HOLE_LEFT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_UP:
            case PATH:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileUpRight) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_LEFT:
            case BOMB_HOLE_LEFT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_RIGHT:
            case BOMB_HOLE_RIGHT:
            case DOOR_UP:
            case PATH:
            case BOMB_HOLE:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isDownValid(float x, float y) {
        DungeonRoom currentMapRoom = dungeonRooms[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        DungeonTile tileDownLeft = currentMapRoom.getTile(tileX, tileY + 1);
        DungeonTile tileDownRight = currentMapRoom.getTile(tileX + 1, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileDownLeft) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_RIGHT:
            case BOMB_HOLE_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case BOMB_HOLE_LEFT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case BOMB_HOLE:
            case DOOR_UP:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_LEFT:
            case BOMB_HOLE_LEFT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_RIGHT:
            case BOMB_HOLE_RIGHT:
            case DOOR_UP:
            case PATH:
            case BOMB_HOLE:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isLeftValid(float x, float y) {
        DungeonRoom currentMapRoom = dungeonRooms[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        DungeonTile tileUpLeft = currentMapRoom.getTile(tileX, tileY);
        DungeonTile tileDownLeft = currentMapRoom.getTile(tileX, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpLeft) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_LEFT:
            case DOOR_RIGHT:
            case BOMB_HOLE_LEFT:
            case BOMB_HOLE_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case BOMB_HOLE:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownLeft) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case DOOR_RIGHT:
            case BOMB_HOLE_LEFT:
            case BOMB_HOLE_RIGHT:
                if (deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case BOMB_HOLE:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isRightValid(float x, float y) {
        DungeonRoom currentMapRoom = dungeonRooms[currentAbscissa][currentOrdinate];
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);

        DungeonTile tileUpRight = currentMapRoom.getTile(tileX + 1, tileY);
        DungeonTile tileDownRight = currentMapRoom.getTile(tileX + 1, tileY + 1);

        float deltaX = x - LocationUtil.getXFromGrid(tileX);
        float deltaY = y - LocationUtil.getYFromGrid(tileY);

        switch (tileUpRight) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
            case DOOR_LEFT:
            case DOOR_RIGHT:
            case BOMB_HOLE_LEFT:
            case BOMB_HOLE_RIGHT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case BOMB_HOLE:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case CLOSED_DOOR:
            case CLOSED_DOOR_LEFT:
            case CLOSED_DOOR_RIGHT:
            case WATER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case DOOR_RIGHT:
            case BOMB_HOLE_LEFT:
            case BOMB_HOLE_RIGHT:
                if (deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case BOMB_HOLE:
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
    public boolean isExplored(int x, int y) {
        if (3 < x && x < 12) {
            return exploredRooms[x - 4][y];
        }
        return true;
    }

    @Override
    public Image getMiniMap() {
        return imagesDungeon.get("mini_dungeon" + dungeon.id);
    }

    @Override
    public float getCurrentMiniAbscissa() {
        return currentMiniAbscissa + 64;
    }

    @Override
    public float getCurrentMiniOrdinate() {
        return currentMiniOrdinate;
    }

    @Override
    public boolean isLinkFarEnoughFromBorderToAttack(Link link) {
        return !LocationUtil.isTileAtBorder(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE)
            && !LocationUtil.isTileAtBorderMinusOne(link.x + LocationUtil.HALF_TILE_SIZE, link.y + LocationUtil.HALF_TILE_SIZE);
    }

    @Override
    public boolean upAndDownAuthorized(Link link) {
        return link.x > LocationUtil.LEFT_MAP + 6 * LocationUtil.QUARTER_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE
                && link.x < LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - 10 * LocationUtil.QUARTER_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE;
    }

    @Override
    public boolean leftAndRightAuthorized(Link link) {
        return link.y > LocationUtil.TOP_MAP + 5 * LocationUtil.QUARTER_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE
                && link.y < LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - 10 * LocationUtil.QUARTER_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE;
    }

    @Override
    public void fireHasJustFinished(Fire fire) {
    }

    @Override
    public void bombHasExploded(Bomb bomb) {
        colorMatrix.activate();
        for (DungeonDoor dungeonDoor : dungeonDoors[nextAbscissa][nextOrdinate].values()) {
            if (dungeonDoor.type == DungeonDoorType.BOMB && !dungeonDoor.isOpen
                && LocationUtil.areColliding(bomb.hitbox, dungeonDoor.placement.hitbox)) {
                soundEffectManager.play("find_secret");
                Location nextRoom = LocationUtil.findNextRoomLocation(currentAbscissa, currentOrdinate, Orientation.valueOf(dungeonDoor.placement.name()));
                DungeonDoorPlacement oppositePlacement = dungeonDoor.placement.reversePlacement();
                updateDungeonRoomsWithDoorOpen(currentAbscissa, currentOrdinate, dungeonDoor.placement, DungeonDoorType.BOMB);
                updateDungeonRoomsWithDoorOpen(nextRoom.x, nextRoom.y, oppositePlacement, DungeonDoorType.BOMB);
                updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
                dungeonDoor.isOpen = true;
                dungeonDoors[nextRoom.x][nextRoom.y].get(oppositePlacement).isOpen = true;
                String openedDoorKey1 = generateOpenedDoorKey(currentAbscissa, currentOrdinate, dungeonDoor.placement.name());
                String openedDoorKey2 = generateOpenedDoorKey(nextRoom.x, nextRoom.y, oppositePlacement.name());
                saveManager.updateOpenedDoors(dungeon.id, openedDoorKey1, openedDoorKey2);
            }
        }
    }

    /**
     * Update the rooms to make the closed doors not walkable
     */
    private void updateDungeonRoomsWithDoorClosed(int abscissa, int ordinate, DungeonDoorPlacement placement, DungeonDoorType type) {
        if (type == DungeonDoorType.KEY || type == DungeonDoorType.NO_MORE_ENEMY || type == DungeonDoorType.PUSH) {
            updateDungeonRoomsDoors(abscissa, ordinate, placement, DungeonTile.CLOSED_DOOR_LEFT, DungeonTile.CLOSED_DOOR_RIGHT, DungeonTile.CLOSED_DOOR);
        } else if (type == DungeonDoorType.BOMB) {
            updateDungeonRoomsDoors(abscissa, ordinate, placement, DungeonTile.BLOC, DungeonTile.BLOC, DungeonTile.BLOC);
        }
    }

    /**
     * Update the rooms to make the open doors walkable
     */
    private void updateDungeonRoomsWithDoorOpen(int abscissa, int ordinate, DungeonDoorPlacement placement, DungeonDoorType type) {
        if (type == DungeonDoorType.KEY || type == DungeonDoorType.NO_MORE_ENEMY || type == DungeonDoorType.PUSH) {
            updateDungeonRoomsDoors(abscissa, ordinate, placement, DungeonTile.DOOR_LEFT, DungeonTile.DOOR_RIGHT, DungeonTile.PATH);
        } else if (type == DungeonDoorType.BOMB) {
            updateDungeonRoomsDoors(abscissa, ordinate, placement, DungeonTile.BOMB_HOLE_LEFT, DungeonTile.BOMB_HOLE_RIGHT, DungeonTile.BOMB_HOLE);
        }
    }

    /**
     * Update the rooms to make them walkable or not
     */
    private void updateDungeonRoomsDoors(int abscissa, int ordinate, DungeonDoorPlacement placement, DungeonTile left, DungeonTile right, DungeonTile middle) {
        switch (placement) {
            case UP:
                dungeonRooms[abscissa][ordinate].changeTile(7, 1, left);
                dungeonRooms[abscissa][ordinate].changeTile(8, 1, right);
                break;
            case DOWN:
                dungeonRooms[abscissa][ordinate].changeTile(7, 9, left);
                dungeonRooms[abscissa][ordinate].changeTile(8, 9, right);
                break;
            case LEFT:
                dungeonRooms[abscissa][ordinate].changeTile(1, 5, middle);
                break;
            case RIGHT:
                dungeonRooms[abscissa][ordinate].changeTile(14, 5, middle);
                break;
        }
    }

    /**
     * Obtain the current room coordinate
     */
    public String getCoordinate() {
        return dungeon.id + String.valueOf(currentAbscissa) + currentOrdinate;
    }

    /**
     * Obtain the location of the dungeon
     */
    public Location getDungeonLocation() {
        return dungeon.location;
    }

    /**
     * Obtain the entrance of the dungeon
     */
    public Coordinate getDungeonExit() {
        return dungeon.exit;
    }

    /**
     * True if link has exited the zone
     */
    public boolean hasExitedZone() {
        return hasExitedZone;
    }

    /**
     * generate a unique key for an opened door
     */
    private String generateOpenedDoorKey(int abscissa, int ordinate, String placement) {
        return abscissa + "_" + ordinate + "_" + placement;
    }
}
