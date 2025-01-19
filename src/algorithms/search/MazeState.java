package algorithms.search;

import algorithms.mazeGenerators.Position;

/**
 * state im SearchableMaze
 */
public class MazeState extends AState{
    private Position curr;

    public MazeState(int row, int column) {
        super();
        this.curr = new Position(row, column);
    }

    /**
     *
     * @return Position's represention
     */
    @Override
    public String toString() {
        return curr.toString();
    }

    /**
     *
     * @return Position's row index
     */
    public int getStateRow(){
        return curr.getRowIndex();
    }

    /**
     *
     * @return Position's column index
     */
    public int getStateColumn(){
        return curr.getColumnIndex();
    }
}
