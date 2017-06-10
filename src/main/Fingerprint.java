package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the fingerprint of a chunk
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class Fingerprint {
    /**
     * Constants used to compute locality sensitive hash
     */
    private static final int[] HASH_CONSTANTS = 
            new int[]{10, 12, 18, -19, -20, 23, 25, -21, 24, 22};
    /**
     * The time represented by one chunk in seconds
     */
    private static final double TIME_OF_ONE_CHUNK = (1024.0 / 11025.0);
    /**
     * The number of frequency bands in a fingerprint
     */
    private static final int NUMBER_OF_BANDS = 10;
    /**
     * The scale of the values in fingerprint bins
     */
    private static final double HASH_SCALE_MAX = 5001;
    /**
     * Maximum possible value of the sum of fingerprint bins
     */
    private static final int P = (int) (Math.pow(2, 32)-5);
    /**
     * Helps create time representation of location of chunk in song
     */
    private static final DecimalFormat df = new DecimalFormat("#.#");
    /**
     * Points to the next fingerprint or to null
     */
    private Fingerprint next;
    /**
     * Bands representing frequencies described by fingerprint
     */
    private int[] bands;
    /**
     * Song name
     */
    private String name;
    /**
     * Location in a file as the index of a chunk
     */
    private int location;

    /**
     * Constructor for a Fingerprint
     * @param samples Array of doubles
     * @param name Song name
     * @param location Location as an index of an array of fingerprints
     */
    public Fingerprint(double[] samples, String name, int location) {
        bands = new int[NUMBER_OF_BANDS];
        double[] amplitudes = new double[samples.length];
        FFT.transform(samples, new double[samples.length],
                new double[samples.length], amplitudes);
        bandFilter(amplitudes);
        scale();
        this.name = name;
        this.location = location;
        this.next = null;
    }

    /**
     * Finds the time in the song of the chunk represented by the given
     * fingerprint in seconds
     * @param f Fingerprint
     * @return Time in seconds as a double
     */
    public static double findTimeInFileDouble(Fingerprint f){
        return (double)f.location * TIME_OF_ONE_CHUNK;
    }


    /**
     * Finds the time in the song of the chunk represented by the given
     * fingerprint in seconds as a string
     * @param f Fingerprint
     * @return Time in seconds as a string
     */
    public static String findTimeInFile(Fingerprint f){
        return df.format((double)f.location * TIME_OF_ONE_CHUNK);
    }

    /**
     * Adds a pointer to the fingerprint of the next chunk
     * @param f Fingerprint of the next chunk
     */
    public void addNext(Fingerprint f){
        next = f;
    }

    /**
     * Returns the next fingerprint chunk
     * @return The next fingerprint chunk
     */
    public Fingerprint getNext(){
        return next;
    }

    /**
     * sums of amplitudes over ten bands of frequencies.
     * So:
     * band[0] = amplitudes[0]
     * band[1] = amplitudes[1] + amplitudes[2];
     * band[2] = amplitudes[3]+[4]+[5]+[6]
     * ...
     * band[10] = amplitudes[511] +...+amplitudes[1023]
     * @param amplitudes Representation of the spectral density 
     * of the song at this fingerprint's location
     */
    public void bandFilter(double[] amplitudes) {
        int window = 1;
        int pointer = 1;
        for(int i = 0; i < bands.length; i++){
            bands[i] = addBand(pointer, window, amplitudes);
            pointer += window;
            window *= 2;
        }
    }

    /**
     * Given a start index, a length, and an array of doubles,
     * adds up all of the array values from start to 
     * (start + length - 1)
     * @param start Index in the array
     * @param length Number of doubles to add
     * @param amplitudes Array of doubles
     * @return Sum of values in the array from start to 
     * (start + length - 1)
     */
    private int addBand(int start, int length, double[] amplitudes) {
        int acc = 0;
        for(int i = start; i < start + length; i++){
            acc += amplitudes[i];
        }
        return acc;
    }

    /**
     * Scales the band-filtered fingerprint to be between 0 and
     * PRIMARY_SCALE_MAX
     */
    private void scale(){
        int largest = findLargest(bands);
        double scaleFactor = HASH_SCALE_MAX / ((double)largest);
        for(int i = 0; i < bands.length; i++){
            bands[i] *= scaleFactor;
        }
    }

    /**
     * Gets the frequency bands representing this fingerprint
     * @return Array of ints
     */
    public int[] getBands() {
        return bands;
    }

    /**
     * Sets the frequency bands representing this fingerprint
     * @param bands Array of ints
     */
    public void setBands(int[] bands) {
        this.bands = bands;
    }

    /**
     * Gets the name of the file
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the file
     * @param name Name of file
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the index of the fingerprint array at which this
     * fingerprint's chunk begins
     * @return Index of fingerprint array
     */
    public int getLocation() {
        return location;
    }

    /**
     * Returns the largest int in the given array
     * @param bins Array of ints 
     * @return Largest int in the given array
     */
    private int findLargest(int[] bins){
        int largest = bins[0];
        for(int i = 1; i < bins.length; i++){
            if (bins[i] > largest)
                largest = bins[i];
        }
        return largest;
    }

    /**
     * Returns a locality-sensitive hashCode for this fingerprint
     * @return hashCode for this fingerprint
     */
    public int hashCode() {
        int result = 0;
        int l = findLargest(bands);

        if(l == 0) {
            return 0;
        }
        else { 
            for (int i = 0; i < bands.length; i++) {
                result +=  HASH_CONSTANTS[i] * (bands[i]);  		
            }
        }
        return (int) ((Math.abs(result) % P) % HASH_SCALE_MAX);
    }

    /**
     * Checks if this fingerprint is equal to the given object
     * @return Is this fingerprint equal to the given object?
     */
    public boolean equals(Object o) {
        if(!(o instanceof Fingerprint))
            return false;
        if(o == this)
            return true;
        else {
            Fingerprint f = (Fingerprint) o;
            int[] bandsF1 = this.getBands();
            int[] bandsF2 = f.getBands();
            for(int i = 0; i<bands.length; i++)
                bandsF1[i] = bandsF2[i];
        }
        return true;
    }
}
