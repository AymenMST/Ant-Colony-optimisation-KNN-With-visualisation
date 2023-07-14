package dataReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import main.Row;

/**
 *
 * @author Pc
 */
public class DermatologyReader extends Reader {

    private final String filePath = "datasets/dermatology.data";

    public DermatologyReader() {
        name = "dermatology";
        inputs = 34;
        outputs = 6;
    }

    @Override
    public void parseFile() {
        data = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(filePath));

            // find possible classes for dataset
            findClasses();

            // loop through entire data file.
            while (sc.hasNext()) {
                String[] split = sc.nextLine().split(",");
                List<Double> featureList = new ArrayList<>();

                // loop through all inputes, building up the
                // featureList list.
                // Data is split up according to mapping in header
                // comment.
                for (int featureIterator = 0; featureIterator < inputs + 1; featureIterator++) {
                    try {
                        featureList.add(Double.valueOf(split[featureIterator]));
                        //  System.out.println(featureList);
                    } catch (NumberFormatException e) {
                        //not a double
                    }
                }

                // get output. Inputs is a pointer to the point after
                // the last feature.
                // It is used questionably here, but it will work --
                // possibly consider something nicer
                List<Double> output = getOutputVector(split[inputs]);
                Row p = new Row(featureList, output);
                data.add(p);

                //System.out.println(data.size());
               // System.out.println("" + p.getInputes()+" "+ p.getOutputs());
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found for dermatology dataset.");
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
        possibleClasses.add("1");
        possibleClasses.add("2");
        possibleClasses.add("3");
        possibleClasses.add("4");
        possibleClasses.add("5");
        possibleClasses.add("6");
    }
   //  public static void main(String[] args) { new DermatologyReader().parseFile(); }
}
