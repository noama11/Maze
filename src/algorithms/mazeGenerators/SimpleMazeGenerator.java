package algorithms.mazeGenerators;

import java.util.Random;

public class SimpleMazeGenerator extends AMazeGenerator {
    /**
     * Generates a maze with specified number of rows and columns. It starts by creating an empty maze,
     * randomly places walls, ensures the start and end cells are open, and then guarantees a path from
     * start to end. If the dimensions are less than 1x1, it returns null.
     */
    public SimpleMazeGenerator() {
    }

    public Maze generate(int rows, int columns) {
        if (rows >= 1 && columns >= 1) {
            Maze maze = new Maze(rows, columns);
            maze = this.randomWalls(maze);
            maze.setCell(0, 0, 0);
            maze.setCell(rows - 1, columns - 1, 0);
            maze = this.downRight(maze);
            return maze;
        } else {
            return null;
        }
    }

    /**
     * Populates a given empty maze with walls based on a 70% probability for each cell. Cells that do
     * not meet the probability threshold remain unchanged. This method provides the initial random
     * setup of the maze.

     */
    private Maze randomWalls(Maze emptyMaze) {
        Random ran = new Random();

        for(int i = 0; i < emptyMaze.getRows(); ++i) {
            for(int j = 0; j < emptyMaze.getColumns(); ++j) {
                double prob = ran.nextDouble();
                if (prob <= 0.7) {
                    int randomNum = ran.nextInt(2);
                    emptyMaze.setCell(i, j, randomNum); // Set each cell randomly as wall or open path
                }
            }
        }

        return emptyMaze;
    }

    // Creates a guaranteed path from the top-left to the bottom-right of the maze.
    private Maze downRight(Maze maze) {
        Random ran = new Random();
        int rowIndex = 0;
        int columnIndex = 0;

        while(true) {
            while(true) {
                int move;
                int i;
                do {
                    if (rowIndex >= maze.getRows() - 1 && columnIndex >= maze.getColumns() - 1) {
                        return maze; // Exit once end is reached
                    }

                    if (maze.getRows() - rowIndex > 1) {
                        if (maze.getRows() - rowIndex == 2) {
                            maze.setCell(rowIndex + 1, columnIndex, 0);
                            ++rowIndex;
                        } else {
                            move = ran.nextInt(maze.getRows() - rowIndex);

                            for(i = 1; i < move + 1; ++i) {
                                maze.setCell(rowIndex + i, columnIndex, 0);
                            }

                            rowIndex += move;
                        }
                    }
                } while(maze.getColumns() - columnIndex <= 1);

                if (maze.getColumns() - columnIndex == 2) {
                    maze.setCell(rowIndex, columnIndex + 1, 0);
                    ++columnIndex;
                } else {
                    move = ran.nextInt(maze.getColumns() - columnIndex);

                    for(i = 1; i < move + 1; ++i) {
                        maze.setCell(rowIndex, columnIndex + i, 0);
                    }

                    columnIndex += move;
                }
            }
        }
    }
}
