package com.playMidi.SoundFont.soundFontMidiTimbreSet;


import android.util.Log;

import com.musicxml.noteDataTypes.note.RegularNote;
import com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentModulator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.SampleHeaders;
import com.playMidi.player.Midi.MidiHelperFunctions;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Analytics.CrashReporter;

import static com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator.keyRange;
import static com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator.overridingRootKey;
import static com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator.sampleID;

/**
 * Created by ra on 5/8/2017.
 */

public class TimbreRange  implements MidiTimbreSet {

    private int topKey = -1;
    private int bottomKey = -1;
    private int baseKey = -1;
    private SampleHeaders sampleInfo;
    private List<InstrumentGenerator> generatorsList;
    private List<InstrumentModulator> modulatorsList;
    private short[] rawFrames;

    public TimbreRange(SampleMetas sampleMetas, int genModIndex, short[] rawFrames) {
        this.rawFrames = rawFrames;
        if(
                sampleMetas.allInstrumentZones.get(genModIndex+1).instGenNdx
                >=
                sampleMetas.zoneGeneratorsPerZone.size()
        ){
            throw  new IndexOutOfBoundsException(
                    "inst gen ATTRIBUTE_index:"+sampleMetas.allInstrumentZones.get(genModIndex+1).instGenNdx+"\n,"
                    +sampleMetas.zoneGeneratorsPerZone.size()+",\n"
                    + sampleMetas.zoneGeneratorsPerZone.toString()
            );
        }



        generatorsList = sampleMetas.zoneGeneratorsPerZone.subList(
                sampleMetas.allInstrumentZones.get(genModIndex).instGenNdx,
                sampleMetas.allInstrumentZones.get(genModIndex+1).instGenNdx
        );
        modulatorsList = sampleMetas.allInstrumentZoneModulators.subList(
                sampleMetas.allInstrumentZones.get(genModIndex).instModNdx,
                sampleMetas.allInstrumentZones.get(genModIndex+1).instModNdx
        );

        for(int y = 0; y<generatorsList.size(); y++){
            if(generatorsList.get(y).sfGenOper == keyRange){
                InstrumentGenerator ig = generatorsList.get(y);
                if(topKey != -1){
                    CrashReporter.sendDefaultErrorReport("failed to read the timber range","generators: "+generatorsList.toString());
                }
                topKey = ig.genAmount_byHI;
                bottomKey = ig.genAmount_byLO;
            }
            else if(generatorsList.get(y).sfGenOper == sampleID){
                InstrumentGenerator ig = generatorsList.get(y);
                int sampleId = ig.genAmount_byLO;
                this.sampleInfo =
                        sampleMetas.
                                sampleHeadersList.
                                get(sampleId);
            }
            else if(generatorsList.get(y).sfGenOper == overridingRootKey){
                InstrumentGenerator ig = generatorsList.get(y);
                baseKey = ig.genAmount_byLO;
            }
        }

        if(baseKey == -1 && generatorsList.get(generatorsList.size()-1).sfGenOper == sampleID){
            baseKey = sampleInfo.originalPitch;
        }
        if(sampleInfo == null){
            topKey = -1;
            bottomKey = -1;
        }
        /**
        else if(genModIndex!=0 && baseKey == -1){
            CrashReporter.log("gen mod index:"+genModIndex);
            CrashReporter.log("timberrange.generatorsList:"+generatorsList);
            sampleMetas.LogValues();
            throw new NullPointerException();
        }
         **/
        Log.i("new timber range", "allocated");
        this.logValues();

    }
    public TimbreRange(int topKey, int bottomKey, int baseKey, SampleHeaders sampleInfo, short[] frames){
        if(baseKey == -1){
            throw new NullPointerException();
        }
        this.topKey = topKey;
        this.bottomKey  =bottomKey;
        this.baseKey = baseKey;
        this.sampleInfo = sampleInfo;
        this.rawFrames = frames;

    }




    /**
     * returns true if the Timbre range can handle the specified midi note
     **/
    public boolean canHandleMidiNote(int noteNumber){
        if(bottomKey<0 || topKey<0){ return false; }
        if(noteNumber>=bottomKey && noteNumber <= topKey){
            return true;
        }
        else{
            return false;
        }
    }
    private float quadraticInterpolation(double value){

        int floor = (int)value;
        if(floor+2>=rawFrames.length){
            return linearInterpolation(value);
        }
        short one = rawFrames[floor], two = rawFrames[floor+1], three = rawFrames[floor+2];
        double diff = value-floor;
        return
                (float)(
                    (diff*diff*((one-(2*two)+three)/2))//ax^2
                    -(diff*(three-(4*two)+(3*one))/2)//bx
                    +one//c
                );
    }
    private float linearInterpolation(double value){
        if(((int)value)+1>= rawFrames.length){
            return stepInterpolation(value);
        }
        double diff = value-((int)value);
        return (float)
                (
                        rawFrames[(int)value]+
                        (diff*(rawFrames[(int) Math.ceil(value)]-rawFrames[(int)value]))
                );
    }
    private float stepInterpolation(double value){
        //return rawFrames[ (int) (inputStart+(change*inputIncrement))];
        return rawFrames[(int) value];
    }

    /**
     *
     * @param inputStart
     * @param inputEnd
     * @param inputIncrement
     * @param output
     * @param outputStart
     * @return the change in output position
     */
    private int fillbuffer(double inputStart,float inputEnd, float inputIncrement, short[] output, int outputStart, float decayEnvelope){
        int change = 0;
        //float origInStart = inputStart;
        //int origOutStart = outputStart;
        int endOfSection = (int)( (inputEnd-inputStart)/inputIncrement );
        while(change<endOfSection && outputStart<output.length){
            output[outputStart] +=
                (short) (
                        (decayEnvelope)*quadraticInterpolation(inputStart+(change*inputIncrement))
                );
            outputStart++;
            change ++;
        }
        return change;
    }
    private int frameRate = 44100;
    public void setFrameRate(int rate){ frameRate = rate;}
    public boolean fillBuffer(int midiNoteNumber, float position, float noteLength, short[] data, float volumeMultiplier, int articulation){
        double newWavelength = MidiHelperFunctions.getWavelength(midiNoteNumber,frameRate);
        double rawWavelength = MidiHelperFunctions.getWavelength(baseKey,sampleInfo.getSampleRate());
        //float rawToOutputFramesConverter =  newWavelength/rawWavelength;//rawWavelength/newWavelength;
        float outputToRawConverter = (float) (rawWavelength/newWavelength);


        //outputToRawConverter = 0.1f;
        //rawToOutputFramesConverter = 10f;
        //write attack
        int index = 0;
        float rawPosition = sampleInfo.getStart()+(position*outputToRawConverter);

        if(rawPosition<sampleInfo.getEndLoop()){
            float attackEnvelope = 1.0f;
            if(articulation == RegularNote.Articulation_accent){ //accent notes have
                attackEnvelope  = 1.5f-
                        ((1.25f*(rawPosition-sampleInfo.getStart()))/(sampleInfo.getStartLoop()-sampleInfo.getStart()));
                if(attackEnvelope<0.25){ attackEnvelope = 0.25f; }
                //attackEnvelope = 1.0f;
            }
            else if(articulation == RegularNote.Articulation_rooftop){ //rooftop staccato has rounded volume
                attackEnvelope = (rawPosition/(sampleInfo.getEndLoop()-sampleInfo.getStart()));
            }
            attackEnvelope *=volumeMultiplier;
            float change = fillbuffer(rawPosition, sampleInfo.getEndLoop(),outputToRawConverter, data, 0,attackEnvelope);
            rawPosition+=change*outputToRawConverter;
            index+=change;
            //og.i("attack", "midiNoteNumber:"+midiNoteNumber+", position:"+(position/data.length));
        }



        //write sustain
        float rawNoteLength = noteLength*outputToRawConverter;
        float rawEndOfSustain = (rawNoteLength+sampleInfo.getStart()) - (sampleInfo.getEnd()-sampleInfo.getEndLoop());
            int singleLoopLength = sampleInfo.getEndLoop()-sampleInfo.getStartLoop();
            float firstLoopStart = rawPosition-(sampleInfo.getEndLoop());
            int AlreadyPassedFrames = (int)(firstLoopStart/singleLoopLength);

        float sustainEnvelope = 1.0f-(0.001f*AlreadyPassedFrames);
            if(articulation == RegularNote.Articulation_legato || articulation == RegularNote.Articulation_rooftop){//legatto and rooftop sound does not decay
                sustainEnvelope = 1;
            }
            else if(articulation == RegularNote.Articulation_accent){
                sustainEnvelope*=0.25f-(0.001f*AlreadyPassedFrames);
            }
        sustainEnvelope *=volumeMultiplier;

        if(index<data.length && rawPosition<rawEndOfSustain && sustainEnvelope>0){
            //og.i("sustain", "midiNoteNumber:"+midiNoteNumber+", position:"+(position/data.length));
            firstLoopStart = firstLoopStart % singleLoopLength;

            //set first loop
            float change = fillbuffer(sampleInfo.getStartLoop()+firstLoopStart, sampleInfo.getEndLoop(),outputToRawConverter, data, index, volumeMultiplier*sustainEnvelope);
            rawPosition+=change*outputToRawConverter;
            index+=change;
            AlreadyPassedFrames++;

            //handle rest of loops
            while(rawPosition<rawEndOfSustain && index<data.length){
                change = fillbuffer(sampleInfo.getStartLoop(), sampleInfo.getEndLoop(),outputToRawConverter, data, index,volumeMultiplier* sustainEnvelope );
                //data[ATTRIBUTE_index] = Short.MIN_VALUE;
                rawPosition+=change*outputToRawConverter;
                index+=change;
                AlreadyPassedFrames++;
            }
            //data[0] = Short.MAX_VALUE;


        }



        //write release
        float releasePosition = (position-noteLength)*outputToRawConverter;
        float rawReleasePosition = sampleInfo.getEnd()-sampleInfo.getEndLoop();
        if(index<data.length && sustainEnvelope>=0){//if still needs buffer filled and we have not sustained down to 0
            //og.i("release", "midiNoteNumber:"+midiNoteNumber+", position:"+(position/data.length)+", note length:"+noteLength);
            index+=fillbuffer(sampleInfo.getEndLoop()+releasePosition, sampleInfo.getEnd(),outputToRawConverter, data, index, volumeMultiplier*sustainEnvelope );

        }


        //_________________________________________________

        if(
                position>noteLength &&//already finished note
                        (
                            releasePosition > rawReleasePosition || //already finished release
                                    rawNoteLength<sampleInfo.getEnd()-sampleInfo.getStart()//note not long enough for sustain
                        )
                ){
           /* Log.i(
                    "note complete",
                    "note:"+midiNoteNumber+
                            ", position:"+(position)+
                            ", note length:"+noteLength+
                            ", output-raw-converter:"+outputToRawConverter+
                            ", note length ratio:"+(rawNoteLength/(sampleInfo.getEnd()-sampleInfo.getStart()))
                            +", release position:"+releasePosition+", raw release position:"+rawReleasePosition
            );
            */
            return true;
        }
        return false;



    }

    /**
     * writes the release noise to buffer
     * @param midiNoteNumber
     * @param position
     * @param noteLength
     * @param buffer
     * @param volumeMultiplier
     * @param articulation
     * @return number of frames written
     */
    public int fillReleaseBuffer(int midiNoteNumber, float position, float noteLength, short[] buffer, float volumeMultiplier, int articulation){
        double newWavelength = MidiHelperFunctions.getWavelength(midiNoteNumber,frameRate);
        double rawWavelength = MidiHelperFunctions.getWavelength(baseKey,sampleInfo.getSampleRate());
        //float rawToOutputFramesConverter =  newWavelength/rawWavelength;//rawWavelength/newWavelength;
        float outputToRawConverter = (float) (rawWavelength/newWavelength);
        float rawPosition = sampleInfo.getStart()+(position*outputToRawConverter);
        float firstLoopStart = rawPosition-(sampleInfo.getEndLoop());
        int singleLoopLength = sampleInfo.getEndLoop()-sampleInfo.getStartLoop();
        int AlreadyPassedFrames = (int)(firstLoopStart/singleLoopLength);
        float sustainEnvelope = 1.0f-(0.001f*AlreadyPassedFrames);
        int howMany = fillbuffer(sampleInfo.getEndLoop(), sampleInfo.getEnd(),outputToRawConverter, buffer, 0, volumeMultiplier*sustainEnvelope );
        return howMany;
    }

    public String getName() {
        if(sampleInfo == null){
            return "no sample ascociated";
        }
        return sampleInfo.hSampleName;
    }
    public String toString(){
        return "{\n ATTRIBUTE_name:"+getName()+",\n"
                +"topKey:"+topKey+",\n"
                +"bottom key:"+bottomKey+",\n"
                +"baseKey:"+baseKey+",\n"
                +"sampleInfo:"+sampleInfo+",\n"
                +"generatorsList:"+generatorsList+",\n"
                +"modulatorsList:"+modulatorsList+",\n"
                +"}";
    }

    private void dumpSampleHeaders(ArrayList<SampleHeaders> out){
        if(sampleInfo == null){ return; }
        out.add(sampleInfo);
    }
    private void dumpModulators(ArrayList<InstrumentModulator> items) {
        if(modulatorsList == null){
            return;//TODO fix this
        }
            items.addAll(modulatorsList);
    }

    /**
     * dumps:<br>
     *     {@link SampleMetas#sampleHeadersList}
     *     {@link SampleMetas#allInstrumentZoneModulators}
     *     {@link SampleMetas#zoneGeneratorsPerZone}
     * @param retu
     */
    public void dump(SampleMetas retu){
        dumpSampleHeaders(retu.sampleHeadersList);
        dumpModulators(retu.allInstrumentZoneModulators);
        dumpGenerators(retu.zoneGeneratorsPerZone, retu.sampleHeadersList);
    }
    private void dumpUnloadedGenerators(ArrayList<InstrumentGenerator> items, ArrayList<SampleHeaders> headers){

        InstrumentGenerator instGen = new InstrumentGenerator();
        instGen.sfGenOper = sampleID;
        instGen.genAmount_byHI = 0;
        instGen.genAmount_byLO = headers.size()-1;
        items.add(instGen);

        instGen = new InstrumentGenerator();
        instGen.sfGenOper = overridingRootKey;
        instGen.genAmount_byHI = 0;
        instGen.genAmount_byLO = baseKey;
        items.add(instGen);

        instGen = new InstrumentGenerator();
        instGen.sfGenOper = keyRange;
        instGen.genAmount_byHI = topKey;
        instGen.genAmount_byLO = bottomKey;
        items.add(instGen);


    }
    private void dumpGenerators(ArrayList<InstrumentGenerator> items, ArrayList<SampleHeaders> headers) {
        if(generatorsList == null){
            dumpUnloadedGenerators(items,headers);
            return;
        }
        items.addAll(generatorsList);
    }

    public void logValues() {
        CrashReporter.log("logging values of timbreset");
        if(sampleInfo!=null){
            CrashReporter.log("timbrset:"+ sampleInfo.hSampleName);
            CrashReporter.log("frame rate:"+sampleInfo.getSampleRate());
        }
        else{
            CrashReporter.log("timbreset:"+ "null");
        }
        CrashReporter.log("key range:"+ this.bottomKey +"=>"+this.topKey);
        CrashReporter.log("base key:"+baseKey);
    }
}
