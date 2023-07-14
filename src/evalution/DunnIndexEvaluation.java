package evalution;

import graph.Node;

import java.util.List;

/**
 *
 * @author Pc
 */
public class DunnIndexEvaluation extends ClusteringEvaluation {

    /**
     * The Dunn index value
     *
     * This value is the ratio of distance between centers : maximum average
     * distance between two centers
     */
    @Override
    public double getIndex(List<List<Node>> clusters) {

        this.clusters = clusters;
        double index = Double.MAX_VALUE;

        // calculate necessary information
        calculateCentersAndAverageDistances();

        // calculate max distance between centers
        double maxAvgDistance = 0.0;
        for (int k = 0; k < clusters.size(); k++) {
            maxAvgDistance = Math.max(maxAvgDistance, avgDistances.get(k));
        }

        // for every cluster
        for (int i = 0; i < clusters.size(); i++) {
            // compare to every other cluster
            for (int j = 0; j < clusters.size(); j++) {
                if (i != j) {
                    // perform Dunn index calculation
                    double value = ant.distance(centers.get(i), centers.get(j)) / maxAvgDistance;
                    index = Math.min(index, value);
                }
            }
        }

        // reset centers in case called again
        centers = null;

        return index;
    }

}
