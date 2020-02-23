package com.twoplayers.legend.util;

import android.content.res.AssetManager;
import android.util.Log;

import com.twoplayers.legend.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class FileUtil {

    public static Properties extractPropertiesFromAsset(AssetManager assetManager, String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Logger.warn("An exception has occured when trying to load properties. - " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Logger.warn("An exception has occured when trying to load properties. - " + e.getMessage());
            }
        }
        return properties;
    }

    public static ArrayList<String> extractLinesFromAsset(AssetManager assetManager, String fileName) {
        String line = null;
        ArrayList<String> lines = new ArrayList<String>();
        InputStream ips = null;
        BufferedReader reader = null;

        try {
            ips = assetManager.open(fileName);
            reader = new BufferedReader(new InputStreamReader(ips));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
            ips.close();
        } catch (IOException e) {
            Logger.warn("An exception has occured while reading file '" + fileName + "'. - " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (ips != null) {
                    ips.close();
                }
            } catch (IOException e) {
                Logger.warn("An exception has occured while reading file '" + fileName + "'. - " + e.getMessage());
            }
        }
        return lines;
    }
}
