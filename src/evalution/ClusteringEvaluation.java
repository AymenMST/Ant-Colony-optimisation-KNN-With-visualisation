package evalution;

import aco.Ant;
import graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pc
 */
public abstract class ClusteringEvaluation {

    Ant ant = new Ant();
    List<List<Node>> clusters;
    List<Double> avgDistances;
    List<List<Double>> centers = null;

    public ClusteringEvaluation() {

    }

    /**
     * Get the fitness of the clusters using intra and inter metrics.
     *
     * @param clusters	The clusters found by a clustering algorithm.
     * @return	The fitness value, where larger numbers indicate better
     * clustering.
     */
    public abstract double getIndex(List<List<Node>> clusters);

    /**
     * Utility function used to calculate centers of clusters as well as the
     * average distance of points within clusters.
     */
    protected void calculateCentersAndAverageDistances() {
        // calculate centers and average distances
        if (centers == null) {

            // loop through the clusters
            centers = new ArrayList<>(clusters.size());
            for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {

                List<Node> cluster = clusters.get(clusterIndex);
                List<Double> center = new ArrayList<>();

                // for each node in the current cluster
                for (int nodeIndex = 0; nodeIndex < cluster.size(); nodeIndex++) {
                    Node node = cluster.get(nodeIndex);
                    // for each feature in the node
                    for (int i = 0; i < node.getFeatureLocation().size(); i++) {
                        // get average position
                        double newValue = node.getFeatureLocation().get(i);
                        if (nodeIndex == 0) {
                            center.add(newValue);
                        } else {
                            center.set(i, center.get(i) + newValue);
                        }
                    }
                }
                // set center to calculated average position of all nodes
                for (int i = 0; i < center.size(); i++) {
                    center.set(i, center.get(i) / cluster.size());
                }
                centers.add(center);
            }
        }

        // calculate the average distance that each node is from the current center
        avgDistances = new ArrayList<>(clusters.size());
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            List<Node> cluster = clusters.get(clusterIndex);
            avgDistances.add(getAverageClusterDistance(cluster, centers.get(clusterIndex)));
        }

    }

    /**
     * Calculate the average distance that a set of nodes are from their
     * corresponding center.
     *
     * @param cluster	The cluster of nodes surrounding a center.
     * @param center	The center of the cluster.
     * @return	The average distance each node in the cluster is from the center.
     */
    private double getAverageClusterDistance(List<Node> cluster, List<Double> center) {
        double avgDistance = 0.0;
        avgDistance = cluster.stream().map((node) -> ant.distance(center, node.getFeatureLocation())).reduce(avgDistance, (accumulator, _item) -> accumulator + _item);
        avgDistance /= cluster.size();
        return avgDistance;
    }

    /**
     * Allows the centers to be set manually. This is done to save computational
     * complexity if the clustering algorithm has already done these
     * calculations ahead of time.
     *
     * @param centers	The centers of each cluster.
     */
    public void setCenters(List<List<Double>> centers) {
        this.centers = centers;
    }

}
