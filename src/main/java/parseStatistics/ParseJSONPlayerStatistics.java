package parseStatistics;
import statistics.*;
import org.json.JSONObject;

public class ParseJSONPlayerStatistics {

    private static String accountID = "";
    private static String url;
    private static PlayerStatistics stat;

  synchronized   public static PlayerStatistics playerStatistics(String accID) {
        accountID = accID;
        url = "https://api.opendota.com/api/players/" + accountID;
        try {
            JSONObject object = new JSONObject(ParseJSONPlayerMatchStatistics.getResponse(url));
            if (!object.has("profile")) {
                return null;
            }
            JSONObject name = object.getJSONObject("profile");
            PlayerStatistics playerStatistics = new PlayerStatistics();
            playerStatistics.setNickName(name.getString("personaname"));
            url = "https://api.opendota.com/api/players/" + accountID + "/wl";
            JSONObject winRate = new JSONObject(ParseJSONPlayerMatchStatistics.getResponse(url));
            playerStatistics.setWin(winRate.getDouble("win"));
            playerStatistics.setLose(winRate.getDouble("lose"));
            playerStatistics.setWinrate((playerStatistics.getWin()) / (playerStatistics.getWin() + playerStatistics.getLose()) * 100);
            url = "https://api.opendota.com/api/players/" + accountID + "/matches";
            if(MatchesPlayed.getNeedMatches()==true) {
                MatchesPlayed[] matchPlayeds = ParseJSONLastMatches.parseJSONLastMatches(accID);
                playerStatistics.setMatchesPlayeds(matchPlayeds);
            }
            stat = playerStatistics;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }
}