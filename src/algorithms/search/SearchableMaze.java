package algorithms.search;

import algorithms.mazeGenerators.Maze;

import java.util.ArrayList;

public class SearchableMaze implements ISearchable {
    private Maze maze; // Holds the original maze we're adapting

    public SearchableMaze(Maze maze) {
        this.maze = maze;
    }

    /**
     *
     * @return start MazeState
     */
    @Override
    // Translates maze's start position into a search state
    public AState getStartState() {
        if (this.maze == null)
            return null;
        AState start = new MazeState(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
        return start;
    }
    /**
     *
     * @return goal MazeState
     */
    @Override
    public AState getGoalState() {
        if (this.maze == null)
            return null;
        AState goal = new MazeState(maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColumnIndex());
        return goal;
    }

    /**
     *
     * @param currA current state to check
     * @return array represent All Possible moves from current position
     */
    public ArrayList<AState> getAllPossibleStates(AState currA){
        ArrayList<AState> states = new ArrayList<AState>();
        ArrayList<AState> directStates;
        ArrayList<AState> diagonalStates;
        directStates = getAllDirectStates(currA); // Direct movements
        diagonalStates = getAllDiagonalStates(currA);   // Diagonal movements
        states.addAll(directStates);
        states.addAll(diagonalStates);
        return states;
    }
    /**
     *
     * @param currA current state to check
     * @return array represent All Possible direct moves from current position
     */
    private ArrayList<AState> getAllDirectStates(AState currA){
        ArrayList<AState> states = new ArrayList<AState>();
        int currRow = ((MazeState)currA).getStateRow();
        int currCol = ((MazeState)currA).getStateColumn();
        //down
        if (currRow < maze.getRows() - 1 && maze.getCell(currRow+1,currCol) == 0){
            AState newS = new MazeState(currRow+1, currCol);
            newS.setCost(10);
            states.add(newS);
        }
        //right
        if (currCol < maze.getColumns() - 1 && maze.getCell(currRow,currCol+1) == 0){
            AState newS = new MazeState(currRow, currCol+1);
            newS.setCost(10);
            states.add(newS);
        }
        //up
        if (currRow > 0 && maze.getCell(currRow-1,currCol) == 0){
            AState newS = new MazeState(currRow-1, currCol);
            newS.setCost(10);
            states.add(newS);
        }
        //left
        if (currCol > 0  && maze.getCell(currRow,currCol-1) == 0){
            AState newS = new MazeState(currRow, currCol-1);
            newS.setCost(10);
            states.add(newS);
        }
        return states;
    }
    /**
     *
     * @param currA current state to check
     * @return array represent All Possible diagonal moves from current position
     */
    private ArrayList<AState> getAllDiagonalStates(AState currA){
        ArrayList<AState> states = new ArrayList<AState>();
        int currRow = ((MazeState)currA).getStateRow();
        int currCol = ((MazeState)currA).getStateColumn();
        //down right
        if ((currRow < maze.getRows() - 1) && (currCol < maze.getColumns() - 1) && (maze.getCell(currRow+1,currCol+1) == 0)){
            if(maze.getCell(currRow+1,currCol) == 0 || maze.getCell(currRow,currCol+1) == 0) {
                AState newS = new MazeState(currRow + 1, currCol + 1);
                newS.setCost(15);
                states.add(newS);
            }
        }
        //down left
        if ((currRow < maze.getRows() - 1) && (currCol > 0) && (maze.getCell(currRow+1,currCol-1) == 0)){
            if(maze.getCell(currRow+1,currCol) == 0 || maze.getCell(currRow,currCol-1) == 0) {
                AState newS = new MazeState(currRow + 1, currCol - 1);
                newS.setCost(15);
                states.add(newS);
            }
        }
        //up right
        if ((currRow > 0) && (currCol < maze.getColumns() - 1) && (maze.getCell(currRow-1,currCol+1) == 0)){
            if(maze.getCell(currRow-1,currCol) == 0 || maze.getCell(currRow,currCol+1) == 0) {
                AState newS = new MazeState(currRow - 1, currCol + 1);
                newS.setCost(15);
                states.add(newS);
            }
        }
        //up left
        if ((currRow > 0) && (currCol > 0) && (maze.getCell(currRow-1,currCol-1) == 0)){
            if(maze.getCell(currRow-1,currCol) == 0 || maze.getCell(currRow,currCol-1) == 0) {
                AState newS = new MazeState(currRow - 1, currCol - 1);
                newS.setCost(15);
                states.add(newS);
            }
        }
        return states;
    }
}
