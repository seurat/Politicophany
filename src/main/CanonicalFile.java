package main;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class CanonicalFile {
	public static final int CANONICAL_SAMPLE_RATE = 10240;
	public static final int SAMPLES_PER_CHUNK = 1024;
	private File file;
	private String baseFileName;
	private int sampleSize;
	private int channels;
	private float sampleRate;
	private int numChunks;
	
	public CanonicalFile(String name, File file){
		this.file = file;
		this.baseFileName = name;
		checkCorrect();
	}
	
	private void checkCorrect(){
		
		AudioFileFormat fileFormat = null;
		
		try {
			fileFormat = AudioSystem.getAudioFileFormat(file);
		} catch (Exception e){
			System.err.println("Debug error: cannot get canonical file's fileformat " + baseFileName);
		}
		
		AudioFormat format = fileFormat.getFormat();
		AudioFileFormat.Type type = fileFormat.getType();
		sampleSize = format.getSampleSizeInBits();
		channels = format.getChannels();
		sampleRate = format.getSampleRate();
		
		/*
		if 	((!format.isBigEndian()) &&
			(channels == 1) &&
			(sampleSize == 8 || sampleSize == 16) &&
			(sampleRate == CANONICAL_SAMPLE_RATE) && 
			type.toString().equals("WAVE")){
			System.err.println("debugerror: " +
								baseFileName +
								" is not a supported format");
			System.exit(1);
		} */
	}
	
	public Fingerprint[] fingerprintFile(){
		try{
			AudioInputStream fileIn =  AudioSystem.getAudioInputStream(file);
			
			int pcmByteSize = (int) (fileIn.getFrameLength() * channels * (sampleSize/8));
			fileIn.skip(44);
			int bytesToReadPerChunk = SAMPLES_PER_CHUNK * (sampleSize / 8) * channels;
			numChunks = (int) Math.ceil((double)pcmByteSize / bytesToReadPerChunk);
			byte[] bytes = new byte[bytesToReadPerChunk];
			boolean repeat = true;
			Fingerprint[] fc = new Fingerprint[numChunks];
			int i = 0;
			
			while(repeat){
				int read = fileIn.read(bytes, 0, bytesToReadPerChunk);
				if(read == -1){
					break;
				} else if(read != bytesToReadPerChunk){
					fillByteArray(bytes, read);
					repeat = false;
				}
				fc[i] = new Fingerprint(convertToMonoDouble(bytes), baseFileName, i);
				i++;
				//fc.add(new Fingerprint(convertToMonoDouble(bytes)));				
			}
			fileIn.close();
		} catch (Exception e) {
			System.err.println("debugerror: " +
					baseFileName +
					" is not a supported format");
			System.exit(1);
		}
		return null;
	}
	
	private void fillByteArray(byte[] bytes, int offset){
		for(int i = offset; i < bytes.length; i++){
			bytes[i] = 0;
		}
	}
	
	private double[] convertToMonoDouble(byte[] bytes){
		double[] samples = new double[SAMPLES_PER_CHUNK];
		int bytesPerSample = (sampleSize / 8) * channels;
		for(int i  = 0; i < SAMPLES_PER_CHUNK; i++){
			samples[i] = getSample(bytes, i*bytesPerSample);
		}
		return samples;
	}
	
	private double getSample(byte[] bytes, int index){
		int bytesPerChannel = sampleSize / 8;
		if(channels == 1){ 
			return bytesToDouble(bytes, index, bytesPerChannel);
		} else {
			return (bytesToDouble(bytes, index, bytesPerChannel) +
					bytesToDouble(bytes, index + bytesPerChannel, bytesPerChannel)) / 2.0;
		}
	}
	
	private double bytesToDouble(byte[] arr, int startIndex, int len){
		 ByteBuffer bb = ByteBuffer.allocate(len);
	     bb.order(ByteOrder.LITTLE_ENDIAN);
	     for(int i = 0; i < len; i++){
	    	 bb.put(arr[startIndex+i]);
	     }
	     
	     return (double)bb.getShort(0);   	 
	}

}