package algorithms.search;

import java.util.ArrayList;

public interface ISearchable {
    /**
     *
     * @return start state
     */
    AState getStartState();

    /**
     *
     * @return goal state
     */
    AState getGoalState();

    /**
     *
     * @param currA current state to check
     * @return all possible moves from current state
     */
    ArrayList<AState> getAllPossibleStates(AState currA);
}
