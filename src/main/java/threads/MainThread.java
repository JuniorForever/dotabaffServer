package threads;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.*;

public class MainThread {

    private static List<UpdatePlayerStat> updateList = new ArrayList<>();

    public static void createPlayerStatisticsUpdateThread() throws SQLException {
        List<Long> playerList = PlayerStatisticsDatabase.readRequestHistoryFromPlayerStatisticsDatabase();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        proceed(playerList, executor);
    }

    private static void proceed(List<Long> playerlist, ExecutorService executorService) {
        Iterator<Long> iterator = playerlist.iterator();
        updateList.clear();
        for (int i = 0; i < playerlist.size(); i++) {updateList.add(null);}
        updateList.parallelStream().forEach(i -> executorService.execute(new UpdatePlayerStat(iterator.next().toString())));
    }

    public static List<UpdatePlayerStat> getUpdateList() {
        return updateList;
    }

    public static Boolean isPlayerUpdate(String accID) throws SQLException {
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             PreparedStatement ps = connection.prepareStatement("select 1 from activeupdatingthreads where accID  = '" + accID + "'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addThreadToDatabase(String accID) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            PreparedStatement st = connection.prepareStatement("INSERT INTO activeupdatingthreads (accID) VALUES (?)");
            st.setString(1, accID);
            st.execute();
        }
    }

    synchronized public static void deleteFromThreadDatabase(String accID) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(PlayerMatchStatisticsDatabase.connectionURL, PlayerMatchStatisticsDatabase.userName, PlayerMatchStatisticsDatabase.password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("delete from activeupdatingthreads where accID = '" + accID + "'");
        }
    }

}
