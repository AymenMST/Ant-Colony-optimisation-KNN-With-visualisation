package aco;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import edu.uci.ics.jung.graph.Forest;
import graph.Edge;
import graph.Node;

/**
 *
 * @author Pc
 */
public class Colony {

    private List<Ant> ants = new ArrayList<Ant>();
    private Random rand = new Random(1000);

    /**
     * Creates a colony of ants.
     *
     * @param size	The number of ants that will be put in the population.
     */
    public Colony(int size) {
        initialize(size);
    }

    /**
     * Adds the specified number of ants to random locations in a virtual word.
     *
     * @param size	The number of ants to create in the colony.
     */
    private void initialize(int size) {
        for (int i = 0; i < size; i++) {
            int x = rand.nextInt(ACO.x);
            int y = rand.nextInt(ACO.y);
            Ant ant = new Ant(new Point2D.Double(x, y));
            ants.add(ant);
        }
    }

    /**
     * Perform actions that move all ants during a time step.
     */
    public void move() {

        for (Ant ant : ants) {

            // randomly get movement
            double moveX = (rand.nextDouble() * ACO.maxMove * 2 - ACO.maxMove);
            double moveY = (rand.nextDouble() * ACO.maxMove * 2 - ACO.maxMove);

            // calculate and bound X
            double newX = ant.getX() + moveX;
            if (newX < 0 || newX > ACO.x) {
                moveX = -moveX;
            }
            // calculate and bound Y
            double newY = ant.getY() + moveY;
            if (newY < 0 || newY > ACO.y) {
                moveY = -moveY;
            }

            // move ant
            ant.move(moveX, moveY);

        }
    }

    /**
     * Perform actions that decide whether or not to pick something up at a
     * given time step.
     *
     * @param g	The graph that the ACO is clustering.
     */
    public void act(Forest<Node, Edge> g) {
        // for all ants
        for (Ant ant : ants) {
            List<Node> neighborhood = new ArrayList<>();

            // calculate which nodes belong in the neighborhood
            for (Node vertex : g.getVertices()) {
                double dist = ant.distance(vertex.getLocationVector(), ant.getLocationVector());
                if (dist < ACO.neighborhoodSize) {
                    neighborhood.add(vertex);
                }
            }

            // get density of the surrounding area
            double density = (double) neighborhood.size() / g.getVertexCount();
            double alpha = tools.Tools.getRandomDouble(0.5, ACO.maxAlpha);
            double beta = tools.Tools.getRandomDouble(1, ACO.maxBeta);
            double rho = tools.Tools.getRandomDouble(0.1, ACO.maxRho);
            // if holding nothing, attempt pickup
            if (!ant.isHolding()) {
                if (ant.pickup(neighborhood, density, alpha, beta)) {
                    // if ant has opted to pick up a node, remove it from graph
                    g.removeVertex(ant.getHolding());
                }
                // if holding a node, attempt dropoff
            } else {
                Node temp = ant.getHolding();
                if (ant.drop(neighborhood, density, alpha, beta)) {
                    // if an ant has dropped a node, add to graph
                    temp.setLocation(ant.getLocation());
                    temp.setColor(Color.YELLOW);
                    temp.setAlpha(0.2);
                    g.addVertex(temp);

                }
            }
        }
    }

    /**
     * @return	A list of the ants used by the ACO algorithm.
     */
    public List<Ant> getAnts() {
        return ants;
    }

}
