package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the LAME process
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class LAME {
    /**
     * Canonical sampling rate to which all files will be downsampled
     */
    private static final int CANONICAL_SAMPLING_RATE = 11025;
    /**
     * Canonical sampling rate converted to a string
     */
    private static final String CANONICAL_SR_STRING =
            Integer.toString(CANONICAL_SAMPLING_RATE);

    /**
     * /**
     * Converts the given file to the canonical file type
     * @param infile File to be converted
     * @param outfile Converted file
     * @param opts Options to be run with LAME in command line
     */
    public static void convert(String infile,
            String outfile, List<String> opts){
        ProcessBuilder lamePB = new ProcessBuilder();
        ArrayList<String> lameargs = new ArrayList<String>();

        String lamePath = "/course/cs4500f14/bin/lame";
        lameargs.add(lamePath);
        lameargs.addAll(opts);
        lameargs.add(infile);
        lameargs.add(outfile);

        lamePB.command(lameargs);

        Process lameP = null;
        try{
            lameP = lamePB.start();
            lameP.waitFor();
        }
        catch (Exception e){
            System.out.println("ERROR: "
                    + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Converts .mp3 targetFile to .wav format at the canonical sample
     * rate and stores it at newFilePath
     * @param targetFilePath Path to directory
     * @param newFilePath Path to directory
     */
    public static void convertMP3toCanonical(String targetFilePath,
            String newFilePath){
        ArrayList<String> args = new ArrayList<String>();
        args.add("--silent");
        args.add("--resample");
        args.add(CANONICAL_SR_STRING);
        File dir = new File(newFilePath);
        File intermed = new File(dir.getParentFile(), 
                "intermediate-file-transform.mp3");

        convert(targetFilePath, intermed.getAbsolutePath(), args);
        ArrayList<String> args2 = new ArrayList<String>();
        args2.add("--silent");
        args2.add("--decode");
        convert(intermed.getAbsolutePath(), newFilePath, args2);
    }
    /**
     * Converts .wav targetFile to .mp3 and then back to .wav format 
     * at the canonical sample rate and stores it at newFilePath
     * @param targetFilePath Path to directory
     * @param newFilePath Path to directory
     */
    public static void convertWAVtoCanonical(String targetFilePath, 
            String newFilePath){
        ArrayList<String> args = new ArrayList<String>();
        args.add("--silent");

        File inputFile = new File(targetFilePath);
        File createdFile = new File(newFilePath);
        File mp3MediaryDir = createdFile.getParentFile();
        File mp3Mediary = new File(mp3MediaryDir,
                inputFile.getName() +"-mp31.mp3");
        convert(targetFilePath, mp3Mediary.getAbsolutePath(), args);

        convertMP3toCanonical(mp3Mediary.getAbsolutePath(),
                newFilePath);
    }
}