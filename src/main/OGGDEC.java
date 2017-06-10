package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the oggdec process
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class OGGDEC {
    /**
     * Converts file in .ogg format to .wav format
     * Runs oggdec on infile and outfile with opts as options
     * @param inFileName Name of file to be converted
     * @param outFileName Name of converted file
     * @param opts Options to be run with oggdec in command line
     */
    public static void convert(String inFileName,
            String outFileName, List<String> opts) {
        ProcessBuilder oggPB = new ProcessBuilder();
        ArrayList<String> oggArgs = new ArrayList<String>();
        String oggPath = "oggdec";
        oggArgs.add(oggPath);
        oggArgs.add("--quiet");
        oggArgs.add(inFileName);
        oggArgs.add("--output");
        oggArgs.add(outFileName);
        oggPB.command(oggArgs);

        Process oggP = null;

        try{
            oggP = oggPB.start();
            oggP.waitFor();
        }
        catch (Exception e){
            System.out.println("ERROR: "
                    + e.getMessage());
            System.exit(1);
        }
    }
    /**
     * Converts an OGG file to a WAV file at the canonical sample rate
     * and stores it at newFilePath
     * @param targetFilePath Path to directory
     * @param newFilePath Path to directory
     */
    public static void convertOggtoCanonical(String targetFilePath,
            String newFilePath){
        ArrayList<String> args = new ArrayList<String>();
        args.add("--quiet");
        args.add("--output");
        File dir = new File(newFilePath);

        convert(targetFilePath, newFilePath, args);
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

        convertOggtoCanonical(mp3Mediary.getAbsolutePath(),
                newFilePath);
    }
}
