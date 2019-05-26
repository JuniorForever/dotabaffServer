package database;
import statistics.PlayerMatchStatistics;

import java.io.*;
import java.sql.*;


public class PlayerMatchStatisticsDatabase {

    public static String userName = "root";
    public static String password = "1234";
    public static String connectionURL = "jdbc:mysql://localhost:3306/dotabaff?autoReconnect=true&useSSL=false";

    public static Boolean isMatchExists(String accID) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionURL, userName, password);
             PreparedStatement ps = connection.prepareStatement("select 1 from playermatchstatistics where matchID  = '" + accID + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ByteArrayOutputStream serializeObject(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream;
    }

    public static void addToPlayerStatisticsDatabase(PlayerMatchStatistics[] playerMatchStatistics, String matchID) throws ClassNotFoundException, IOException, SQLException {
        if (!isMatchExists(matchID)) {
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(connectionURL, userName, password);
                 Statement statement = connection.createStatement()) {
                PreparedStatement st = connection.prepareStatement("INSERT INTO playermatchstatistics (matchID,playerMatchStatistics) VALUES (?,?)");
                st.setString(1, matchID);
                st.setBytes(2, serializeObject(playerMatchStatistics).toByteArray());
                st.execute();
            }
        }
    }

    public static PlayerMatchStatistics[] readFromPlaterMatchStatisticsDataBase(String matchID) throws SQLException, IOException, ClassNotFoundException {

        PlayerMatchStatistics[] playerMatchStat = new PlayerMatchStatistics[10];
        try (Connection connection = DriverManager.getConnection(connectionURL, userName, password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select playerMatchStatistics from playermatchstatistics where matchID  = '" + matchID + "'");
            while (resultSet.next()) {
                byte[] matchStatistics = resultSet.getBytes("playerMatchStatistics");
                ObjectInputStream objectIn = new ObjectInputStream(
                        new ByteArrayInputStream(matchStatistics));
                playerMatchStat = (PlayerMatchStatistics[]) objectIn.readObject();
            }
        }
        return playerMatchStat;
    }
}


