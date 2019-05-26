package database;

import statistics.DataForGraphs;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataForGraphsDatabase {

    public static List<Long> matchRequestHistory = new ArrayList<>();

    protected static Boolean isMatchExists(String accID) throws SQLException {
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             PreparedStatement ps = connection.prepareStatement("select 1 from dotaforgraph  where matchID  = '" + accID + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addToDataForGraphDatabase(DataForGraphs dataForGraphs, String matchID) throws ClassNotFoundException, IOException, SQLException {
        if (!isMatchExists(matchID)) {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement st = connection.prepareStatement("INSERT INTO dotaforgraph (matchID,dataForGraph) VALUES (?,?)");
                st.setString(1, matchID);
                st.setBytes(2, PlayerMatchStatisticsDatabase.serializeObject(dataForGraphs).toByteArray());
                st.execute();
            }
        }
    }

    public static List<Long> readRequestHistoryFromGraphDataBase() throws SQLException {
        matchRequestHistory.clear();
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select matchID from dotaforgraph");
            while (resultSet.next()) {
                Long match = resultSet.getLong("matchID");
                matchRequestHistory.add(match);
            }
        }
        return matchRequestHistory;
    }

    public static DataForGraphs readFromDataForGraphDataBase(String matchID) throws SQLException, IOException, ClassNotFoundException {

        DataForGraphs dataForGraphs = new DataForGraphs();
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select dataForGraph from dotaforgraph where matchID  = '" + matchID + "'");
            while (resultSet.next()) {
                byte[] matchStatistics = resultSet.getBytes("dataForGraph");
                ObjectInputStream objectIn = new ObjectInputStream(
                        new ByteArrayInputStream(matchStatistics));
                dataForGraphs = (DataForGraphs) objectIn.readObject();
            }
        }
        return dataForGraphs;
    }
}
