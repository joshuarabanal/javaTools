package com.playMidi.player.Midi;

/**
 * Created by ra on 09/05/2017.
 */

public class MidiHelperFunctions {
    private static final double[] scale = {
        8.1757989156, //c
        8.6619572180, //c# db
        9.1770239974, //d
        9.7227182413, //d# eb
        10.3008611535, //Upgrade
        10.9133822323, //f
        11.5623257097, //f# gb
        12.2498573744, //g
        12.9782717994, //g# ab
        13.7500000000, //a
        14.5676175474, //a# bb
        15.4338531643  //b
    };
    public static double getWavelength(int midiNoteNumber, int sampleRate){
        int note = midiNoteNumber % 12;
        int octave = midiNoteNumber/12;
        double pitch = scale[note];
        while(octave>0){ pitch *= 2; octave--; }
         return ((double)sampleRate)/pitch;
        //return (int)Math.round(ATTRIBUTE_sampleRate/frequencies[MidiNoteNumber]);
    }

}
