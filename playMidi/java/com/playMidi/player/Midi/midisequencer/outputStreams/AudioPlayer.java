package com.playMidi.player.Midi.midisequencer.outputStreams;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import com.musicxml.customerTracker.CustomerTracker;
import com.playMidi.player.Midi.midisequencer.OnCompletionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Analytics.CrashReporter;
import waveFormat.BufferedInputStream;
import waveFormat.PcmHelpers;

public class AudioPlayer  implements AudioOutputStream , Runnable{

    private int sampleRate;
    private int framesPerBuffer;
    private File writingFile;
    private boolean writingComplete = false;
    private short[] buffer;

    private int readingIndex = -1;
    private int writingIndex = 0;

    private boolean running  = false;
    private OnCompletionListener ocl;



    public AudioPlayer(Context c) throws IOException {
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

    private void setValues(short[] data,int start,  int length){
        if(length<0){
            CrashReporter.log("start = "+start);
            CrashReporter.log("length = "+length);
            CrashReporter.log("writing index = "+writingIndex);
            throw new IndexOutOfBoundsException("set values->length cannot be negative");
        }
        //Log.i("setValues", "start:"+start+", length:"+length+", writing index:"+writingIndex+", reading index:"+readingIndex);
            int origstart = start, origLength = length;
            int end = start+length;

            for(; start<end; start++, writingIndex++){
                try {
                    buffer[writingIndex] = data[start];
                    /**if(writingIndex == readingIndex){
                        throw new IndexOutOfBoundsException("writing index cannot equal reading index");
                    }
                     **/
                }catch(IndexOutOfBoundsException e){
                    CrashReporter.log("origStart = "+origstart);
                    CrashReporter.log("orig length = "+origLength);
                    CrashReporter.log("buffer["+writingIndex+"] ;  buffer.length = "+buffer.length);
                    CrashReporter.log("data["+start+"] ;  data.length = "+data.length);
                    throw e;
                }
            }
        if(writingIndex == buffer.length){
            writingIndex = 0;
        }
    }
    public int write(short[] inputData, int start, int length) throws IOException {
        if(writingComplete){
            throw new IOException("write called after close");
        }
        while(writingIndex ==  readingIndex){//while we are waiting for the audiotrack to catch up
            try {
                //Log.i("write waiting for read","reading index:"+readingIndex+", writing index:"+writingIndex);
                if(!running){
                    new Thread(this, "AudioPlayer.AudioTrack").start();
                    //Log.i("thread startd", "from write");
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        if(buffer == null){
            buffer = new short[sampleRate*2];
            readingIndex = buffer.length-1;
        }
        if(writingIndex<readingIndex){//if the writer has overlapped and is chasing the reading index
            if(writingIndex+length<readingIndex){//if we can fill the whole buffer without issues
                //Log.i("write chase read", "fill whole buf");
                setValues(inputData,start, length );
            }
            else{//we can only fill a small amount
                int distanceToReadingIndex = (readingIndex)-writingIndex;
                //Log.i("write chase read", "partial fill");
                setValues(inputData, start, distanceToReadingIndex);
                write(inputData, start+distanceToReadingIndex, length-distanceToReadingIndex);
            }
        }
        else{//the writing index is beig chased by the reading index
            if(writingIndex+length<(buffer.length)){//if we can write without overlapping
                //Log.i("read chase write ", "fill whole buff");
                setValues(inputData,start,length);
            }
            else {//we need to overlap to compete this
                int distanceToEnd = (buffer.length-1)-writingIndex;
                //Log.i("read chase write ", "partial fill");
                setValues(inputData, start, distanceToEnd);
                writingIndex = 0;
                write(inputData, start+distanceToEnd, length-distanceToEnd);
            }
        }

        if(writingIndex<0 || writingIndex>buffer.length){
            throw new IndexOutOfBoundsException("bad writing index:"+writingIndex);
        }
        return length;
    }

    /**
     * use this function yto halt playback early
     */
    public void stop(){
        Log.i("Audio player", "stop called");
        if(running) {
            running = false;
            ocl.onCompletion(null);
        }
    }
    /**
     * use this function to mark the end of the audio
     * @throws IOException
     */
    public void close(){
        writingComplete = true;
        if(!running){
             new Thread(this,"AudioPlayer.AudioTrack").start();
        }
    }


    @Override
    public int getSampleRate() {
        return sampleRate;
    }


    @Override
    public void setOnCompletionListener(OnCompletionListener ocl) {
        this.ocl = ocl;
    }

    public boolean isPlaying() {
        return running;
    }

    public void run(){
        running = true;

        AudioTrack t = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    10000, AudioTrack.MODE_STREAM);
            //Log.w("run started", "reading index:"+readingIndex);
            while (
                    running
                    ) {


                //perform the writing
                int howManyWrite = 0;
                if (readingIndex+1 == writingIndex) {//wait for buffer to be filled
                    if (writingComplete) {// if no more samples
                        break;
                    }
                    try {
                        //Log.w("playback", "waiting for writer:"+ "reading index:"+readingIndex+", writing index:"+writingIndex);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        CrashReporter.sendDefaultErrorReport(e);
                    }
                    continue;
                }
                else if(readingIndex<writingIndex){//if were following the writer
                    //Log.w("read","following the writer");
                    int length = (writingIndex-1)-readingIndex;
                    howManyWrite = t.write(buffer, readingIndex, length);

                }
                else{//if we can read till the end
                    //Log.w("read", "followed by writer");
                    int length = buffer.length - readingIndex;
                    if(writingIndex == 0 ){//prevent read from crossing the write index
                        length-=1;
                    }
                    howManyWrite = t.write(buffer, readingIndex, length);
                }

                //check for errors
                if(howManyWrite<0 ){
                    CrashReporter.sendDefaultErrorReport(
                            "Audio player error",

                            "error code:+"+howManyWrite,
                            "bad value:"+AudioTrack.ERROR_BAD_VALUE,
                            "dead object:"+-6,//AudioTrack.ERROR_DEAD_OBJECT
                            "invalid operation:"+AudioTrack.ERROR_INVALID_OPERATION,
                            "buffer length:"+buffer.length,
                            "readingINdex:"+readingIndex,
                            "writingInex:"+writingIndex
                        );
                }
                if(howManyWrite>0){
                    if(readingIndex+howManyWrite == writingIndex){
                        CrashReporter.log("reading index:"+readingIndex);
                        CrashReporter.log("writing Index:"+writingIndex);
                        CrashReporter.log("how many write:"+howManyWrite);
                        throw new IndexOutOfBoundsException("cannot have reading and writing indicies equal");

                    }
                    readingIndex += howManyWrite;
                }
                if(readingIndex == buffer.length){//wrap the reading index over itself
                    readingIndex = 0;
                }
                //Log.w("new reading index","reading index:"+readingIndex+", writing index:"+writingIndex);

                if(t.getState() !=  AudioTrack.PLAYSTATE_PLAYING) {
                    try {
                        t.play();
                    } catch (IllegalStateException e) {
                        CrashReporter.sendDefaultErrorReport(e, "t.playstate:" + t.getPlayState());
                    }
                }




            }
            ocl.onCompletion(null);

    }
}
