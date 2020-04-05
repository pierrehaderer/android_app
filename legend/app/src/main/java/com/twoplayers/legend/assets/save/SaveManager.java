package com.twoplayers.legend.assets.save;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.twoplayers.legend.IManager;
import com.twoplayers.legend.MainActivity;
import com.twoplayers.legend.util.FileUtil;
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

    public void updateExploredRooms(int nextAbscissa, int nextOrdinate) {
        save.worldMapSave.exploredRooms[nextAbscissa][nextOrdinate] = true;
        saveState();
    }

    public void updateOpenedEntrances(int currentAbscissa, int currentOrdinate) {
        save.worldMapSave.openedEntrances[currentAbscissa][currentOrdinate] = true;
        saveState();
    }
}
