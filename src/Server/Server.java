package Server;

import Server.IServerStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A generic server that can handle multiple clients simultaneously using a thread pool
 */
public class Server {
    private int port;
    private int listeningIntervalMS;
    private IServerStrategy strategy;
    private volatile boolean stop; //stop is marked as volatile so changes are visible across threads
    private ExecutorService executor;    // Thread pool management
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private int numOfThreadsPool;

    public Server(int port, int listeningIntervalMS, IServerStrategy strategy) {
        if (port <= 0 || listeningIntervalMS <= 0 || strategy == null) {
            throw new IllegalArgumentException("Invalid server configuration parameters");
        }
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.strategy = strategy;


        this.initThreadPool();
    }

    /**
     * Initializes the thread pool with configured number of threads
     */
    private void initThreadPool() {
        int numberOfThreads = Configurations.getInstance().getThreadPoolSize();
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    //Starts the server in a new thread
    public void start() {
        new Thread(() -> run(), "ServerMainThread").start();
        logger.info("Server started on port " + port);
    }

    //The server listens for connections and hands them off to the thread pool
    private void run() {

        try(ServerSocket serverSocket = new ServerSocket(port))  {
            serverSocket.setSoTimeout(listeningIntervalMS);

            while (!stop) {
                try {
                    // Wait for client connection
                    Socket clientSocket = serverSocket.accept();

                    // Double-check stop flag to prevent accepting new clients during shutdown
                    if (!stop) {
                        executor.submit(() -> handleClient(clientSocket));
                    } else {
                        clientSocket.close();
                    }
//                    executor.submit(() -> handleClient(clientSocket));
                }
                catch (SocketTimeoutException e) {
                    // No client connected within our timeout period
                    // check our stop flag now
//                    e.getStackTrace();
                }
            }

        }
        catch (IOException e) {
            System.out.println("Server socket error: " + e.getMessage());
            e.getStackTrace();

        } finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            strategy.applyStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
        } catch (IOException e){
            logger.log(Level.WARNING, "Error handling client", e);

        }
    }
    /**
     * Performs graceful shutdown of the thread pool
     * 1. Attempts graceful shutdown (waits for running tasks)
     * 2. If tasks don't complete in time, forces shutdown
     */
    private void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            try {
                // Step 1: Reject new tasks but complete running ones
                executor.shutdown();

                // Step 2: Wait up to 60 seconds for existing tasks to complete
                boolean completed = executor.awaitTermination(60, TimeUnit.SECONDS);

                // Step 3: If tasks didn't complete, force shutdown
                if (!completed) {
                    System.out.println("Forcing shutdown after timeout");
                    executor.shutdownNow(); // Interrupts running tasks
                }

            } catch (InterruptedException e) {
                // If waiting is interrupted, force shutdown
                executor.shutdownNow();
                Thread.currentThread().interrupt(); // Preserve interrupt status
            }
            System.out.println("Server shutdown completed");
        }
    }



    public void stop(){
        stop = true;
    }
}


