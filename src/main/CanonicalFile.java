package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A class representation of a file's canonical format
 * @author Ariel Winton
 * @author James O'Brien
 * @author Nnamdi Okeke
 * @author Rani Aljondi
 *
 */
public class CanonicalFile {
    /**
     * A list of acceptable sample rates
     */
    public ArrayList<Float> acceptableSampleRates = new ArrayList<Float>();
    /**
     * Number of samples in a chunk
     */
    public static final int SAMPLES_PER_CHUNK = 1024;
    /**
     * Formatted audio file
     */
    private File file;
    /**
     * Name of formatted audio file
     */
    private String baseFileName;
    /**
     * Sample size in bits of formatted audio file
     */
    private int sampleSize;
    /**
     * Number of channels in formatted audio file
     */
    private int channels;
    /**
     * Sample rate of formatted audio file
     */
    private float sampleRate;
    /**
     * Number of chunks that need to be converted to Fingerprints
     */
    private int numChunks;

    /**
     * Constructor for a CanonicalFile
     * @param name Name of file
     * @param file Formatted audio file
     */
    public CanonicalFile(String name, File file){
        this.file = file;
        this.baseFileName = name;
        acceptableSampleRates.add(new Float(11025.0));
        acceptableSampleRates.add(new Float(22050.0));
        acceptableSampleRates.add(new Float(44100.0));
        acceptableSampleRates.add(new Float(48000.0));
        checkCorrect();
    }

    /**
     * Checks if the audio file has been formatted correctly
     * Exits if the file has not been formatted correctly or doesn't exist
     */
    private void checkCorrect(){

        AudioFileFormat fileFormat = null;

        try {
            fileFormat = AudioSystem.getAudioFileFormat(file);
        } catch (Exception e){
            System.err.println("ERROR: " + baseFileName +
                    " does not exist");
            System.exit(1);
        }

        AudioFormat format = fileFormat.getFormat();
        AudioFileFormat.Type type = fileFormat.getType();
        sampleSize = format.getSampleSizeInBits();
        channels = format.getChannels();
        sampleRate = format.getSampleRate();

        if 	((!format.isBigEndian()) && 
                format.getEncoding().equals
                (AudioFormat.Encoding.PCM_SIGNED) &&
                (channels == 1 || channels == 2) &&
                (sampleSize == 8 || sampleSize == 16) &&
                (acceptableSampleRates.contains(sampleRate)) &&
                type.toString().equals("WAVE")) {

        }
        else {
            System.err.println("ERROR: " + baseFileName +
                    " is not in a supported format");
            System.exit(1);
        } 

    }

    /**
     * Takes the byte data of a formatted audio file and converts
     * it into an array of fingerprints
     * @return Array of fingerprints
     */
    public Fingerprint[] fingerprintFile(){
        try{
            InputStream audioSrc = 
                    new FileInputStream(file.getAbsolutePath());
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream fileIn = 
                    AudioSystem.getAudioInputStream(bufferedIn);
            int pcmByteSize = (int) (fileIn.getFrameLength() * 
                    channels * (sampleSize/8));
            fileIn.skip(44);
            int bytesToReadPerChunk = SAMPLES_PER_CHUNK * 
                    (sampleSize / 8) * channels;
            numChunks = (int) 
                    Math.ceil((double)pcmByteSize / bytesToReadPerChunk);
            byte[] bytes = new byte[bytesToReadPerChunk];
            boolean repeat = true;
            Fingerprint[] fc = new Fingerprint[numChunks];
            int i = 0;

            /**
             * Reads in bytes from the formatted audio file,
             * converts those bytes into samples, passes those
             * samples to the Fingerprint constructor, and
             * establishes links between all of the fingerprints
             * in sequential order
             */
            while(repeat){
                int read = fileIn.read(bytes, 0, bytesToReadPerChunk);
                if(read == -1){
                    break;
                } else if(read != bytesToReadPerChunk){
                    fillByteArray(bytes, read);
                    repeat = false;
                }
                fc[i] = new Fingerprint(convertToMonoDouble(bytes), 
                        baseFileName, i);
                if (i > 0){
                    fc[i-1].addNext(fc[i]);
                }
                i++;
            }

            fileIn.close();
            return fc;
        } 

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            System.exit(1);
            return null;
        }
    }

    /**
     * Fills an array of bytes with zeroes starting
     * at index offset
     * @param bytes Array of bytes
     * @param offset Index of the array of bytes where fill starts
     */
    private void fillByteArray(byte[] bytes, int offset){
        for(int i = offset; i < bytes.length; i++){
            bytes[i] = 0;
        }
    }

    /**
     * Converts a byte array to an array of samples
     * of the right format
     * @param bytes Array of bytes
     * @return Array of samples
     */
    private double[] convertToMonoDouble(byte[] bytes){
        double[] samples = new double[SAMPLES_PER_CHUNK];
        int bytesPerSample = (sampleSize / 8) * channels;
        for(int i  = 0; i < SAMPLES_PER_CHUNK; i++){
            samples[i] = getSample(bytes, i*bytesPerSample);
        }
        return samples;
    }

    /**
     * Given an array of bytes and an index, converts
     * bytesPerChannel bytes into a sample of the right
     * format starting at the given index
     * @param bytes An array of bytes
     * @param index An index in the array of bytes
     * @return A sample
     */
    private double getSample(byte[] bytes, int index){
        int bytesPerChannel = sampleSize / 8;
        if(channels == 1){ 
            return bytesToDouble(bytes, index, bytesPerChannel);
        } else {
            return (bytesToDouble(bytes, index, bytesPerChannel) +
                    bytesToDouble(bytes, index + bytesPerChannel, 
                            bytesPerChannel))
                            / 2.0;
        }
    }

    /**
     * Given an array of bytes, an index startIndex, and a length len,
     * converts len bytes into a sample of the right format starting 
     * at startIndex
     * @param arr Array of bytes
     * @param startIndex Index in array of bytes
     * @param len Number of bytes to convert into a sample
     * @return
     */
    private double bytesToDouble(byte[] arr, int startIndex, int len){
        ByteBuffer bb = ByteBuffer.allocate(len);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < len; i++){
            bb.put(arr[startIndex+i]);
        }

        return (double)bb.getShort(0);   	 
    }
}
