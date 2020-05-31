package com.twoplayers.legend.assets.save;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.util.FileUtil;
import com.twoplayers.legend.util.Location;
import com.twoplayers.legend.util.Logger;

import java.io.IOException;
import java.util.Date;

public class SaveManager implements IManager {

    private String savePath;
    private Save save;

    /**
     * Initialise this manager
     */
    public void init(Game game) {
        savePath = ((MainActivity) game).getFilesDir().getPath() + "/save.txt";
        try {
            save = Save.fromJson(FileUtil.getContent(savePath));
            Logger.info("Game initialized with the saved file :" + save.toJson());
        } catch (Exception e) {
            Logger.error("Could not init save from file : " + e.getMessage());
            save = new Save();
        }
        save.attempt++;
        saveState();
    }

    public void reset() {
        Logger.info("Reset save.");
        save = new Save();
        saveState();
    }

    @Override
    public void update(float deltaTime, Graphics g) {

    }

    @Override
    public void paint(float deltaTime, Graphics g) {

    }

    private void saveState() {
        save.date = new Date();
        try {
            FileUtil.writeContent(savePath, save.toJson());
        } catch (IOException e) {
            Logger.error("Could not save file : " + e.getMessage());
        }
    }

    public Save getSave() {
        return save;
    }

    public void updateWorldMapExploredRooms(int abscissa, int ordinate) {
        if (!save.worldMapSave.exploredRooms[abscissa][ordinate]) {
            save.worldMapSave.exploredRooms[abscissa][ordinate] = true;
            saveState();
        }
    }

    public void updateOpenedEntrances(int abscissa, int ordinate) {
        if (!save.worldMapSave.openedEntrances[abscissa][ordinate]) {
            save.worldMapSave.openedEntrances[abscissa][ordinate] = true;
            saveState();
        }
    }

    public void updateDungeonExploredRooms(String dungeonId, int abscissa, int ordinate) {
        if (!save.getDungeonSave(dungeonId).exploredRooms[abscissa][ordinate]) {
            save.getDungeonSave(dungeonId).exploredRooms[abscissa][ordinate] = true;
            saveState();
        }
    }

    public void updateOpenedDoors(String dungeonId, String openedDoorKey1, String openedDoorKey2) {
        save.getDungeonSave(dungeonId).openedDoors.add(openedDoorKey1);
        save.getDungeonSave(dungeonId).openedDoors.add(openedDoorKey2);
        saveState();
    }

    public void updateOpenedBasements(String dungeonId, String key) {
        save.getDungeonSave(dungeonId).openedBasements.add(key);
        saveState();
    }

    public void increaseItemTaken(String dungeonId, Location basementLocationInTheDungeon) {
        String key = basementLocationInTheDungeon.toString();
        if (save.getDungeonSave(dungeonId).itemsTaken.containsKey(key)) {
            save.getDungeonSave(dungeonId).itemsTaken.put(key, save.getDungeonSave(dungeonId).itemsTaken.get(key) + 1);
        } else {
            save.getDungeonSave(dungeonId).itemsTaken.put(key, 1);
        }
        saveState();
    }
}
