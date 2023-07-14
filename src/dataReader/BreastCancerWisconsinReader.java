package dataReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import main.Row;

public class BreastCancerWisconsinReader extends Reader {

    private final String filePath = "datasets/Breast-Cancer-Wisconsin.data";

    public BreastCancerWisconsinReader() {
        name = "BreastCancerWisconsin";
        inputs = 9;
        outputs = 2;
    }

    @Override
    public void parseFile() {

        data = new ArrayList<>();
        try {
            // find possible classes for dataset
            Scanner in = new Scanner(new File(filePath));
            // find possible classes for dataset
            findClasses();

            // loop through entire data file.
            while (in.hasNext()) {
                String[] split = in.nextLine().split(",");
                List<Double> featureList = new ArrayList<>();

                // loop through all features, building up the
                // featureList list.
                // Data is split up according to mapping in header
                // comment.
                for (int featureIterator = 1; featureIterator < inputs + 1; featureIterator++) {
                   try { featureList.add(Double.valueOf(split[featureIterator]));
                   } catch (NumberFormatException e) {
                        //not a double
                    }                  
                }

                // get output. Inputs is a pointer to the point after
                // the last feature.
                // It is used questionably here, but it will work --
                // possibly consider something nicer
                List<Double> output = getOutputVector(split[inputs + 1]);
                Row p = new Row(featureList, output);
                data.add(p);

                //System.out.println(data.size());
               // System.out.println("" + p.getInputes()+" "+ p.getOutputs());
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found for Breast Cancer Wisconsin dataset.");
            e.printStackTrace();
        } finally {

            // regardless of above, assign the possible classes to the
            // possible classes list.
            findClasses();
        }

    }

    @Override
    public void findClasses() {
        possibleClasses = new ArrayList<>();
        possibleClasses.add("2");
        possibleClasses.add("4");

    }

   // public static void main(String[] args) {new BreastCancerWisconsinReader().parseFile();}
}
