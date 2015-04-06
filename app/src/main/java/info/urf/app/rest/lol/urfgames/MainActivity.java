package info.urf.app.rest.lol.urfgames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
    private RecentURFGamesAdapter adapter;
    private final Context context = this;
    private boolean canLoadMoreGames = false;
    private String REGION = "euw";

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
        initializeGamesList();

        // Load the region.
        getRegionFromPreferences();

        // Set the text of the region.
        TextView tVRegion = (TextView) findViewById(R.id.textViewRegion);
        tVRegion.setText(this.REGION.toUpperCase());

        // Check if the activity was restored.
        if (savedInstanceState != null){
            this.minutesToBack = savedInstanceState.getInt("minutesToBack");
            this.recentGames = savedInstanceState.getStringArrayList("recentGames");
            this.canLoadMoreGames = savedInstanceState.getBoolean("canLoadMoreGames");
            actualizeGamesList();
            // This means: there was an error (a list was saved but it's empty).
            if (this.recentGames.isEmpty()) loadRecentURFGames(); // Let's try to load it again.
        } else {
            loadRecentURFGames();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("recentGames", this.recentGames);
        outState.putInt("minutesToBack", this.minutesToBack);
        outState.putBoolean("canLoadMoreGames", this.canLoadMoreGames);
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

    private ArrayList<String> getActivityVariableRecentGames() {return this.recentGames;}

    private void actualizeGamesList(){
        this.adapter.notifyDataSetChanged();
    }

    private void setMinutesToBack(Integer minutes){
        this.minutesToBack = minutes;
    }

    private Integer getMinutesToBack(){
        return this.minutesToBack;
    }

    private void initializeGamesList() {
        ListView list;
        list = (ListView) findViewById(R.id.listViewGames);
        this.adapter = new RecentURFGamesAdapter();
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMatchDetails(position);
            }
        });
    }

    private void startMatchDetails(int pos) {
        final Intent intent = new Intent(this, MatchDataActivity.class);
        String matchId = this.recentGames.get(pos).split(" ")[0];
        intent.putExtra("matchId", matchId);
        startActivity(intent);
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

                        // Set the textViewRegion.
                        TextView tVRegion = (TextView) findViewById(R.id.textViewRegion);
                        tVRegion.setText(items[item]);

                        // Actualize the variable and delete the list.
                        getRegionFromPreferences();
                        clearActivityVariableRecentGames();
                        actualizeGamesList();

                        // Restart the "minutesToBack" variable.
                        setMinutesToBack(0);

                        // Also restart the load of games.
                        loadRecentURFGames();
                    }
                });
        builder.create().show();
    }

    // Shows a dialog with "about the app" info.
    private void showAbout(){
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(context);
        aboutDialog.setTitle(getString(R.string.about_the_app))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.about))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_cool), new DialogInterface.OnClickListener() {
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
                    getString(R.string.more_games_cannot_be_loaded_now),
                    Toast.LENGTH_LONG).show();
    }

    /**********************************************************************************************/

    // Subclasses.

    // Contains an async task needed to perform asynchronously the load of the recent URF games IDs.
    // Receives nothing and returns a list with Game IDs and the minutes ago they started.
    // IMPORTANT: This asyncTasks uses and manages the global variable "minutes to back".

    public class LoadGamesAsync extends AsyncTask<Void, String, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pB = (ProgressBar) findViewById(R.id.progressBarGames);
            TextView tV = (TextView) findViewById(R.id.textViewLoadGamesProgress);
            TextView tV2 = (TextView) findViewById(R.id.textViewTitle);

            pB.setVisibility(View.VISIBLE);
            tV.setText(getString(R.string.loading_recent));
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
                Log.d("Request", request);
                loLResponse = SendRequest.get(request);

                // Check the result of the petition.
                switch (loLResponse.getStatus()) {
                    case -1: {
                        publishProgress(getString(R.string.connection_error));
                        Logger.appendLog("Error 01 - " + loLResponse.getError());
                        return null;
                    }
                    case 200: { // Only now, we continue and parse the games.
                        mustBack5Minutes = false;
                        break;
                    }
                    case 400: {
                        publishProgress(getString(R.string.bad_request));
                        Logger.appendLog("Error 02 - Bad request");
                        return null;
                    }
                    case 404: {
                        publishProgress(getString(R.string.there_were_no_games_in) + (minutesToBack + 5) + " " + getString(R.string.minutes));
                        setMinutesToBack(minutesToBack + 5);
                        Logger.appendLog("Error 03 - 404 Not Found");
                        break;
                    }
                    case 429: {
                        publishProgress(getString(R.string.rate_limit_exceeded));
                        Logger.appendLog("Error 04 - Rate limit exceeded");
                        return null;
                    }
                    case 500: {
                        publishProgress(getString(R.string.internal_server));
                        Logger.appendLog("Error 05 - Internal server error");
                        return null;
                    }
                    case 503: {
                        publishProgress(getString(R.string.service_unavailable));
                        Logger.appendLog("Error 06 - Service unavailable");
                        return null;
                    }
                    default: {
                        publishProgress(getString(R.string.unknown_error));
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
                    // We will return a list with Strings like: [gameId][blank_space][minutesAgoItStarted]
                    gamesId.add(id.toString() + " " + (minutesToBack + 5));
                }
                publishProgress("");
                setMinutesToBack(minutesToBack + 5); // This is, the next time this asyncTask is executed,
                return gamesId;                      // we will load previous games (not the same).
            } catch (JSONException e) {
                Logger.appendLog("Error 08 - Unknown error" + e.toString());
                publishProgress(getString(R.string.wtf_this_should_never_happen));
                return null;
            }
        }

        // Get a valid date (example: 11:00, 11:05, 11:10...) less the amount of backMinutes.
        private String getCurrentValidDate(int backMinutes){
            Date currentDate = new Date();
            Integer minutes = currentDate.getMinutes();
            // Now we make the minutes to be multiplicative by 5.
            minutes = minutes - (minutes % 5);
            // Adapt the date.
            currentDate.setSeconds(0);
            currentDate.setMinutes(minutes-backMinutes);
            Long currentDateLong = currentDate.getTime();
            String currentDateStr = currentDateLong.toString();

            return currentDateStr.substring(0, currentDateStr.length()-3); // We remove the 3 last characters
        };                                                                 // to convert milliseconds to seconds.
    }

    // Subclass for the custom listView.

    // The adapter won't contain itself any data, it will use the global variable "recentGames"
    private class RecentURFGamesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getActivityVariableRecentGames().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Integer pos = position + 1 ;
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.urf_game_item, parent, false);
            TextView title, sub1, sub2;
            title = (TextView) row.findViewById(R.id.textViewItemTitle);
            sub1 = (TextView) row.findViewById(R.id.textViewItemSubtitle1);
            sub2 = (TextView) row.findViewById(R.id.textViewItemSubtitle2);
            //Set the data.
            // Now we obtain a string array with [0] = gameId and [1] = started ... minutes ago
            String [] itemData = getActivityVariableRecentGames().get(position).split(" ");
            title.setText(pos.toString() + ": " + getString(R.string.urf));
            sub1.setText(getString(R.string.started_over) + itemData[1] + getString(R.string.minutes_ago));
            sub2.setText(getString(R.string.game_id) + " " + itemData[0]);
            return row;
        }
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
                    String error = "-----------------------------------------\nFATAL: An unhandled exception has occurred!\n";
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
