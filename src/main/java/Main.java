import multiThreadServer.Server;
import threads.UpdateThreads;

public class Main {
    public static void main(String[] args) {
        UpdateThreads updateThreads = new UpdateThreads();
        Server.setServer();
    }
}

