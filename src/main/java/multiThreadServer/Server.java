package multiThreadServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int port = 7777;
    private static ServerSocket server;
    private static InetAddress ip;
    static ExecutorService executors = Executors.newFixedThreadPool(5);

    public static void setServer() {
        try {
            ip = InetAddress.getByName("192.168.100.9");
            server = new ServerSocket(port, 0, ip);
            System.out.println("IP   : " + server.getInetAddress());
            System.out.println("PORT : " + server.getLocalPort());
            while (true) {
                Socket toClient = server.accept();
                server.setSoTimeout(1000000);
                executors.execute(new ClientHandler(toClient));
                System.out.println("Клиент подключен");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
