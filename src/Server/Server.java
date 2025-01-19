package Server;

import Server.IServerStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private int port;
    private int listeningIntervalMS;
    private IServerStrategy strategy;
    private boolean stop;
    // Thread pool management
//    private final ExecutorService executor;

    private int numOfThreadsPool;

    public Server(int port, int listeningIntervalMS, IServerStrategy strategy) {
        if (port <= 0 || listeningIntervalMS <= 0 || strategy == null) {
            throw new IllegalArgumentException("Invalid server configuration parameters");
        }
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.strategy = strategy;

        // Creates thread pool based on configuration settings
        // This allows the server to handle multiple clients simultaneously
        int numOfThreadsPool = Configurations.getInstance().getThreadPoolSize();
    }

    /**
     * Launches the server in a separate thread to avoid blocking the main application
     * This is important for applications that need to do other things while the server runs
     */
    public void start() {
        new Thread(() -> {
            run();
        }).start();
    }

    //The server listens for connections and hands them off to the thread pool
    private void run() {

        try {

            // Configurations
            Configurations config = Configurations.getInstance();
            int numberOfThreads = config.getThreadPoolSize();

            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(listeningIntervalMS);

            while (!stop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Thread thread = new Thread(() -> {
                        handleClient(clientSocket);
                    });
                    pool.execute(thread);
                }
                catch (SocketTimeoutException e) {
                    e.getStackTrace();
                }
            }
            serverSocket.close();
            pool.shutdown();
        }
        catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            strategy.applyStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e){

        }
    }


    public void stop(){
        stop = true;
    }
}


