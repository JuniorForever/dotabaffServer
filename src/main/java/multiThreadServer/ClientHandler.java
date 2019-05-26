package multiThreadServer;

import database.DataForGraphsDatabase;
import database.PlayerMatchStatisticsDatabase;
import database.PlayerStatisticsDatabase;
import statistics.DataForGraphs;
import statistics.PlayerMatchStatistics;
import statistics.PlayerStatistics;
import statistics.SearchResponseByName;
import org.json.JSONException;
import parseStatistics.ParseDataForGraphs;
import parseStatistics.ParseJSONPlayerMatchStatistics;
import parseStatistics.ParseJSONPlayerStatistics;
import parseStatistics.ParseJSONSearchResponseByName;
import threads.MainThread;
import threads.UpdatePlayerStat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ClientHandler implements Runnable {

    private static Object msg = "";
    private static Socket clientDialog;

    public ClientHandler(Socket client) {
        ClientHandler.clientDialog = client;
    }

    private void handler() throws IOException, ClassNotFoundException, SQLException, InterruptedException, JSONException {
        ObjectOutputStream serializer = new ObjectOutputStream(clientDialog.getOutputStream());
        ObjectInputStream deserializer = new ObjectInputStream(clientDialog.getInputStream());
        msg = deserializer.readObject();
        msg.toString();
        switch (msg.toString()) {
            case "1": {
                PlayerStatistics playerStatistics;
                String playerRequest = deserializer.readObject().toString();
                if (PlayerStatisticsDatabase.isPlayerExists(playerRequest)) {
                    if (MainThread.isPlayerUpdate(playerRequest)) {
                        sleep(2000);
                    } else {
                        UpdatePlayerStat updatePlayerStat = new UpdatePlayerStat(playerRequest);
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(updatePlayerStat);
                        updatePlayerStat.setPriority(1);
                        sleep(2000);

                    }
                    playerStatistics = PlayerStatisticsDatabase.readFromPlayerStatisticsDataBase(playerRequest);
                } else {
                    playerStatistics = ParseJSONPlayerStatistics.playerStatistics(playerRequest);
                    PlayerStatisticsDatabase.addToPlayerStatisticsDatabase(playerStatistics, playerRequest);
                }
                serializer.writeObject(playerStatistics);
                serializer.flush();
                break;
            }
            case "2": {
                DataForGraphs dataForGraphs;
                PlayerMatchStatistics[] playerMatchStatistics;
                String msg1 = deserializer.readObject().toString();
                if (!PlayerMatchStatisticsDatabase.isMatchExists(msg1)) {
                    dataForGraphs = ParseDataForGraphs.parseDataForGraphs(msg1);
                    playerMatchStatistics = ParseJSONPlayerMatchStatistics.parseJsonObject(msg1);
                    PlayerMatchStatisticsDatabase.addToPlayerStatisticsDatabase(playerMatchStatistics, msg1);
                    DataForGraphsDatabase.addToDataForGraphDatabase(dataForGraphs, msg1);
                } else {
                    dataForGraphs = DataForGraphsDatabase.readFromDataForGraphDataBase(msg1);
                    playerMatchStatistics = PlayerMatchStatisticsDatabase.readFromPlaterMatchStatisticsDataBase(msg1);
                }
                serializer.writeObject(dataForGraphs);
                serializer.writeObject(playerMatchStatistics);
                serializer.flush();
                break;
            }
            case "3": {
                SearchResponseByName[] searchResponseByName = ParseJSONSearchResponseByName.searchByName(deserializer.readObject().toString());
                serializer.writeObject(searchResponseByName);
                serializer.flush();
                break;
            }
        }
    }

    @Override
    public void run() {
        try {
            handler();
            System.out.println("Клиент отключен");
            System.out.println("Ожидание подключения клиентов");
        } catch (IOException | ClassNotFoundException | SQLException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}