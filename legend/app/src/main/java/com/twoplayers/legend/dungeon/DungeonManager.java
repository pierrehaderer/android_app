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
import com.twoplayers.legend.character.link.inventory.rod.RodWave;
import com.twoplayers.legend.character.link.inventory.sword.ThrowingSword;
import com.twoplayers.legend.character.npc.Npc;
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
import com.twoplayers.legend.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DungeonManager implements IZoneManager {

    private static final float INITIAL_STUN_COUNTER = 50f;
    private static final float TRANSITION_SPEED = 4.0f;
    private static final float TEXT_SPEED = 0.12f;

    private ImagesDungeon imagesDungeon;

    private GuiManager guiManager;
    private LinkManager linkManager;
    private DungeonEnemyManager dungeonEnemyManager;
    private MusicManager musicManager;
    private SoundEffectManager soundEffectManager;
    private SaveManager saveManager;

    /** dungeonRooms that represent the whole dungeon in this game */
    private DungeonRoom[][] rooms;
    private boolean[][] realRooms;
    private Boolean[][] exploredRooms;
    private Map<DungeonDoorPlacement, DungeonDoor>[][] doors;
    private DungeonTreasure[][] treasures;
    private Map<String, Npc> npcs;
    private Map<String, BasementInfo> basements;
    private Location triforceLocation;
    private DungeonBloc[][] dungeonBlocs;

    private boolean shouldInitialize = true;
    private float stunCounter;
    private Dungeon dungeon;
    private boolean hasExitedZone;
    private Npc npc;
    private float textCounter;
    private int textSoundCounter;

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
        stunCounter = INITIAL_STUN_COUNTER;

        dungeon = new Dungeon(imagesDungeon, game.getGraphics(), dungeonInfo);
        Location start = dungeonInfo.linkStartLocationInTheDungeon;
        hasExitedZone = false;

        rooms = new DungeonRoom[8][8];
        realRooms = new boolean[8][8];
        exploredRooms = new Boolean[8][8];
        treasures = new DungeonTreasure[8][8];
        npcs = new HashMap<>();
        basements = new HashMap<>();
        doors = new Map[8][8];
        dungeonBlocs = new DungeonBloc[8][8];
        initDungeon(FileUtil.extractLinesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon" + dungeon.id + ".txt"), start);
        initDungeonDoors(FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon" + dungeon.id + "_doors.properties"));
        initDungeonElements(FileUtil.extractPropertiesFromAsset(((MainActivity) game).getAssetManager(), "other/dungeon" + dungeon.id + "_elements.properties"), dungeonInfo);

        if (dungeonInfo.startMusic) {
            musicManager.clear();
            musicManager.plan(100, "dungeon_loop", true);
        }

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
    private void initDungeon(List<String> dungeonFileContent, Location start) {
        Boolean[][] savedExploredRooms = saveManager.getSave().getDungeonSave(dungeon.id).getExploredRooms();

        // Initialise the mapRooms
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rooms[i][j] = new DungeonRoom();
                exploredRooms[i][j] = savedExploredRooms[i][j];
            }
        }

        // Explore the first room of the dungeon if not already explored
        if (!exploredRooms[start.x][start.y]) {
            exploredRooms[start.x][start.y] = true;
            saveManager.updateDungeonExploredRooms(dungeon.id, start.x, start.y);
        }

        // Fill the mapRooms, line by line
        int indexLine = 0;
        for (int index1 = 0; index1 < 8; index1 = index1) {
            String line = dungeonFileContent.get(indexLine++);
            for (int index2 = 0; index2 < 8; index2++) {
                boolean isARealRoom = rooms[index2][index1].addALine(line.substring(17 * index2, 17 * index2 + 16));
                realRooms[index2][index1] |= isARealRoom;
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
        List<String> openedDoors = saveManager.getSave().getDungeonSave(dungeon.id).getOpenedDoors();

        // Init dungeonDoors
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                doors[i][j] = new HashMap<>();
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
                    DungeonDoor dungeonDoor = new DungeonDoor(placement, type);
                    if (openedDoors.contains(generateOpenedDoorKey(abscissa, ordinate, dungeonDoorInfo[0]))) {
                        // Open the door, it has already been opened
                        dungeonDoor.isOpen = true;
                    } else {
                        // Make the door not walkable
                        updateDungeonRoomsWithDoorClosed(abscissa, ordinate, placement, type);
                    }
                    doors[abscissa][ordinate].put(placement, dungeonDoor);
                    if (type == DungeonDoorType.PUSH) {
                        Location location =  new Location(dungeonDoorInfo[2]);
                        dungeonBlocs[abscissa][ordinate] = new DungeonBloc(imagesDungeon, location, dungeon.id);
                    }
                }
            }
        }
    }

    /**
     * Init the elements of the dungeon
     */
    private void initDungeonElements(Properties elementsProperties, DungeonInfo dungeonInfo) {
        List<String> openBasements = saveManager.getSave().getDungeonSave(dungeon.id).getOpenedBasements();
        // Parse the properties to add the elements
        for (String key : elementsProperties.stringPropertyNames()) {
            String elementProperty = ((String) elementsProperties.get(key)).trim();
            if (elementProperty.length() > 0) {
                int abscissa = Integer.parseInt(key.substring(1,2));
                int ordinate = Integer.parseInt(key.substring(2,3));
                String[] elementAsArray = elementProperty.split("\\|");
                if ("ITEM".equals(elementAsArray[0])) {
                    Logger.info("Adding treasure (" + abscissa + "," + ordinate + ") : " + elementProperty);
                    treasures[abscissa][ordinate] = new DungeonTreasure(elementAsArray[0], elementAsArray[1], elementAsArray[2]);
                    if ("triforce".equals(elementAsArray[2])) {
                        triforceLocation = new Location(abscissa, ordinate);
                    }
                }
                if ("BASEMENT".equals(elementAsArray[0])) {
                    if ("PUSH".equals(elementAsArray[1])) {
                        String[] pushLocationAsArray = elementAsArray[2].split(",");
                        Location pushLocation = new Location(Integer.parseInt(pushLocationAsArray[0]), Integer.parseInt(pushLocationAsArray[1]));
                        dungeonBlocs[abscissa][ordinate] = new DungeonBloc(imagesDungeon, pushLocation, dungeon.id);
                    }
                    Location basementLocationInTheDungeon = new Location(abscissa, ordinate);
                    String[] locationAsArray = elementAsArray[4].split(",");
                    Location stairsLocationInTheRoom = new Location(Integer.parseInt(locationAsArray[0]), Integer.parseInt(locationAsArray[1]));
                    String[] exitAsArray = elementAsArray[5].split(",");
                    Coordinate linkExitCoordinateInTheDungeon = new Coordinate(LocationUtil.getXFromGrid(Integer.parseInt(exitAsArray[0])), LocationUtil.getYFromGrid(Integer.parseInt(exitAsArray[1])));
                    basements.put(key, new BasementInfo(dungeonInfo, elementAsArray[3], basementLocationInTheDungeon, stairsLocationInTheRoom, linkExitCoordinateInTheDungeon, openBasements.contains(key)));
                }
                if ("NPC".equals(elementAsArray[0])) {
                    Logger.info("Adding NPC (" + abscissa + "," + ordinate + ") : " + elementProperty);
                    npcs.put(key, new Npc(imagesDungeon, elementAsArray[4], elementAsArray[1], elementAsArray[2], elementAsArray[3]));
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

        // Open noMoreEnemy doors if all the enemies have been killed
        if (!transitionRunning && dungeonEnemyManager.noMoreEnemy()) {
            openNoMoreEnemyDoors();
        }

        // Handle NPC if there is one
        if (!transitionRunning && npc != null) {
            textCounter += deltaTime * TEXT_SPEED;
            textCounter = Math.min(textCounter, npc.message1.length() + npc.message2.length() + npc.message3.length());
            int end1 = (int) Math.min(textCounter, npc.message1.length());
            int end2 = (int) Math.max(0, Math.min(textCounter - npc.message1.length(), npc.message2.length()));
            int end3 = (int) Math.max(0, Math.min(textCounter - npc.message1.length() - npc.message2.length(), npc.message3.length()));
            npc.displayedMessage1 = npc.message1.substring(0, end1);
            npc.displayedMessage2 = npc.message2.substring(0, end2);
            npc.displayedMessage3 = npc.message3.substring(0, end3);
            if (textSoundCounter < (int) textCounter) {
                textSoundCounter = (int) textCounter;
                soundEffectManager.play("text");
            }
            dungeon.fireAnimation.update(deltaTime);
        }

        // Handle pushable bloc if there is one
        DungeonBloc bloc = dungeonBlocs[currentAbscissa][currentOrdinate];
        if (bloc != null && bloc.count > 0) {
            float blocDistance = Math.min(DungeonBloc.BLOC_SPEED * deltaTime,  bloc.count);
            bloc.count -= blocDistance;
            switch (bloc.orientation) {
                case UP:
                    bloc.y -= blocDistance;
                    break;
                case DOWN:
                    bloc.y += blocDistance;
                    break;
                case LEFT:
                    bloc.x -= blocDistance;
                    break;
                case RIGHT:
                    bloc.x += blocDistance;
                    break;
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
                // After room transition, link is entering one or 2 tiles into the room
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
                resetBlocAndCloseDoors(transitionOrientation);
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
                textCounter = 0;
                textSoundCounter = 0;
                npc = npcs.get(getCoordinate());
                if (npc != null) npc.reset();
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
        paintBloc(g);
        paintNpc(g);
    }

    /**
     * Paint the doors of the dungeon
     */
    private void paintDoors( Graphics g) {
        if (transitionRunning) {
            for (DungeonDoor dungeonDoor : doors[nextAbscissa][nextOrdinate].values()) {
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
        for (DungeonDoor dungeonDoor : doors[currentAbscissa][currentOrdinate].values()) {
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
     * Paint the pushable bloc when it has been pushed
     */
    private void paintBloc(Graphics g) {
        DungeonBloc bloc = dungeonBlocs[currentAbscissa][currentOrdinate];
        if (bloc != null && bloc.hasBeenPushed) {
            g.drawImage(bloc.floorImage, (int) (leftCurrentRoom + bloc.initialLocation.x * LocationUtil.TILE_SIZE), (int) (topCurrentRoom + bloc.initialLocation.y * LocationUtil.TILE_SIZE));
            g.drawImage(bloc.blocImage, (int) (leftCurrentRoom + bloc.x), (int) (topCurrentRoom + bloc.y));
        }
    }

    /**
     * Paint the NPC and its message
     */
    private void paintNpc(Graphics g) {
        if (npc != null) {
            float message1X = LocationUtil.LEFT_MAP + 2.2f * LocationUtil.TILE_SIZE + 6.5f * (1f - npc.message1.length() / 22f) * LocationUtil.TILE_SIZE;
            float message2X = LocationUtil.LEFT_MAP + 2.2f * LocationUtil.TILE_SIZE + 6.5f * (1f - npc.message2.length() / 22f) * LocationUtil.TILE_SIZE;
            float message3X = LocationUtil.LEFT_MAP + 2.2f * LocationUtil.TILE_SIZE + 6.5f * (1f - npc.message3.length() / 22f) * LocationUtil.TILE_SIZE;
            g.drawString(npc.displayedMessage1, (int) message1X, (int) (LocationUtil.getYFromGrid(2) + LocationUtil.HALF_TILE_SIZE), TextUtil.getPaint());
            g.drawString(npc.displayedMessage2, (int) message2X, (int) (LocationUtil.getYFromGrid(3)), TextUtil.getPaint());
            g.drawString(npc.displayedMessage3, (int) message3X, (int) (LocationUtil.getYFromGrid(3) + LocationUtil.HALF_TILE_SIZE), TextUtil.getPaint());
            g.drawAnimation(dungeon.fireAnimation, (int) LocationUtil.getXFromGrid(5), (int) npc.y);
            g.drawAnimation(dungeon.fireAnimation, (int) LocationUtil.getXFromGrid(10), (int) npc.y);
            g.drawScaledImage(npc.image, (int) npc.x, (int) npc.y, AllImages.COEF);
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
            Logger.info("Starting room transition to " + nextAbscissa + nextOrdinate);
            imageNextRoom = imagesDungeon.get(dungeon.id + String.valueOf(nextAbscissa) + nextOrdinate);
            updateDoorCache(nextDoorCache, nextAbscissa, nextOrdinate);
            if (!exploredRooms[nextAbscissa][nextOrdinate]) {
                exploredRooms[nextAbscissa][nextOrdinate] = true;
                saveManager.updateDungeonExploredRooms(dungeon.id, nextAbscissa, nextOrdinate);
            }
            transitionRunning = true;
            transitionOrientation = orientation;
            npc = null;
            // Prepare for the behaviour to apply according to the doors
            DungeonDoor dungeonDoor = doors[currentAbscissa][currentOrdinate].get(DungeonDoorPlacement.valueOf(orientation.name()));
            DungeonDoor nextDungeonDoor = doors[nextAbscissa][nextOrdinate].get(DungeonDoorPlacement.valueOf(orientation.reverseOrientation().name()));
            if (dungeonDoor != null && dungeonDoor.type == DungeonDoorType.BOMB) {
                transitionSteps = 4 * LocationUtil.TILE_SIZE;
            } else if (nextDungeonDoor != null && (nextDungeonDoor.type == DungeonDoorType.NO_MORE_ENEMY || nextDungeonDoor.type == DungeonDoorType.PUSH)) {
                nextDungeonDoor.isOpen = true;
                transitionSteps = 2 * LocationUtil.TILE_SIZE;
            } else {
                transitionSteps = LocationUtil.TILE_SIZE;
            }
        }
    }

    @Override
    public boolean isTileADoor(float x, float y) {
        return false;
    }

    @Override
    public boolean isTileStairs(float x, float y) {
        BasementInfo basementInfo = basements.get(getCoordinate());
        if (basementInfo != null && basementInfo.isOpen) {
            int tileX = LocationUtil.getTileXFromPositionX(x);
            int tileY = LocationUtil.getTileYFromPositionY(y);
            return (basementInfo.stairsLocationInTheRoom.x == tileX && basementInfo.stairsLocationInTheRoom.y == tileY);
        }
        return false;
    }

    @Override
    public boolean isTileABombHole(float x, float y) {
        int tileX = LocationUtil.getTileXFromPositionX(x);
        int tileY = LocationUtil.getTileYFromPositionY(y);
        DungeonTile tile = rooms[currentAbscissa][currentOrdinate].getTile(tileX, tileY);
        return tile == DungeonTile.BOMB_HOLE || tile == DungeonTile.BOMB_HOLE_LEFT || tile == DungeonTile.BOMB_HOLE_RIGHT;

    }

    @Override
    public boolean isTileWalkable(float x, float y) {
        DungeonRoom currentMapRoom = rooms[currentAbscissa][currentOrdinate];
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
    public boolean checkKeyDoor(Orientation orientation, float x, float y) {
        DungeonDoor dungeonDoor = doors[currentAbscissa][currentOrdinate].get(DungeonDoorPlacement.valueOf(orientation.name()));
        // If the door is not special or if the door is not a key door or if it has already been opened
        if (dungeonDoor != null && dungeonDoor.type == DungeonDoorType.KEY && !dungeonDoor.isOpen) {
            int tileX = LocationUtil.getTileXFromPositionX(x);
            int tileY = LocationUtil.getTileYFromPositionY(y);
            float deltaX = LocationUtil.getDeltaX(x);
            float deltaY = LocationUtil.getDeltaY(y);
            switch (orientation) {
                case UP:
                    return tileX == 7 && tileY == 1 && deltaX > LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE;
                case DOWN:
                    return tileX == 7 && tileY == 8 && deltaX > LocationUtil.HALF_TILE_SIZE;
                case LEFT:
                    return tileX == 1 && tileY == 5 && deltaY < LocationUtil.HALF_TILE_SIZE;
                case RIGHT:
                    return tileX == 13 && tileY == 5 && deltaY < LocationUtil.HALF_TILE_SIZE;
            }
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
        doors[currentAbscissa][currentOrdinate].get(placement).isOpen = true;
        doors[nextRoom.x][nextRoom.y].get(oppositePlacement).isOpen = true;
        String openedDoorKey1 = generateOpenedDoorKey(currentAbscissa, currentOrdinate, placement.name());
        String openedDoorKey2 = generateOpenedDoorKey(nextRoom.x, nextRoom.y, oppositePlacement.name());
        saveManager.updateOpenedDoors(dungeon.id, openedDoorKey1, openedDoorKey2);
    }

    @Override
    public boolean checkPushableBlock(Orientation orientation, float x, float y) {
        DungeonBloc bloc = dungeonBlocs[currentAbscissa][currentOrdinate];
        if (bloc != null && !bloc.hasBeenPushed) {
            Location location = bloc.initialLocation;
            int tileX = LocationUtil.getTileXFromPositionX(x);
            int tileY = LocationUtil.getTileYFromPositionY(y);
            float deltaX = LocationUtil.getDeltaX(x);
            float deltaY = LocationUtil.getDeltaY(y);
            switch (orientation) {
                case UP:
                    return tileX == location.x && tileY == location.y && deltaX < LocationUtil.HALF_TILE_SIZE && deltaY < LocationUtil.HALF_TILE_SIZE;
                case DOWN:
                    return tileX == location.x && tileY == location.y - 1 && deltaX < LocationUtil.HALF_TILE_SIZE;
                case LEFT:
                    return tileX == location.x && tileY == location.y && deltaY < LocationUtil.HALF_TILE_SIZE;
                case RIGHT:
                    return tileX == location.x - 1 && tileY == location.y && deltaY < LocationUtil.HALF_TILE_SIZE;
            }
        }
        return false;
    }

    @Override
    public void pushBloc(Orientation orientation) {
        DungeonBloc bloc = dungeonBlocs[currentAbscissa][currentOrdinate];
        bloc.hasBeenPushed = true;
        bloc.count = LocationUtil.TILE_SIZE;
        bloc.orientation = orientation;
        bloc.newLocation = new Location(bloc.initialLocation.x, bloc.initialLocation.y);
        switch (orientation) {
            case UP:
                bloc.newLocation.y -= 1;
                break;
            case DOWN:
                bloc.newLocation.y += 1;
                break;
            case LEFT:
                bloc.newLocation.x -= 1;
                break;
            case RIGHT:
                bloc.newLocation.x += 1;
                break;
        }
        rooms[currentAbscissa][currentOrdinate].changeTile(bloc.initialLocation.x, bloc.initialLocation.y, DungeonTile.PATH);
        rooms[currentAbscissa][currentOrdinate].changeTile(bloc.newLocation.x, bloc.newLocation.y, DungeonTile.BLOC);

        // Open the doors
        for (DungeonDoor dungeonDoor : doors[currentAbscissa][currentOrdinate].values()) {
            if (dungeonDoor.type == DungeonDoorType.PUSH) {
                dungeonDoor.isOpen = true;
                updateDungeonRoomsWithDoorOpen(currentAbscissa, currentOrdinate, dungeonDoor.placement, dungeonDoor.type);
            }
        }

        // Opens the basement
        BasementInfo basementInfo = basements.get(getCoordinate());
        if (basementInfo != null && !basementInfo.isOpen) {
            basementInfo.isOpen = true;
            saveManager.updateOpenedBasements(dungeon.id, getCoordinate());
        }
    }

    /**
     * Put bloc back in place and handle doors in the previous room and in the new one
     */
    private void resetBlocAndCloseDoors(Orientation orientation) {
        // Reset the bloc
        DungeonBloc bloc = dungeonBlocs[currentAbscissa][currentOrdinate];
        if (bloc != null) {
            rooms[currentAbscissa][currentOrdinate].changeTile(bloc.initialLocation.x, bloc.initialLocation.y, DungeonTile.BLOC);
            rooms[currentAbscissa][currentOrdinate].changeTile(bloc.newLocation.x, bloc.newLocation.y, DungeonTile.PATH);
            bloc.reset();
        }
        // Close all the door in the room link just left
        for (DungeonDoor dungeonDoor : doors[currentAbscissa][currentOrdinate].values()) {
            if (dungeonDoor.isOpen && (dungeonDoor.type == DungeonDoorType.PUSH || dungeonDoor.type == DungeonDoorType.NO_MORE_ENEMY)) {
                updateDungeonRoomsWithDoorClosed(currentAbscissa, currentOrdinate, dungeonDoor.placement, dungeonDoor.type);
                dungeonDoor.isOpen = false;
            }
        }
        // This is the door where link is entering the new room. If it is a closed door, close it behind him
        DungeonDoor nextDungeonDoor = doors[nextAbscissa][nextOrdinate].get(DungeonDoorPlacement.valueOf(orientation.reverseOrientation().name()));
        if (nextDungeonDoor != null && (nextDungeonDoor.type == DungeonDoorType.PUSH || nextDungeonDoor.type == DungeonDoorType.NO_MORE_ENEMY)) {
            updateDungeonRoomsWithDoorClosed(nextAbscissa, nextOrdinate, nextDungeonDoor.placement, nextDungeonDoor.type);
            nextDungeonDoor.isOpen = false;
        }
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
        DungeonRoom dungeonRoom = rooms[abscissa][ordinate];
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
        DungeonRoom currentMapRoom = rooms[currentAbscissa][currentOrdinate];
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
            case LIMIT:
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
            case LIMIT:
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isDownValid(float x, float y) {
        DungeonRoom currentMapRoom = rooms[currentAbscissa][currentOrdinate];
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
        DungeonRoom currentMapRoom = rooms[currentAbscissa][currentOrdinate];
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
        DungeonRoom currentMapRoom = rooms[currentAbscissa][currentOrdinate];
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
    public void linkHasPickedItem(Item item) {
        item.hideItem();
    }

    @Override
    public boolean isExplored(int x, int y) {
        if (3 < x && x < 12) {
            return exploredRooms[x - 4][y];
        }
        return true;
    }

    @Override
    public String getDungeonId() {
        return dungeon.id;
    }

    @Override
    public boolean isARealRoom(int i, int j) {
        if (i < 0 || i > 8) {
            return false;
        }
        return realRooms[i][j];
    }

    @Override
    public Location getTriforceLocation() {
        return triforceLocation;
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
    public boolean hasThrowingSwordHitBorder(ThrowingSword throwingSword) {
        switch (throwingSword.orientation) {
            case UP:
                return throwingSword.y < LocationUtil.TOP_MAP + 3 * LocationUtil.HALF_TILE_SIZE;
            case DOWN:
                return throwingSword.y > LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - 5 * LocationUtil.HALF_TILE_SIZE;
            case LEFT:
                return throwingSword.x < LocationUtil.LEFT_MAP + 3 * LocationUtil.HALF_TILE_SIZE;
            case RIGHT:
                return throwingSword.x > LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - 5 * LocationUtil.HALF_TILE_SIZE;
        }
        return true;
    }


    @Override
    public boolean hasRodWaveHitBorder(RodWave rodWave) {
        switch (rodWave.orientation) {
            case UP:
                return rodWave.y < LocationUtil.TOP_MAP + 2 * LocationUtil.TILE_SIZE;
            case DOWN:
                return rodWave.y > LocationUtil.TOP_MAP + LocationUtil.HEIGHT_MAP - 3 * LocationUtil.TILE_SIZE;
            case LEFT:
                return rodWave.x < LocationUtil.LEFT_MAP + 2 * LocationUtil.TILE_SIZE;
            case RIGHT:
                return rodWave.x > LocationUtil.LEFT_MAP + LocationUtil.WIDTH_MAP - 3 * LocationUtil.TILE_SIZE;
        }
        return true;
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
        for (DungeonDoor dungeonDoor : doors[nextAbscissa][nextOrdinate].values()) {
            if (dungeonDoor.type == DungeonDoorType.BOMB && !dungeonDoor.isOpen
                && LocationUtil.areColliding(bomb.hitbox, dungeonDoor.placement.hitbox)) {
                soundEffectManager.play("find_secret");
                Location nextRoom = LocationUtil.findNextRoomLocation(currentAbscissa, currentOrdinate, Orientation.valueOf(dungeonDoor.placement.name()));
                DungeonDoorPlacement oppositePlacement = dungeonDoor.placement.reversePlacement();
                updateDungeonRoomsWithDoorOpen(currentAbscissa, currentOrdinate, dungeonDoor.placement, DungeonDoorType.BOMB);
                updateDungeonRoomsWithDoorOpen(nextRoom.x, nextRoom.y, oppositePlacement, DungeonDoorType.BOMB);
                updateDoorCache(currentDoorCache, currentAbscissa, currentOrdinate);
                dungeonDoor.isOpen = true;
                doors[nextRoom.x][nextRoom.y].get(oppositePlacement).isOpen = true;
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
                rooms[abscissa][ordinate].changeTile(7, 1, left);
                rooms[abscissa][ordinate].changeTile(8, 1, right);
                break;
            case DOWN:
                rooms[abscissa][ordinate].changeTile(7, 9, left);
                rooms[abscissa][ordinate].changeTile(8, 9, right);
                break;
            case LEFT:
                rooms[abscissa][ordinate].changeTile(1, 5, middle);
                break;
            case RIGHT:
                rooms[abscissa][ordinate].changeTile(14, 5, middle);
                break;
        }
    }

    /**
     * Open the noMoreEnemy doors
     */
    public void openNoMoreEnemyDoors() {
        boolean doorHasOpened = false;
        for (DungeonDoor dungeonDoor : doors[currentAbscissa][currentOrdinate].values()) {
            if (!dungeonDoor.isOpen && dungeonDoor.type == DungeonDoorType.NO_MORE_ENEMY) {
                dungeonDoor.isOpen = true;
                doorHasOpened = true;
                updateDungeonRoomsWithDoorOpen(currentAbscissa, currentOrdinate, dungeonDoor.placement, dungeonDoor.type);
            }
        }
        if (doorHasOpened) {
            soundEffectManager.play("open_door");
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
     * Obtain the information on the basement in the current room
     */
    public BasementInfo getBasementInfo() {
        return basements.get(getCoordinate());
    }

    /**
     * generate a unique key for an opened door
     */
    private String generateOpenedDoorKey(int abscissa, int ordinate, String placement) {
        return abscissa + "_" + ordinate + "_" + placement;
    }
}
