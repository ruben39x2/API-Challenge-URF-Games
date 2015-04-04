package info.urf.app.rest.lol.urfgames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import util.APIKey;
import util.LoLResponse;

import util.Logger;
import util.SendRequest;

// MainActivity.java

// Launcher activity. Shows a list with the recent URF Games.

public class MainActivity extends ActionBarActivity {
    private ArrayList<String> recentGames = new ArrayList<>();
    private Integer minutesToBack = 0;
    private ArrayAdapter<String> adapter;
    private final Context context = this;
    private boolean canLoadMoreGames = false;
    String REGION = "euw";

    /**********************************************************************************************/

    // Methods overrided from the superclass.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the view.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the uncaught exception handler (it's a logger)
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);

        // Initialize the listView.
        this.adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, this.recentGames);
        ListView listView = (ListView) findViewById(R.id.listViewGames);
        listView.setAdapter(this.adapter);

        // Load the region.
        getRegionFromPreferences();

        // Check if the activity was restored.
        if (savedInstanceState != null){
            writeActivityVariableRecentGames(savedInstanceState.getStringArrayList("recentGames"));
            actualizeGamesList();
        } else {
            loadRecentURFGames();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("recentGames", this.recentGames);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch (id) {
            // Three posibilities of the menu:
            // - Load more.
            // - Change the Region in preferences (EUW, NA...)
            // - Show About.
            case R.id.action_more: {
                onLoadMoreGames();
                return true;
            }
            case R.id.action_region: {
                changePreferences();
                return true;
            }
            case R.id.action_about: {
                showAbout();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/

    // Private methods.

    private void getRegionFromPreferences(){
        SharedPreferences prefs = getSharedPreferences("URFGames", MODE_PRIVATE);
        this.REGION = prefs.getString("region", "EUW").toLowerCase();
    }

    private void loadRecentURFGames(){
        new LoadGamesAsync().execute();
    }

    private void setCanLoadMoreGames(boolean canLoadMoreGames){
        this.canLoadMoreGames = canLoadMoreGames;
    }

    private void writeActivityVariableRecentGames(List<String> recent){
        this.recentGames.addAll(recent);
    }

    private void clearActivityVariableRecentGames(){
        this.recentGames.clear();
    }

    private void actualizeGamesList(){
        this.adapter.notifyDataSetChanged();
    }

    private void setMinutesToBack(Integer minutes){
        this.minutesToBack = minutes;
    }

    private Integer getMinutesToBack(){
        return this.minutesToBack;
    }

    // Shows a dialog to change the current region (also from preferences).
    private void changePreferences(){
        // Get current region.
        SharedPreferences prefs = getSharedPreferences("URFGames", MODE_PRIVATE);
        String region = prefs.getString("region", "EUW");
        Integer regionInt = 0;
        if (region.equals("BR")) regionInt = 0;
        if (region.equals("EUNE")) regionInt = 1;
        if (region.equals("EUW")) regionInt = 2;
        if (region.equals("KR")) regionInt = 3;
        if (region.equals("LAN")) regionInt = 4;
        if (region.equals("LAS")) regionInt = 5;
        if (region.equals("NA")) regionInt = 6;
        if (region.equals("OCE")) regionInt = 7;
        if (region.equals("RU")) regionInt = 8;
        if (region.equals("TR")) regionInt = 9;

        final String[] items =
                {"BR", "EUNE", "EUW", "KR", "LAN", "LAS", "NA", "OCE", "RU", "TR"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your region")
                .setSingleChoiceItems(items, regionInt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item) {
                        // Every time you click on a singleChoice Option, this is executed.
                        SharedPreferences.Editor editor =
                                getSharedPreferences("URFGames", MODE_PRIVATE).edit();
                        editor.putString("region", items[item]);
                        editor.commit();

                        // Actualize the variable and delete the list.
                        getRegionFromPreferences();
                        clearActivityVariableRecentGames();
                        actualizeGamesList();

                        // Also restart the load of games.
                        loadRecentURFGames();
                    }
                });

        builder.create().show();
    }

    // Shows a dialog with "about the app" info.
    private void showAbout(){
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(context);
        aboutDialog.setTitle("About the app")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("About")
                .setCancelable(false)
                .setPositiveButton("Ok, cool", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create the alert dialog and show it.
        AlertDialog alertDialog = aboutDialog.create();
        alertDialog.show();
    }

    public void onLoadMoreGames(){
        if (canLoadMoreGames)
            loadRecentURFGames();
        else
            Toast.makeText(getApplicationContext(),
                    "More games cannot be loaded now!",
                    Toast.LENGTH_LONG).show();
    }

    /**********************************************************************************************/

    // Subclasses.

    // Contains an async task needed to perform asynchronously the load of the recent URF games IDs.
    // Receives nothing and returns a list with Game IDs.
    // IMPORTANT: This asyncTasks uses and manages the global variable "minutes to back".

    public class LoadGamesAsync extends AsyncTask<Void, String, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pB = (ProgressBar) findViewById(R.id.progressBarGames);
            TextView tV = (TextView) findViewById(R.id.textViewLoadGamesProgress);
            TextView tV2 = (TextView) findViewById(R.id.textViewTitle);

            pB.setVisibility(View.VISIBLE);
            tV.setText("Loading recent URF games...");
            tV2.setVisibility(View.INVISIBLE);

            setCanLoadMoreGames(false);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            ProgressBar pB = (ProgressBar) findViewById(R.id.progressBarGames);
            TextView tV2 = (TextView) findViewById(R.id.textViewTitle);
            pB.setVisibility(View.INVISIBLE);
            // Methods of the activity.
            if (strings != null) {
                tV2.setVisibility(View.VISIBLE);
                writeActivityVariableRecentGames(strings);
                actualizeGamesList();
                setCanLoadMoreGames(true);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            TextView tV = (TextView) findViewById(R.id.textViewLoadGamesProgress);
            tV.setText(values[0]);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            LoLResponse loLResponse;
            Integer minutesToBack;
            Boolean mustBack5Minutes = true;
            // We have to do requests until we get a petition with game IDs (also control another errors)
            // WHY? Because maybe in the last 5 minutes, no one URF game has started. And maybe also
            // in the last 10 minutes, the last 15...
            do {
                minutesToBack = getMinutesToBack();
                String beginDate = getCurrentValidDate(minutesToBack);
                // Send the petition.
                String request = "https://" + REGION + ".api.pvp.net/api/lol/" +
                        REGION + "/v4.1/game/ids?beginDate=" + beginDate + "&api_key=" + APIKey.KEY;
                Log.d("REQUEST", request);
                loLResponse = SendRequest.get(request);

                // Check the result of the petition.
                switch (loLResponse.getStatus()) {
                    case -1: {
                        publishProgress("Connection error");
                        Logger.appendLog("Error 01 - " + loLResponse.getError());
                        return null;
                    }
                    case 200: { // Only now, we continue and parse the games.
                        mustBack5Minutes = false;
                        break;
                    }
                    case 400: {
                        publishProgress("Bad request");
                        Logger.appendLog("Error 02 - Bad request");
                        return null;
                    }
                    case 404: {
                        publishProgress("There were no URF games in " + (minutesToBack + 5) + " minutes\nLoading 5 minutes more...");
                        setMinutesToBack(minutesToBack + 5);
                        Logger.appendLog("Error 03 - 404 Not Found");
                        break;
                    }
                    case 429: {
                        publishProgress("Rate limit exceeded");
                        Logger.appendLog("Error 04 - Rate limit exceeded");
                        return null;
                    }
                    case 500: {
                        publishProgress("Internal server error");
                        Logger.appendLog("Error 05 - Internal server error");
                        return null;
                    }
                    case 503: {
                        publishProgress("Service unavailable");
                        Logger.appendLog("Error 06 - Service unavailable");
                        return null;
                    }
                    default: {
                        publishProgress("Unknown error");
                        Logger.appendLog("Error 07 - Unknown error - Status line: " + loLResponse.getStatus());
                        return null;
                    }
                }
            } while (mustBack5Minutes);

            // If the do - while ends and this code is executed, then the games were loaded correctly.
            try {
                // Insert the games ID into a list and return it.
                JSONArray games = new JSONArray(loLResponse.getJsonString());
                List<String> gamesId = new LinkedList<>();
                for (int i = 0; i<games.length(); i++){
                    Long id = games.getLong(i);
                    gamesId.add(id.toString());
                }
                publishProgress("");
                return gamesId;
            } catch (JSONException e) {
                Logger.appendLog("Error 08 - Unknown error" + e.toString());
                publishProgress("An error that should never happen has happened :)");
                return null;
            }
        }

        // Get a valid date (example: 11:00, 11:05, 11:10...) less the amount of backMinutes.
        private String getCurrentValidDate(int backMinutes){
            Date currentDate = new Date();
            Integer minutes = currentDate.getMinutes();
            // This two operations are not stupid! Remember we are working with integers!
            minutes = minutes / 5;
            minutes = minutes * 5;
            currentDate.setSeconds(0);
            currentDate.setMinutes(minutes-backMinutes);
            Long currentDateLong = currentDate.getTime();
            String currentDateStr = currentDateLong.toString();
            return currentDateStr.substring(0, currentDateStr.length()-3);
        };
    }

    /**********************************************************************************************/

    // Uncaught exceptions handler.

    // The following things are used to log the uncaught exceptions in a file. They are not
    // really relevant for the app functionality, but also important.
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    String error = "-----------------------------------------\nFATAL - Black Code: An unhandled exception has occured!\n";
                    error += "Reason of the error: " + ex.getMessage() + "\nTRACE:\n";
                    StackTraceElement[] trace = ex.getStackTrace();
                    for (StackTraceElement aTrace : trace) error += aTrace.toString() + "\n";
                    error += "CAUSE:\n";
                    StackTraceElement[] cause = ex.getCause().getStackTrace();
                    for (StackTraceElement aCause : cause) error += aCause.toString() + "\n";
                    error += "-----------------------------------------\n";
                    Logger.appendLog(error);
                    // Re-throw critical exception further to the os (important)
                    defaultUEH.uncaughtException(thread, ex);
                }
            };

}
