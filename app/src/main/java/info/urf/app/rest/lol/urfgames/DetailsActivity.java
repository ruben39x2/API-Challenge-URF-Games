package info.urf.app.rest.lol.urfgames;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import util.DataVersionGetter;
import util.LoLBuildData;
import util.LoLDataParser;
import util.Logger;

// DetailsActivity.java

// Shows the details of a player in a game.


public class DetailsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Auxiliar fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private LoLBuildData build = null;
    private String JsonParticipant = null;
    private String JsonTeam = null;
    private byte[] icon = null;


    /**
     * ******************************************************************************************
     */

    // Own methods.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the variables that came from the previous activity.
        this.icon = getIntent().getByteArrayExtra("icon");
        this.JsonParticipant = getIntent().getStringExtra("participant");
        this.JsonTeam = getIntent().getStringExtra("team");

        // Inflate the view.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Manage the status of the variable build (the items of the player).
        if (savedInstanceState == null) {
            // Activity created for the first time.
            // Start the asyncTask that is going to download the images of the icons.
            new LoadItemsTask().execute(getItemsId());
        } else {
            if (savedInstanceState.getSerializable("build") == null) {
                // Activity being restored, but items not loaded yet.
                // Start the asyncTask that is going to download the images of the icons.
                new LoadItemsTask().execute(getItemsId());
            } else {
                // Items already loaded.
                this.build = (LoLBuildData) savedInstanceState.getSerializable("build");
            }
        }

        // Variable for the drawer fragment.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.build != null) outState.putSerializable("build", this.build);
    }

    // This will manage each interaction with the list of the drawer.
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0)
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentStats.newInstance(this.icon, this.JsonParticipant))
                    .commit();
        if (position == 1)
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentBuild.newInstance(this.icon, this.build))
                    .commit();
        if (position == 2)
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentTeam.newInstance(this.JsonTeam))
                    .commit();
    }


    /**
     * ******************************************************************************************
     */

    // Private methods.

    // This will be called at the end of the asyncTask.
    private void writeGlobalVariableBuild(LoLBuildData items) {
        this.build = items;
    }

    // Get the items ID of the current participant var.
    private Long[] getItemsId() {
        final int MAX_ITEMS = 7;
        Long[] itemsId = new Long[MAX_ITEMS];

        JSONObject participant;
        try {
            participant = new JSONObject(this.JsonParticipant);
        } catch (JSONException e) {
            Logger.appendLog("Error 34 - " + e.toString());
            return null;
        }
        // Get the item IDs.
        for (int i = 0; i < itemsId.length; i++) {
            itemsId[i] = LoLDataParser.getItemId(participant, i);
        }
        return itemsId;
    }

    /**
     * ******************************************************************************************
     */

    // Subclasses.

    // Fragment for the stats.

    public static class FragmentStats extends Fragment {
        private final int IMAGE_SIZE = 128;

        public static FragmentStats newInstance(byte[] icon, String jsonParticipant) {
            FragmentStats fragment = new FragmentStats();
            Bundle args = new Bundle();
            args.putByteArray("icon", icon);
            args.putString("participant", jsonParticipant);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Initialize the json.
            JSONObject participant;
            try {
                participant = new JSONObject(getArguments().getString("participant"));
            } catch (JSONException e) {
                Logger.appendLog("Error 31 - " + e.toString());
                return inflater.inflate(R.layout.fragment_stats, container, false);
            }

            // Initialize the rootView (the parent view which contains everything for this fragment)
            View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

            // Initialize the views.
            ImageView champIcon = (ImageView) rootView.findViewById(R.id.imageViewStatsChampion);
            TextView tVContributionForKill = (TextView) rootView.findViewById(R.id.textViewStatsContributionForKill);
            TextView tVKills = (TextView) rootView.findViewById(R.id.textViewStatsKills);
            TextView tVDeaths = (TextView) rootView.findViewById(R.id.textViewStatsDeaths);
            TextView tVAssists = (TextView) rootView.findViewById(R.id.textViewStatsAssists);
            TextView tVMinionsKilled = (TextView) rootView.findViewById(R.id.textViewStatsMinionsKilled);
            TextView tVNeutralMinionsKilled = (TextView) rootView.findViewById(R.id.textViewStatsNeutralMinionsKilled);
            TextView tVLargestMultikill = (TextView) rootView.findViewById(R.id.textViewStatsLargestMultiKill);
            TextView tVLargestKillingSpree = (TextView) rootView.findViewById(R.id.textViewStatsLargestKillingSpree);
            TextView tVTowerKills = (TextView) rootView.findViewById(R.id.textViewStatsTowerKills);
            TextView tVInhibitorKills = (TextView) rootView.findViewById(R.id.textViewStatsInhibitorKills);
            TextView tVGoldEarned = (TextView) rootView.findViewById(R.id.textViewStatsGoldEarned);
            TextView tVChampLevel = (TextView) rootView.findViewById(R.id.textViewStatsChampLevel);
            TextView tVTotalHeal = (TextView) rootView.findViewById(R.id.textViewStatsTotalHeal);
            TextView tVTrueDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsTrueDamageDealtToChampions);
            TextView tVMagicDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsMagicDamageDealtToChampions);
            TextView tVPhysicalDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsPhysicalDamageDealtToChampions);

            // Set the champion icon.
            byte[] image = getArguments().getByteArray("icon");
            Bitmap icon = null;
            if (image != null && image.length > 0) {
                icon = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            icon = Bitmap.createScaledBitmap(icon, IMAGE_SIZE, IMAGE_SIZE, false);
            champIcon.setImageBitmap(icon);

            // Set the texts.
            tVContributionForKill.setText(getString(R.string.contribution_for_kill) + LoLDataParser.getContributionForKill(participant)
                    .replace("perfect", getString(R.string.perfect_kda))
                    .replace("unknown", getString(R.string.unknown)));
            tVKills.setText(getString(R.string.kills) + LoLDataParser.getKills(participant));
            tVDeaths.setText(getString(R.string.deaths) + LoLDataParser.getDeaths(participant));
            tVAssists.setText(getString(R.string.assists) + LoLDataParser.getAssists(participant));
            tVMinionsKilled.setText(getString(R.string.minions_killed) + LoLDataParser.getMinionsKilled(participant));
            tVNeutralMinionsKilled.setText(getString(R.string.neutral_minions_killed) + LoLDataParser.getNeutralMinionsKilled(participant));
            tVLargestMultikill.setText(getString(R.string.largest_multikill) + LoLDataParser.getLargestMultiKill(participant));
            tVLargestKillingSpree.setText(getString(R.string.largest_killing_spree) + LoLDataParser.getLargestKillingSpree(participant));
            tVTowerKills.setText(getString(R.string.turrets_destroyed) + LoLDataParser.getTowerKills(participant));
            tVInhibitorKills.setText(getString(R.string.inhibitors_destroyed) + LoLDataParser.getInhibitorKills(participant));
            tVGoldEarned.setText(getString(R.string.gold_earned) + LoLDataParser.getGoldEarned(participant));
            tVChampLevel.setText(getString(R.string.champ_level) + LoLDataParser.getChampLevel(participant));
            tVTotalHeal.setText(getString(R.string.heal) + LoLDataParser.getTotalHeal(participant));
            tVTrueDamageDealtToChampions.setText(getString(R.string.truedmg) + LoLDataParser.getTrueDamageDealtToChampions(participant));
            tVMagicDamageDealtToChampions.setText(getString(R.string.magicdmg) + LoLDataParser.getMagicDamageDealtToChampions(participant));
            tVPhysicalDamageDealtToChampions.setText(getString(R.string.physicaldmg) + LoLDataParser.getPhysicalDamageDealtToChampions(participant));

            // Return the rootView.
            return rootView;
        }
    }

    // Fragment for the build.

    public static class FragmentBuild extends Fragment {
        private final int IMAGE_SIZE = 128;

        public static FragmentBuild newInstance(byte[] icon, LoLBuildData build) {
            FragmentBuild fragment = new FragmentBuild();
            Bundle args = new Bundle();
            args.putByteArray("icon", icon);
            if (build != null) args.putSerializable("build", build);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Variables for setting up the view.
            LoLBuildData build;
            View rootView = inflater.inflate(R.layout.fragment_build, container, false);
            ImageView[] items = new ImageView[]{
                    (ImageView) rootView.findViewById(R.id.imageViewItem1),
                    (ImageView) rootView.findViewById(R.id.imageViewItem2),
                    (ImageView) rootView.findViewById(R.id.imageViewItem3),
                    (ImageView) rootView.findViewById(R.id.imageViewItem4),
                    (ImageView) rootView.findViewById(R.id.imageViewItem5),
                    (ImageView) rootView.findViewById(R.id.imageViewItem6),
                    (ImageView) rootView.findViewById(R.id.imageViewItem7)
            };

            // Set the image for the champion icon.
            byte[] image = getArguments().getByteArray("icon");
            Bitmap icon = null;
            if (image != null && image.length > 0) {
                icon = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            icon = Bitmap.createScaledBitmap(icon, IMAGE_SIZE, IMAGE_SIZE, false);
            ImageView champIcon = (ImageView) rootView.findViewById(R.id.imageViewBuildChampion);
            champIcon.setImageBitmap(icon);

            // Now, show the items if they have been loaded and don't do it if they're still loading.
            build = (LoLBuildData) getArguments().getSerializable("build");
            if (build != null) { // Things loaded.
                for (int i = 0; i < build.getCount(); i++) {
                    items[i].setImageBitmap(build.getItem(i));
                }
            }

            return rootView;
        }
    }

    // Fragment for the team.

    public static class FragmentTeam extends Fragment {

        public static FragmentTeam newInstance(String jsonTeam) {
            FragmentTeam fragment = new FragmentTeam();
            Bundle args = new Bundle();
            args.putString("team", jsonTeam);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Initialize the json.
            JSONObject team;
            try {
                team = new JSONObject(getArguments().getString("team"));
            } catch (JSONException e) {
                Logger.appendLog("Error 37 - " + e.toString());
                return inflater.inflate(R.layout.fragment_team, container, false);
            }

            // Initialize the rootView (the parent view which contains everything for this fragment)
            View rootView = inflater.inflate(R.layout.fragment_team, container, false);

            // Initialize the views.
            TextView tVTeam = (TextView) rootView.findViewById(R.id.textViewTeam);
            TextView tVResult = (TextView) rootView.findViewById(R.id.textViewTeamResult);
            TextView tVAchievements = (TextView) rootView.findViewById(R.id.textViewTeamAchievements);

            // Generate a string with the achievements.
            String achievements = "";

            if (LoLDataParser.getFirstBlood(team))
                achievements += getString(R.string.team_first_blood);
            if (LoLDataParser.getFirstDragon(team))
                achievements += getString(R.string.team_first_drake);
            if (LoLDataParser.getFirstBaron(team))
                achievements += getString(R.string.team_first_baron);
            if (LoLDataParser.getFirstTower(team))
                achievements += getString(R.string.team_first_tower);
            if (LoLDataParser.getFirstInhibitor(team))
                achievements += getString(R.string.team_first_inhibitor);
            achievements += "\n";
            achievements += getString(R.string.team_towers_destroyed) + LoLDataParser.getTeamTowerKills(team) + "\n";
            achievements += getString(R.string.team_inhibitors_destroyed) + LoLDataParser.getTeamInhibitorKills(team) + "\n";
            achievements += getString(R.string.team_dragon_kills) + LoLDataParser.getTeamDragonKills(team) + "\n";
            achievements += getString(R.string.team_baron_kills) + LoLDataParser.getTeamBaronKills(team) + "\n";

            // Set the views.

            // The team ID.
            if (LoLDataParser.getTeamId(team) == 100) {
                // Blue team.
                tVTeam.setText(getString(R.string.blue_team));
                tVTeam.setTextColor(Color.rgb(10, 10, 250));
            } else {
                if (LoLDataParser.getTeamId(team) == 200) {
                    // Red team.
                    tVTeam.setText(getString(R.string.red_team));
                    tVTeam.setTextColor(Color.rgb(250, 10, 10));
                } else {
                    // Unknown team.
                    tVTeam.setText(getString(R.string.unknown));
                }
            }

            // The "VICTORY" or "DEFEAT" text.
            if (LoLDataParser.getTeamWinner(team)) {
                // Blue team.
                tVResult.setText(getString(R.string.team_victory));
            } else {
                if (!LoLDataParser.getTeamWinner(team)) {
                    // Red team.
                    tVResult.setText(getString(R.string.team_defeat));
                } else {
                    // Unknown team.
                    tVResult.setText(getString(R.string.unknown));
                }
            }

            // The achievements.
            tVAchievements.setText(achievements);

            return rootView;
        }
    }

    private class LoadItemsTask extends AsyncTask<Long[], Void, LoLBuildData> {
        private final int IMAGE_SIZE = 64;

        @Override
        protected LoLBuildData doInBackground(Long[]... params) {
            LoLBuildData result = new LoLBuildData();
            Long[] items = params[0];
            String VERSION;
            VERSION = DataVersionGetter.getVersion();
            for (int i = 0; i < 7; i++) {
                if (!items[i].equals((long) 0)) { // If we, a priori, have a valid icon Id.
                    try {
                        InputStream in = new java.net.URL("http://ddragon.leagueoflegends.com/cdn/" + VERSION + "/img/item/" +
                                items[i] + ".png").openStream();
                        Bitmap image = BitmapFactory.decodeStream(in);
                        result.addItem(Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, false));
                    } catch (Exception e) {
                        Logger.appendLog("Error 35 - " + e.toString());
                        result.addItem(Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(getResources(), R.drawable.unknown),
                                IMAGE_SIZE, IMAGE_SIZE, false));
                    }
                } else {
                    result.addItem(Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(getResources(), R.drawable.no_item),
                            IMAGE_SIZE, IMAGE_SIZE, false));
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoLBuildData bitmaps) {
            super.onPostExecute(bitmaps);
            writeGlobalVariableBuild(bitmaps);
        }
    }


}
