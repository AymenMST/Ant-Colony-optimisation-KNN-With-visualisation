package tools;

import java.util.Random;

/**
 *
 * @author Pc
 */
public class Tools {

    public static Random random = new Random(1000);

    //Returns a random value between min and max.
    public static double getRandomDouble(double min, double max) {
        return getRandomDouble(min, max, random);
    }

    //Returns a random value between any two double values.
    public static double getRandomDouble(double min, double max, Random random) {
        return min + (max - min) * random.nextDouble();
    }

    // Rounds a double to a number of decimal places.
    public static double round(double number, int decimal) {
        int pow = (int) Math.pow(10, decimal);
        double val = Math.round(number * pow);
        return (double) Math.round(val) / pow;
    }

}
