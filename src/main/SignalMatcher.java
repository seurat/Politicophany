package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/**
 * Checks songs for plagiarism and prints matches at relevant times
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class SignalMatcher {
    /**
     * The maximum acceptable Euclidean distance between 
     * two fingerprints to consider them identical
     */
    private static int EUCLIDEAN_DISTANCE_MAX = 600;
    /**
     * The maximum acceptable difference between two fingerprints'
     * hashCodes to consider them similar
     */
    private static final int ACCEPTABLE_HASH_RANGE = 650;
    /**
     * The minimum size of a copyright protected fragment of song
     * in fingerprints
     */
    private static final int FRAGMENT_SIZE = 55;
    /**
     * The minimum percentage of matches between fingerprints 
     * to say two fragments match
     */
    private static final double ACCEPTABLE_HIT_RATE = 0.75;
    /**
     * A temporary directory 
     */
    private static File temp1 = new File("/tmp/SignalMatcher/D1");
    /**
     * A temporary directory
     */
    private static File temp2 = new File("/tmp/SignalMatcher/D2");

    /**
     * Runs program
     * @param args Files to be compared
     */
    public static void main(String[] args){
        // Evaluates command line syntax
        checkArgs(args);
        // Creates temporary directories if none were previously created
        temp1.mkdirs();
        temp2.mkdirs();
        // Lists of files in canonical form
        ArrayList<CanonicalFile> f1 = 
                createCanonicalFiles(args[0], args[1], temp1);
        ArrayList<CanonicalFile> f2 = createCanonicalFiles(args[2], 
                args[3], temp2);

        // Database of fingerprints and their hashCodes for the current
        // canonical file cf
        HashMap<Integer,Fingerprint> h1 = 
                new HashMap<Integer,Fingerprint>();
        // Runs through every file in directory f1 and compares the
        // fingerprints in each cf in f1 to the fingerprints of all
        // files in directory f2
        for(CanonicalFile cf : f1) {
            Fingerprint[] fingerprints = cf.fingerprintFile();
            for(int i = 0; i < fingerprints.length; i++){
                h1.put(
                        fingerprints[i].hashCode(), 
                        fingerprints[i]);
            }
            for(CanonicalFile cf2: f2){
                if(cf2.fingerprintFile()==null)
                    continue;
                Fingerprint[] fingerprints2 = cf2.fingerprintFile();
                findMatches(h1, fingerprints2);

            }
            h1.clear();

        }
        temp1.delete();
        temp2.delete();
        System.exit(0);
    }

    /**
     * Detects whether or not matches of fragments exist
     * @param map HashMap of fingerprints and their hashcodes
     * @param fingerprints Array of fingerprints
     */
    private static void 
    findMatches(HashMap<Integer, Fingerprint> map,
            Fingerprint[] fingerprints){
        ArrayList<String> matches = new ArrayList<String>();

        for(int i = 0; i < fingerprints.length; i++){
            ArrayList<Fingerprint> validMatches = 
                    scanMap(map, fingerprints[i].hashCode());

            if(validMatches == null) {
                continue;
            }

            for(Fingerprint match : validMatches){

                if(!hashAcceptable(match.hashCode(),
                        fingerprints[i].hashCode())) {
                    continue;
                }

                if(compareFingerprints(match, fingerprints[i])) {

                }

                else {
                    continue;
                }

                if(!matches.contains(match.getName() + 
                        fingerprints[i].getName()) 
                        && chainCompare(match,fingerprints[i])) {
                    matches.add(match.getName() + 
                            fingerprints[i].getName());
                    System.out.println("MATCH " +
                            match.getName() + " " +  
                            fingerprints[i].getName() 
                            + " " +
                            Fingerprint.findTimeInFile(match) 
                            + " " +
                            Fingerprint.findTimeInFile(
                                    fingerprints[i]));
                }
            }
        }    
    }

    /**
     * Determines whether or not hashCode of h1 is within
     * ACCEPTABLE_HASH_RANGE of hashCode of h2
     * @param h1 hashCode of a fingerprint
     * @param h2 hashCode of a fingerprint
     * @return True if the hashCodes are within an acceptable
     * range; otherwise, false
     */
    private static boolean hashAcceptable(int h1, int h2){
        return Math.abs(h1 - h2) <= ACCEPTABLE_HASH_RANGE;
    }

    /**
     * Checks which fingerprints in map exist within an acceptable
     * hash range of hash
     * If they exist, they are returned in an ArrayList
     * Else, null is returned
     * @param map HashMap of fingerprints and their hashCodes
     * @param hash hashCode
     * @return ArrayList of fingerprints or null
     */
    private static ArrayList<Fingerprint> scanMap(HashMap<Integer, 
            Fingerprint> map, int hash){
        ArrayList<Fingerprint> matches = new ArrayList<Fingerprint>();
        Fingerprint match;
        for(int i = Math.max(0, hash - ACCEPTABLE_HASH_RANGE); 
                i <= hash + ACCEPTABLE_HASH_RANGE; i++){
            if(map.containsKey(i)){
                match = map.get(i);
                if(hashAcceptable(match.hashCode(), hash))
                    matches.add(match);
                else continue;
            }
            else continue;
        }
        if(matches.size() == 0){
            return null;
        } else {
            return matches;
        }
    }

    /**
     * Compares two fragments starting at f1 and f2
     * @param f1 Fingerprint
     * @param f2 Fingerprint
     * @return True if the fragments match; otherwise, false
     */
    private static boolean chainCompare(Fingerprint f1, 
            Fingerprint f2){
        Fingerprint currentF1 = f1;
        Fingerprint currentF2 = f2;
        int hits = 0;
        for(int i = 1; i < FRAGMENT_SIZE; i++){
            currentF1 = currentF1.getNext();
            currentF2 = currentF2.getNext();	
            if(currentF1 == null || currentF2 == null) {
                return false;
            }

            if(compareFingerprints(currentF1, currentF2)) {
                hits++;
            } 
        }
        return ((double) hits)/FRAGMENT_SIZE >= ACCEPTABLE_HIT_RATE;
    }


    /**
     * Uses Euclidean distance function to evaluate whether or
     * not two fingerprints match
     * @param f1 Fingerprint
     * @param f2 Fingerprint
     * @return True if the fingerprints match; otherwise, false
     */
    private static boolean compareFingerprints(Fingerprint f1,
            Fingerprint f2){
        return 
                EuclideanValue.getEuclideanValue(f1.getBands(), 
                        f2.getBands())
                        <= EUCLIDEAN_DISTANCE_MAX;
    }

    /**
     * Creates a list of files in canonical format and places
     * them in dir
     * @param mode -f | -d
     * @param target Relative or absolute path
     * @param dir Directory
     * @return ArrayList of canonical files created
     */
    private static ArrayList<CanonicalFile> createCanonicalFiles
    (String mode, String target, File dir){
        ArrayList<FileWrapper> list = 
                FilesCreator.makeMusicFileList(mode, target);
        ArrayList<CanonicalFile> canonicalList = 
                new ArrayList<CanonicalFile>();
        for(FileWrapper fw: list){
            canonicalList.add(fw.convert(dir));
        }
        return canonicalList;
    }

    /**
     * Checks that the given command line arguments are valid
     * @param args Command line arguments
     */
    private static void checkArgs(String[] args){
        if(((args.length == 4) &&
                (args[0].equals("-f") || args[0].equals("-d")) &&
                (args[2].equals("-f") || args[2].equals("-d")))) {
            return;
        } 
        else {
            System.err.println("ERROR: Incorrect command line arguments");
            System.exit(1);
        }
    }
}
