package parseStatistics;

import statistics.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class ParseJSONPlayerMatchStatistics {

    private static String matchID = "";
    private static String Url;
    private static String responseStr;

    public static void postResponse(String matchId) {
        try {
            matchID = matchId;
            URL url = new URL("https://api.opendota.com/api/request/" + matchID);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            Map<String, String> arguments = new HashMap<>();
            arguments.put("match_id", matchID);
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        } catch (Exception e) {
            System.out.println("Getting information error");
        }
    }

    public static String getResponse(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            responseStr = response.toString();

        } catch (Exception e) {
            ParseDataForGraphs.errorFlag = true;
        }
        return responseStr;
    }


    public static PlayerMatchStatistics[] parseJsonObject(String matchId) {
        matchID = matchId;
        Url = "https://api.opendota.com/api/matches/" + matchId;
        PlayerMatchStatistics[] playerMatchStatistics = new PlayerMatchStatistics[10];
        try {
            JSONObject obj_JSONObject = new JSONObject(getResponse(Url));
            JSONArray obj_JSONArray = obj_JSONObject.getJSONArray("players");
            for (int i = 0; i < obj_JSONArray.length(); i++)
                playerMatchStatistics[i] = new PlayerMatchStatistics();
            for (int numOfPlayer = 0; numOfPlayer < obj_JSONArray.length(); numOfPlayer++) {
                JSONObject object = obj_JSONArray.getJSONObject(numOfPlayer);
                if (object.get("account_id").toString() != "null") {
                    playerMatchStatistics[numOfPlayer].setAccount_id(object.getLong("account_id"));
                } else {
                    playerMatchStatistics[numOfPlayer].setAccount_id(0);
                }
                if (object.has("personaname")) {
                    playerMatchStatistics[numOfPlayer].setNickName(object.getString("personaname"));
                } else {
                    playerMatchStatistics[numOfPlayer].setNickName("Anonymous");
                }
                playerMatchStatistics[numOfPlayer].setPlayer_slot(object.getInt("player_slot"));
                playerMatchStatistics[numOfPlayer].setHero_id(object.getInt("hero_id"));
                playerMatchStatistics[numOfPlayer].setKills(object.getInt("kills"));
                playerMatchStatistics[numOfPlayer].setDeaths(object.getInt("deaths"));
                playerMatchStatistics[numOfPlayer].setAssists(object.getInt("assists"));
                playerMatchStatistics[numOfPlayer].setLast_hits(object.getInt("last_hits"));
                playerMatchStatistics[numOfPlayer].setDeaths(object.getInt("deaths"));
                playerMatchStatistics[numOfPlayer].setGPM(object.getInt("gold_per_min"));
                playerMatchStatistics[numOfPlayer].setXPM(object.getInt("xp_per_min"));
                playerMatchStatistics[numOfPlayer].setDenies(object.getInt("denies"));
                playerMatchStatistics[numOfPlayer].setLevel(object.getInt("level"));
                playerMatchStatistics[numOfPlayer].setHero_damage(object.getInt("hero_damage"));
                playerMatchStatistics[numOfPlayer].setHero_healing(object.getInt("hero_healing"));
                playerMatchStatistics[numOfPlayer].setNetWorse(object.getInt("total_gold"));
                List<Integer> hero_inventory = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    hero_inventory.add(object.getInt("item_" + i));
                }
                playerMatchStatistics[numOfPlayer].setHero_inventory(hero_inventory);
                List<Integer> pack = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Integer backlack = object.getInt("backpack_" + i);
                    pack.add(backlack);
                }
                playerMatchStatistics[numOfPlayer].setBackPack(pack);
                if (PlayerMatchStatistics.getMatchProcessingFlag()) {
                    List<Integer> goldChange = new ArrayList<>();
                    JSONArray goldChangeArray = object.getJSONArray("gold_t");
                    for (int i = 0; i < goldChangeArray.length(); i++) {
                        goldChange.add(goldChangeArray.getInt(i));
                    }
                    playerMatchStatistics[numOfPlayer].setGoldChange(goldChange);
                    JSONArray expChangeArray = object.getJSONArray("xp_t");
                    List<Integer> expChange = new ArrayList<>();
                    for (int i = 0; i < goldChangeArray.length(); i++) {
                        expChange.add(expChangeArray.getInt(i));
                    }
                    playerMatchStatistics[numOfPlayer].setExpChange(expChange);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerMatchStatistics;
    }
}