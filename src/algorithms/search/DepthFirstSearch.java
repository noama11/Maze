package algorithms.search;

import java.util.*;
/**
 * solves searchable problem with Breadth First Search
 */
public class DepthFirstSearch extends ASearchingAlgorithm {
    /**
     * solve searchable problem with Breadth First Search
     * @param  searchable the searchable problem to solve
     * @return the solution path
     */
    @Override
    public Solution solve(ISearchable searchable) {
        if(searchable == null)
            return null;
        Solution sol = new Solution();
        AState start = searchable.getStartState();
        HashSet<String> visited = new HashSet<>();
        ArrayList<AState> listSol = new ArrayList<>();
        Stack<AState> stack = new Stack<AState>();
        stack.push(start);
        visited.add(start.toString());
        int visitedNodes = 0;
        while (!stack.isEmpty()){
            AState curr = stack.pop();
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
                //check if the neighbor was seen before
                if(!visited.contains(neighbor.toString())){
                    visited.add(neighbor.toString());
                    neighbor.setPredecessor(curr);
                    stack.push(neighbor);
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
        return "Depth First Search";
    }
}
