package Model;

import algorithms.mazeGenerators.Position;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observer;

public interface IModel {
    void startServers();
    void stopServers();

    void assignObserver(Observer o);

    void GenerateMaze(int Width, int Height);
    void SolveMaze();

    void MoveCharacter(int direction);
    int[][] GetMaze();

    int GetCharacterPositionRow();
    int GetCharacterPositionColumn();

    boolean isGameOver();
    boolean isSolvedMaze();
    boolean isHint();

    void Hint();

    void setHint(boolean hintValue);
    void setSolvedMaze(boolean solvedMaze);

    void SaveFile(File MyFile) throws IOException;
    void LoadFile(File MyFile) throws IOException, ClassNotFoundException;
    void saveProperties(String generateMaze, String solvingMethod, String numOfThreads) throws IOException, ClassNotFoundException;

    ArrayList<Integer> GetColumnsSolution();
    ArrayList<Integer>GetRowsSolution();

    Position getGoalPosition();

    void addToLog(String msg);

}
