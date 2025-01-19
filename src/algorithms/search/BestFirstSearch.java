package algorithms.search;

import java.util.*;

/**
 * solves searchable problem with Best First Search
 */
public class BestFirstSearch extends ASearchingAlgorithm {
    /**
     * solve searchable problem with Best First Search
     * @param searchable the searchable problem to solve
     * @return the solution path
     */
    @Override
    public Solution solve(ISearchable searchable) {
        if(searchable.getStartState() == null)
            return null;
        PriorityQueue<AState> pq = new PriorityQueue<AState>(AState::compareTo);
        Solution sol = new Solution();
        AState start = searchable.getStartState();
        HashSet<String> visited = new HashSet<>();
        ArrayList<AState> listSol = new ArrayList<>();
        pq.offer(start);
        visited.add(start.toString());
        int visitedNodes = 0;
        while (!pq.isEmpty()){
            AState curr = pq.poll(); // Get the most promising stat- the state with the lowest cost
            visitedNodes++;

            //check if it's the goal state
            if(curr.toString().equals(searchable.getGoalState().toString())){
                while (curr != null){
                    listSol.add(curr);
                    curr = curr.getPredecessor();
                }
                Collections.reverse(listSol);
                sol.setSolutionPath(listSol);
                break;
            }

            //check all state's neighbors
            for (int i=0; i<searchable.getAllPossibleStates(curr).size(); i++){
                AState neighbor = searchable.getAllPossibleStates(curr).get(i);
                int newCost = curr.getCost() + neighbor.getCost();
                //check if the neighbor was seen before
                if(!visited.contains(neighbor.toString())){
                    neighbor.setPredecessor(curr); // set to neighbor its parent (curr)
                    neighbor.setCost(newCost);
                    visited.add(neighbor.toString());
                    pq.offer(neighbor); // push to the queue
                }
            }
        }
        this.setVisitedNodes(visitedNodes);
        return sol;
    }
    /**
     * get algorithm name
     * @return algorithm's name
     */
    @Override
    public String getName() {
        return "Best First Search";
    }
}
