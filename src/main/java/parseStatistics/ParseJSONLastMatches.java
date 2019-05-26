package parseStatistics;
import statistics.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParseJSONLastMatches {

    private static String accountID = "";
    private static String url;
    private static MatchesPlayed[] array;
    private static int maxMatchArraySize = 500;

    synchronized public static MatchesPlayed[] parseJSONLastMatches(String accID) {
        try {
            accountID = accID;
            url = "https://api.opendota.com/api/players/" + accountID + "/matches";
            int matchesArraySize;
            JSONArray matches = new JSONArray(ParseJSONPlayerMatchStatistics.getResponse(url));
            if (matches.length() < maxMatchArraySize) {
                matchesArraySize = matches.length();
            } else {
                matchesArraySize = maxMatchArraySize;
            }
            MatchesPlayed matchPlayeds[] = new MatchesPlayed[matchesArraySize];

            for (int i = 0; i < matchesArraySize; i++) {
                matchPlayeds[i] = new MatchesPlayed();
            }
            for (int matchesCount = 0; matchesCount < matchesArraySize; matchesCount++) {
                JSONObject object = matches.getJSONObject(matchesCount);
                matchPlayeds[matchesCount].setMatchID(object.getLong("match_id"));
                matchPlayeds[matchesCount].setLobbyType(object.getInt("lobby_type"));
                if (object.get("radiant_win").toString() != "null") {
                    matchPlayeds[matchesCount].setRadiantWin(object.getBoolean("radiant_win"));
                } else {
                    matchPlayeds[matchesCount].setRadiantWin(null);
                }
                matchPlayeds[matchesCount].setPlayerSlot(object.getInt("player_slot"));
                matchPlayeds[matchesCount].setDuration(object.getInt("duration"));
                matchPlayeds[matchesCount].setAssists(object.getInt("assists"));
                matchPlayeds[matchesCount].setKills(object.getInt("kills"));
                matchPlayeds[matchesCount].setDeaths(object.getInt("deaths"));
                matchPlayeds[matchesCount].setHeroId(object.getInt("hero_id"));
            }
            array = matchPlayeds;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return array;
    }
}