package test;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Client.Client;
import Client.IClientStrategy;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class RunCommunicateWithServers {
    public static void main(String[] args) throws InterruptedException {
//Initializing servers
        // listeningIntervalMS = 1000: represents the timeout interval in milliseconds for the server's socket
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
//Starting servers
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
//stringReverserServer.start();
//Communicating with servers
//        CommunicateWithServer_MazeGenerating();
//        CommunicateWithServer_SolveSearchProblem();
////CommunicateWithServer_StringReverser();
////Stopping all servers
//        mazeGeneratingServer.stop();
//        solveSearchProblemServer.stop();

        // New concurrent test
        System.out.println("\nStarting concurrent clients test...");
        testConcurrentClients();

        // Give time for concurrent clients to finish
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//stringReverserServer.stop();
    }
    private static void CommunicateWithServer_MazeGenerating() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new
                                        ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new
                                        ObjectInputStream(inFromServer);
                                toServer.flush();
                                int[] mazeDimensions = new int[]{50,50};
                                toServer.writeObject(mazeDimensions); //send maze dimensions to server
                                toServer.flush();
                                byte[] compressedMaze = (byte[])
                                        fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                                InputStream is = new MyDecompressorInputStream(new
                                        ByteArrayInputStream(compressedMaze));
                                byte[] decompressedMaze = new byte[2512]; //allocating byte[] for the decompressed maze -
                                        is.read(decompressedMaze); //Fill decompressedMaze
                                Maze maze = new Maze(decompressedMaze);
                                maze.print();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    private static void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new
                                        ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new
                                        ObjectInputStream(inFromServer);
                                toServer.flush();
                                MyMazeGenerator mg = new MyMazeGenerator();
                                Maze maze = mg.generate(50, 50);
                                maze.print();
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                Solution mazeSolution = (Solution)
                                        fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
//Print Maze Solution retrieved from the server
                                System.out.println(String.format("Solution steps: %s", mazeSolution));
                                ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                                for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                                    System.out.println(String.format("%s. %s", i,
                                            mazeSolutionSteps.get(i).toString()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



    // Add this new method for concurrent testing
    private static void testConcurrentClients() {
        int numberOfClients = 5;  // Number of concurrent clients
        Thread[] clientThreads = new Thread[numberOfClients];
        CountDownLatch latch = new CountDownLatch(numberOfClients);

        System.out.println("Launching " + numberOfClients + " concurrent maze generation clients...");

        for (int i = 0; i < numberOfClients; i++) {
            final int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try {
                    System.out.println("Client " + clientId + " starting...");
                    Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                long startTime = System.currentTimeMillis();

                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                                toServer.flush();

                                // Each client requests different size maze
                                int[] mazeDimensions = new int[]{30 + clientId * 10, 30 + clientId * 10};
                                System.out.println("Client " + clientId + " requesting maze size: " + mazeDimensions[0] + "x" + mazeDimensions[1]);

                                toServer.writeObject(mazeDimensions);
                                toServer.flush();

                                byte[] compressedMaze = (byte[]) fromServer.readObject();
                                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                                byte[] decompressedMaze = new byte[(mazeDimensions[0] * mazeDimensions[1]) + 12];
                                is.read(decompressedMaze);

                                long endTime = System.currentTimeMillis();
                                System.out.println("Client " + clientId + " completed in " + (endTime - startTime) + "ms");

                            } catch (Exception e) {
                                System.err.println("Error in client " + clientId);
                                e.printStackTrace();
                            }
                        }
                    });
                    client.communicateWithServer();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }, "Client-" + i);

            clientThreads[i].start();
        }

        try {
            // Wait for all clients to finish (with timeout)
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                System.out.println("Warning: Not all clients completed within timeout!");
            } else {
                System.out.println("All concurrent clients completed successfully!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void CommunicateWithServer_StringReverser() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5402, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                BufferedReader fromServer = new BufferedReader(new
                                        InputStreamReader(inFromServer));
                                PrintWriter toServer = new PrintWriter(outToServer);
                                String message = "Client Message";
                                String serverResponse;
                                toServer.write(message + "\n");
                                toServer.flush();
                                serverResponse = fromServer.readLine();
                                System.out.println(String.format("Server response:  %s", serverResponse));
                                        toServer.flush();
                                fromServer.close();
                                toServer.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}