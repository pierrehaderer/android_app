package com.twoplayers.legend.dungeon;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Image;
import com.twoplayers.legend.IZoneManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.Orientation;
import com.twoplayers.legend.assets.image.AllImages;
import com.twoplayers.legend.assets.image.ImagesDungeon;
import com.twoplayers.legend.assets.sound.MusicManager;
import com.twoplayers.legend.character.Item;
import com.twoplayers.legend.character.enemy.DungeonEnemyManager;
import com.twoplayers.legend.character.link.Link;
import com.twoplayers.legend.character.link.LinkManager;
import com.twoplayers.legend.gui.GuiManager;
import com.twoplayers.legend.util.Coordinate;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.LocationUtil;
import com.twoplayers.legend.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager implements IZoneManager {

    private static final float TRANSITION_SPEED = 4.0f;

    private ImagesDungeon imagesDungeon;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private DungeonEnemyManager dungeonEnemyManager;
    private MusicManager musicManager;

    /** dungeonRooms that represent the whole dungeon in this game */
    private DungeonRoom[][] dungeonRooms;
    private Boolean[][] exploredDungeon;

    private boolean initNotDone = true;
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
    private float leftCurrentDungeonRoom;
    private float topCurrentDungeonRoom;
    private Image imageCurrentDungeonRoom;
    private float leftNextDungeonRoom;
    private float topNextDungeonRoom;
    private Image imageNextDungeonRoom;
    private DoorCache currentDoorCache;
    private DoorCache nextDoorCache;

    /**
     * Load this manager
     */
    public void load(Game game, String dungeonInfo) {
        if (initNotDone) {
            initNotDone = false;
            init(game);
        }

        String[] dungeonArray = dungeonInfo.split("\\|");
        dungeon = new Dungeon();
        dungeon.id = dungeonArray[1];
        Location start = new Location(dungeonArray[2]);
        dungeon.location = new Location(dungeonArray[3]);
        dungeon.entrance = new Coordinate(dungeonArray[4]);
        hasExitedZone = false;

        initDungeon(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "dungeon/dungeon" + dungeon.id + ".txt"), start);

        musicManager.clear();
        musicManager.plan(100, "dungeon_loop", true);

        currentAbscissa = start.x;
        currentOrdinate = start.y;
        nextAbscissa = currentAbscissa;
        nextOrdinate = currentOrdinate;
        currentMiniAbscissa = 16 * currentAbscissa;
        currentMiniOrdinate = 11 * currentOrdinate;

        transitionRunning = false;
        leftCurrentDungeonRoom = LocationUtil.LEFT_MAP;
        topCurrentDungeonRoom = LocationUtil.TOP_MAP;
        imageCurrentDungeonRoom = imagesDungeon.get(getCoordinate());
        currentDoorCache = new DoorCache(imagesDungeon);
        updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
        leftNextDungeonRoom = LocationUtil.LEFT_MAP;
        topNextDungeonRoom = LocationUtil.TOP_MAP;
        imageNextDungeonRoom = imagesDungeon.get("empty");
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

        imagesDungeon = ((MainActivity) game).getAllImages().getImagesDungeon();
        imagesDungeon.load(((MainActivity) game).getAssetManager(), game.getGraphics());

        DungeonTile.initHashMap();
    }

    /**
     * Create all the DungeonRoom objects from the world_map file
     */
    private void initDungeon(List<String> dungeonFileContent, Location start) {
        dungeonRooms = new DungeonRoom[8][8];
        exploredDungeon = new Boolean[8][8];

        // Initialise the mapRooms
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                dungeonRooms[i][j] = (new DungeonRoom());
                exploredDungeon[i][j] = false;
            }
        }
        exploredDungeon[start.x][start.y] = true;

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

    @Override
    public void update(float deltaTime, Graphics g) {
        if (transitionRunning) {
            if (transitionCount> 0) {
                float transitionDelta = Math.min(TRANSITION_SPEED * deltaTime, transitionCount);
                transitionCount -= transitionDelta;
                switch (transitionOrientation) {
                    case UP:
                        topCurrentDungeonRoom += transitionDelta;
                        topNextDungeonRoom += transitionDelta;
                        currentMiniOrdinate -= transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkY(transitionDelta);
                        break;
                    case DOWN:
                        topCurrentDungeonRoom -= transitionDelta;
                        topNextDungeonRoom -= transitionDelta;
                        currentMiniOrdinate += transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkY(-1 * transitionDelta);
                    break;
                    case LEFT:
                        leftCurrentDungeonRoom += transitionDelta;
                        leftNextDungeonRoom += transitionDelta;
                        currentMiniAbscissa -= transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkX(transitionDelta);
                    break;
                    case RIGHT:
                        leftCurrentDungeonRoom -= transitionDelta;
                        leftNextDungeonRoom -= transitionDelta;
                        currentMiniAbscissa += transitionDelta * 16 / LocationUtil.WIDTH_MAP;
                        linkManager.moveLinkX(-1 * transitionDelta);
                    break;
                }
            } else if (transitionSteps > 0) {
                float transitionStep = Math.min(deltaTime * Link.SPEED, transitionSteps);
                transitionSteps -= transitionStep;
                switch (transitionOrientation) {
                    case UP:
                        linkManager.moveLinkY(-1 * transitionStep);
                        break;
                    case DOWN:
                        linkManager.moveLinkY(transitionStep);
                        break;
                    case LEFT:
                        linkManager.moveLinkX(-1 * transitionStep);
                        break;
                    case RIGHT:
                        linkManager.moveLinkX(transitionStep);
                        break;
                }
            } else {
                // End of the transition
                imageCurrentDungeonRoom = imageNextDungeonRoom;
                leftCurrentDungeonRoom = LocationUtil.LEFT_MAP;
                topCurrentDungeonRoom = LocationUtil.TOP_MAP;
                currentAbscissa = nextAbscissa;
                currentOrdinate = nextOrdinate;
                updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
                currentMiniAbscissa = 16 * currentAbscissa;
                currentMiniOrdinate = 11 * currentOrdinate;
                imageNextDungeonRoom = imagesDungeon.get("empty");
                transitionRunning = false;
                dungeonEnemyManager.requestEnemiesLoading();
                guiManager.activateButtons();
            }
        }
    }

    @Override
    public void paint(float deltaTime, Graphics g) {
        if (transitionRunning) {
            g.drawScaledImage(imageNextDungeonRoom, (int) leftNextDungeonRoom, (int) topNextDungeonRoom, AllImages.COEF);
        }
        g.drawScaledImage(imageCurrentDungeonRoom, (int) leftCurrentDungeonRoom, (int) topCurrentDungeonRoom, AllImages.COEF);
    }

    public void paintDoorCache(float deltaTime, Graphics g) {
        if (transitionRunning) {
            g.drawScaledImage(nextDoorCache.up, (int) leftNextDungeonRoom, (int) topNextDungeonRoom, AllImages.COEF);
            g.drawScaledImage(nextDoorCache.down, (int) leftNextDungeonRoom, (int) (topNextDungeonRoom + LocationUtil.HEIGHT_MAP - 17 * AllImages.COEF), AllImages.COEF);
            g.drawScaledImage(nextDoorCache.left, (int) leftNextDungeonRoom, (int) topNextDungeonRoom, AllImages.COEF);
            g.drawScaledImage(nextDoorCache.right, (int) (leftNextDungeonRoom + LocationUtil.WIDTH_MAP - 17 * AllImages.COEF), (int) topNextDungeonRoom, AllImages.COEF);
        }
        g.drawScaledImage(currentDoorCache.up, (int) leftCurrentDungeonRoom, (int) topCurrentDungeonRoom, AllImages.COEF);
        g.drawScaledImage(currentDoorCache.down, (int) leftCurrentDungeonRoom, (int) (topCurrentDungeonRoom + LocationUtil.HEIGHT_MAP - 17 * AllImages.COEF), AllImages.COEF);
        g.drawScaledImage(currentDoorCache.left, (int) leftCurrentDungeonRoom, (int) topCurrentDungeonRoom, AllImages.COEF);
        g.drawScaledImage(currentDoorCache.right, (int) (leftCurrentDungeonRoom + LocationUtil.WIDTH_MAP - 17 * AllImages.COEF), (int) topCurrentDungeonRoom, AllImages.COEF);
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
    public boolean isTileACave(float x, float y) {
        return false;
    }

    @Override
    public void changeRoom(Orientation orientation) {
        guiManager.deactivateButtons();
        dungeonEnemyManager.unloadEnemies();
        if (currentOrdinate == 7 && orientation == Orientation.DOWN) {
            Logger.info("Link has exited the cave.");
            linkManager.exitZone();
            hasExitedZone = true;
        } else {
            switch (orientation) {
                case UP:
                    transitionCount = 176 * AllImages.COEF;
                    transitionSteps = LocationUtil.TILE_SIZE;
                    leftNextDungeonRoom = LocationUtil.LEFT_MAP;
                    topNextDungeonRoom = LocationUtil.TOP_MAP - LocationUtil.HEIGHT_MAP;
                    nextAbscissa = currentAbscissa;
                    nextOrdinate = currentOrdinate - 1;
                    break;
                case DOWN:
                    transitionCount = 176 * AllImages.COEF;
                    transitionSteps = LocationUtil.TILE_SIZE;
                    leftNextDungeonRoom = LocationUtil.LEFT_MAP;
                    topNextDungeonRoom = LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP;
                    nextAbscissa = currentAbscissa;
                    nextOrdinate = currentOrdinate + 1;
                    break;
                case LEFT:
                    transitionCount = 256 * AllImages.COEF;
                    transitionSteps = LocationUtil.TILE_SIZE;
                    leftNextDungeonRoom = LocationUtil.LEFT_MAP - LocationUtil.WIDTH_MAP;
                    topNextDungeonRoom = LocationUtil.TOP_MAP;
                    nextAbscissa = currentAbscissa - 1;
                    nextOrdinate = currentOrdinate;
                    break;
                case RIGHT:
                    transitionCount = 256 * AllImages.COEF;
                    transitionSteps = LocationUtil.TILE_SIZE;
                    leftNextDungeonRoom = LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP;
                    topNextDungeonRoom = LocationUtil.TOP_MAP;
                    nextAbscissa = currentAbscissa + 1;
                    nextOrdinate = currentOrdinate;
                    break;
            }
            imageNextDungeonRoom = imagesDungeon.get(dungeon.id + String.valueOf(nextAbscissa) + nextOrdinate);
            updateDoorCache(nextDoorCache, nextAbscissa, nextOrdinate);
            Logger.info("Starting room transition to " + nextAbscissa + nextOrdinate);
            exploredDungeon[nextAbscissa][nextOrdinate] = true;
            transitionRunning = true;
            transitionOrientation = orientation;
        }
    }

    /**
     * Find a tile where a enemy can spawn avoid dungeon borders
     */
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

    /**
     * Update the door cache of the current room or the next one.
     */
    private void updateDoorCache(DoorCache doorCache, int abscissa, int ordinate) {
        DungeonRoom dungeonRoom = dungeonRooms[abscissa][ordinate];
        DungeonTile tileUp = dungeonRoom.getTile(7, 1);
        if (tileUp == DungeonTile.DOOR_LEFT) {
            doorCache.up = imagesDungeon.get("door_cache_up_" + dungeon.id);
        } else {
            doorCache.up = imagesDungeon.get("empty");
        }
        DungeonTile tileDown = dungeonRoom.getTile(7, 9);
        if (tileDown == DungeonTile.DOOR_LEFT) {
            doorCache.down = imagesDungeon.get("door_cache_down_" + dungeon.id);
        } else {
            doorCache.down = imagesDungeon.get("empty");
        }
        DungeonTile tileLeft = dungeonRoom.getTile(1, 5);
        if (tileLeft == DungeonTile.PATH) {
            doorCache.left = imagesDungeon.get("door_cache_left_" + dungeon.id);
        } else {
            doorCache.left = imagesDungeon.get("empty");
        }
        DungeonTile tileRight = dungeonRoom.getTile(14, 5);
        if (tileRight == DungeonTile.PATH) {
            doorCache.right = imagesDungeon.get("door_cache_right_" + dungeon.id);
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
            case WATER:
            case DOOR_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
            case PATH:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileUpRight) {
            case BLOC:
            case WATER:
            case DOOR_LEFT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_RIGHT:
            case PATH:
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
            case WATER:
            case DOOR_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
                if (deltaX < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case WATER:
            case DOOR_LEFT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_RIGHT:
            case PATH:
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
            case WATER:
            case DOOR_LEFT:
            case DOOR_RIGHT:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownLeft) {
            case BLOC:
            case WATER:
                if (deltaX < LocationUtil.TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case DOOR_RIGHT:
                if (deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
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
            case WATER:
            case DOOR_LEFT:
            case DOOR_RIGHT:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY < LocationUtil.HALF_TILE_SIZE - LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
            case OUT_OF_BOUNDS:
                break;
            default:
                return false;
        }

        switch (tileDownRight) {
            case BLOC:
            case WATER:
                if (deltaX > LocationUtil.OBSTACLE_TOLERANCE && deltaY > LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case DOOR_LEFT:
            case DOOR_RIGHT:
                if (deltaY > LocationUtil.HALF_TILE_SIZE + LocationUtil.OBSTACLE_TOLERANCE) {
                    return false;
                }
                break;
            case PATH:
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
            return exploredDungeon[x - 4][y];
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
    public Coordinate getDungeonEntrance() {
        return dungeon.entrance;
    }

    /**
     * True if link has exited the zone
     */
    public boolean hasExitedZone() {
        return hasExitedZone;
    }
}