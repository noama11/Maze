package Server;

import IO.MyCompressorOutputStream;
import IO.SimpleCompressorOutputStream;
import algorithms.mazeGenerators.*;

import java.io.*;
import java.util.ArrayList;

public class ServerStrategyGenerateMaze implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {

        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            int[] mazeSizes = (int[]) fromClient.readObject();

            // Check which algorithm to use to generate the maze.
            Configurations conf = Configurations.getInstance();
            String generateAlgo =  conf.getMazeGeneratingAlgorithm();
            IMazeGenerator mazeGenerator = null;

            // Choose the appropriate maze generator based on the algorithm specified
            if(generateAlgo.equalsIgnoreCase("empty")){
                mazeGenerator = new EmptyMazeGenerator();
            }
            else if(generateAlgo.equalsIgnoreCase("simple")){
                mazeGenerator = new SimpleMazeGenerator();
            }
            else if(generateAlgo.equalsIgnoreCase("DepthFirstSearch")){
                mazeGenerator = new MyMazeGenerator();
            }

            // Generate a maze with the chosen algorithm and specified dimensions
            Maze maze = mazeGenerator.generate(mazeSizes[0], mazeSizes[1]);

            // Convert the maze to a byte array for compression
            byte[] uncompressedMaze = maze.toByteArray();

            // Compress the maze using the selected compressor
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(out);
            //SimpleCompressorOutputStream compressor = new SimpleCompressorOutputStream(out);
            compressor.write(uncompressedMaze);
            compressor.flush();

            // Send the compressed maze data back to the client
            toClient.writeObject(out.toByteArray());
            // Ensure all buffered data is immediately sent to the client
            toClient.flush();

            // Close the input/output streams to free resources
            fromClient.close();
            toClient.close();

        } catch (IOException e) {
            // Handle exceptions related to input/output operations
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Handle cases where the incoming data could not be deserialized
            throw new RuntimeException(e);
        }
    }

}