package graph;

import java.util.List;

/**
 *
 * @author Pc
 */
public class Utilities {

    /**
     * Given a set of nodes, find the most dissimilar.
     *
     * @param neighborhood	The group of nodes to search.
     * @return	The most unique node in the group.
     */
    public static Node mostUnique(List<Node> neighborhood) {
        double greatestError = 0;
        double currentError = 0;
        Node unique = null;
        // for each node in the neighborhood
        for (Node current : neighborhood) {
            // get error of the current node
            currentError = calculateError(current, neighborhood);
            // store largest error
            if (currentError >= greatestError) {
                greatestError = currentError;
                unique = current;
            }
        }

        // store node with largest error as most unique and return
        if (unique != null) {
            greatestError /= (neighborhood.size() - 1);
            unique.setError(greatestError);
        } else {
            //unique is null
            System.out.println(neighborhood.size());
        }
        return unique;
    }

    /**
     * Calculate the error that a node is contributing to a neighborhood.
     *
     * @param node	The node to test.
     * @param neighborhood	The neighborhood to test.
     * @return	The value corresponding to how different the node is from the
     * rest.
     */
    public static double calculateError(Node node, List<Node> neighborhood) {
        double runningError = 0;
        // for each node in the neighborhood
        for (Node neighbor : neighborhood) {
            // compare the given node to each node in the neighborhood
            if (node != neighbor) {
                // for each feature
                for (int featureNum = 0; featureNum < node.getDataPoint().getInputes().size(); featureNum++) {
                    // calculate error based on feature differences
                    Double currentFeature = node.getDataPoint().getInputes().get(featureNum);
                    Double neighborFeature = neighbor.getDataPoint().getInputes().get(featureNum);
                    runningError += Math.abs(currentFeature - neighborFeature);
                }
            }
        }
        // calculate error, set value in node, and return error
        runningError /= (neighborhood.size() - 1);
        node.setError(runningError);
        return runningError;
    }

}
