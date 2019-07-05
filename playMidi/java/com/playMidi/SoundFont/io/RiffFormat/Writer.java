package com.playMidi.SoundFont.io.RiffFormat;


import java.io.IOException;
import java.io.OutputStream;

import waveFormat.PcmHelpers;

/**
 * Created by ra on 24/05/2017.
 */

public class Writer {
    /**
     *
     * @param out
     * @param value ATTRIBUTE_length of the following chunk(excluding the ATTRIBUTE_length of the ATTRIBUTE_length param)
     * @throws IOException
     */
    public static void writeRiffLength(OutputStream out, int value) throws IOException {
        PcmHelpers.writeUnsignedLittleEndian(out,value,4);
    }
    public static void writeWord(OutputStream out, int value) throws IOException {
        PcmHelpers.writeUnsignedLittleEndian(out,value,2);
    }
    public static void writeRiffName(OutputStream out, String name) throws IOException {
        writeString(out, name);
    }
    public static void writeRiffName(OutputStream out, String name, int length) throws IOException {
        writeString(out, name);
        writeRiffLength(out, length);
    }


    public static void writeString(OutputStream out, String name) throws IOException {
        for(int i = 0; i<name.length(); i++){
            out.write(name.charAt(i));
        }
        return;
    }

    public static void write_StringOfLength(OutputStream out, String string, int length) throws IOException {
        while(string.length() <length){
            string+='\0';
        }
        if(string.length() != length){ throw new IndexOutOfBoundsException("failed to write string of ATTRIBUTE_length:"+length+" for:"+string); }
        writeString(out,string);
    }
}
