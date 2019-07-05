package com.playMidi.SoundFont;

import android.content.Context;
import androidx.annotation.AnyRes;
import android.util.Log;

import com.playMidi.SoundFont.io.SoundFontInputStream;
import com.playMidi.SoundFont.io.soundFontInputStream.MetaData;
import com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenModIndcies;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetHeader;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneIndex;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneModulator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.SampleHeaders;
import com.playMidi.SoundFont.soundFontMidiTimbreSet.InstrumentTimbreSet;
import com.playMidi.SoundFont.soundFontMidiTimbreSet.TimbreRange;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Analytics.CrashReporter;

import static com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator.startAddrsOffset;

/**
 * <a href="http://freepats.zenvoid.org/sf2/sfspec24.pdf">specification</a><br>
 * Created by ra on 4/28/2017.
 */
public class   SoundFontMidiTimbreSet implements MidiTimbreSet {

    public MetaData metaData;
    /**
     *<h1> 6.1 Sample Data Format in the smpl Sub-chunk </h1>
     The smpl sub-chunk, if present, contains one or more “samples” of digital audio information in the form of linearly coded
     sixteen bit, signed, little endian (least significant byte first) words. Each sample is followed by a minimum of forty-six zero
     valued sample data points. These zero valued data points are necessary to guarantee that any reasonable upward pitch shift
     using any reasonable interpolator can loop on zero data at the end of the sound.
     */
    public short[] rawSamples;
    public InstrumentTimbreSet[] instrumentTimbreSets;
    private int frameRate;

    public SoundFontMidiTimbreSet(MetaData metas,short[] samples, InstrumentTimbreSet[] instruments){
        this.metaData = metas;
        this.rawSamples = samples;
        this.instrumentTimbreSets = instruments;
    }
    public SoundFontMidiTimbreSet(File f, boolean availableMemory) throws IOException {
            this(new FileInputStream(f),availableMemory);
        Log.e("memory allocated", "new sound font midi timbre set created");
    }
    public SoundFontMidiTimbreSet(Context c, @AnyRes int resId, boolean availableMemory) throws IOException {
        this(c.getResources().openRawResource(resId),availableMemory);
    }
    /*
        @param availableMemory the maximum amount of memory in bytes that is wanted to be used
     */
    public SoundFontMidiTimbreSet(InputStream in, boolean lowMemory)throws IOException{
        long time = System.currentTimeMillis();
        SoundFontInputStream.readSoundFont(in,this, lowMemory);
        Log.i("opened soundfont", "in "+((System.currentTimeMillis()-time)/1000)+" seconds");
    }


    public void save(File f){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public SampleMetas getSampleMetas(){
        SampleMetas retu = new SampleMetas();
        retu.zoneGeneratorsPerZone = new ArrayList<>();
        retu.allInstrumentZoneModulators = new ArrayList<>();
        retu.allInstrumentZones = new ArrayList<InstrumentGenModIndcies>();
        retu.instrumentNames = new ArrayList<String>();
        retu.instrumentZonIndices = new ArrayList<Integer>();
        retu.sampleHeadersList = new ArrayList<>();
        retu.presets = new ArrayList<>();

        retu.zoneModulators = new ArrayList<PresetZoneModulator> ();
        retu.presetZones = new ArrayList<PresetZoneIndex> ();
        retu.zoneGenerators = new ArrayList<PresetZoneGenerator>();

        for(int i = 0; i<instrumentTimbreSets.length; i++){
            InstrumentTimbreSet inst = instrumentTimbreSets[i];
            inst.dumpValues(retu);
        }


        retu.instrumentNames.add("EOI");

        SampleHeaders sh = new SampleHeaders(
                "EOS",
                0,//start
                0,//end
                0,//startloop
                0,//endloop
                0,//samplerate
                0,//original pitch
                0,// pitchCorrection
                0,//sampleLink
                "null"// fSampleType
            );
       // sh.hSampleName = "EOS";     sh.start = 0;       sh.end = 0;             sh.startLoop = 0;
       // sh.endLoop = 0;             sh.sampleRate = 0;  sh.originalPitch = 0;   sh.pitchCorrection = 0;
       // sh.sampleLink = 0;          sh.fSampleType = "null";
        retu.sampleHeadersList.add(sh);


        PresetHeader pre = new PresetHeader();
        pre.name = "EOP";   pre.MIDI_number = 255;  pre.bank = 255;     pre.presetZones_index = retu.presetZones.size();
        pre.library = 0;    pre.genre = 0;          pre.morphology = 0;
        retu.presets.add(pre);

        PresetZoneIndex p = new PresetZoneIndex();
        p.generatorIndex = retu.zoneGenerators.size();
        p.modulatorIndex = retu.zoneModulators.size();
        retu.presetZones.add(p);

        InstrumentGenerator igen = new InstrumentGenerator();
        igen.sfGenOper = startAddrsOffset;
        igen.genAmount_byLO = 0;
        igen.genAmount_byHI = 0;
        retu.zoneGeneratorsPerZone.add(igen);



        return retu;
    }
    public void setSampleMetas(SampleMetas sampleMetas) {
        //sampleMetas.LogValues();
            ArrayList<TimbreRange> timbres = new ArrayList<TimbreRange>(sampleMetas.allInstrumentZones.size() - 1);

            for (int i = 0; i < sampleMetas.allInstrumentZones.size() - 1; i++) {
                TimbreRange tr = new TimbreRange(sampleMetas, i, this.rawSamples);
                timbres.add(tr);
            }
            instrumentTimbreSets = new InstrumentTimbreSet[sampleMetas.presets.size() - 1];
            if(instrumentTimbreSets.length == 0){
                sampleMetas.logValues();
                throw new IndexOutOfBoundsException("empty presets");
            }

            for (int i = 0; i < instrumentTimbreSets.length; i++) {
                try {
                    instrumentTimbreSets[i] = new InstrumentTimbreSet(
                            sampleMetas.instrumentNames.get(i),
                            sampleMetas.presets.get(i),
                            timbres.subList(
                                    sampleMetas.instrumentZonIndices.get(i),
                                    sampleMetas.instrumentZonIndices.get(i + 1)
                            ),
                            sampleMetas.zoneModulators.subList(
                                    sampleMetas.presetZones.get(sampleMetas.presets.get(i).presetZones_index).modulatorIndex,
                                    sampleMetas.presetZones.get(sampleMetas.presets.get(i + 1).presetZones_index).modulatorIndex
                            ),
                            sampleMetas.zoneGenerators.subList(
                                    sampleMetas.presetZones.get(sampleMetas.presets.get(i).presetZones_index).generatorIndex,
                                    sampleMetas.presetZones.get(sampleMetas.presets.get(i + 1).presetZones_index).generatorIndex
                            )

                    );
                }
                catch(Exception e){
                    CrashReporter.log("index out of bounds:"+i);
                    if(i+1>=sampleMetas.instrumentZonIndices.size()){
                        CrashReporter.log("instrumentZoneIndicies= "+ i+">="+sampleMetas.instrumentZonIndices.size());
                    }
                    if(i+1>=timbres.size()){
                        CrashReporter.log("timbres = "+ i+">="+timbres.size());
                    }
                    if(i+1>=sampleMetas.zoneModulators.size()){
                        CrashReporter.log("zoneModulators = "+ i+">="+sampleMetas.zoneModulators.size());
                    }
                    CrashReporter.log("sample metas:"+sampleMetas.toString());
                    sampleMetas.logValues();
                    throw e;
                }
            }

    }
    public void setFrameRate(int rate){
        this.frameRate = rate;
    }
    public boolean fillBuffer(int midiNoteNumber, float position, float noteLength, short[] data, float volumeMultipler, int articulation) {
        //Log.i("fill buffer", "note number:"+midiNoteNumber +", data.ATTRIBUTE_length:"+ data.length+", note ATTRIBUTE_length:"+noteLength);
        for(int i = 0; i<instrumentTimbreSets.length; i++){
            TimbreRange t = instrumentTimbreSets[i].getTimbreRangeForMidiNote(midiNoteNumber);
            //t.logValues();
            if(t == null) {
                continue;
            }
            else{
                //og.i("frame chosen", t.getName()+","+t.toString());
                t.setFrameRate(frameRate);
                return t.fillBuffer(midiNoteNumber,position,noteLength,data,volumeMultipler,articulation);

            }
        }
        //Log.i("fill buffer failed","position:"+position+", midi number:"+midiNoteNumber);
            //og.alues();
            CrashReporter.log("midi timber set failed");
            for(int i = 0; i<instrumentTimbreSets.length; i++) {
                CrashReporter.log("instrument number["+i+"]");
                instrumentTimbreSets[i].logValues();
            }
            CrashReporter.sendDefaultErrorReport("failed to find an instrument to sreve the call","needed instrument for midi number:"+midiNoteNumber);
        //throw new IndexOutOfBoundsException("failed to find an instrument to sreve the call");
        return true;

    }
    public int fillReleaseBuffer(int midiNoteNumber, float position, float noteLength, short[] data, float volumeMultipler, int articulation) {
        //og.i("fill buffer", "note number:"+midiNoteNumber +", data.ATTRIBUTE_length:"+ data.ATTRIBUTE_length+", note ATTRIBUTE_length:"+noteLength);
        for(int i = 0; i<instrumentTimbreSets.length; i++){
            TimbreRange t = instrumentTimbreSets[i].getTimbreRangeForMidiNote(midiNoteNumber);
            Log.i("timber range found:", ":"+t.toString());
            if(t == null) {
                continue;
            }
            else{
                //og.i("frame chosen", t.getName()+","+t.toString());
                t.setFrameRate(frameRate);
                return t.fillReleaseBuffer(midiNoteNumber,position,noteLength,data,volumeMultipler,articulation);
            }
        }
        Log.i("fill buffer failed","position:"+position+", midi number:"+midiNoteNumber);
        //og.alues();
        CrashReporter.log("midi timber set failed");
        for(int i = 0; i<instrumentTimbreSets.length; i++) {
            CrashReporter.log("instrument number["+i+"]");
            instrumentTimbreSets[i].logValues();
        }
        CrashReporter.log("needed instrument for midi number:"+midiNoteNumber);
        throw new IndexOutOfBoundsException("failed to find an instrument to sreve the call");

    }

    @Override
    public String toString() {
        logValues();
        return "see logs for details";
    }

    private void logValues(){
        CrashReporter.log("meta data:"+metaData.toString());
        if(instrumentTimbreSets.length == 0){
            CrashReporter.log("instruments:"+ "this instrument is empty");
        }
        for(int i = 0; i<instrumentTimbreSets.length; i++){
            CrashReporter.log("instrument:"+i);
            instrumentTimbreSets[i].logValues();
        }
    }
}
