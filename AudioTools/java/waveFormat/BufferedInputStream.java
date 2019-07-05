package waveFormat;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joshua on 26/03/2018.
 */

public class BufferedInputStream extends InputStream {
    private InputStream in;
    private byte[] buffer = new byte[1024];
    private int bufferIndex = 0;
    private int bufferLength = 0;
    public BufferedInputStream(InputStream in){
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        if(bufferIndex>=bufferLength){
            bufferLength = in.read(buffer);
            bufferIndex = 0;
            if(bufferLength == 0){
                //og.("end of stream", bufferLength+"");
                return -1;
            }
        }
        int retu = buffer[bufferIndex];
        bufferIndex++;
        return 0xff&retu;
    }
    public void close() throws IOException {
        in.close();
    }
}
