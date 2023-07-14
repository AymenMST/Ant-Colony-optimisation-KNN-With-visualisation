package aco;

import graph.Graph;
import graph.Node;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

import main.Row;

/**
 *
 * @author Pc
 */
public class ACO {

    private Random rand = new Random(1000);
    private Graph g = new Graph();
    private Colony colony;

    // initialize tunable parameters
    static int x = 800;
    static int y = 600;
    static int neighborhoodSize = 35;
    static int maxMove = 40;
    static int numAnts = 10;
    static double pickupGain = 0.5;
    static double dropGain = 0.0005;
    static double worseDropoffProbability = 0.05;
    static double maxAlpha = 2.0;
    static double maxBeta = 5.0;
    static double maxRho = 0.7;
    /**
     * Constructor
     */
    public ACO(List<Row> data) {
        initialize(data);
    }

    /**
     * Places randomly rows and ants .
     */
    public void initialize(List<Row> data) {
        // place data points
        for (Row d : data) {
            int x = rand.nextInt(ACO.x);
            int y = rand.nextInt(ACO.y);
            Node vertex = new Node(d, new Point2D.Double(x, y));
            vertex.setAlpha(0.1);
            g.addVertex(vertex);
        }
        // place ants
        colony = new Colony(numAnts);
    }

    /**
     * Runs a single iteration of the ACO algorithm.
     */
    public void runIteration() {
        colony.move();
        colony.act(g);
    }

    /**
     * @return	A list of the ants being used by the algorithm.
     */
    public List<Ant> getAnts() {
        return colony.getAnts();
    }

    /**
     * @return	The graph being modified by the ACO algorithm.
     */
    public Graph getGraph() {
        return g;
    }

    /**
     * @return	The width of the virtual space.
     */
    public int getXSpace() {
        return x;
    }

    /**
     * @return	The height of the virtual space.
     */
    public int getYSpace() {
        return y;
    }

}
