/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Joshua
 */
public class ShortInputStream {
    private DataInputStream in;
    public ShortInputStream(InputStream in){
        this.in = new DataInputStream(in);
    }
    public int read(short[] array) throws IOException{
       return read(array,0,array.length);
    }
    public int read(short[] array, int start, int length) throws IOException{
        int i = 0; 
        
        try{
            for(; i<length; i++){
                array[start+i] = in.readShort();
            }
        }
        catch(EOFException e){
            return i;
        }
        return i;
    }
    public short read() throws IOException{
        try{
            return in.readShort();
        }
        catch(EOFException e){
            return -1;
        }
    }
    
}
