package com.playMidi.SoundFont.io.RiffFormat;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ra on 4/28/2017.
 */

public class Reader {





    public static int getRiffLength(InputStream in) throws IOException {
            int retu = 0;
            int temp = in.read();
            if (temp < 0) {
                throw new IOException("end of file reached");
            }
            retu += temp;

            temp = in.read();
            if (temp < 0) {
                throw new IOException("end of file reached");
            }
            retu += temp << 8;

            temp = in.read();
            if (temp < 0) {
                throw new IOException("end of file reached");
            }
            retu += temp << 16;

            temp = in.read();
            if (temp < 0) {
                throw new IOException("end of file reached");
            }
            retu += temp << 24;
            return retu;

        /**
        int retu = PcmHelpers.readunsignedLittleEndian(in,4);
        if(retu<0){ throw new IOException("return negativ value:"+retu); }
        return retu;
         **/
    }
    private static byte[] word = new byte[2];
    public static int getWord(InputStream in) throws IOException {
        int howMany = in.read(word);
        //int temp  = in.read();
        if ( howMany<2){//(retu | temp) < 0) {
            throw new IOException("end of file reached");
        }
        return (word[0] | (word[1]<<8));
    }
    public static String getRiffName(InputStream in) throws IOException {
        return getString(in,4);
    }

    /**
     *
     * @param in
     * @param length ATTRIBUTE_length of string in bytes
     * @return
     */
    public static String getString(InputStream in, int length) throws IOException {
        byte[] b = new byte[length];

        for(int i = 0; i<b.length; i++){
            int bte = in.read();
            if(bte ==-1){ throw new IOException("unexpected end of file"); }
            b[i] = (byte)bte;

        }
        return new String(b);
    }
}
