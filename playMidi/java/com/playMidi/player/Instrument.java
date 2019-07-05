package com.playMidi.player;

import com.playMidi.player.Midi.MidiHelperFunctions;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.IOException;
import java.io.OutputStream;

import waveFormat.PcmHelpers;

/**
 * Created by ra on 3/7/2017.
 */

public class Instrument {


    private final String name;
    private final int midiNumber;
    private final MidiTimbreSet instrument;
    private final int frameRate;
    public Instrument(String name, int midiNumber, MidiTimbreSet inst, int frameRate){
        this.name = name;
        this.midiNumber = midiNumber;
        this.instrument = inst;
        this.frameRate = frameRate;
    }

    public final String getInstrumentName(){
        return name;
    }
    public final int getInstrumentMidiNumber(){
        return midiNumber;
    }


    /**
     *
     * @param volume float from 0:1
     * @param buffer
     */
    public final void fillBuffer(
            int midiNoteNumber, int position, int noteLength, float volume, short[] buffer){
        int wavelength = (int)MidiHelperFunctions.getWavelength(midiNoteNumber, frameRate);
        double[] singleWave = new double[wavelength];
        for(int i = 0; i<(wavelength ); i++){
            singleWave[i] =  Math.sin((2*Math.PI*i)/wavelength)*volume;
        }
        double[] b = new double[buffer.length];
        instrument.setFrameRate(frameRate);
        instrument.fillBuffer(midiNoteNumber,position,noteLength,buffer,volume,0);
        for(int i = 0; i<buffer.length; i++){
            buffer[i] = (short)b[i];
        }
    }

    /**
     *
     * @param out destination output
     * @param noteNumber midi sound number
     * @param volume number from 0:{@link Short#MAX_VALUE Short.MAX_VALUE}
     * @throws IOException
     */
    public void SaveToFile(OutputStream out, int noteNumber, int volume)  throws IOException{
        int seconds = 0;
        short[] buffer = new short[2048];
        while(seconds>44100*3){
            for(int i = 0; i<buffer.length; i++){ buffer[i] = 0; }
            fillBuffer( noteNumber, seconds,buffer.length, volume, buffer);
            PcmHelpers.Short.write(buffer, out);
            seconds+=buffer.length;
        }

    }

}
