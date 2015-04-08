package util;

// Data Version getter.

// Gives the version to use for the ddragon data. This class read that variable from a file.
// That file can be manually actualized.

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataVersionGetter {
    public static String getVersion() {
        final String DEFAULT = "5.6.1";
        File file = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("Log", "Couldn't access to logFile");
            return DEFAULT;
        } else {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "data";
            File folder = new File(dir); //folder name
            folder.mkdirs();
            file = new File(dir, "data_dragon_version.txt");
            // If the data version file doesn't exist (for example, this code is probably running for
            // the first time) we create it.
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                    buf.append(DEFAULT);
                    buf.close();
                    return DEFAULT;
                } catch (IOException e) {
                    Logger.appendLog("Error 35 - " + e.toString());
                    return DEFAULT;
                }
                // If the file exists, read it. Maybe it has been manually actualized.
            } else {
                try {
                    BufferedReader buf = new BufferedReader(new FileReader(file));
                    String version = buf.readLine();
                    buf.close();
                    return version;
                } catch (IOException e) {
                    Logger.appendLog("Error 36 - " + e.toString());
                    return DEFAULT;
                }
            }
        }
    }
}

