package algorithms.search;

/**
 * abstract class for searching algorithms that solves searchable problems
 */
public abstract class ASearchingAlgorithm implements ISearchingAlgorithm {
    private int visitedNodes;

    /**
     *
     * @return how many nodes were visited id the searching algorithm
     */
    @Override
    public int getNumberOfNodesEvaluated() {
        return visitedNodes;
    }

    /**
     * set how many nodes were visited id the searching algorithm
     * @param visitedNodes the number to set
     */
    public void setVisitedNodes(int visitedNodes) {
        this.visitedNodes = visitedNodes;
    }
}
