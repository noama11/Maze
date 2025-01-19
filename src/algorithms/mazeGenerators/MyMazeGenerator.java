package algorithms.mazeGenerators;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * MyMazeGenerator is a maze generation class that uses a variation of the
 * depth-first search (DFS) algorithm. It starts with all walls intact, then
 * iteratively breaks walls in random directions to create a navigable maze.
 */
public class MyMazeGenerator extends AMazeGenerator {
    private Stack<Position> unvisited; // Stack to keep track of cells to visit
    private boolean check = false; // Tracks whether a wall was successfully broken

    public MyMazeGenerator() {
    }

    public Maze generate(int rows, int columns) {
        if (rows >= 1 && columns >= 1) {
            Maze maze = new Maze(rows, columns);
            maze.allWalls(); // Start with all walls in place
            maze.setCell(0, 0, 0);  // Open the initial cell
            this.unvisited = new Stack();
            this.unvisited.push(maze.getStartPosition());

            // Continue until all cells have been visited
            while(!this.unvisited.isEmpty()) {
                ArrayList<String> directions = this.randomDirectionsList();// Get a random order of directions
                this.check = false;

                for(int i = 0; i < 4; ++i) {
                    int currRow = ((Position)this.unvisited.peek()).getRowIndex();
                    int currColumn = ((Position)this.unvisited.peek()).getColumnIndex();
                    if (directions.get(i) == "U") {
                        if (currRow > 1) {
                            this.breakWallRow(maze, currRow, currColumn, -2, -1);
                        }
                    } else if (directions.get(i) == "D") {
                        if (maze.getRows() - currRow > 2) {
                            this.breakWallRow(maze, currRow, currColumn, 2, 1);
                        }
                    } else if (directions.get(i) == "L") {
                        if (currColumn > 1) {
                            this.breakWallsCol(maze, currRow, currColumn, -2, -1);
                        }
                    } else if (maze.getColumns() - currColumn > 2) {
                        this.breakWallsCol(maze, currRow, currColumn, 2, 1);
                    }
                }
                // If no walls were broken, backtrack
                if (!this.check) {
                    this.unvisited.pop();
                }
            }

            maze.setGoal();
            return maze;
        } else {
            return null; // Return null if dimensions are invalid
        }
    }

    /**
     * Returns a shuffled list of the four possible movement directions.
     *
     * @return a randomized list of directions ("U", "D", "L", "R")
     */
    public ArrayList<String> randomDirectionsList() {
        ArrayList<String> directions = new ArrayList();
        directions.add("U");
        directions.add("D");
        directions.add("L");
        directions.add("R");
        Collections.shuffle(directions);
        return directions;
    }

    /**
     * Attempts to break a wall between the current cell and a cell
     * located by moving a certain number of rows.
     *
     * @param maze      the maze object
     * @param indexR    the current row
     * @param indexC    the current column
     * @param rowMove2  the row offset to the target cell
     * @param rowMove1  the row offset to the wall cell
     */
    public void breakWallRow(Maze maze, int indexR, int indexC, int rowMove2, int rowMove1) {
        if (maze.getCell(indexR + rowMove2, indexC) != 0) {
            this.check = true;
            maze.setCell(indexR + rowMove2, indexC, 0); // Open target cell
            maze.setCell(indexR + rowMove1, indexC, 0); // Break wall
            this.unvisited.push(new Position(indexR + rowMove2, indexC));
        }

    }

    public void breakWallsCol(Maze maze, int indexR, int indexC, int colMove2, int colMove1) {
        if (maze.getCell(indexR, indexC + colMove2) != 0) {
            this.check = true;
            maze.setCell(indexR, indexC + colMove2, 0);
            maze.setCell(indexR, indexC + colMove1, 0);
            this.unvisited.push(new Position(indexR, indexC + colMove2));
        }

    }
}
