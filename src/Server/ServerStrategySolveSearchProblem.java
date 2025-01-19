package Server;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class ServerStrategySolveSearchProblem implements IServerStrategy {
    private HashMap<String, Solution> mazeSolutions = new HashMap<>();


    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            Maze maze = (Maze) fromClient.readObject();

            String tempDirectoryPath = System.getProperty("java.io.tmpdir");
            String request = maze.toString();
            Solution sol = null;
            if (mazeSolutions.containsKey(request)) {
                sol = mazeSolutions.get(request);
                sendSolutionToClient(sol, toClient); // Check if exist in cache memory

            } else {
                // Check if solution file exists
                if (solutionFileExists(tempDirectoryPath, request)) {  // If exist in file
                    Solution solution = retrieveSolutionFromFile(tempDirectoryPath, request);

                    // Store solution in memory
                    mazeSolutions.put(request, solution);

                    // Send solution to client
                    sendSolutionToClient(solution, toClient);

                } else {

                    // Generate maze and solve it according to the configuration file
                    Configurations config = Configurations.getInstance();
                    ISearchingAlgorithm searchingAlgorithm = null;
                    String searchAlgoName = config.getMazeSearchingAlgorithm();

                    if(searchAlgoName.equalsIgnoreCase("BreadthFirstSearch")){
                        searchingAlgorithm = new BreadthFirstSearch();
                    }
                    else if(searchAlgoName.equalsIgnoreCase("BestFirstSearch")){
                        searchingAlgorithm = new BestFirstSearch();
                    }
                    else if(searchAlgoName.equalsIgnoreCase("DepthFirstSearch")){
                        searchingAlgorithm = new DepthFirstSearch();
                    }

                    SearchableMaze searchableMaze = new SearchableMaze(maze);
                    Solution solution = searchingAlgorithm.solve(searchableMaze);

                    // Save solution to file
                    saveSolutionToFile(tempDirectoryPath, request, solution);
                    // Store solution in memory
                    mazeSolutions.put(request, solution);

                    // Send solution to client
                    sendSolutionToClient(solution, toClient);

                }
            }
            fromClient.close();
            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    // Method to send the solution to the client
    private void sendSolutionToClient(Solution solution, ObjectOutputStream outputStream) {
        try {
            outputStream.writeObject(solution);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean solutionFileExists(String tempDirectoryPath, String request) {
        String uniqueFileName = getFileNameFromBinaryString(request);
        if (uniqueFileName == null) {
            return false;
        }
        File file = new File(tempDirectoryPath, uniqueFileName);
        return file.exists() && !file.isDirectory();
    }

    private Solution retrieveSolutionFromFile(String tempDirectoryPath, String request) {
        Solution solution = null;
        String uniqueFileName = getFileNameFromBinaryString(request);
        if (uniqueFileName == null) {
            return null;
        }
        File file = new File(tempDirectoryPath, uniqueFileName);
        try (FileInputStream inputFile = new FileInputStream(file);
             ObjectInputStream inputStream = new ObjectInputStream(inputFile)) {
            solution = (Solution) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return solution;
    }


    private void saveSolutionToFile(String tempDirectoryPath, String request, Solution solution) {
        try {
            String uniqueFileName = getFileNameFromBinaryString(request);
            if(uniqueFileName == null){
                return;
            }
            File file = new File(tempDirectoryPath, uniqueFileName);
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(solution);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            System.err.println("An error occurred while saving the file: " + e.getMessage());
            e.printStackTrace();
            // Additional error handling code or alternative actions can be implemented here
        }
    }

    // Function to get a shorter filename to save in the temp directory.
    private String getFileNameFromBinaryString(String request) {
        try {
            byte[] binaryData = request.getBytes();
            // Generate hash using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(binaryData);

            // Convert hash bytes to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            // Return the hexadecimal string as the file name
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle algorithm not found error
            e.printStackTrace();
            return null;
        }

    }
}