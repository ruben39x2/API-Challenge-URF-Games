package info.urf.app.rest.lol.urfgames;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.LoLDataParser;
import util.Logger;

// DetailsActivity.java

// Shows the details of a player in a game.


public class DetailsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Auxiliar fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private String JsonParticipant = null;
    private String JsonTeam = null;
    private byte[] icon = null;


    /**********************************************************************************************/

    // Own methods.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the variables that came from the previous activity.
        this.icon = getIntent().getByteArrayExtra("icon");
        this.JsonParticipant = getIntent().getStringExtra("participant");
        this.JsonTeam = getIntent().getStringExtra("team");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        // Variable for the drawer fragment.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Variables, variables...

        //new LoadItemsTask().execute(new ArrayList<String>());

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
                    .replace(R.id.container, FragmentBuild.newInstance(this.icon))
                    .commit();
        if (position == 2)
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentTeam.newInstance())
                    .commit();
    }

    /**********************************************************************************************/

    // Subclasses.

    // Fragment for the stats.

    public static class FragmentStats extends Fragment {
        private final int IMAGE_SIZE = 128;
        private final int WIDTH = 30;

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
            TextView tVKills = (TextView) rootView.findViewById(R.id.textViewStatsKills);
            TextView tVDeaths = (TextView) rootView.findViewById(R.id.textViewStatsDeaths);
            TextView tVAssists = (TextView) rootView.findViewById(R.id.textViewStatsAssists);
            TextView tVMinionsKilled = (TextView) rootView.findViewById(R.id.textViewStatsMinionsKilled);
            TextView tVNeutralMinionsKilled = (TextView) rootView.findViewById(R.id.textViewStatsNeutralMinionsKilled);
            TextView tVLargestMultikill = (TextView) rootView.findViewById(R.id.textViewStatsLargestMultiKill);
            TextView tVLargestKillingSpree = (TextView) rootView.findViewById(R.id.textViewStatsLargestKillingSpree);
            TextView tVGoldEarned = (TextView) rootView.findViewById(R.id.textViewStatsGoldEarned);
            TextView tVChampLevel = (TextView) rootView.findViewById(R.id.textViewStatsChampLevel);
            TextView tVTotalHeal = (TextView) rootView.findViewById(R.id.textViewStatsTotalHeal);
            TextView tVTrueDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsTrueDamageDealtToChampions);
            TextView tVMagicDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsMagicDamageDealtToChampions);
            TextView tVPhysicalDamageDealtToChampions = (TextView) rootView.findViewById(R.id.textViewStatsPhysicalDamageDealtToChampions);

            // Set the champion icon.
            byte [] image = getArguments().getByteArray("icon");
            Bitmap icon = null;
            if (image != null && image.length > 0) {
                icon = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            icon = Bitmap.createScaledBitmap(icon, IMAGE_SIZE, IMAGE_SIZE, false);
            champIcon.setImageBitmap(icon);

            // Set the texts.
            tVKills.setText("Kills: " + LoLDataParser.getKills(participant));
            tVDeaths.setText("Deaths: " + LoLDataParser.getDeaths(participant));
            tVAssists.setText("Assists: " + LoLDataParser.getAssists(participant));
            tVMinionsKilled.setText("Minions killed: " + LoLDataParser.getMinionsKilled(participant));
            tVNeutralMinionsKilled.setText("Neutral minions killed: " + LoLDataParser.getNeutralMinionsKilled(participant));
            tVLargestMultikill.setText("Largest multikill: " + LoLDataParser.getLargestMultiKill(participant));
            tVLargestKillingSpree.setText("Largest killing spree: " + LoLDataParser.getLargestKillingSpree(participant));
            tVGoldEarned.setText("Gold earned: " + LoLDataParser.getGoldEarned(participant));
            tVChampLevel.setText("Champion level: " + LoLDataParser.getChampLevel(participant));

            tVTotalHeal.setText("Total heal: " + LoLDataParser.getTotalHeal(participant));
            tVTrueDamageDealtToChampions.setText("True damage dealt to champions: " + LoLDataParser.getTrueDamageDealtToChampions(participant));
            tVMagicDamageDealtToChampions.setText("Magic damage dealt to champions: " + LoLDataParser.getMagicDamageDealtToChampions(participant));
            tVPhysicalDamageDealtToChampions.setText("Physical damage dealt to champions: " + LoLDataParser.getPhysicalDamageDealtToChampions(participant));

            // Return the rootView.
            return rootView;
        }
    }

    // Fragment for the build.

    public static class FragmentBuild extends Fragment {
        private final int IMAGE_SIZE = 128;

        public static FragmentBuild newInstance(byte[] icon) {
            FragmentBuild fragment = new FragmentBuild();
            Bundle args = new Bundle();
            args.putByteArray("icon", icon);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_build, container, false);

            // Set the things
            byte [] image = getArguments().getByteArray("icon");
            Bitmap icon = null;
            if (image != null && image.length > 0) {
                icon = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            icon = Bitmap.createScaledBitmap(icon, IMAGE_SIZE, IMAGE_SIZE, false);
            ImageView champIcon = (ImageView) rootView.findViewById(R.id.imageViewBuildChampion);
            champIcon.setImageBitmap(icon);
            return rootView;
        }
    }

    // Fragment for the team.

    public static class FragmentTeam extends Fragment {

        public static FragmentTeam newInstance() {
            FragmentTeam fragment = new FragmentTeam();
            Bundle args = new Bundle();
            //args.putByteArray("icon", icon);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_team, container, false);

            // Set the things
            return rootView;
        }

    }

    private class LoadItemsTask extends AsyncTask<List<String>, Void, List<Bitmap>>{
        @Override
        protected List<Bitmap> doInBackground(List<String>... params) {
            return null;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);

            //onNavigationDrawerItemSelected(0);
        }
    }


}
