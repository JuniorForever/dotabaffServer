package parseStatistics;
import statistics.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


import static java.lang.Thread.sleep;

public class ParseDataForGraphs {

    private static String matchID = "";
    private static DataForGraphs result;
    public static Boolean errorFlag = false;
    public static int timeCounter = 0;

    public static DataForGraphs parseDataForGraphs(String matchId) {
        try {
            timeCounter = 0;
            matchID = matchId;
            String Url = "https://api.opendota.com/api/matches/" + matchID;
            DataForGraphs dataForGraphs = new DataForGraphs();
            ParseJSONPlayerMatchStatistics.postResponse(matchID);
            JSONObject obj_JSONObject;
            do {
                if (timeCounter > 17) {
                    PlayerMatchStatistics.setMatchProcessingFlag(false);
                    return null;
                }
                sleep(5000);
                timeCounter++;
                obj_JSONObject = new JSONObject(ParseJSONPlayerMatchStatistics.getResponse(Url));
            } while (obj_JSONObject.get("radiant_gold_adv").toString() == "null");
            JSONArray jsonArray = obj_JSONObject.getJSONArray("radiant_gold_adv");
            List<Integer> goldList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                goldList.add(jsonArray.getInt(i));
            }
            dataForGraphs.setNetWorse(goldList);
            JSONArray jsonArray1 = obj_JSONObject.getJSONArray("radiant_xp_adv");
            List<Integer> expList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                expList.add(jsonArray1.getInt(i));
            }
            dataForGraphs.setExp(expList);
            result = dataForGraphs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
