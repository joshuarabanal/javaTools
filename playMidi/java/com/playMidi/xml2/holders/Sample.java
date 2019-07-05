package com.playMidi.xml2.holders;

import android.util.Log;

import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.player.Midi.MidiHelperFunctions;
import com.playMidi.xml.functions.Function;
import com.playMidi.xml.functions.Overtone;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import waveFormat.PcmHelpers;
import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

/**
 * Created by ra on 05/06/2017.
 */

public class Sample implements Function {
    private int rootKey;
    private float loopPoint;
    private int sampleRate;
    private float length;
    String name;
    private ArrayList<Overtone> overtones;

    public Sample(NameValuePairList attributes) {
        for(int i = 0; i<attributes.size(); i++){
            NameValuePair attr = attributes.get(i);
            switch (attr.getNameIndex()){
                case XmlMidiTimbreSet.attribute_name:
                    this.name = attr.getValue();
                    break;
                case XmlMidiTimbreSet.attribute_length:
                    this.length = Float.parseFloat(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_loopPoint:
                    this.loopPoint = Float.parseFloat(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_sampleRate:
                    this.sampleRate = Integer.parseInt(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_rootKey:
                    this.rootKey = Integer.parseInt(attr.getValue());
                    break;
            }
        }
    }

    public int getSampleLength(){
        return (int) (length* MidiHelperFunctions.getWavelength(rootKey,sampleRate));
    }

    private static short[] buffer = null;
    private static short[] baseWave = null;
    public void writeSample(OutputStream out) throws IOException {
        long time = System.currentTimeMillis();

        int wavelength = (int) MidiHelperFunctions.getWavelength(this.rootKey ,this.sampleRate);
        if(baseWave == null){
            baseWave = new short[(int) MidiHelperFunctions.getWavelength(1 ,44100)];
        }
        float frequency = (float)((2.0*Math.PI)/wavelength);
        for(int i = 0; i<wavelength; i++){//create sine wave
            baseWave[i] = (short)(Short.MAX_VALUE*Math.sin( frequency*i ));
        }

        if(buffer == null){
            buffer = new short[1028];
        }


        float position = 0;
        int index = 0;
        int length = this.getSampleLength();
        float increment = buffer.length/wavelength;

        while(index<length) {

            //clear buffer
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = 0;
            }

            //fill buffer
            for (int o = 0; o < overtones.size(); o++) {
                overtones.get(o).fillBuffer((int) position, baseWave, buffer, wavelength, index);
                position += increment;
            }


            //write buffer
            if (index + buffer.length < length) {
                PcmHelpers.Short.write(buffer, out);
                index += buffer.length;
            } else {
                int i = 0;
                while (index < length) {
                    out.write(buffer[i] & 0xFF);
                    out.write((buffer[i] >> 8) & 0xff);
                    index++;
                    i++;
                }
            }
        }
        time  = System.currentTimeMillis()-time;
        time /= 1000;
        //og.("sample drawn in", ""+time);
    }


    public static void writeDefaultSHDR(OutputStream out) throws IOException {
        Writer.write_StringOfLength(out, "EOI", 20);
        Writer.writeRiffLength(out, 0);//start
        Writer.writeRiffLength(out, 0);//end
        Writer.writeRiffLength(out,0);//loop start
        Writer.writeRiffLength(out, 0);//loop end
        Writer.writeRiffLength(out, 0);//sampleRate
        out.write(0);//root key
        out.write(0);//pitch corection
        out.write(0);//sample link
        out.write(0);//pitch corection
        Writer.writeWord(out, 0);
    }

    public void writeSHDR(OutputStream out,int start) throws IOException {
        int wl = (int) MidiHelperFunctions.getWavelength(rootKey,sampleRate);
        
        Writer.write_StringOfLength(out, name, 20);
        Writer.writeRiffLength(out, start);//start
        Writer.writeRiffLength(out, start+getSampleLength());//end
        Writer.writeRiffLength(out, (int) (start+(wl*loopPoint)));//loop start
        Writer.writeRiffLength(out, (int) (start+(wl*loopPoint)+wl));//loop end
        Writer.writeRiffLength(out, sampleRate);//sampleRate
        out.write(rootKey);//root key
        out.write(0);//pitch corection
        out.write(0);//sample link
        out.write(0);//pitch corection
        Writer.writeWord(out, 0);
        
    }

    @Override
    public double valueAt(int x) {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {
        if(overtones == null){ overtones = new ArrayList<>(); }
        overtones.add((Overtone) f);
    }

    @Override
    public void closeElement() throws Exception {

    }

}
