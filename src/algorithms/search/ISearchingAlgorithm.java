package algorithms.search;

public interface ISearchingAlgorithm {
    /**
     *
     * @param searchable the searchable problem to solve
     * @return solution for the maze
     */
    Solution solve(ISearchable searchable);

    /**
     *
     * @return how many nodes evaluated during the search
     */
    int getNumberOfNodesEvaluated();
    String getName();


}
