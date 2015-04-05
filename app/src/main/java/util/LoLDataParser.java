package util;

// LoLDataParser.java

// An utility class to parse the JSONs from Riot.

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.urf.app.rest.lol.urfgames.R;

public class LoLDataParser {

    // Receives an object with the result of a matchv2.2 petition, and returns the duration.
    public static String getMatchDuration(Context context, JSONObject match){
        Long matchDuration;
        String duration;
        try {
            matchDuration = match.getLong("matchDuration");
            Long minutes = matchDuration / 60;
            Long seconds = matchDuration % 60;
            duration = minutes + ":" + seconds;
        } catch (JSONException e) {
            duration = context.getString(R.string.unknown_duration);
            Logger.appendLog("Error 24 - " + e.toString());
        }
        return duration;
    }

    // Receives an object with a participant information (see matchv2.2 petition), and returns the KDA.
    public static String getKDA(Context context, JSONObject participant){
        String KDA;
        try {
            JSONObject stats = participant.getJSONObject("stats");
            Long kills = stats.getLong("kills");
            Long deaths = stats.getLong("deaths");
            Long assists = stats.getLong("assists");
            KDA = kills.toString() + "/" + deaths.toString() + "/" + assists.toString();
        } catch (JSONException e) {
            KDA = context.getString(R.string.unknown);
            Logger.appendLog("Error 26 - " + e.toString());
        }
        return KDA;
    }

    // Receives an object with a participant information (see matchv2.2 petition), and returns the
    // Highest League Achieved.
    public static String getLeague(Context context, JSONObject participant){
        try {
            String leagueValue = participant.getString("highestAchievedSeasonTier");
            if (leagueValue.equals("UNRANKED")) return context.getString(R.string.unranked);
            if (leagueValue.equals("BRONZE")) return context.getString(R.string.bronze);
            if (leagueValue.equals("SILVER")) return context.getString(R.string.silver);
            if (leagueValue.equals("GOLD")) return context.getString(R.string.gold);
            if (leagueValue.equals("PLATINUM")) return context.getString(R.string.platinum);
            if (leagueValue.equals("DIAMOND")) return context.getString(R.string.diamond);
            if (leagueValue.equals("MASTER")) return context.getString(R.string.master);
            if (leagueValue.equals("CHALLENGER")) return context.getString(R.string.challenger);
            return context.getString(R.string.unknown);
        } catch (JSONException e) {
            Logger.appendLog("Error 27 - " + e.toString());
            return context.getString(R.string.unknown);
        }
    }

    // Receives an object with the result of a matchv2.2 petition, and returns the winner team 100 or 200.
    public static int getWinnerTeam(JSONObject match){
        JSONArray teams = null;
        try {
            teams = match.getJSONArray("teams");
            // We could check a lot of things, but let's think that everything is working OK.
            if (teams.getJSONObject(0).getInt("teamId") == 100)
                if (teams.getJSONObject(0).getBoolean("winner"))
                    return 100;
                else
                    return 200;
            if (teams.getJSONObject(0).getInt("teamId") == 200)
                if (teams.getJSONObject(0).getBoolean("winner"))
                    return 200;
                else
                    return 100;
        } catch (JSONException e) {
            Logger.appendLog("Error 30 - " + e.toString());
            return 0;
        }
        return 0;
    }
}