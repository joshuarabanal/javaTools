package waveFormat;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ra on 12/23/2016.
 * see: http://soundfile.sapp.org/doc/WaveFormat/ for details about format
 */

public class WaveOutputStream extends OutputStream{
    FileOutputStream fos;
    File temp;
    File out;
    int sampleRate = -1;
    private byte[] buffer = new byte[1024];
    private int index= 0;
    private boolean eightBitAudio = false;

    public WaveOutputStream(File file) throws IOException {
        temp = File.createTempFile("mama", null);
        fos = new FileOutputStream(temp);
        out = file;
    }
    public WaveOutputStream(File file, int sampleRate) throws IOException {
        temp = File.createTempFile("mama", null);
        fos = new FileOutputStream(temp);
        out = file;
        setSampleRate(sampleRate);
    }
    public void set8bitAudio(){
        eightBitAudio = true;
    }
    public void setSampleRate(int sampleRate){
        this.sampleRate = sampleRate;
    }

    public void close() throws IOException{
        if(sampleRate == -1){
            throw new IOException("sample rate must be called WaveOutputStream.setSampleRate(int)");
        }
        flush();
        fos.close();
        FileInputStream fis = new FileInputStream(temp);
        out.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(out);
        if(eightBitAudio){
            SaveToWave.writeWAVHeader(fos,(long)temp.length(), sampleRate,(byte)1);
        }else{
            SaveToWave.writeWAVHeader(fos,(long)temp.length(), sampleRate);
        }

        byte[] b = new byte[1024];
        int howmany = 0;
        while( (howmany = fis.read(b)) >0 ){
            fos.write(b,0,howmany);
        }
        fos.close();
    }

    public void flush() throws IOException {
        if(index == 0){ return; }
        fos.write(buffer, 0, index);
        index= 0;
    }


    public void write(short[] data ) throws IOException {

        write(data,0, data.length);
        //PcmHelpers.writeShortBuffer(data, fos);
    }
    public void write(byte[] b) throws IOException {
        write(b,0,b.length);

    }
    public void write(byte[] b, int offset, int bytecount) throws IOException {
        if(!eightBitAudio){
            throw new NullPointerException("use write(short[]) instead to write short audio");
        }
        flush();
        fos.write(b,offset,bytecount);
    }
    public void write(short[] b, int offset, int bytecount) throws IOException {
        if(eightBitAudio){
            throw new NullPointerException("use write(byte[]) instead to write eight bit audio");
        }
        int end = offset+bytecount;
        for(int i = offset; i<end; i++){
            if(index+1>=buffer.length){  flush(); }
            buffer[index++] = (byte)(b[i]&0xff);
            buffer[index++] = (byte)( (b[i]>>8) &0xff);
            /**
            buffer[index++] = (byte)( (b[i]>>8) &0xff);
            buffer[index++] = (byte)(b[i]&0xff);
            **/
            // out.write((byte)(val & 0xFF));
            //            out.write((byte)((val>>8) & 0xff));
        }
    }


    /**
     * not to be used when writing a single short, you must use write(short[]) when writing shorts
     * @param oneByte
     * @throws IOException
     */
    @Override
    public void write(int oneByte) throws IOException {
        fos.write(oneByte);
    }
}
