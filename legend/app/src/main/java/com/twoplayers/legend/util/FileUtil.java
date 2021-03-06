package com.twoplayers.legend.util;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

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

    /**
     * Get content of a file
     */
    public static String getContent(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
        }
        return content.toString();
    }

    public static void writeContent(String filePath, String content) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
