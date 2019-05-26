package threads;

import java.sql.SQLException;

public class UpdateThreads extends Thread {

    public Thread thread;

    public UpdateThreads() {
        thread = new Thread(this);
        thread.start();
    }


    @Override

    public void run() {
        while (true) {
            try {
                MainThread.createPlayerStatisticsUpdateThread();
                sleep(180000);
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
