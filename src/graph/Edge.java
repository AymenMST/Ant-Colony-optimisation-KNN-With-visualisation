package graph;

/**
 *
 * @author Pc
 */
public class Edge {

    private double weight = -1.0;
    private double pheromone = 0.7;

    /**
     * @return	The weight of the edge
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight	The weight of the edge.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return	The pheromone on the edge.
     */
    public double getPheromone() {
        return pheromone;
    }

    /**
     * @param pheromone	The pheromone on the edge.
     */
    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

}
