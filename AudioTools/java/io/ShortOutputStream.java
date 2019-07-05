/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Joshua
 */
public class ShortOutputStream{
    private DataOutputStream out;
    public ShortOutputStream(OutputStream out){
         this.out = new DataOutputStream(out);
    }
    
    public void write(short[] data) throws IOException{
        write(data,0, data.length);
    }
    private byte[] byteBuffer;
    public void write(short[] data, int start, int length) throws IOException{
        if(byteBuffer == null){ byteBuffer = new byte[1028];}
        int byteBufferIndex = 0;
        for(int i = 0; i<length; i++){
            if(byteBufferIndex+1>=byteBuffer.length){
                out.write(byteBuffer);
                byteBufferIndex = 0;
            }
            byteBuffer[byteBufferIndex++] =(byte)((data[start+i]>>8) & 0xFF);
            byteBuffer[byteBufferIndex++] = (byte)(data[start+i]&0xFF);

        }
        out.write(byteBuffer,0,byteBufferIndex);

        //for(int i =0; i<length; i++){
          //  out.writeShort(data[i+start]);
        //}
    }
    public void close() throws IOException{
        if(out == null){
            return;
        }
        flush();
        out.close();
        out = null;
    }
    public void flush() throws IOException {
        out.flush();
    }
    
    
}
