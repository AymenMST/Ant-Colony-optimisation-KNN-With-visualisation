package clustering;

import aco.Ant;
import evalution.ClusteringEvaluation;
import graph.Graph;
import graph.Node;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tools.Tools;
import main.Main1;
import main.Row;

/**
 *
 * @author Pc
 */
public class ClusteringMethod extends ClusteringModel {

    private Graph graph;
    private Ant ant = new Ant();
    // the number of clusters
    private int numClusters = 0;
    // the maximum number of cluster
    private int maxK;
    private Main1 Main1;
    private int numFeatures = 0;
    // the amount of change allowable during an update of the algorithm for completion
    private double threshold = 0.001;
    // set the min and max values for the random center initialization
    private double initCenterMin = -0.3;
    private double initCenterMax = 0.3;
    // the actual centers found by the algorithm
    private List<List<Double>> centers;
    // the map that keeps track of which center datapoints are assigned to
    private Map<Row, Integer> clustersMap = new HashMap<Row, Integer>();
    // whether or not to use the datapoints' virtual locations
    private boolean useVirtualLocations = false;

    /**
     * Creates a driver for the Clustering algorithm.
     *
     * @param data The data to cluster
     * @param evaluation The evaluation to use.
     */
    public ClusteringMethod(List<Row> data, ClusteringEvaluation evaluation) {
        super(data, evaluation);
    }

    /**
     */
    public void cluster() {

        // build graph from datapoints if not set
        if (graph == null) {
            this.graph = new Graph();
            for (Row point : data) {
                graph.addVertex(new Node(point));
            }
        }

        // the size of the input space
        numFeatures = 0;
        for (Node node : graph.getVertices()) {
            // use the first data point to get the number of features
            Row datapoint = node.getDataPoint();
            // usage either virtual or feature space based on selection
            if (useVirtualLocations) {
                numFeatures = node.getLocationVector().size();
            } else {
                numFeatures = datapoint.getInputes().size();
            }
            break;
        }

        // attempt all possible values of k up to maxK, choosing the k with the best index
        double bestIndex = 0;
        int bestK = 0;
        int k;
        String info= (String) Main1.comboBox.getItemAt(Main1.comboBox.getSelectedIndex());  
        if(info=="Iris") {
        	maxK=3;
        	
        	for (k=3; k <= maxK; k++) {
                this.numClusters = k;
                runCluster();
                try {
                    double index = evaluation.getIndex(clusters);
                    if (index > bestIndex) {
                        bestIndex = index;
                        bestK = k;
                    }
                } catch (Exception e) {
                    // invalid number of clusters for the data
                    break;
                }
            }
        }else  if(info=="Breast Cancer Wisconsin") {
        	maxK=2;
        	for (k=2; k <= maxK; k++) {
                this.numClusters = k;
                runCluster();
                try {
                    double index = evaluation.getIndex(clusters);
                    if (index > bestIndex) {
                        bestIndex = index;
                        bestK = k;
                    }
                } catch (Exception e) {
                    // invalid number of clusters for the data
                    break;
                }
            }
        }else  if(info=="Dermatology") {
        	maxK=6;
        	for (k=6; k <= maxK; k++) {
                this.numClusters = k;
                runCluster();
                try {
                    double index = evaluation.getIndex(clusters);
                    if (index > bestIndex) {
                        bestIndex = index;
                        bestK = k;
                    }
                } catch (Exception e) {
                    // invalid number of clusters for the data
                    break;
                }
            }
        }
        

        // use the k with the best dunn index and re-cluster
        this.numClusters = bestK;
        runCluster();
        double index = evaluation.getIndex(clusters);

    }

    /**
     * Performs clustering by adjusting the k centers iteratively.
     */
    private void runCluster() {

        // set up the new list of centers
        initializeCenters();

        // until changes are very small, continue training iterations
        double change;
        do {
            // perform a single training iteration
            change = trainIteration();
           // ant.updatePheromone();
            // draw the visualizer to the screen
            if (visualize) {
                drawGraph();
            }
        } while (change > threshold);

        // get clusters
        clusters = new ArrayList<>(numClusters);
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<Node>());
        }
        for (Node node : graph.getVertices()) {
            Row datapoint = node.getDataPoint();
            clusters.get(clustersMap.get(datapoint)).add(node);
        }
    }

    /**
     * Assign a node to the nearest center.
     *
     * @param node The node to assign.
     * @return The cluster corresponding to the nearest center.
     */
    public int assignCluster(Node node) {
        Row datapoint = node.getDataPoint();
        double minDistance = Double.MAX_VALUE;
        int closestCenter = 0;

        // for each center
        for (int center = 0; center < numClusters; center++) {
            // calculate distance to current center
            double distance = 0.0;
            if (useVirtualLocations) {
                distance = ant.distance(node.getLocationVector(), centers.get(center));
            } else {
                distance = ant.distance(datapoint.getInputes(), centers.get(center));
            }
            // store min distance (and corresponding closest center)
            if (distance < minDistance) {
                minDistance = distance;
                closestCenter = center;
            }
        }
        return closestCenter;
    }

    /**
     * Performs a single training iteration for the KMeans clustering algorithm.
     *
     * @return The largest change that occurred to the centers.
     */
    public double trainIteration() {
        double change = 0.0;
        // for all nodes in the graph
        for (Node node : graph.getVertices()) {
            // assign to the closest center
            Row datapoint = node.getDataPoint();
            int closestCenter = assignCluster(node);
            // map datapoints to their corresponding centers
            clustersMap.put(datapoint, closestCenter);
        }
        // update the centers
        change = calculateCenters();
        // System.out.println(change);
        return change;
    }

    /**
     * Performs the center update rule according to the KMeans algorithm.
     *
     * @return The largest change made to a center.
     */
    private double calculateCenters() {
        double change = 0.0;
        // create points structure
        List<List<List<Double>>> points = new ArrayList<>(numClusters);
        for (int cluster = 0; cluster < numClusters; cluster++) {
            List<List<Double>> clust = new ArrayList<>();
            points.add(clust);
        }
        // add datapoints to points structure using clusters
        for (Node node : graph.getVertices()) {
            Row datapoint = node.getDataPoint();
            // find the cluster index this point belongs to
            int cluster = clustersMap.get(datapoint);
            // get existing cluster
            List<List<Double>> updated = points.get(cluster);
            // add datapoint to cluster
            if (useVirtualLocations) {
                updated.add(node.getLocationVector());
            } else {
                updated.add(datapoint.getInputes());
            }
            // update the cluster to use the new cluster
            points.set(cluster, updated);
        }
        // update centers
        for (int center = 0; center < numClusters; center++) {
            List<Double> newCenter = new ArrayList<Double>();
            // loop through all features of the current center
            for (int feature = 0; feature < numFeatures; feature++) {
                double average = 0.0;
                // loop through all the datapoints associated with the current center
                for (List<Double> point : points.get(center)) {
                    average += point.get(feature);
                }

                // calculate averages
                if (points.get(center).size() != 0) {
                    average /= points.get(center).size();
                } else {
                    average = centers.get(center).get(feature);
                }

                // record the max amount by which the centers have changed
                change = Math.max(change, Math.abs(average - centers.get(center).get(feature)));

                // build new center
                newCenter.add(average);
            }
            // move center to new location
            centers.set(center, newCenter);
        }
        return change;
    }

    /**
     * Initializes the centers to random variables within a specified range.
     */
    private void initializeCenters() {
        centers = new ArrayList<>(numClusters);
        for (int center = 0; center < numClusters; center++) {
            ArrayList<Double> newCenter = new ArrayList<Double>(numFeatures);
            for (int feature = 0; feature < numFeatures; feature++) {
                newCenter.add(Tools.getRandomDouble(initCenterMin, initCenterMax, new Random(1)));
            }
            centers.add(newCenter);
        }
    }

    /**
     * Retrieves the centers that were calculated by the algorithm.
     *
     * @return The centers as calculated by the algorithm.
     */
    public List<List<Double>> getCenters() {
        return centers;
    }

    /**
     * Draws a visualization of the KMeans algorithm for each time step.
     */
    public void drawGraph() {

        g = new Graph();

        // draw first 2 dimensions
        int dim1 = 0;
        int dim2 = 1;

        // add data points
        for (Node node : graph.getVertices()) {
            Row d = node.getDataPoint();
            double point1 = d.getInputes().get(dim1);
            System.out.println(point1);
            double point2 = d.getInputes().get(dim2);
            System.out.println(point2);
            Node vertex = new Node(d, new Point2D.Double((point1 + 20) * 20, (point2 + 20) * 20));

            // Add color to nodes here
            if (d.getClassIndex() == 0) {
                vertex.setColor(Color.CYAN);
            }
            vertex.setAlpha(0.1);
            g.addVertex(vertex);
        }

        // add centers
        for (List<Double> center : centers) {
            double point1 = center.get(dim1);
            double point2 = center.get(dim2);
            Node node = new Node(null, new Point2D.Double((point1 + 20) * 20, (point2 + 20) * 20));
            node.setColor(Color.GREEN);
            g.addVertex(node);
        }

        jungHandler.setGraph(g);
        jungHandler.draw();

    }

    /**
     * Sets the graph extracted from virtual 2D space (used by ACO).
     *
     * @param graph The graph in virtual 2D space to cluster.
     */
    public void setPseudoGraph(Graph graph) {
        this.graph = graph;
        this.useVirtualLocations = true;
    }

}
