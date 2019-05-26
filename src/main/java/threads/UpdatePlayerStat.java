package threads;

import statistics.PlayerStatistics;
import parseStatistics.ParseJSONLastMatches;
import parseStatistics.ParseJSONPlayerStatistics;
import database.*;
import statistics.MatchesPlayed;
import java.io.IOException;
import java.sql.SQLException;


public class UpdatePlayerStat extends Thread {

    private String accID;

    public String getAccID() {
        return accID;
    }

    public Thread thread;

     public UpdatePlayerStat(String accID) {
        this.accID = accID;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            MainThread.addThreadToDatabase(accID);
            PlayerStatistics playerStatistics = PlayerStatisticsDatabase.readFromPlayerStatisticsDataBase(accID);
            MatchesPlayed matchesPlayed[] =  ParseJSONLastMatches.parseJSONLastMatches(accID);
            if(!playerStatistics.getMatchesPlayeds()[0].getMatchID().equals(matchesPlayed[0].getMatchID())){
                MatchesPlayed.setNeedMatches(false);
                PlayerStatistics playerMatchStatistics = ParseJSONPlayerStatistics.playerStatistics(accID);
                MatchesPlayed.setNeedMatches(true);
                playerMatchStatistics.setMatchesPlayeds(matchesPlayed);
                PlayerStatisticsDatabase.deleteFromPlayerStatisticsDatabase(accID);
                PlayerStatisticsDatabase.addToPlayerStatisticsDatabase(playerMatchStatistics, accID);
            }
            MainThread.deleteFromThreadDatabase(accID);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
