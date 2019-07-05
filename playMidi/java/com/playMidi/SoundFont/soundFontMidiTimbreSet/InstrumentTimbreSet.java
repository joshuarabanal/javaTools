package com.playMidi.SoundFont.soundFontMidiTimbreSet;


import android.util.Log;

import com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenModIndcies;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetHeader;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneIndex;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneModulator;

import java.util.List;

import Analytics.CrashReporter;

/**
 * Created by ra on 5/8/2017.
 * representation of a single instrument in this midi file
 */

public class InstrumentTimbreSet{
    private String name;
    private List<TimbreRange> timbres;
    private final int midi_number;
    private final int bank;
    private final int library;
    private final int genre;
    private final int morphology;
    private List<PresetZoneModulator> zoneModulators;
    private List<PresetZoneGenerator> zoneGenerators;



    public InstrumentTimbreSet(String name, PresetHeader pre, List<TimbreRange> timbres, List<PresetZoneModulator> mods,List<PresetZoneGenerator> gens){
        this.name = name;
        this.zoneGenerators = gens;
        this.zoneModulators = mods;
        this.midi_number = pre.MIDI_number;
        this.library = pre.library;
        this.bank = pre.bank;
        this.genre = pre.genre;
        this.morphology = pre.morphology;
        this.timbres = timbres;
    }



    public String getInstrumentName(){ return name; }
    public TimbreRange getTimbreRangeForMidiNote(int noteNumber){
        for(int i = 0; i<timbres.size(); i++){
            if(timbres.get(i).canHandleMidiNote(noteNumber)){
                return timbres.get(i);
            }
        }
        return null;
    }

    /**
     * dumps the
     * {@link SampleMetas#zoneGeneratorsPerZone}
     * and {@link SampleMetas#allInstrumentZoneModulators}
     * and {@link SampleMetas#allInstrumentZones}
     * and {@link SampleMetas#instrumentNames}
     * and {@link SampleMetas#instrumentZonIndices}
     * and {@link SampleMetas#sampleHeadersList}
     * @param retu
     */
    public void dumpValues(SampleMetas retu){

        if(retu.allInstrumentZones.size() == 0){
            InstrumentGenModIndcies index = new InstrumentGenModIndcies();
            index.instGenNdx = 0;
            index.instModNdx = 0;
            retu.allInstrumentZones.add(index);
        }if(retu.instrumentZonIndices.size() == 0){
            retu.instrumentZonIndices.add(0);
        }
        PresetHeader pre = new PresetHeader();
        pre.name = name;
        pre.MIDI_number = midi_number;
        pre.bank = bank;
        pre.presetZones_index = retu.presetZones.size();
        pre.library = library;
        pre.genre = genre;
        pre.morphology = morphology;
        retu.presets.add(pre);
        retu.instrumentNames.add(name);

        PresetZoneIndex pzi = new PresetZoneIndex();
        pzi.generatorIndex = retu.zoneGenerators.size();
        pzi.modulatorIndex = retu.zoneModulators.size();
        retu.presetZones.add(pzi);

        for(TimbreRange i : timbres){
            i.dump(retu);
            InstrumentGenModIndcies index = new InstrumentGenModIndcies();
            index.instGenNdx = retu.zoneGeneratorsPerZone.size();
            index.instModNdx = retu.allInstrumentZoneModulators.size();
            retu.allInstrumentZones.add(index);
        }


        retu.zoneGenerators.addAll(zoneGenerators);
        retu.zoneModulators.addAll(zoneModulators);

        int previousVal = retu.instrumentZonIndices.get(retu.instrumentZonIndices.size()-1);
        retu.instrumentZonIndices.add(previousVal + timbres.size());
    }


    public void logValues() {
        CrashReporter.log("Instrument timbreset");
        CrashReporter.log("instrument:"+ name);
        CrashReporter.log("timbres:"+timbres);
        CrashReporter.log("midi number:"+midi_number);
        CrashReporter.log("bank:"+bank);
        CrashReporter.log("library:"+library);
        CrashReporter.log("genre:"+genre);
        CrashReporter.log("morphology:"+morphology);
        CrashReporter.log("zoneModulators:"+zoneModulators);
        CrashReporter.log("zoneGenerators:"+zoneGenerators);

    }
}
