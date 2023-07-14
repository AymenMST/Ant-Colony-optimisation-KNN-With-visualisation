package clustering;

import java.util.List;

import tools.Tools;
import aco.ACO;
import aco.Ant;
import main.Row;
import evalution.ClusteringEvaluation;
import graph.Graph;
import graph.Node;

/**
 *
 * @author Pc
 */
public class AntColonyClustering extends ClusteringModel {

    private ACO aco;
    private ClusteringMethod clusteringMethod;

    private double maxPerformance;
    // initialize tunable params
    private int checks = 10000;
    private int maxIterations = 1000;
    // the number of times performance is allowed to be worse during checks before exiting the loop
    private int maxWorse = 100;
    boolean echo = false;

    /**
     * Creates the driver for the ACO clustering algorithm.
     *
     * @param data	The data to run ACO on.
     * @param evaluation The fitness evaluation to use for ACO.
     */
    public AntColonyClustering(List<Row> data, ClusteringEvaluation evaluation) {
        super(data, evaluation);
        aco = new ACO(data);
        jungHandler.setDimensions(aco.getXSpace(), aco.getYSpace());
    }

    @Override
    public void cluster() {

        clusteringMethod = new ClusteringMethod(data, evaluation);
        maxPerformance = 0;
        List<List<Node>> bestClustering = null;
        int worseCount = 0;
        // loop until converged, or max iterations reached
        for (int i = 0; worseCount < maxWorse && i < maxIterations*15; i++) {

            aco.runIteration();

            // draw the visualizer for the ACO
            if (visualize) {
                drawGraph();
            }

            // stop to evaluate dunn index of the created graph
            if (i % checks == 0) {
                clusteringMethod.setPseudoGraph(aco.getGraph());
                clusteringMethod.cluster();
                this.clusters = clusteringMethod.clusters;

                //evaluate fitness after a clustering iteration has passed.
                double index = evaluation.getIndex(clusters);
                if (echo) {
                    System.out.println(Tools.round(index, 4));
                }

                //take best cluster seen so far as the best cluster
                if (index > maxPerformance) {
                    worseCount = 0;
                    maxPerformance = index;
                    bestClustering = clusters;
                } else {
                    worseCount++;
                }
            }
            clusters = bestClustering;

        }

    }

    /**
     * Draw a graph of the ACO algorithm at the current time step.
     */
    public void drawGraph() {

        g = new Graph();

        // add data points
        for (Node vertex : aco.getGraph().getVertices()) {
            g.addVertex(vertex);
        }

        // add ants
        for (Ant ant : aco.getAnts()) {
            Node node = ant.toNode();

            node.setColor(ant.getColor());
            g.addVertex(node);
        }

        // draw graph
        jungHandler.setGraph(g);
        jungHandler.draw();

    }

}
