package main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pc
 */
/**
 * Structure that contains a raw of a dataset example, matching inputs to
 * outputs.
 */
public class Row {

    private int classIdentifier = -1;
    private List<Double> inputes;
    private List<Double> outputs;

    //Constructor
    public Row(List<Double> inputes, List<Double> outputs) {
        setInputes(inputes);
        setOutputs(outputs);
    }

    public void printInputs() {
        for (Double value : inputes) {
            System.out.print(value + " ");
        }
    }

    public void printOutputs() {
        for (Double value : outputs) {
            System.out.print(value + " ");
        }
    }

    public void printResults() {
        List<Double> feat = inputes;
        List<Double> out = outputs;
        Map<Double, Double> map = new LinkedHashMap<>();  // ordered

        for (int i = 0; i < feat.size(); i++) {
            map.put(feat.get(i), out.get(i));
        }
        System.out.println(map);
    }

    public List<Double> getInputes() {
        return inputes;
    }

    public void setInputes(List<Double> inputes) {
        this.inputes = inputes;
    }

    public void setOutputs(List<Double> outputs) {
        this.outputs = outputs;
    }

    public List<Double> getOutputs() {
        return outputs;
    }

    /**
     * If the row is used for classification, returns the index of the
     * class that should be chosen
     */
    public int getClassIndex() {
        // find class Identifier if not previously found
        if (classIdentifier < 0) {
            boolean found = false;
            // loop through all outputs of the row
            for (int output = 0; output < outputs.size(); output++) {
                // if the output is not zero, mark as target class
                if (outputs.get(output) > 0.0) {
                    // if there is more than one target class, throw error
                    if (found) {
                        throw new IllegalArgumentException("Output vector must have only one nonzero value for classification.");
                    }
                    // set target class
                    classIdentifier = output;
                    found = true;
                }
            }
        }
        // return the class Identifier
        return classIdentifier;
    }

}
