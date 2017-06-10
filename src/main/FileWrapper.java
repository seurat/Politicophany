package main;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/**
 * Abstract class for data representation of different acceptable
 * audio file formats
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 *
 */
public abstract class FileWrapper {
    /**
     * File
     */
    public File file;

    /**
     * Constructor for a FileWrapper
     * @param f File
     */
    public FileWrapper(File f){
        checkExistsAndIsFile(f);
        checkTypeReqs(f);
        this.file = f;
    }

    /**
     * Makes sure the given file both exists and is a file
     * @param file File
     */
    private void checkExistsAndIsFile(File file) {
        if(!file.exists()) {
            System.err.println("ERROR: " + 
                    file.getName() + 
                    " does not exist");
            System.exit(1);
        } 
        else if (!file.isFile()) {
            System.err.println("ERROR: " + 
                    file.getName() +
                    " is not a file");
            System.exit(1);
        }
    }

    /**
     * Checks type requirements for the given file
     * @param file File
     */
    protected abstract void checkTypeReqs(File file);

    /**
     * Converts the given file to the canonical file type
     * @param targetFile File
     * @return A canonical file
     */
    public abstract CanonicalFile convert(File targetFile);

}
