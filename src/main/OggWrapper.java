package main;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/**
 * A FileWrapper representation of an OGG file
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 */
public class OggWrapper extends FileWrapper {
    /**
     * Constructor for an OggWrapper
     * @param f OGG file
     */
    public OggWrapper(File f){
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
        OGGDEC.convertOggtoCanonical(file.getAbsolutePath(),
                newFile.getAbsolutePath());
        LAME.convertWAVtoCanonical(newFile.getAbsolutePath(), 
                newFile.getAbsolutePath());
        return new CanonicalFile(file.getName(), newFile);
    }

}
