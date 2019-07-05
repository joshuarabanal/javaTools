package com.playMidi.player;


import android.util.Log;

import com.playMidi.player.Midi.MidiEvent;

import java.util.ArrayList;

/**
 * Created by ra on 12/5/2016.
 */

public class SoundEventRecycler {
    private ArrayList<SoundEvent> allocated;
    private ArrayList<SoundEvent> cached;
    public SoundEventRecycler(){
        allocated = new ArrayList<SoundEvent>();
        cached = new ArrayList<SoundEvent>();
    }
    public SoundEvent get(int index){
        return allocated.get(index);
    }
    public void remove(int index){
        SoundEvent se = allocated.remove(index);
        cached.add(se);
    }
    public void add(int noteNumber, int noteLength, int sampleRate, int track){
        for(int i = 0; i<allocated.size(); i++){
            if(allocated.get(i).getNoteNumber() == noteNumber){ return; }
        }
        //og.("SoundeventRecycler.add", ""+noteLength);
        SoundEvent se;
        if(cached.size()>0){
            se = cached.remove(0);
            se.reset(noteNumber, noteLength, sampleRate, track);
        }
        else{
            se = new SoundEvent(noteNumber, noteLength, sampleRate, track);
        }
        allocated.add(se);
    }

    public void add(MidiEvent me, int sampleRate)throws Exception{
        SoundEvent se;
        if(cached.size()>0){
            se = cached.remove(0);
            se.reset(me, sampleRate);
        }

        else{
            se = SoundEvent.new_SoundEvent(me, sampleRate);
        }

        allocated.add(se);
    }
    public int size(){ return allocated.size(); }
    public void clear(){
        allocated.clear();
        cached.clear();
    }
    public String toString(){
        return allocated.toString();
    }
}
