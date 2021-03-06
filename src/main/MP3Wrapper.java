package main;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/**
 * A FileWrapper representation of an MP3 file
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class MP3Wrapper extends FileWrapper {
    /**
     * Constructor for an MP3Wrapper
     * @param f MP3 file
     */
    public MP3Wrapper(File f){
        super(f);
    }

    @Override
    protected void checkTypeReqs(File file) {
        return;
    }

    @Override
    public CanonicalFile convert(File targetFile) {
        File newFile = new File(targetFile, 
                file.getName().concat(".wav"));
        LAME.convertMP3toCanonical(file.getAbsolutePath(),
                newFile.getAbsolutePath());
        return new CanonicalFile(file.getName(), newFile);
    }

}
