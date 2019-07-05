package com.playMidi.player.notate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.musicxml.noteDataTypes.DrawingDataBox;
import com.playMidi.player.AudioPlayerIterface;
import com.playMidi.player.Midi.MidiSequencer;
import com.playMidi.player.Midi.midisequencer.OnCompletionListener;
import com.playMidi.player.Midi.midisequencer.OnPreparedListener;
import com.playMidi.player.Midi.midisequencer.PrepareRunnable;
import com.playMidi.player.notate.player.PlaybackRunnable;

/**
 * Created by Joshua on 19/12/2017.
 */

public class Player implements AudioPlayerIterface {
    private PlaybackRunnable pbr = null;
    private Activity c;
    private DrawingDataBox ddb;
    private OnCompletionListener ocl;
    private OnPreparedListener opl;

    public Player(Activity c, DrawingDataBox d){
//        new IndexOutOfBoundsException("player allocated:alot of memory").printStackTrace();
        this.c = c;
        this.ddb = d;
    }

    @Override
    public boolean isRunning() {
        return pbr.isRunning();
    }

    public boolean isPlaying(){
        if(pbr == null){
            return false;
        }
        else{
            return pbr.isPlaying();
        }
    }
    public boolean isLoading(){
    return false;
    }
    public boolean isPrepared(){
        return true;
    }


    // media player functions
    public void setDataSource(Uri f){

    }
    public void prepare(){
        opl.onPrepared(this);
    }
    public void setOnCompletionListener(OnCompletionListener ocl){
        this.ocl = ocl;
    }
    public void setOnPreparedListener(OnPreparedListener opl){
        this.opl = opl;

    }
    public void onCompletion(){
        if(ocl!=null){
            ocl.onCompletion(this);
        }
    }
    public void stop(){
        if(pbr!=null){ pbr.stop(); }
    }
    public void release(){
        stop();
    }
    public int getCurrentPosition(){
        return 0;
    }
    public int getDuration(){
        return 1;
    }
    public void pause(){
        if(pbr!=null){pbr.pause();}
    }
    public void play(){
        if(isPlaying()){ return; }
        start();
    }
    public void seekTo(int position){}
    public void start(){
        if(pbr == null){
            pbr = new PlaybackRunnable(c,ddb,this);
        }

        pbr.play();
    }
    public void reset(){
        stop();
    }

}
