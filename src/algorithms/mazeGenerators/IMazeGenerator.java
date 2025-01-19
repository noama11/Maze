package algorithms.mazeGenerators;

public interface IMazeGenerator {
    Maze generate(int var1, int var2);

    long measureAlgorithmTimeMillis(int var1, int var2);
}
