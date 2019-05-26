package database;

import statistics.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.List;


public class PlayerStatisticsDatabase {


    public static Boolean isPlayerExists(String accID) throws SQLException {
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             PreparedStatement ps = connection.prepareStatement("select 1 from playerstatistics where accID  = '" + accID + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Long> readRequestHistoryFromPlayerStatisticsDatabase() throws SQLException {
        DataForGraphsDatabase.matchRequestHistory.clear();
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select accID from playerstatistics");
            while (resultSet.next()) {
                Long match = resultSet.getLong("accID");
                DataForGraphsDatabase.matchRequestHistory.add(match);
            }
        }
        return DataForGraphsDatabase.matchRequestHistory;
    }

    public static void addToPlayerStatisticsDatabase(PlayerStatistics playerStatistics, String accID) throws ClassNotFoundException, IOException, SQLException {
        if (!isPlayerExists(accID)) {
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement st = connection.prepareStatement("INSERT INTO playerstatistics (accID, nickName, matchesPlayed, lose, win, winrate) VALUES (?,?,?,?,?,?)");
                st.setString(1, accID);
                st.setString(2, playerStatistics.getNickName());
                st.setBytes(3, PlayerMatchStatisticsDatabase.serializeObject(playerStatistics.getMatchesPlayeds()).toByteArray());
                st.setDouble(4, playerStatistics.getLose());
                st.setDouble(5, playerStatistics.getWin());
                st.setDouble(6, playerStatistics.getWinrate());
                st.execute();
            }
        }
    }

    public static void deleteFromPlayerStatisticsDatabase(String accID) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            PreparedStatement st = connection.prepareStatement("delete from playerstatistics where accID = '" + accID + "'");
            st.execute();
        }
    }

    public static PlayerStatistics readFromPlayerStatisticsDataBase(String accID) throws SQLException, IOException, ClassNotFoundException {

        PlayerStatistics playerStat = new PlayerStatistics();
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from playerstatistics where accID  = '" + accID + "'");
            while (resultSet.next()) {
                playerStat.setNickName(resultSet.getString("nickName"));
                playerStat.setWinrate(resultSet.getDouble("winrate"));
                playerStat.setLose(resultSet.getDouble("lose"));
                playerStat.setWin(resultSet.getDouble("win"));
                byte[] matchStatistics = resultSet.getBytes("matchesPlayed");
                ObjectInputStream objectIn = new ObjectInputStream(
                        new ByteArrayInputStream(matchStatistics));
                playerStat.setMatchesPlayeds((MatchesPlayed[]) objectIn.readObject());
            }
        }
        return playerStat;
    }
}