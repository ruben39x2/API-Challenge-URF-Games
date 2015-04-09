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
    public static String getMatchDuration(Context context, JSONObject match) {
        Long matchDuration;
        String duration;
        try {
            matchDuration = match.getLong("matchDuration");
            Long minutes = matchDuration / 60;
            Long seconds = matchDuration % 60;

            // This is made in order to get "23:07" instead of "23:7"
            String secondsStr;
            if (seconds.toString().length() == 1)
                secondsStr = "0" + seconds.toString();
            else
                secondsStr = seconds.toString();

            duration = context.getString(R.string.duration) + " " + minutes + ":" + secondsStr;
        } catch (JSONException e) {
            duration = context.getString(R.string.unknown_duration);
            Logger.appendLog("Error 24 - " + e.toString());
        }
        return duration;
    }

    // Receives an object with a participant information (see matchv2.2 petition), and returns the KDA.
    public static String getKDA(Context context, JSONObject participant) {
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
    public static String getLeague(Context context, JSONObject participant) {
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
    public static int getWinnerTeam(JSONObject match) {
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

    // Receives an object with a "participant"
    public static Long getKills(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("kills");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getDeaths(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("deaths");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getAssists(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("assists");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getMinionsKilled(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("minionsKilled");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getNeutralMinionsKilled(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("neutralMinionsKilled");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getLargestMultiKill(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("largestMultiKill");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getLargestKillingSpree(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("largestKillingSpree");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getGoldEarned(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("goldEarned");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getChampLevel(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("champLevel");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getTotalHeal(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("totalHeal");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getTrueDamageDealtToChampions(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("trueDamageDealtToChampions");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getMagicDamageDealtToChampions(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("magicDamageDealtToChampions");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getPhysicalDamageDealtToChampions(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("physicalDamageDealtToChampions");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getTowerKills(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("towerKills");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a "participant"
    public static Long getInhibitorKills(JSONObject participant) {
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong("inhibitorKills");
        } catch (JSONException e) {
            return (long) 0;
        }
    }

    // Receives an object with a participant information (see matchv2.2 petition), and returns the
    // Contribution for kill.
    public static String getContributionForKill(JSONObject participant) {
        String contribution;
        try {
            JSONObject stats = participant.getJSONObject("stats");
            Long kills = stats.getLong("kills");
            Long deaths = stats.getLong("deaths");
            Long assists = stats.getLong("assists");
            if (deaths == (long) 0) return "perfect";
            Float contributionFlt = ((float) kills + (float) assists) / (float) deaths;
            // Avoid numbers with length > 4 chars (They're not necessary)
            String pureContribution = contributionFlt.toString();
            if (pureContribution.length() > 4) pureContribution = pureContribution.substring(0, 3);
            contribution = pureContribution + ":1";
        } catch (JSONException e) {
            contribution = "unknown";
            Logger.appendLog("Error 32 - " + e.toString());
        }
        return contribution;
    }

    // Receives an object with a participant information (see matchv2.2 petition) and
    // an item number, (0 to 6), and returns the itemId.
    public static Long getItemId(JSONObject participant, int itemNumber) {
        String item;
        item = "item" + ((Integer) itemNumber).toString();
        try {
            JSONObject stats = participant.getJSONObject("stats");
            return stats.getLong(item);
        } catch (JSONException e) {
            Logger.appendLog("Error 34 - " + e.toString());
            return (long) 0;
        }
    }

    // Receives an object with the team info.
    public static boolean getFirstBlood(JSONObject team) {
        try {
            return team.getBoolean("firstBlood");
        } catch (JSONException e) {
            Logger.appendLog("Error 38 - " + e.toString());
            return false;
        }
    }

    // Receives an object with the team info.
    public static boolean getFirstTower(JSONObject team) {
        try {
            return team.getBoolean("firstTower");
        } catch (JSONException e) {
            Logger.appendLog("Error 39 - " + e.toString());
            return false;
        }
    }

    // Receives an object with the team info.
    public static boolean getFirstBaron(JSONObject team) {
        try {
            return team.getBoolean("firstBaron");
        } catch (JSONException e) {
            Logger.appendLog("Error 39 - " + e.toString());
            return false;
        }
    }

    // Receives an object with the team info.
    public static boolean getFirstDragon(JSONObject team) {
        try {
            return team.getBoolean("firstDragon");
        } catch (JSONException e) {
            Logger.appendLog("Error 40 - " + e.toString());
            return false;
        }
    }

    // Receives an object with the team info.
    public static boolean getFirstInhibitor(JSONObject team) {
        try {
            return team.getBoolean("firstInhibitor");
        } catch (JSONException e) {
            Logger.appendLog("Error 41 - " + e.toString());
            return false;
        }
    }

    // Receives an object with the team info.
    public static Integer getTeamTowerKills(JSONObject team) {
        try {
            return team.getInt("towerKills");
        } catch (JSONException e) {
            Logger.appendLog("Error 42 - " + e.toString());
            return 0;
        }
    }

    // Receives an object with the team info.
    public static Integer getTeamInhibitorKills(JSONObject team) {
        try {
            return team.getInt("inhibitorKills");
        } catch (JSONException e) {
            Logger.appendLog("Error 43 - " + e.toString());
            return 0;
        }
    }

    // Receives an object with the team info.
    public static Integer getTeamDragonKills(JSONObject team) {
        try {
            return team.getInt("dragonKills");
        } catch (JSONException e) {
            Logger.appendLog("Error 44 - " + e.toString());
            return 0;
        }
    }

    // Receives an object with the team info.
    public static Integer getTeamBaronKills(JSONObject team) {
        try {
            return team.getInt("baronKills");
        } catch (JSONException e) {
            Logger.appendLog("Error 45 - " + e.toString());
            return 0;
        }
    }

    // Receives an object with the team info.
    public static Boolean getTeamWinner(JSONObject team) {
        try {
            return team.getBoolean("winner");
        } catch (JSONException e) {
            Logger.appendLog("Error 46 - " + e.toString());
            return null;
        }
    }

    // Receives an object with the team info.
    public static Integer getTeamId(JSONObject team) {
        try {
            return team.getInt("teamId");
        } catch (JSONException e) {
            Logger.appendLog("Error 47 - " + e.toString());
            return null;
        }
    }
}
