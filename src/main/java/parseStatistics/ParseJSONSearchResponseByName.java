package parseStatistics;
import statistics.SearchResponseByName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJSONSearchResponseByName {

    private static String nickName;

    public static SearchResponseByName[] searchByName(String nickname) throws JSONException {
        if(nickname.indexOf(" ")!=-1){
            nickName = nickname.replace(" ","%20");
        }else{nickName = nickname;}
        String Url = "https://api.opendota.com/api/search?q=" + nickName;
        ParseJSONPlayerMatchStatistics.getResponse(Url);
        JSONArray nickNameJSONArray = new JSONArray(ParseJSONPlayerMatchStatistics.getResponse(Url));
        SearchResponseByName[] searchResponseByName = new SearchResponseByName[nickNameJSONArray.length()];
        for(int i=0;i<nickNameJSONArray.length();i++) {
            JSONObject nickNameJSONObject = nickNameJSONArray.getJSONObject(i);
            searchResponseByName[i] = new SearchResponseByName();
            searchResponseByName[i].setAccountID(nickNameJSONObject.getLong("account_id"));
            searchResponseByName[i].setAvatarURL(nickNameJSONObject.getString("avatarfull"));
            searchResponseByName[i].setNickName(nickNameJSONObject.getString("personaname"));
        }
        return searchResponseByName;
    }
}
