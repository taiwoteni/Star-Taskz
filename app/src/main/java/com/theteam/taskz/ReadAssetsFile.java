package com.theteam.taskz;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class ReadAssetsFile {

    public static String readAssetsFile(AssetManager assetManager, String fileName) throws IOException {
        // Open the assets file
        InputStream inputStream = assetManager.open(fileName);

        // Read the file contents into a string
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            builder.append((char) ch);
        }

        // Close the input stream
        inputStream.close();

        // Return the file contents as a string
        return builder.toString();
    }
}