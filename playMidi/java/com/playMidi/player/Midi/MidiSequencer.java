package com.playMidi.player.Midi;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.RawRes;

import com.playMidi.player.AudioPlayerIterface;
import com.playMidi.player.Midi.midisequencer.OnCompletionListener;
import com.playMidi.player.Midi.midisequencer.OnPreparedListener;
import com.playMidi.player.Midi.midisequencer.PlaybackRunnable;
import com.playMidi.player.Midi.midisequencer.PrepareRunnable;

import java.io.File;

/**
 * <ul>
 *     <li>
 * <a href="http://www.ccarh.org/courses/253/handout/smf/"> midi header info</a>
 *     </li>
 *     <li>
 * <a href="http://www.onicos.com/staff/iz/formats/midi-event.html"> note on/off info</a>
 *     </li>
 *     <li>
 * <a href="http://cs.fit.edu/~ryan/cse4051/projects/midi/midi.html"> other event info</a>
 *     </li>
 * </ul>
 * Created by ra on 11/26/2016.
 */

public class MidiSequencer implements AudioPlayerIterface {
    private Activity context;
    private Uri file;
    private PlaybackRunnable pbr = null;
    private PrepareRunnable pr = null;
    //event listeners
    private OnPreparedListener onPreparedListener;
    private OnCompletionListener onCompletionListener;
    private int defaultSynthResourceId;
    private int backupResId;
    private File outputFile;

    /**
     *
     * @param c
     * @param f
     * @param defaultSynthRes xml resource id
     * @param backupResourceId raw.tight_piano_sf soundfont res id
     */
    public MidiSequencer(Activity c, File f, @RawRes int defaultSynthRes, @RawRes int backupResourceId) {
        if(defaultSynthRes == 0 || backupResourceId == 0){ throw new NullPointerException("resource not found"); }
        backupResId = backupResourceId;
        defaultSynthResourceId = defaultSynthRes;
        context = c;
        setDataSource(Uri.fromFile(f));
    }
    public MidiSequencer(Context c, Uri f, @RawRes int defaultSynthRes, @RawRes int backupResourceId) {
        defaultSynthResourceId = defaultSynthRes;
        backupResId = backupResourceId;
        context = (Activity)c;
        setDataSource(f);
    }

    public void setOutPutFile(File f){
        this.outputFile = f;
    }

/**
//media player getters and setters
**/
public boolean isRunning(){
    return (isPlaying() || isLoading());
}
public boolean isPlaying(){
    return (pbr != null && pbr.isRunning());
}
public boolean isLoading(){

    return (pr != null && pr.isRunning());
}
public boolean isPrepared(){
    return (pr != null && !pr.isRunning() && pr.getTicksPerNote() != -1);
}


// media player functions
    public void setDataSource(Uri f){
        file = f;
    }
    public void prepare(){
        if(isRunning() || isPrepared()){return; }
        pr = new PrepareRunnable(this,file,context, defaultSynthResourceId,backupResId);
        pr.setOnPreparedListener(onPreparedListener);
        pr.execute();
    }
    public void setOnCompletionListener(OnCompletionListener ocl){
        onCompletionListener = ocl;
    }
    public void setOnPreparedListener(OnPreparedListener opl){
        onPreparedListener = opl;
    }

    public void stop(){
        //og.("stop", "is playing:"+isPlaying());
        if(isPlaying()){
            pbr.stop();
        }
        if(isLoading()){
            pr.stop();
        }
        onCompletionListener.onCompletion(this);

    }
    public void release(){
        if(isLoading() || isPlaying()){ stop(); }
        pbr = null;
        pr = null;


    }
    public int getCurrentPosition(){
        return 0;
    }
    public int getDuration(){
        return 1;
    }
    public void pause(){
        if(pbr!= null && pbr.isRunning()){
            pbr.stop();
        }
    }
    public void play(){
        if(isRunning()){ return; }
        start();

    }
    public void seekTo(int position){}
    public void start(){
        if(isRunning() || !isPrepared()){ throw new IllegalStateException("the midi sequencer is not yet prepared"); }

        if(pbr == null){
            pbr = new PlaybackRunnable(this,context,pr.getTicksPerNote(),pr.getTracks());
            pbr.setOutputFile(outputFile);
            pbr.setOnCompletionListener(onCompletionListener);
            pbr.setInstruments(pr.getMidiInstrument());
        }
        pbr.execute();

    }
    public void reset(){

    }

    /**
     *
     public void reset(){
     if(positions == null){
     positions = new int[tracks.size()];
     }
     for(int i = 0; i<positions.length; i++){
     positions[i] = 0;
     }
     if(t != null ){
     t.flush();
     }
     }


     */





}
