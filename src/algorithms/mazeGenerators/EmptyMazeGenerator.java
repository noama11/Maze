package algorithms.mazeGenerators;

public class EmptyMazeGenerator extends AMazeGenerator {
    public EmptyMazeGenerator() {
    }

    public Maze generate(int rows, int columns) {
        if (rows >= 1 && columns >= 1) {
            Maze maze = new Maze(rows, columns);
            return maze;
        } else {
            return null;
        }
    }
}
