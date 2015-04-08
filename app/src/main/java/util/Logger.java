package util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// Logger.java

// A simple class with an static method that logs a message to a file in the external SDCard.
// The date is also appended automatically.

public class Logger {
    public static void appendLog(String text) {
        File logFile = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("Log", "Couldn't access to logFile");
            return;
        } else {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "logs";
            File folder = new File(dir); //folder name
            folder.mkdirs();
            logFile = new File(dir, "URFGamesAppLog.txt");
        }
        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            String dateStr = dateFormat.format(date);
            buf.append(text);
            buf.append(" - (");
            buf.append(dateStr);
            buf.append(")");
            buf.newLine();
            buf.close();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
}