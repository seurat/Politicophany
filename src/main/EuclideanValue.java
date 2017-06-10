package main;

/**
 * Class containing implementation of Euclidean distance function
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 *
 */
public class EuclideanValue {

    /**
     * Given two arrays of ints, computes the Euclidean distance
     * between two points of dimension array1.length and containing
     * coordinates of array1 and array2
     * @param array1 Array of ints
     * @param array2 Array of ints
     * @return Euclidean distance between points represented in 
     * given arrays
     * @throws Runtime exception if lengths of given arrays vary
     */
    public static int getEuclideanValue(int[] array1, int[] array2) {

        if (array2.length != array1.length) {
            throw new RuntimeException("Euclidean values cannot be " +
                    "computed");
        }

        int output = 0 ;

        for (int i = 0; i < array1.length; i++) {
            int diffSquare = (int)Math.pow((array1[i] - array2[i]), 2);
            output = output + diffSquare;
        }

        output = (int) Math.sqrt(output);

        return output;
    }
}