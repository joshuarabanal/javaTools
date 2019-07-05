package waveFormat;
import Analytics.CrashReporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;


import waveFormat.PcmHelpers;

public class SaveToWave {
	private SaveToWave(){
		throw new UnsupportedOperationException("do not instantate this class use wave output stream instead");
	}


	/**
	 * writes the buffer supplied in the wave format to the cache directory of this application
	 * @param data
	 * @param sampleRate
     * @param c
     */
	public static void writeFile(double[] data, int sampleRate, Context c){
		try {
			File f = new File(c.getExternalFilesDir(null), "audioOutput.wav");
			//og.("saved to wave", f.toString());
			FileOutputStream fops = new FileOutputStream(f);
			writeWAVHeader(fops,data.length, sampleRate);
			fops.write(doubleTo16bitPCM(data), 0 ,data.length*2);
			fops.close();
			//og.ile(c);
			//og.("file lenght",""+f.length());

		} catch (FileNotFoundException e) {
			CrashReporter.sendDefaultErrorReport(e);
		} catch (IOException e) {		CrashReporter.sendDefaultErrorReport(e);	}
	}
	public static void writeFile(short[] data, int sampleRate, Context c){
		try {
			File f = new File(c.getExternalFilesDir(null), "audioOutput.wav");
			//og.("saved to wave", f.toString());
			FileOutputStream fops = new FileOutputStream(f);
			writeWAVHeader(fops,data.length, sampleRate);
			PcmHelpers.Short.write(data,fops);
			fops.close();
			//og.ile(c);
			//og.("file lenght",""+f.length());

		} catch (FileNotFoundException e) {	CrashReporter.sendDefaultErrorReport(e);
		} catch (IOException e) {		CrashReporter.sendDefaultErrorReport(e);	}
	}
	static private void logfile(Context c){
		try {
			FileInputStream fips = new FileInputStream(new File(c.getExternalFilesDir(null), "audioOutput.wav"));
			FileWriter fops = new FileWriter(new File(c.getExternalFilesDir(null), "audioArray.txt"));
			
			int i;
			//String s = "";	
			//String second = ""; 
			int buffer = -1;
			int index = 0;
			while( (i = fips.read())>=0){
				//s+="("+ATTRIBUTE_index+", "+i+"),";
				if(index>43){
					if(buffer == -1){
						buffer = i;
					}
					else{
						fops.write(""+PcmHelpers.frameValueLittleEndian(buffer, i)+",");
						//second+=""+PcmHelpers.frameValueLittleEndian(buffer, i)+", ";
						//second+="("+ATTRIBUTE_index+", "+PcmHelpers.frameValueLittleEndian(buffer, i)+"),";//+(buffer+(i*(256)))+"),";
						buffer = -1;
					}
				}
				index++;
			}
			//og.i("file read", s);
			//og.i("data section", second);
			fops.close();
			fips.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			CrashReporter.sendDefaultErrorReport(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CrashReporter.sendDefaultErrorReport(e);
		}
		
	}
	/**
	 * highest byte val = {255, 127}
     * converts an array of floats to a byte array in pcm
	 * @param data
	 * @return
	 */
	public static byte[] intTo16bitPCM(int[] data){
		byte[] fin = new byte[data.length*2];
		
		int index = 0;
		for(int sound : data){
			fin[index+1] = (byte) (((sound >> 8) & 0xff) );
			fin[index] = (byte) (sound & 0xFF);
			index+=2;
		}
		return fin;
		
	}
    /**
	 * little endian
     * highest byte val = {255, 127}
     * converts an array of floats to a byte array in pcm
     * @param data
     * @return
     */
    public static byte[] doubleTo16bitPCM(double[] data){
        byte[] fin = new byte[data.length*2];

        int index = 0;
        for(double sound : data){
            int frame = (int) sound;
            fin[index+1] = (byte) ((((frame&0xff) >> 8) & 0xff) );
            fin[index] = (byte) (frame & 0xFF);
            index+=2;
        }
        return fin;

    }


    /**
     * writes the apropriate wave header to the outputstream
     * @param fops
     * @param totalAudioLen = number of frames in data
     * @param sampleRate
     * @throws IOException
     */
    public static void writeWAVHeader(OutputStream fops, long totalAudioLen, int sampleRate) throws IOException {
    	writeWAVHeader(fops, totalAudioLen,sampleRate, (byte) 2);
	}
    public static void writeWAVHeader(OutputStream fops, long totalAudioLen, int sampleRate, byte bytesPerSample) throws IOException{
		byte[] header = new byte[44];
		totalAudioLen = totalAudioLen;
		long totalDataLen = 40+totalAudioLen;
		
        header[0] = 82;//R  // RIFF/WAVE header
        header[1] = 73;//I
        header[2] = 70;//F
        header[3] = 70;//F
        
        header[4] = (byte) (totalDataLen & 0xff);//size of file
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        
        header[8] = 87;//W
        header[9] = 65;//A
        header[10] = 86;//V
        header[11] = 69;//E
        
        header[12] = 102;//f  // 'fmt ' chunk
        header[13] = 109;//m
        header[14] = 116;//t
        header[15] = 32;//null(space)
        
        header[16] = 16;  // size of header
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        
        header[20] = 1;  //ATTRIBUTE_type pcm = 1
        header[21] = 0;
        
        header[22] = 1;//number of channels
        header[23] = 0;
        
        header[24] = (byte) (sampleRate & 0xff);//sample rate
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        
        header[28] = (byte) ((sampleRate*bytesPerSample) & 0xff);//samplerate*bytespersample*channels
        header[29] = (byte) (((sampleRate*bytesPerSample) >> 8) & 0xff);
        header[30] = (byte) (((sampleRate*bytesPerSample) >> 16) & 0xff);
        header[31] = (byte) (((sampleRate*bytesPerSample) >> 24) & 0xff);
        
        header[32] = bytesPerSample;//bytespersample*channels = bytes per frame
        header[33] = 0;
        
        header[34] = (byte) (bytesPerSample*8);  // bits per sample
        header[35] = 0;
        //error here unknown missing 2 bytes
        header[36] = 100;//d
        header[37] = 97;//a
        header[38] = 116;//t
        header[39] = 97;//a
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        fops.write(header, 0, 44);
	}
}
