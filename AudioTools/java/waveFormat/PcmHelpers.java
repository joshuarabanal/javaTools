package waveFormat;

import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PcmHelpers {
    /**
     * IO class for reading and writing short values
     */
    public static class Short{
        /**
         * warning this functionwas abandoned and never tested
         * @param from
         * @param to
         */
        public static void convertToByte(short[] from, byte[] to){
            if(to.length*2 != from.length){
                throw new IndexOutOfBoundsException("conversiont failed byte.length != short.length/2");
            }
            for(int b = 0,s = 0; b+1<to.length && s<from.length; b+=2,s++){
                to[b] = (byte)(from[s] & 0xFF);
                to[b+1] = (byte)((from[s]>>8) & 0xff);
            }
        }
        public static void write(short[] buffer, OutputStream fos)throws IOException {
            write(fos, buffer, 0, buffer.length);
        }
        public static void write(OutputStream fos, short[] buffer, int start, int length)throws IOException {
            if(byteBuffer == null){ byteBuffer = new byte[1028];}
            int byteBufferIndex = 0;
            for(int i = 0; i<length; i++){
                if(byteBufferIndex+1>=byteBuffer.length){
                    fos.write(byteBuffer);
                    byteBufferIndex = 0;
                }
                byteBuffer[byteBufferIndex++] = (byte)(buffer[start+i]&0xFF);
                byteBuffer[byteBufferIndex++] =(byte)((buffer[start+i]>>8) & 0xff);

            }
            fos.write(byteBuffer,0,byteBufferIndex);
        }
        public static void write(OutputStream out, short val) throws IOException {
            out.write((byte)(val & 0xFF));
            out.write((byte)((val>>8) & 0xff));
        }

        /**
         * returns the vale of reading one single short from the input stream
         * @param in
         * @return
         * @throws IOException
         */
        public static int read(InputStream in) throws IOException {
            int first = in.read();
            int second = in.read();
            if(first<0 || second<0){
                return -1;
            }
            return ((second&0xff)<<8) | (first& 0xff);
        }
        public static int read(InputStream in, short[] b) throws IOException {
            return read(in,b,0,b.length);
        }
        public static int read(InputStream in, short[] b, int start, int length) throws IOException {
            int i = 0;
            int val;
            for(; i<length; i++){
                val = read(in);
                if(val<0){
                    return i;
                }
                b[start+i] = (short) val;
            }
            return i;
        }
    }

	public static int frameValueLittleEndian(int firstByte, int secondByte){
		int one = 1;
		if(secondByte>127){//this is a negative frame value
			secondByte-=256;
			one = -1;
		}
		return (firstByte+(secondByte*(256)));
	}
	public static void writeFrameToAudioTrack(AudioTrack track, int frame){
		byte[] b = {
		 (byte) (frame & 0xFF),
		 (byte) (((frame >> 8) & 0xff) )
		};
		track.write(b, 0, 2);
	}
	private static byte[] byteBuffer;

    /**
     *
     * @param in stream to pull the value from
     * @param length number of bytes to read
     * @return
     * @throws IOException
     */
	public static int readunsignedLittleEndian(InputStream in, int length) throws IOException {
        int retu = 0;
		int temp;
        for (int i = 0; i <length; i++) {
            if((temp = in.read())<0){ throw new IOException("unexpected end of file:"+temp); }
            retu += temp << (8*i);
        }

        return retu;
    }
    /**
     * same as readunsignedLittleEndian except calculates the sign
     * see: {@link #readunsignedLittleEndian(InputStream, int)} for explanation
     */
    public static int readsignedLittleEndian(InputStream in, int length) throws IOException {
		int val = readunsignedLittleEndian(in,length);

		int tempmaxPositive = (0x01<<((8*(length-1))+7)) - 1;
		if(val>tempmaxPositive) {
			tempmaxPositive = (tempmaxPositive<<1) +1;
			return val - tempmaxPositive;
		}
		return val;
	}

	public static void writeUnsignedLittleEndian(OutputStream out, int value, int length) throws IOException {
		for(int i = 0; i<length; i++){
			out.write( 0xff&((value)>>(i*8)) );
		}
	}
	public static float linearInterpolation(float index, short[] data){
		int bottom = data[(int)index];
		int top = data[((int)index)+1];
		float position = index- ((int)index);

		return bottom + ((top-bottom)*position);

	}


}
