package com.playMidi.xml;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Analytics.CrashReporter;

/**
 * Created by ra on 28/05/2017.
 */

public class BufferedInputStream extends InputStream {
    private InputStream in;
    private byte[] buffer;
    private int bufferIndex = 0;
    private int bufferEnd = 0;
    public BufferedInputStream(InputStream in){
        this.in = in;
        buffer = new byte[8192];
    }

    @Override
    public int read() throws IOException {
        if(bufferIndex>=bufferEnd){
           return refilBuffer();
        }
        return 0xff & buffer[bufferIndex++];
    }
    private int refilBuffer() throws IOException {
        bufferIndex = 0;
        bufferEnd = in.read(buffer);
        if(bufferEnd <=0){
            return -1;
        }
        return 0xff & buffer[bufferIndex++];
    }
    public int read(byte[] buffer) throws IOException {
        int i =0 ;
        while(i< buffer.length){
            if(bufferIndex>=bufferEnd){
                int refil = refilBuffer();
                buffer[i] = (byte)(refil);
                if(refil<0){ return i; }
                continue;
            }
            buffer[i] = buffer[bufferIndex++];
        }
        return i;
    }
    public int getWord() throws IOException {
        if(bufferIndex+1<bufferEnd){
            return
                (0xff& buffer[bufferIndex++])
                |
                ((0xff&buffer[bufferIndex++])<<8)
            ;
        }
        int howMany = read();
        int temp  = read();
        if ( (howMany| temp)<0){//(retu | temp) < 0) {
            throw new IOException("end of file reached:"+howMany+","+temp);
        }
        return (
                (0xff & howMany)
                        |
                ((0xff&temp)<<8)
        );
    }

    public int read(short[] data) throws IOException {
        int i =0 ;
        try {
            while (i < data.length) {
                data[i] = (short) getWord(); i++; //this automatically fills buffer when needed
                int length = (bufferEnd-bufferIndex)/2;
                if(i+length>=data.length){
                    length = data.length-i;
                }
                for(int y = 0; y<length; y++){
                    data[i] =
                            (short) (
                                    (0xff& buffer[bufferIndex])
                                            |
                                            ((0xff&buffer[bufferIndex+1])<<8)
                            );
                    bufferIndex+=2;
                    i++;
                }


            }
        }catch (Exception e) {
            CrashReporter.log("BufferedInputStrea.read(short[]) failed at:"+ i + "<" + data.length);
            throw e;
        }
        return i;
    }
}
