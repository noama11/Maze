package algorithms.search;

import java.io.Serializable;

/**
 * abstract class that represent a state in the searchable problem
 */
public abstract class AState implements Comparable<AState>, Serializable {
    //Comparable: This allows states to be compared based on their costs, which is essential for algorithms that need to choose the "best" path
    //Serializable: This enables states to be saved to files or sent across networks if needed
    private AState predecessor;
    private int cost;

    /**
     * constructor
     */
    public AState() {
        this.predecessor = null;
    }

    /**
     * Compare the AStates' cost
     * @param o the object to be compared.
     * @return a number to indicate which state is bigger or they are equal
     */
    @Override
    public int compareTo(AState o) {
        if (o == null)
            return -2;
        if(this.getCost() > o.getCost())
            return -1;
        else if (this.getCost() < o.getCost())
            return 1;
        return 0;
    }

    public abstract String toString();

    /**
     *
     * @return the AState's predecessor
     */
    public AState getPredecessor() {
        return predecessor;
    }

    /**
     *
     * @return the AState's cost
     */
    public int getCost(){
        return cost;
    }

    /**
     * set the AState's predecessor
     * @param predecessor
     */
    public void setPredecessor(AState predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * set the AState's cost
     * @param cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

}
