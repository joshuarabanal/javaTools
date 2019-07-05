package com.playMidi.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import Analytics.CrashReporter;

import com.musicxml.R;
import com.musicxml.dialogs.editInstrumentDialog.PickSoundFontDialog;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.File;
import java.io.IOException;


/**
 * Created by ra on 6/6/2016.
 */
public class AudioPool implements Runnable{
    private MidiTimbreSet synth = null;
    private AudioTrack t = null;
    private int sampleRate;
    private short[] buffer;
    private SoundEventRecycler keyEvents = new SoundEventRecycler();
    private boolean isDestroyed = false;
    private boolean isRunning = false;
    private File currentSynthResId = null;
    private File newSynthId = null;
    //private int defaultResId;

    private Activity c;
    public AudioPool(Activity c){
        //defaultResId = defaultResource;
        try {
            newSynthId = PickSoundFontDialog.getDefault(c, (byte) 0);
        }catch (Exception e){
            CrashReporter.sendDefaultErrorReport(e,"failed to move file from raw");
        }
        this.c = c;
    }
    public void destroy(){
        if(!isRunning){// if the music is not playing it is safe to cut it
            keyEvents.clear();
            if(t != null){
              AudioTrack t = this.t;
                this.t = null;
                if(t.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
                    t.pause();
                }
                t.release();
            }
            t = null;
            buffer = null;
        }
        isDestroyed = true;//else notify the playback that the music should be cut

    }

    private void revive() {
        isDestroyed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
            String sampleRatestring = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            if(sampleRatestring == null){ sampleRatestring = 44100+""; }
            sampleRate = Integer.parseInt(sampleRatestring);

        }
        else{
            sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        }


        t = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                4800, AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && false) {
            buffer = new short[t.getBufferSizeInFrames()];

        }
        else {
            buffer = new short[7500];
        }
    }


    /**
     * on down event for the keyboard
     * @param note midiNoteNumber
     * @param octave
     */
    public void test(int note, int octave, File synthResId) {
            if (t == null) {//initialize audio track
                newSynthId = synthResId;
                revive();
            }
        int sound = note + ((octave+1)*12)-1;
        keyEvents.add(sound, (int)(sampleRate*1.5), sampleRate,0);
        if(!isRunning){
            isRunning = true;
            Thread backgrund = new Thread(this);
            backgrund.setName("load synth");
            backgrund.start();
        }
    }
    /**
     * on down up event for the keyboard
     * @param note midi key number
     * @param octave
     */
    public void release(int note, int octave){

        int sound = note + (octave*12)-1;
        for(int i = 0; i<keyEvents.size();  i++){
            SoundEvent s = keyEvents.get(i);
            if(s.getNoteNumber() == sound){
                s.releaseNote(0);
            }
        }

    }
    public void releaseAllNotes(){
        for(int i = 0; i<keyEvents.size();  i++){
            keyEvents.get(i).releaseNote(this.sampleRate);
        }
    }

    private void allocateSynth(){
        if(
                synth ==null || //no current synth
                        ( newSynthId != null && currentSynthResId == null )//new synth id
                        ||
                        ( currentSynthResId != null &&!currentSynthResId.equals(newSynthId) )//changing the synth id
                ){
            try{
                if(newSynthId != null){
                    currentSynthResId = newSynthId;
                    synth = new SoundFontMidiTimbreSet(currentSynthResId, true);//mlMidiTimbreSet.retrieveCachedSoundFont(c, currentSynthResId, defaultResId);

                }
                else{
                    currentSynthResId = null;
                    synth = new SoundFontMidiTimbreSet(c, R.raw.grand_piano,true);
                    synth.setFrameRate(sampleRate);
                }
            } catch (IOException e) {
                CrashReporter.sendDefaultErrorReport(e, "opening soudfont:"+newSynthId);
            }

            keyEvents.clear();
        }
    }

    public void run(){
        isRunning = true;


        //allocate synth
        allocateSynth();
    if(synth == null){
        CrashReporter.sendDefaultErrorReport("synth never allocated");
        isRunning = false;
        return;

    }


        // write buffer
        int howMany = buffer.length;
        while (keyEvents.size() > 0 && !isDestroyed) {
            if(howMany<buffer.length){
                howMany = t.write(buffer,howMany,buffer.length-howMany);
                t.play();
                continue;
            }
            fillBuffer(buffer);
            howMany = t.write(buffer, 0, buffer.length);
        }

        if(t != null) {
            try {
                t.play();
            }
            catch(Exception e){
                CrashReporter.sendDefaultErrorReport(e,"audio track state:"+t.getPlayState(),"track to string:"+toString());
                isDestroyed = true;//destroy everything so we can start anew
            }
        }
        isRunning = false;
            if (isDestroyed) {
                destroy();
                return;
            }
    }
    private void fillBuffer(short[] buffer){


        for(int i = 0; i<buffer.length; i++){// clear buffer
            buffer[i] = 0;
        }
        int size = 10*keyEvents.size();//keyEvents.size() +1;//TODO undo this change
        for(int i = 0; i<keyEvents.size(); i++){
            if(keyEvents.get(i).fillBuffer(buffer, synth, size)){
                keyEvents.remove(i);
                i--;
            }
        }



    }


}