package main;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
/**
 * A FileWrapper representation of a WAV file
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 *
 */
public class WavWrapper extends FileWrapper {
    /**
     * Constructor for a WavWrapper
     * @param f WAV file
     */
    public WavWrapper(File f) {
        super(f);
    }
    /**
     * Checks the type requirements for a WAV file
     * @param file WAV file
     */
    protected void checkTypeReqs(File file) {
        try{
            AudioFileFormat fileFormat = 
                    AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            AudioFileFormat.Type type = fileFormat.getType();
            int sampleSize = format.getSampleSizeInBits();
            int channels = format.getChannels();
            float sampleRate = format.getSampleRate();

            if 	(!((!format.isBigEndian()) &&
                    (channels == 1 || channels == 2) &&
                    (sampleSize == 8 || sampleSize == 16) &&
                    (sampleRate == 11025 || sampleRate == 22050 ||
                    sampleRate == 44100 || sampleRate == 48000) &&
                    type.toString().equals("WAVE")))  {
                System.err.println("ERROR: " +
                        file.getName() + 
                        " is not a supported format");
                System.exit(1);
            }
        } 
        catch (Exception e){
            System.err.println("ERROR: " +
                    file.getName() + 
                    " is not a supported format");
            System.exit(1);
        }
    }

    @Override
    public CanonicalFile convert(File targetDirectory) {
        File newFile = new File(targetDirectory, file.getName());
        LAME.convertWAVtoCanonical(file.getAbsolutePath(),
                newFile.getAbsolutePath());
        return new CanonicalFile(file.getName(), newFile);
    }
}
