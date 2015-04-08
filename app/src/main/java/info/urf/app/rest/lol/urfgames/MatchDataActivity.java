package info.urf.app.rest.lol.urfgames;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import util.APIKey;
import util.DataVersionGetter;
import util.LoLDataParser;
import util.LoLMatchData;
import util.LoLResponse;
import util.Logger;
import util.SendRequest;

// MatchDataActivity.java

// Shows the overall info of a match.

public class MatchDataActivity extends ActionBarActivity {
    private final Context context = this;
    private LoLMatchData matchData = null;
    private String REGION = "euw";

    /**
     * ******************************************************************************************
     */

    // Methods overrided from the superclass.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_data);
        String gameId = getIntent().getStringExtra("matchId");

        // Get the region.
        getRegionFromPreferences();

        // Start the load.
        if (savedInstanceState != null) {
            // MatchData was loaded correctly. We have to setup it.
            if (savedInstanceState.getSerializable("matchData") != null) {
                this.matchData = (LoLMatchData) savedInstanceState.getSerializable("matchData");
                setupMatchData();
            } else
                // The load failed or was cancelled...
                // ...that means...
                // ...we have to restart it.
                loadMatchInfo(gameId);
        } else {
            loadMatchInfo(gameId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.matchData != null) outState.putSerializable("matchData", this.matchData);
    }

    /**
     * ******************************************************************************************
     */

    // Private methods.
    private void loadMatchInfo(String matchId) {
        new LoadMatchTask().execute(matchId);
    }

    private void writeActivityVariableMatchData(LoLMatchData loLMatchData) {
        this.matchData = loLMatchData;
    }

    private void getRegionFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("URFGames", MODE_PRIVATE);
        this.REGION = prefs.getString("region", "EUW").toLowerCase();
    }

    private void setupMatchData() {
        final int IMAGE_SIZE = 72;
        // Initialize the views.
        TextView title = (TextView) findViewById(R.id.textViewMatchTitle);
        TextView subtitle = (TextView) findViewById(R.id.textViewMatchSubtitle);
        TextView textVS = (TextView) findViewById(R.id.textViewVS);
        TextView team100result = (TextView) findViewById(R.id.textViewTeam100Result);
        TextView team200result = (TextView) findViewById(R.id.textViewTeam200Result);
        ImageButton[] icons = new ImageButton[]{
                (ImageButton) findViewById(R.id.imageButton101),
                (ImageButton) findViewById(R.id.imageButton102),
                (ImageButton) findViewById(R.id.imageButton103),
                (ImageButton) findViewById(R.id.imageButton104),
                (ImageButton) findViewById(R.id.imageButton105),
                (ImageButton) findViewById(R.id.imageButton201),
                (ImageButton) findViewById(R.id.imageButton202),
                (ImageButton) findViewById(R.id.imageButton203),
                (ImageButton) findViewById(R.id.imageButton204),
                (ImageButton) findViewById(R.id.imageButton205)
        };
        TextView[] scores = new TextView[]{
                (TextView) findViewById(R.id.textViewScore101),
                (TextView) findViewById(R.id.textViewScore102),
                (TextView) findViewById(R.id.textViewScore103),
                (TextView) findViewById(R.id.textViewScore104),
                (TextView) findViewById(R.id.textViewScore105),
                (TextView) findViewById(R.id.textViewScore201),
                (TextView) findViewById(R.id.textViewScore202),
                (TextView) findViewById(R.id.textViewScore203),
                (TextView) findViewById(R.id.textViewScore204),
                (TextView) findViewById(R.id.textViewScore205)
        };
        TextView[] leagues = new TextView[]{
                (TextView) findViewById(R.id.textViewLeague101),
                (TextView) findViewById(R.id.textViewLeague102),
                (TextView) findViewById(R.id.textViewLeague103),
                (TextView) findViewById(R.id.textViewLeague104),
                (TextView) findViewById(R.id.textViewLeague105),
                (TextView) findViewById(R.id.textViewLeague201),
                (TextView) findViewById(R.id.textViewLeague202),
                (TextView) findViewById(R.id.textViewLeague203),
                (TextView) findViewById(R.id.textViewLeague204),
                (TextView) findViewById(R.id.textViewLeague205)
        };

        // Let's get the array with the info of the participants. They are ordered like this:
        // Firstly the team 100 participants.
        // Secondly the team 200 participants.
        // If they were unordered, all of this would incorrect. We should consider making the
        // LoLMatchData two Bitmap arrays (one for team 100 and another for team 200), and
        // get the data ordered in the asyncTask.
        // But it's not that way, so we can do this as simply as using a 10 elements array - efficient.
        JSONArray participants;
        try {
            participants = this.matchData.getJsonObject().getJSONArray("participants");
        } catch (JSONException e) {
            title.setText(getString(R.string.wtf_this_should_never_happen));
            Logger.appendLog("Error 25 - " + e.toString());
            return;
        }

        // And now, set the views.
        title.setText(getString(R.string.urf));
        subtitle.setText(LoLDataParser.getMatchDuration(context, this.matchData.getJsonObject()));
        for (int i = 0; i < this.matchData.getChampImages().length; i++) {
            Bitmap image = this.matchData.getChampImages()[i];
            // Set the icons.
            icons[i].setImageBitmap(Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, false));
            icons[i].setVisibility(View.VISIBLE);
            // Set the scores.
            try {
                scores[i].setText(LoLDataParser.getKDA(context, participants.getJSONObject(i)));
            } catch (JSONException e) {
                Logger.appendLog("Error 28 - " + e.toString());
                scores[i].setText(getString(R.string.unknown));
            }
            // Set the leagues.
            try {
                leagues[i].setText(LoLDataParser.getLeague(context, participants.getJSONObject(i)));
            } catch (JSONException e) {
                Logger.appendLog("Error 29 - " + e.toString());
                leagues[i].setText(getString(R.string.unknown));
            }
        }
        // And the texts "victory" and "defeat"
        if (LoLDataParser.getWinnerTeam(this.matchData.getJsonObject()) == 100) {
            team100result.setText(getString(R.string.victory));
            team200result.setText(getString(R.string.defeat));
            team100result.setTextColor(Color.rgb(30, 240, 20));
            team200result.setTextColor(Color.rgb(240, 30, 20));
        } else if (LoLDataParser.getWinnerTeam(this.matchData.getJsonObject()) == 200) {
            team100result.setText(getString(R.string.defeat));
            team200result.setText(getString(R.string.victory));
            team100result.setTextColor(Color.rgb(240, 30, 20));
            team200result.setTextColor(Color.rgb(30, 240, 20));
        } else { // Something did not work ok... :(
            team100result.setText(getString(R.string.unknown));
            team200result.setText(getString(R.string.unknown));
        }
        // And the text for "V.S."
        textVS.setText("VS");
    }

    /**
     * ******************************************************************************************
     */

    // onClick methods.
    public void onClickDetails(View v) {
        Integer[] viewsId = new Integer[]{
                R.id.imageButton101,
                R.id.imageButton102,
                R.id.imageButton103,
                R.id.imageButton104,
                R.id.imageButton105,
                R.id.imageButton201,
                R.id.imageButton202,
                R.id.imageButton203,
                R.id.imageButton204,
                R.id.imageButton205
        };
        final Intent intent = new Intent(this, DetailsActivity.class);
        try {
            // Check what button was called.
            for (int i = 0; i < viewsId.length; i++)
                if (v.getId() == viewsId[i]) {
                    // Put the "participant" object
                    intent.putExtra("participant", this.matchData.getJsonObject().getJSONArray("participants").getJSONObject(i).toString());
                    // Put the champ icon.
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    boolean success = this.matchData.getChampImages()[i].compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                    if (success)
                        intent.putExtra("icon", byteStream.toByteArray());
                    // Put the team.
                    if (i < 5)
                        intent.putExtra("team", this.matchData.getJsonObject().getJSONArray("teams").getJSONObject(0).toString());
                    else
                        intent.putExtra("team", this.matchData.getJsonObject().getJSONArray("teams").getJSONObject(1).toString());
                    break;
                }
        } catch (JSONException e) {
            Logger.appendLog("Error 30 - " + e.toString());
            Toast.makeText(getApplicationContext(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }

    /**
     * ******************************************************************************************
     */

    // Subclasses.

    // AsyncTask that will load asynchronously the game data (and champ icons).

    private class LoadMatchTask extends AsyncTask<String, String, LoLMatchData> {

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
            if (loLMatchData == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();
            } else {
                pB.setVisibility(View.INVISIBLE);
                tV.setText("");
                setupMatchData();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            TextView tV = (TextView) findViewById(R.id.textViewLoadMatch);
            tV.setText(values[0]);
        }

        // Will return a null if an unrecoverable error happened; a valid LoLMatchData else.
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
                    String matchDataString = loLResponse.getJsonString();
                    String champKeys = loadChampKeys(); // May return null.
                    if (champKeys == null)
                        return null; // If so, we consider it as an unrecoverable error.
                    return loadIconsAndFinish(champKeys, matchDataString);
                }
                case 400: {
                    publishProgress(getString(R.string.bad_request));
                    Logger.appendLog("Error 10 - Bad request");
                    return null;
                }
                case 404: {
                    publishProgress(getString(R.string.not_found));
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

        // Loads a string containing the result of
        // https://global.api.pvp.net/api/lol/static-data/euw/v1.2/champion?dataById=true&api_key=xxx
        // Returns null if failed.
        private String loadChampKeys() {
            publishProgress(getString(R.string.loading_champs));
            LoLResponse loLResponse;

            String request = "https://global.api.pvp.net/api/lol/static-data/" + REGION +
                    "/v1.2/champion?dataById=true&api_key=" + APIKey.KEY;
            Log.d("Request", request);
            loLResponse = SendRequest.get(request);

            // Check the result of the petition.
            switch (loLResponse.getStatus()) {
                case -1: {
                    publishProgress(getString(R.string.connection_error));
                    Logger.appendLog("Error 16 - " + loLResponse.getError());
                    return null;
                }
                case 200: { // Only now, we continue
                    return loLResponse.getJsonString();
                }
                case 400: {
                    publishProgress(getString(R.string.bad_request));
                    Logger.appendLog("Error 17 - Bad request");
                    return null;
                }
                case 404: {
                    publishProgress(getString(R.string.not_found));
                    Logger.appendLog("Error 18 - 404 Not Found");
                    return null;
                }
                case 429: {
                    publishProgress(getString(R.string.rate_limit_exceeded));
                    Logger.appendLog("Error 19 - Rate limit exceeded");
                    return null;
                }
                case 500: {
                    publishProgress(getString(R.string.internal_server));
                    Logger.appendLog("Error 20 - Internal server error");
                    return null;
                }
                case 503: {
                    publishProgress(getString(R.string.service_unavailable));
                    Logger.appendLog("Error 21 - Service unavailable");
                    return null;
                }
                default: {
                    publishProgress(getString(R.string.unknown_error));
                    Logger.appendLog("Error 22 - Unknown error - Status line: " + loLResponse.getStatus());
                    return null;
                }
            }
        }

        // Receives a String representating the champion keys and other one with the match data, and
        // uses them to get the champ icons. Then, it put all together in a LoLMatchData object and returns it.
        private LoLMatchData loadIconsAndFinish(String jsonStringChampKeys, String jsonStringMatchData) {
            final Integer NUM_SUMMONERS = 10;
            String[] champKeys = new String[NUM_SUMMONERS];
            Bitmap[] champImages = new Bitmap[NUM_SUMMONERS];
            JSONObject matchData;
            String VERSION;
            VERSION = DataVersionGetter.getVersion();
            try {
                matchData = new JSONObject(jsonStringMatchData);
                JSONObject champsData = new JSONObject(jsonStringChampKeys).getJSONObject("data");
                JSONArray participants = matchData.getJSONArray("participants");

                // Let's gather the 10 champions Keys (we need them to load their images).
                for (int i = 0; i < participants.length(); i++) {
                    Integer champId = participants.getJSONObject(i).getInt("championId");
                    champKeys[i] = champsData.getJSONObject(champId.toString()).getString("key");
                }
            } catch (JSONException e) {
                Logger.appendLog("Error 23 - Unknown error" + e.toString());
                publishProgress(getString(R.string.wtf_this_should_never_happen));
                return null;
            }

            // Now, we have an array with the key of the 10 champions. The key is what we needed
            // in order to download their icons. Now, let's download their icons.
            for (int i = 0; i < champKeys.length; i++) {
                publishProgress(getString(R.string.loading_images) + i + "/" + NUM_SUMMONERS.toString() + ")");
                Bitmap image;
                String url = "http://ddragon.leagueoflegends.com/cdn/" + VERSION + "/img/champion/" +
                        champKeys[i] + ".png";
                InputStream in;
                try {
                    in = new java.net.URL(url).openStream();
                    image = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Logger.appendLog("Error 24 - URL error" + e.toString());
                    image = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
                }
                champImages[i] = image;
            }
            return new LoLMatchData(matchData, champImages);
        }
    }
}

