package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import Server.Configurations;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;



public class MyModel extends Observable implements IModel {
    private int[][] mazeArray;

    private ArrayList<Integer> rowsSolution;
    private ArrayList<Integer> columnsSolution;

    private Maze maze;

    private Position startPosition;
    private Position goalPosition;

    private boolean gameOver = false;
    private boolean solvedMaze = false;

    private boolean hint = false;

    private int characterPositionRow;
    private int characterPositionColumn;

    private Server solveSearchProblemServer;
    private Server mazeGeneratingServer;

    //private static final Logger Log = LogManager.getLogger();

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    @Override
    public void startServers() {
        //Log.info("The servers started running\n");
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    @Override
    public void stopServers() {
        //Log.info("The servers stopped");

        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public Position getGoalPosition() {
        return goalPosition;
    }

    @Override
    public void addToLog(String msg) {

    }


    @Override
    public void GenerateMaze(int Width, int Height) { // call by ViewModel
        GenerateMyMaze(Width, Height);
        setChanged();
        notifyObservers();
    }
    private void GenerateMyMaze(int Width, int Height) {
        try {
            // Client sends a request to generate a maze with specific dimensions, receives and processes the compressed maze data.
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        toServer.flush();

                        int[] mazeDimensions = new int[]{Height, Width};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) (new ObjectInputStream(inFromServer)).readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[Width * Height + 12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);

                        //Log.info("Rows: " + Height + " ,Columns:" + Width);

                        mazeArray = new int[Height][Width];
                        for(int row = 0; row < Height; row++) {
                            for (int column = 0; column < Width; column++)
                                mazeArray[row][column] = maze.getCell(row, column);
                        }

                        startPosition = maze.getStartPosition();
                        goalPosition = maze.getGoalPosition();

                        //Log.info("StartPositin :" + startPosition + " ,GoalPostion: " + goalPosition + "\n");

                        characterPositionColumn = startPosition.getColumnIndex();
                        characterPositionRow = startPosition.getRowIndex();

                        gameOver = false;

                    } catch (Exception e) {
                        //Log.error("The maze creation failed");
                    }
                }
            });
            //Log.info("Generate Maze By: " + client.toString());
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            //Log.error("The maze creation failed");
        }
    }

    @Override
    public void SolveMaze(){
        SolveMyMaze();
        setChanged();
        notifyObservers();
    }
    private void SolveMyMaze(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        startPosition = new Position(characterPositionRow, characterPositionColumn);

                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        toServer.writeObject(maze);// Send current maze state
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject();

                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();

                        rowsSolution = new ArrayList<>();
                        columnsSolution = new ArrayList<>();

                        for (int i = 0; i < mazeSolutionSteps.size(); i++){
                            MazeState MS = (MazeState)mazeSolutionSteps.get(i);
                            rowsSolution.add(MS.getStateRow());
                            columnsSolution.add(MS.getStateColumn()) ;
                        }
                    } catch (Exception e) {
                        System.out.println("Failed maze solution");
                        //Log.error("The maze solution failed");
                    }
                }
            });
            client.communicateWithServer();
            if(!hint) {
                solvedMaze = true;
                //Log.info("Solving the maze By: " + client.toString());

                InputStream inputStream = new FileInputStream("resources/config.properties");
                Properties properties = new Properties();
                properties.load(inputStream);
                String solvingMethod = properties.getProperty("mazeSearchingAlgorithm");

                //Log.info("SolvingMethod: " + solvingMethod);
                //Log.info("Solution length: " + rowsSolution.size() + "\n");
            }

        } catch (UnknownHostException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void MoveCharacter(int direction) {
        // Manages character movement based on user input, checking the validity of the move.
        switch (direction) {
            case 8:
                if (checkMove(characterPositionRow - 1, characterPositionColumn)) {
                    characterPositionRow--;
                }
                break;
            case 2:
                if (checkMove(characterPositionRow + 1, characterPositionColumn)) {
                    characterPositionRow++;
                }
                break;
            case 6:
                if (checkMove(characterPositionRow, characterPositionColumn + 1)) {
                    characterPositionColumn++;
                }
                break;
            case 4:
                if (checkMove(characterPositionRow, characterPositionColumn - 1)){
                    characterPositionColumn--;
                }
                break;

                // Up Right
            case 9:
                if (checkMove(characterPositionRow - 1, characterPositionColumn + 1)){
                    if(maze.getCell(characterPositionRow - 1, characterPositionColumn) == 0 || maze.getCell(characterPositionRow, characterPositionColumn + 1) == 0 ) {
                        characterPositionRow--;
                        characterPositionColumn++;
                    }
                }
                break;

            // Up Left
            case 7:
                if (checkMove(characterPositionRow - 1, characterPositionColumn - 1)){
                    if(maze.getCell(characterPositionRow - 1, characterPositionColumn) == 0 || maze.getCell(characterPositionRow, characterPositionColumn - 1) == 0 ) {
                        characterPositionRow--;
                        characterPositionColumn--;
                    }
                }
                break;

            // Down Right
            case 3:
                if (checkMove(characterPositionRow + 1, characterPositionColumn + 1)){
                    if(maze.getCell(characterPositionRow  + 1, characterPositionColumn) == 0 || maze.getCell(characterPositionRow, characterPositionColumn + 1) == 0 ) {
                        characterPositionRow++;
                        characterPositionColumn++;
                    }
                }
                break;
                // Down Left
            case 1:
                if (checkMove(characterPositionRow + 1, characterPositionColumn - 1)){
                    if(maze.getCell(characterPositionRow  + 1, characterPositionColumn) == 0 || maze.getCell(characterPositionRow, characterPositionColumn - 1) == 0 ) {
                        characterPositionRow++;
                        characterPositionColumn--;
                    }
                }
                break;
        }

        if (goalPosition.getColumnIndex() == characterPositionColumn && goalPosition.getRowIndex() == characterPositionRow) {
            gameOver = true;
            //Log.info("The player has reached the GoalPosition");

        }
        setChanged();
        notifyObservers();
    }
    private boolean checkMove(int row, int column) {
        boolean flag = false;
        if(row > -1 && row < mazeArray.length)
            if(column > -1 && column < mazeArray[0].length)
                if(mazeArray[row][column] == 0)
                    flag = true;
        return flag;
    }

    @Override
    public int[][] GetMaze() {
        return mazeArray;
    }

    @Override
    public int GetCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int GetCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }
    @Override
    public boolean isSolvedMaze() {
        return solvedMaze;
    }
    @Override
    public boolean isHint() {
        return hint;
    }

    @Override
    public ArrayList<Integer> GetRowsSolution() {
        return rowsSolution;
    }
    @Override
    public ArrayList<Integer> GetColumnsSolution() {
        return columnsSolution;
    }

    @Override
    public void SaveFile(File newFile) throws IOException {
        //Log.info("The player saved the current maze");

        FileOutputStream outFile = new FileOutputStream(newFile);
        ObjectOutputStream returnFile = new ObjectOutputStream(outFile);

        //maze.getStartPosition().setPosition(characterPositionRow, characterPositionColumn);
        returnFile.writeObject(maze);

        //maze.getStartPosition().setPosition(startPosition.getRowIndex(), startPosition.getColumnIndex());

        returnFile.flush();
    }
    @Override
    public void  LoadFile(File newFile) throws IOException, ClassNotFoundException {
        //Log.info("The player load a maze");
        FileInputStream inputFile = new FileInputStream(newFile);
        ObjectInputStream returnFile = new ObjectInputStream(inputFile);

        Maze returnMaze = (Maze) returnFile.readObject();
        returnFile.close();

        maze = returnMaze;

        mazeArray  = new int[returnMaze.getRows()][returnMaze.getColumns()];
        for(int row = 0; row < returnMaze.getRows(); row++) {
            for (int column = 0; column < returnMaze.getColumns(); column++)
                mazeArray[row][column] = maze.getCell(row, column);
        }

        startPosition = maze.getStartPosition();
        goalPosition = maze.getGoalPosition();

        gameOver = false;
        solvedMaze = false;

        characterPositionRow = startPosition.getRowIndex();
        characterPositionColumn = startPosition.getColumnIndex();

        setChanged();
        notifyObservers();
    }
    @Override

    public void setSolvedMaze(boolean solvedMaze) {
        this.solvedMaze = solvedMaze;
    }
    @Override
    public void setHint(boolean hintValue) {
        hint = hintValue;
    }
    @Override
    public void Hint(){
        //Log.info("The player ask for Hint");
        hint = true;
        SolveMaze();
    }

    @Override
    public void saveProperties(String generateMaze, String solvingMethod, String numOfThreads) throws IOException, ClassNotFoundException{
        //Log.info("The player changed the Game Properties:");
        //Log.info("TypeMaze: " + typeMaze +" ,SolvingMethod: " + solvingMethod + " ,numOfThread: " + numOfThreads + "\n");
        File file = new File("resources/config.properties");
        Properties properties = new Properties();

        if(file.length() == 0)
            Configurations.getInstance();

        InputStream input  = new FileInputStream("resources/config.properties");
        OutputStream out = new FileOutputStream("resources/config.properties");

        properties.load(input);
        properties.setProperty("mazeGeneratingAlgorithm", generateMaze);
        properties.setProperty("mazeSearchingAlgorithm", solvingMethod);
        properties.setProperty("threadPoolSize", numOfThreads);

        properties.store(out, null);


    }


    /*@Override
    public void addToLog(String msg){
        Log.error(msg);
    }*/
}
