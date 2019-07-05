package com.playMidi.xml.functions;

import com.playMidi.SoundFont.io.soundFontInputStream.metaData.SampleHeaders;
import com.playMidi.SoundFont.soundFontMidiTimbreSet.TimbreRange;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

import com.playMidi.player.Midi.MidiHelperFunctions;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ra on 18/05/2017.
 */

public class TimbreSet implements Function {
    private String instrumentName;
    /**
     * ATTRIBUTE_length of sound measures in waves
     */private int length = -1;
    /**
     * wave to loop in the data
     */private int loopPoint = -1;
    /**
     * minimum midi note number supported by this timbreset
     */private int minKey = Integer.MAX_VALUE;
    /**
     * the maximum midi note number this timbre set can hadle
     */private int maxKey = Integer.MIN_VALUE;
    /**
     * the key that this timbreset was created from
     */private int rootKey = -1;
    /** output ATTRIBUTE_sampleRate **/ private int sampleRate = 0;


    ArrayList<Overtone> temp = new ArrayList<Overtone>();
    private Overtone[] overtones;

    public TimbreSet(NameValuePairList attrs){
        for(int i = 0; i<attrs.size(); i++){
            switch(attrs.get(i).getNameIndex()){
                case XmlMidiTimbreSet.attribute_name:
                    instrumentName = attrs.get(i).getValue();
                    break;

                case XmlMidiTimbreSet.attribute_length:
                    length = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;

                case XmlMidiTimbreSet.attribute_loop:
                    loopPoint = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;

                case XmlMidiTimbreSet.attribute_minKey:
                    minKey = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;

                case XmlMidiTimbreSet.attribute_maxKey:
                    maxKey = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;

                case XmlMidiTimbreSet.attribute_rootKey:
                    rootKey = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;

                case XmlMidiTimbreSet.attribute_sampleRate:
                    sampleRate = (int)Float.parseFloat(attrs.get(i).getValue());
                    break;
            }
        }
    }

    public int getLength(){ return (int) (length*MidiHelperFunctions.getWavelength(rootKey,sampleRate)); }
    private int getLoopPoint(){ return (int) (loopPoint*MidiHelperFunctions.getWavelength(rootKey,sampleRate)); }
   /* public TimbreRange toTimbreRange(short[] buffer, int startPosition){
        SampleHeaders sampleInfo = new SampleHeaders(
                instrumentName,
                startPosition,
                startPosition+getLength(),
                startPosition+getLoopPoint(),
                (int) (startPosition+getLoopPoint()+MidiHelperFunctions.getWavelength(rootKey,sampleRate)),
                sampleRate,
                rootKey,
                0,
                0,
                "mono"
        );
        return new TimbreRange(maxKey, minKey, rootKey, sampleInfo, buffer);
    }*/
    public short[] getSample(){

        short[] retu = new short[getLength()];
        for(int i = 0; i<retu.length; i++){ retu[i] = 0; }

        int wavelength = (int) MidiHelperFunctions.getWavelength(rootKey, sampleRate);
        double frequency = (2*Math.PI)/wavelength;
        short[] wave = new short[wavelength+1];
        for(int i = 0; i<wave.length; i++){
            wave[i] = (short)(Short.MAX_VALUE* Math.sin( frequency *i ));
        }

        fillBuffer(rootKey,wave, retu,wavelength ,0);
        return retu;
    }
    public String getName(){ return instrumentName;}

    @Override
    public double valueAt(int x) {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {
        temp.add((Overtone)f);
    }

    @Override
    public void closeElement() throws Exception {
        if(temp.size() == 0){ throw new NullPointerException("overtones ATTRIBUTE_length == 0"); }
        overtones = new Overtone[temp.size()];
        for(int i = 0; i<overtones.length; i++){
            overtones[i] = temp.get(i);
        }
        temp.clear();
        temp = null;
    }
    public boolean canHandleMidiNote(int midiNote){
        if(midiNote<=this.maxKey && midiNote>=this.minKey){
            return true;
        }
        return false;
    }
    public void fillBuffer(int wavenumber, short[] baseWave, short[] buffer, int wavelength, int position){
        for(Overtone o : overtones){
            o.fillBuffer(wavenumber,baseWave,buffer, wavelength, position);
        }
    }

}
