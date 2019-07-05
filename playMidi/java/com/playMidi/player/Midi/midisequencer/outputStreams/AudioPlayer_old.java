package com.playMidi.player.Midi.midisequencer.outputStreams;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.Log;

import Analytics.CrashReporter;
import com.playMidi.player.Midi.midisequencer.OnCompletionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import waveFormat.BufferedInputStream;
import waveFormat.PcmHelpers;

public class AudioPlayer_old implements AudioOutputStream , Runnable {
    private int sampleRate;

    private boolean running = false;
    private int writingIndex = 0;
    private  File  writingFile;
    private Thread thread;
    private int framesPerBuffer;
    private OnCompletionListener ocl;

    public AudioPlayer_old(Context c) throws IOException {
        int buffersizeInFrames = 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
            sampleRate = Integer.parseInt(am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));
            String framesPerBufferst = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            framesPerBuffer = Integer.parseInt(framesPerBufferst);
            if(framesPerBuffer <= 0){
                framesPerBuffer = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
                if(framesPerBuffer<0){
                    framesPerBuffer = 256;
                }
            }
        }
        else{
                sampleRate = 44100; // Use a default value if property not found
                framesPerBuffer = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
                if(framesPerBuffer<0){
                    framesPerBuffer = 256;
                }

        }



        Log.i("frames per buffer", framesPerBuffer+"");
        writingFile = File.createTempFile("writing","frames");

    }

    private FileOutputStream out;
    public int write(short[] buffer, int start, int length) throws IOException {
        if(out == null){
            if(writingFile == null){
                throw new IOException("cannot call write on a closed stream");
            }
            if(writingFile.exists()){ writingFile.delete(); }
            out = /*new ShortOutputStream(*/new FileOutputStream(writingFile);
        }
        /*.write(buffer, 0, length);*/PcmHelpers.Short.write(out, buffer,0,length);

        writingIndex+=length;
        if(writingIndex>100 && thread == null){
            thread = new Thread(this);
            thread.start();
        }
        return length;

    }

    /**
     * use this function yto halt playback early
     */
    public void stop(){
        running = false;
    }
    /**
     * use this function to mark the end of the audio
     * @throws IOException
     */
    public void close() throws IOException {

        if(out!=null){
            out.close();
            out = null;
            writingFile = null;
        }
        if(!running){
            runComplete(null);
        }
        Log.i("closing out", "closing short stream");
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener ocl) {
        this.ocl = ocl;
    }

    public void run(){
        running = true;
        int readingIndex = 0;

        short[] buffer = new short[framesPerBuffer];
        AudioTrack t = null;
        try {
          if(writingFile == null){
            runComplete(null);
            return;
          }
          InputStream in= new BufferedInputStream(new FileInputStream(writingFile));
          t = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                10000, AudioTrack.MODE_STREAM);

            while(
                    (out!=null || readingIndex<writingIndex) && running
                ){

                //if(out == null && readingIndex>=writingIndex){ break; }

                if(readingIndex>writingIndex){//wait for buffer to be filled
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        CrashReporter.sendDefaultErrorReport(e);
                    }
                    continue;
                }
                int howManyRead = /*in.read(buffer);*/PcmHelpers.Short.read(in, buffer);
                int howManyWrite = t.write(buffer, 0, howManyRead);
                try{ t.play(); }catch(IllegalStateException e){
                  CrashReporter.sendDefaultErrorReport(e, "t.playstate:"+t.getPlayState());
                }
                while(howManyWrite<howManyRead) {
                    int howMany =  t.write(buffer, howManyWrite, howManyRead-howManyWrite);
                    if(howMany<0){
                        throw new IOException("write error:"+howMany);
                    }
                    howManyWrite+=howMany;
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        CrashReporter.sendDefaultErrorReport(e);
                    }
                }
                readingIndex+=howManyRead;

            }
        } catch (IOException e) {
            CrashReporter.sendDefaultErrorReport(e);
        }
        catch(NullPointerException e){
          CrashReporter.sendDefaultErrorReport(e, "writingFile:"+writingFile);
        }
        runComplete(t);
    }
    private void runComplete(@Nullable AudioTrack t){
      running = false;
        if(t!=null){
            if(t.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
              t.stop();
            }
            t.release();
            t = null;
        }
        Log.e("on completion", "writing index:"+writingIndex+", out is null:"+(out == null)+"");
        ocl.onCompletion(null);
    }

    public boolean isPlaying() {
        return running;//t!=null && t.getPlayState() ==  AudioTrack.PLAYSTATE_PLAYING;
    }
}
