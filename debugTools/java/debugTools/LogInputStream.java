package debugTools;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends InputStream {
    private InputStream in;
    private File f;
    private FileOutputStream out;
    public LogInputStream(InputStream parent) throws IOException {
        this.in = parent;
        f = File.createTempFile("log", ".html");
        out = new FileOutputStream(f);
    }
    @Override
    public int read() throws IOException {
        int retu = in.read();
        if(retu>0){
            out.write(retu);
        }
        return retu;
    }
    public void close() throws IOException {
        in.close();
        in = null;
        out.close();
        out = null;
        Log.i("file saved to", f.toString());

        FileInputStream in = new FileInputStream(f);
        byte[] b = new byte[1024];
        int howMany;
        while( (howMany = in.read(b))>0){
            Log.i("LogInputStream read", new String(b,0,howMany) );
        }
        in.close();
    }
    public File getFile(){
        return f;
    }
}
