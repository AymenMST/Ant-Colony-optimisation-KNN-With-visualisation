package clustering;

import evalution.ClusteringEvaluation;
import graph.Graph;
import graph.Node;

import java.util.List;

import jung.JungHandler;
import main.Row;
import tools.Tools;

/**
 *
 * @author Pc
 */
public abstract class ClusteringModel {

    protected int outIndex;
    protected List<Row> data;
    protected boolean visualize = false;
    protected Graph g = new Graph();
    protected JungHandler jungHandler = new JungHandler();
    protected List<List<Node>> clusters;
    protected ClusteringEvaluation evaluation;

    /**
     * Constructs a generic TrainingMethod class.
     *
     * @param data The data that will be used to train the network.
     * @param evaluation
     */
    public ClusteringModel(List<Row> data, ClusteringEvaluation evaluation) {
        this.data = data;
        this.evaluation = evaluation;
    }

    /**
     * The main loop that performs all work related to training and testing the
     * network.
     *
     * @return
     */
    public double run() {

        // used for timing the training algorithm
        long startTime, elapsedTime;
        // perform clustering algorithm and record runtime
        startTime = System.currentTimeMillis();
        cluster();
        elapsedTime = System.currentTimeMillis() - startTime;
        // evaluate the perfomance of the clustering algorithm
        double index = evaluate();

        System.out.println("Dunn Index:" +"\t"+ "Time: "+"\t" + "Clusters numbers: ");
        System.out.println(Tools.round(index, 4) + "\t"+"\t" + elapsedTime + "\t"+"\t" + clusters.size());

        return index;

    }

    /**
     * The train method will be different for each implementation.
     */
    public abstract void cluster();

    /**
     * @return A fitness value of the clusters.
     */
    public double evaluate() {
        return evaluation.getIndex(clusters);
    }

    /**
     * @param visualize Whether or not visualization should be used for the
     * algorithm.
     */
    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    /**
     * @param startVisualize At what time step visualization should start
     * occurring.
     */
    public void setStartVisualize(int startVisualize) {
        jungHandler.setStartVisualize(startVisualize);
    }

    /**
     * @param directory The directory to write images from the visualizer to.
     */
    public void setVisualizeDirectory(String directory) {
        jungHandler.saveImagesTo(directory);
    }

    /**
     * @return The clusters returned by the clustering method.
     */
    public List<List<Node>> getClusters() {
        return clusters;
    }

}
