package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * This is a static helper class to create ArrayLists of MusicFiles
 * from the input to the dan script
 * 
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */

public class FilesCreator {
    /**
     * Reads the command line command
     * @param mode -f|-d
     * @param input Relative or absolute path
     * @return A list of MusicFiles from the file/files specified
     */
    public static ArrayList<FileWrapper> makeMusicFileList
    (String mode, String input){
        File inFile = new File(input);
        checkExistsAndReadable(inFile);

        if(mode.equals("-f")){
            return makeSingleFileList(inFile);
        } else if (mode.equals("-d")){
            return makeDirectoryList(inFile);
        } else {
            System.out.println("ERROR: Incorrect command line arguments");
            System.exit(1);
            return null;
        }

    }

    /**
     * Ensures that a file exists and is readable
     * @param inFile Input file
     */
    public static void checkExistsAndReadable(File inFile){
        if(!inFile.exists()){
            System.out.println("ERROR: "+ 
                    inFile.getName() + " does not exist");
            System.exit(1);
        } else if (!inFile.canRead()){
            System.out.println("ERROR: " + 
                    inFile.getName() + " can not be read");
            System.exit(1);
        }
    }


    /**
     * Creates an ArrayList with the given input file as a
     * FileWrapper
     * @param file Input file
     * @return ArrayList containing the given input file as a FileWrapper
     */
    public static ArrayList<FileWrapper> makeSingleFileList(File file){
        ArrayList<FileWrapper> result = new ArrayList<FileWrapper>();
        result.add(makeFileWrapper(file));
        return result;		
    }

    /**
     * Creates an ArrayList containing each of the files in the 
     * given directory as FileWrappers
     * @param dir Input directory
     * @return ArrayList containing each of the files in the
     * given directory as FileWrappers
     */
    public static ArrayList<FileWrapper> makeDirectoryList(File dir){

        if (!dir.isDirectory()) {
            System.out.println("ERROR: "+ 
                    dir.getName() + " is not a directory");
            System.exit(1);
        } 

        File[] files = dir.listFiles();
        if (files.length == 0){
            System.exit(0);
        }

        ArrayList<FileWrapper> result = new ArrayList<FileWrapper>();
        for(File file : Arrays.asList(files)){
            result.add(makeFileWrapper(file));
        }

        return result;
    }

    /**
     * Makes a FileWrapper using the given file
     * Supports .wav, .mp3, and .ogg file formats
     * Returns error if the given file is not in a supported format
     * @param file Input file
     * @return A FileWrapper containing the given file
     */
    public static FileWrapper makeFileWrapper(File file){
        FileWrapper musicFile = null;
        if(file.getName().endsWith(".wav")){
            musicFile = new WavWrapper(file);
        } else if (file.getName().endsWith(".mp3")){
            musicFile = new MP3Wrapper(file);
        } else if(file.getName().endsWith(".ogg")) {
            musicFile = new OggWrapper(file);
        }
        else {
            System.out.println("ERROR: "+ 
                    file.getName() + " is not a supported format");
            System.exit(1);
        }

        return musicFile;
    }

}
