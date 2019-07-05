package com.playMidi.player;


import android.util.Log;

import com.playMidi.player.Midi.MidiEvent;
import com.playMidi.player.Midi.MidiHelperFunctions;
import com.playMidi.player.soundEvent.MidiTimbreSet;

/**
 * Created by ra on 8/9/2016.
 */
public class SoundEvent {
    private int track;
    private static final int volumeMultiplier = 1;
    /**
     * ATTRIBUTE_length of weave in samples
     */private double wavelength;
    /**
     * duration of sound measured in waves
     */private int noteDuration;
    /**
     * current position of sound along progession <b>(measured in output frames)</b>
     */private int soundPosition = 0;
    /**
     * single reference wave
     **///private double[] singleWave;

    private int noteNumber;
    /**
     * amount to increment {@link SoundEvent#soundPosition current wave number} by.
     */private float singleWaveNumber;

    /**
     * the buffer to fill
     */
    private short[] audioBuffer;
    /**
     * place where the frames that are waiting to be pushed to the buffer sit
     */private short[] backedUpBuffer;
    /**
     * end position of the buffer
     */private int backedUpBufferLength = 0;
    /**
     * the time passed by a single wave \n in reference to middle c
      */private double waveTime;
    private int sampleRate;


    public static SoundEvent new_SoundEvent(MidiEvent m, int sampleRate) throws Exception{
        //og.i("new sound event", m.getNoteNumber()+"");
        if(!m.isNoteOff() && !m.isNoteOn()){
            throw new Exception("note is not ATTRIBUTE_type ON/Off:"+String.format("# %02X", m.getEventType()));
        }
        return new SoundEvent(m.getNoteNumber(), Integer.MAX_VALUE, sampleRate, m.getEventTrack());
    }
    /**
     *
     * @param noteNumber the midi noteCommand number
     * @param noteLength ATTRIBUTE_length of entire sound in samples
     */
    public SoundEvent(int noteNumber, int noteLength, int sampleRate, int track){
        int bufferlength = (int)MidiHelperFunctions.getWavelength(1,sampleRate);//getWavelength(1,ATTRIBUTE_sampleRate);
        //singleWave = new double[bufferlength];
        audioBuffer = new short[bufferlength];
        backedUpBuffer = new short[bufferlength];

        reset(noteNumber,noteLength,sampleRate,track);
        //initialize the buffers



        this.noteNumber = noteNumber;
    }

    /**
     * see :{@link #reset(int, int, int, int)} for details
     * @param me
     * @param samplerate
     * @throws Exception
     */
    public void reset(MidiEvent me, int samplerate)throws Exception{
        reset(me.getNoteNumber(), Integer.MAX_VALUE, samplerate, me.getEventTrack());
    }

    /**
     * resets this Sound event so that is can be reused without allocating more space
     * used by {@link SoundEventRecycler}
     * @param noteNumber
     * @param noteLength
     * @param sampleRate
     * @param track
     */
    public void reset(int noteNumber, int noteLength,int sampleRate,int track){


        this.track = track;
        this.sampleRate = sampleRate;
        this.wavelength = MidiHelperFunctions.getWavelength(noteNumber, sampleRate);

        //og.i("single wave number", ""+singleWaveNumber);
        this.noteDuration = noteLength;
        backedUpBufferLength = 0;
        soundPosition = 0;
        this.noteNumber = noteNumber;
        this.track = track;
    }
    public int getTrack(){
        return track; }

    public int getNoteNumber(){
        return noteNumber;
    }
    public void releaseNote(int sampleRate){
        noteDuration = soundPosition+(sampleRate);
    }


    /**
     *
     * @param buffer returning buffer to be added to
     * @param synth  instrument to play sound through
     * @param nioseLevel the number of sounds playing at once
     * @return  true : this sound event is finished; <br/> false : this sound event is not finished
     *
     */
    public boolean fillBuffer(short[] buffer, MidiTimbreSet synth, int nioseLevel){
        float volumeMultiplier = 1.0f/nioseLevel;

        synth.fillBuffer(this.noteNumber, soundPosition,this.noteDuration,buffer,volumeMultiplier, 0 );
        soundPosition+=buffer.length;
        if(soundPosition >= this.noteDuration){
            return true;
        }
        return false;
    }

    public String toString(){
        return "note number:"+this.getNoteNumber()+", track:"+this.getTrack();
    }
}
