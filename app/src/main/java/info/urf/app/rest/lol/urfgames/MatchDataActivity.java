package info.urf.app.rest.lol.urfgames;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.APIKey;
import util.LoLMatchData;
import util.LoLResponse;
import util.Logger;
import util.SendRequest;

// MatchDataActivity.java

// Shows the overall info of a match.

public class MatchDataActivity extends ActionBarActivity {
    private LoLMatchData matchData = null;
    private String REGION = "euw";

    /**********************************************************************************************/

    // Methods overrided from the superclass.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_data);

        // Get the region.
        getRegionFromPreferences();


        TextView tV = (TextView) findViewById(R.id.textViewMatchTitle);
        String gameId = getIntent().getStringExtra("matchId");
        tV.setText(gameId);

        loadMatchInfo(gameId);
    }

    /**********************************************************************************************/

    // Private methods.

    private void loadMatchInfo(String matchId){
        new LoadMatchTask().execute(matchId);
    }

    private void writeActivityVariableMatchData(LoLMatchData loLMatchData){
        this.matchData = loLMatchData;
    }

    private void getRegionFromPreferences(){
        SharedPreferences prefs = getSharedPreferences("URFGames", MODE_PRIVATE);
        this.REGION = prefs.getString("region", "EUW").toLowerCase();
    }


    /**********************************************************************************************/

    // Subclasses.

    // AsyncTask that will load asynchronously the game data (and champ icons).

    private class LoadMatchTask extends AsyncTask<String, String, LoLMatchData>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pB = (ProgressBar) findViewById(R.id.progressBarMatch);
            TextView tV = (TextView) findViewById(R.id.textViewLoadMatch);

            pB.setVisibility(View.VISIBLE);
            tV.setText(getString(R.string.loading_data));
        }

        @Override
        protected void onPostExecute(LoLMatchData loLMatchData) {
            super.onPostExecute(loLMatchData);
            ProgressBar pB = (ProgressBar) findViewById(R.id.progressBarMatch);
            TextView tV = (TextView) findViewById(R.id.textViewLoadMatch);

            writeActivityVariableMatchData(loLMatchData);
            pB.setVisibility(View.INVISIBLE);
            tV.setText("");
        }

        @Override
        protected LoLMatchData doInBackground(String... params) {
            LoLResponse loLResponse;

            String request = "https://" + REGION + ".api.pvp.net/api/lol/" + REGION +
                    "/v2.2/match/" + params[0] + "?includeTimeline=false&api_key=" + APIKey.KEY;
            Log.d("Request", request);
            loLResponse = SendRequest.get(request);

            // Check the result of the petition.
            switch (loLResponse.getStatus()) {
                case -1: {
                    publishProgress(getString(R.string.connection_error));
                    Logger.appendLog("Error 09 - " + loLResponse.getError());
                    return null;
                }
                case 200: { // Only now, we continue
                    return loadIconsAndFinish(loLResponse.getJsonString());
                }
                case 400: {
                    publishProgress(getString(R.string.bad_request));
                    Logger.appendLog("Error 10 - Bad request");
                    return null;
                }
                case 404: {
                    publishProgress("Not found");
                    Logger.appendLog("Error 11 - 404 Not Found");
                    return null;
                }
                case 429: {
                    publishProgress(getString(R.string.rate_limit_exceeded));
                    Logger.appendLog("Error 12 - Rate limit exceeded");
                    return null;
                }
                case 500: {
                    publishProgress(getString(R.string.internal_server));
                    Logger.appendLog("Error 13 - Internal server error");
                    return null;
                }
                case 503: {
                    publishProgress(getString(R.string.service_unavailable));
                    Logger.appendLog("Error 14 - Service unavailable");
                    return null;
                }
                default: {
                    publishProgress(getString(R.string.unknown_error));
                    Logger.appendLog("Error 15 - Unknown error - Status line: " + loLResponse.getStatus());
                    return null;
                }
            }
        }

        // Receives a String representating the match data, and uses it to get the summoners Info (level, name...)
        // and download the champ icons. Then, it put all together in a LoLMatchData object and returns it.
        private LoLMatchData loadIconsAndFinish(String jsonString){
            String [] champIds = new String [10];
            try {
                JSONObject matchData = new JSONObject(jsonString);
                // Let's gather the 10 champions Id.
                JSONArray participants = matchData.getJSONArray("participants");
                for (int i = 0; i<participants.length(); i++){
                    // SHIT
                }
            } catch (JSONException e) {
                Logger.appendLog("Error 16 - Unknown error" + e.toString());
                publishProgress(getString(R.string.wtf_this_should_never_happen));
                return null;
            }

            // Now, let's get the summoner's info.
            publishProgress(getString(R.string.loading_names));

            // Let's gather the 10 participants Id.
            return null;
        }
    }
}
