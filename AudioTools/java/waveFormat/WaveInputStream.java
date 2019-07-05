package waveFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;

public class WaveInputStream {
    private int dataLengthInBytes;
    private int sampleRate;
    FileInputStream in;

    public WaveInputStream(File f) throws IOException {
        //og.("file path", f.toString());
        in = new FileInputStream(f);
        processHead();
    }
    public int getDataLengthInBytes(){
        return dataLengthInBytes;
    }

    private void processHead() throws IOException {
        byte[] header = new byte[44];
        if(in.read(header) != 44){
            throw new IOException("file corrupt, could not read fill wave header");
        }

        if(
            header[0] != 82 ||//R  // RIFF/WAVE header
            header[1] != 73 ||//I
            header[2] != 70||//F
            header[3] != 70//F
        ){
            throw new IOException("not correct format:"+new String(header,0,4));
        }
        //header[4] = (byte) (totalDataLen & 0xff);//size of file
        //header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        //header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        //header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        if(
                header[8] != 87||//W
                header[9] != 65||//A
                header[10] != 86||//V
                header[11] != 69//E
        ){
            throw new IOException("not correct format:"+new String(header,8,4));
        }
        if(
                header[12] != 102||//f  // 'fmt ' chunk
                header[13] != 109||//m
                header[14] != 116||//t
                header[15] != 32//null(space)
        ){
            throw new IOException("not correct format:"+new String(header,12,4));
        }
        //header[16] = 16;  // size of header
        //header[17] = 0;
        //header[18] = 0;
        //header[19] = 0;
        if(
                header[20] != 1||  //ATTRIBUTE_type pcm = 1
                header[21] != 0
        ){
            throw new IOException("only can read type pcm");
        }
        if(
        header[22] != 1||//number of channels
        header[23] != 0){
            throw new IOException("only can read one channel");
        }
        sampleRate = 0;
        sampleRate += header[24]&0xff;
        sampleRate+=((header[25]  & 0xff)<< 8);
        sampleRate+=((header[26]  & 0xff)<< 16);
        sampleRate+=((header[27]  & 0xff)<< 24);

        //header[28] = (byte) ((sampleRate*bytesPerSample) & 0xff);//samplerate*bytespersample*channels
        //header[29] = (byte) (((sampleRate*bytesPerSample) >> 8) & 0xff);
        //header[30] = (byte) (((sampleRate*bytesPerSample) >> 16) & 0xff);
        //header[31] = (byte) (((sampleRate*bytesPerSample) >> 24) & 0xff);

        dataLengthInBytes = (0xff&header[32])+
                ((0xff&header[33])<<8);

        //header[34] = (byte) (bytesPerSample*8);  // bits per sample
        //header[35] = 0;
        //error here unknown missing 2 bytes
        if(
        header[36] != 100||//d
        header[37] != 97||//a
        header[38] != 116||//t
        header[39] != 97//a
        ){
            throw new IOException("format exception could not read final data block");
        }

        //header[40] = (byte) (totalAudioLen & 0xff);
        //header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        //header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        //header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        //og.("data set", "data len in bytes:"+dataLengthInBytes+", sample rate:"+sampleRate);
    }


    public int readShort() throws IOException {
        if(dataLengthInBytes != 2){ throw new IOException("data length not short:"+dataLengthInBytes); }
        return PcmHelpers.Short.read(in);
    }
    public int readByte() throws IOException {
        if(dataLengthInBytes != 1){ throw new IOException("data length not byte:"+dataLengthInBytes); }
        return in.read();
    }

    public int read(short[] buffer, int start, int length) throws IOException {
        int i = 0;
        for(; i<length; i++){
            int read = readShort();
            if(read<0){ break; }
            buffer[i+start] = (short) read;
        }
        return i;
    }
    public int read(byte[] buffer, int start, int length) throws IOException {
        int i = 0;
        for(; i<length; i++){
            int read = readByte();
            if(read<0){ break; }
            buffer[i+start] = (byte) read;
        }
        return i;
    }

    public int read(short[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public void close() throws IOException {
        in.close();
    }

    public int getSampleRate() {
        return sampleRate;
    }

}
