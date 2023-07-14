package aco;

import graph.Node;
import graph.Edge;
import graph.Utilities;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Pc
 */
public class Ant {

    private Point2D.Double location;
    private double lastMoveX = 0;
    private double lastMoveY = 0;
    private Node holding = null;
    private Color colorEmpty = Color.MAGENTA;
    private Color colorHolding = Color.RED;
    private Color color = colorEmpty;
    private Random rand = new Random(11235);
    private double lastError;

    /**
     * Create an ant and place at a specified location.
     *
     * @param location	The location to place the ant in the virtual 2D space.
     */
    public Ant() {

    }

    public Ant(Point2D.Double location) {
        this.location = location;
    }

    /**
     * @return	An empty node at the ant's location (for graphing purposes).
     */
    public Node toNode() {
        return new Node(null, location);
    }

    /**
     * @return	The x location of the ant in virtual 2D space.
     */
    public double getX() {
        return location.x;
    }

    /**
     * @return	The y location of the ant in virtual 2D space.
     */
    public double getY() {
        return location.y;
    }

    /**
     * @return	The location in the virtual 2D space that the ant resides.
     */
    public Point2D.Double getLocation() {
        return new Point2D.Double(location.x, location.y);
    }

    /**
     * @return	The ant's location in List format.
     */
    public List<Double> getLocationVector() {
        List<Double> vector = new ArrayList<Double>(2);
        vector.add(location.x);
        vector.add(location.y);
        return vector;
    }

    /**
     * Moves the ant to the specified location relative to the current location.
     *
     * @param x	The amount to move in the x direction.
     * @param y	The amount to move in the y direction.
     */
    public void move(double x, double y) {
        lastMoveX = x;
        lastMoveY = y;
        location.x += x;
        location.y += y;
    }

    /**
     * Probabilistically picks up a node. More likely to pick up items in sparse
     * areas, most likely to pick up nodes in dense areas. Most different node
     * is always selected when picking up.
     *
     * @param neighborhood	The nodes nearby.
     * @param density	The number of nodes in the neighborhood as compared to all
     * @param beta Heuristic Influence.
     * @param alpha Pheromone Concentration Influence. nodes.
     * @return	Whether or not a node was picked up.
     */
    public boolean pickup(List<Node> neighborhood, double density, double alpha, double beta) {

        //System.out.println(pickupProbability(density));
        // decide to pick something up
        if (rand.nextDouble() <= pickupProbability(density) && neighborhood.size() > 1) {

            // choose the node that is most different
            Node toPickUp = Utilities.mostUnique(neighborhood);

            // pick up the node that was selected
            lastError = toPickUp.getError();
            holding = toPickUp;
            color = colorHolding;

            return true;
        }

        return false;

    }

    /**
     * Probabilistically drops a node.More likely to drop items in dense areas,
     * less likely to drop items in sparse areas.If the new location is better,
     * always drop. If the new location is worse, drop with some probability.
     *
     * @param neighborhood	The nodes nearby.
     * @param density	The number of nodes in the neighborhood as compared to all
     * nodes.
     * @param beta Heuristic Influence.
     * @param alpha Pheromone Concentration Influence.
     * @return	Whether or not a node was dropped.
     */
    public boolean drop(List<Node> neighborhood, double density, double alpha, double beta) {

        // potentially decide to drop something
        if (rand.nextDouble() <= dropProbability(density)) {

            // get the error if this node is placed in this new location
            double newError = Utilities.calculateError(holding, neighborhood);

            // if error has been reduced from when the node was picked up, drop node
            if (newError < lastError || rand.nextDouble() < ACO.worseDropoffProbability) {
                    holding = null;
                    color = colorEmpty;

                return true;
            } else {
                return false;
            }

        }

        return false;
    }

    /**
     * The probability that an ant will pick up a node given current node
     * density.
     *
     * @param density	The number of nodes in the neighborhood as compared to all
     * nodes.
     * @return	The probability value.
     */
    public double pickupProbability(double density) {
        return Math.pow((ACO.pickupGain / (ACO.pickupGain / 10 + density)), 2);
    }

    /**
     * The probability that an ant will drop a node given current node density.
     *
     * @param density	The number of nodes in the neighborhood as compared to all
     * nodes.
     * @return	The probability value.
     */
    public double dropProbability(double density) {
        return Math.pow((density / (ACO.dropGain / 10 + density)), 2);
    }

    /**
     * @return	Whether or not there is an object the ant is currently holding.
     */
    public boolean isHolding() {
        if (holding == null) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * @return	the updated value of pheromone.
     */
  //  public double updatePheromone() {
     //   return ((1-tools.Tools.getRandomDouble(0.1, ACO.maxRho))* new Edge().getPheromone());
   // }

    /**
     * @return	The node that the ant is currently holding.
     */
    public Node getHolding() {
        return holding;
    }

    /**
     * @return	The last amount moved in the x direction.
     */
    public double getLastMoveX() {
        return lastMoveX;
    }

    /**
     * @return	The last amount moved in the y direction.
     */
    public double getLastMoveY() {
        return lastMoveY;
    }

    /**
     * @return	The current Java color value.
     */
    public Color getColor() {
        return color;
    }

    public Double distance(List<Double> vector1, List<Double> vector2) {
        Double distance = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            distance += Math.pow((vector2.get(i) - vector1.get(i)), 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

}
