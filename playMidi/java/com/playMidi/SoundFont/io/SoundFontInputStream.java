package com.playMidi.SoundFont.io;



import android.app.ActivityManager;
import android.util.Log;

import com.playMidi.SoundFont.io.RiffFormat.Reader;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;
import com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenModIndcies;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentModulator;
import com.playMidi.SoundFont.io.soundFontInputStream.MetaData;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetHeader;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneIndex;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.SampleHeaders;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneModulator;
import com.playMidi.xml.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * Created by ra on 4/28/2017.
 */

public class SoundFontInputStream {
    /**
     * reads the input stream and fills the values of the SoundFontMidiTimbreSet
     *
     * @param in
     * @param out
     */
    public static void readSoundFont(InputStream in, SoundFontMidiTimbreSet out, boolean lowMemory) throws IOException {

        BufferedInputStream inst = new BufferedInputStream(in);

        String RiffName = Reader.getRiffName(inst);
        int riffLength =Reader.getRiffLength(inst);
        //og.i("readSoundFont", "file ATTRIBUTE_length:"+(riffLength+4));
        int index = 8;
        if (RiffName.equals("RIFF")) {
            RiffName = Reader.getRiffName(inst);
            if (RiffName.equals("sfbk")) {
                index += readRIFF_SFBK(inst, out, riffLength-4, lowMemory);
            } else {
                throw new IOException("failed to read Riff: expected sfbk, but found:" + RiffName);
            }
        } else {
            throw new IOException("failed to read Riff: expected RIFF, but found:" + RiffName);
        }

    }

    /**
     * this expects the first byte  read to be the first byte of the {@link Reader#getRiffLength(InputStream) riff chunk ATTRIBUTE_length}
     * reads the sfbk chunk and places the data into the out
     *
     * @param out where the data is placed
     * @return
     */
    private static int readRIFF_SFBK(BufferedInputStream in, SoundFontMidiTimbreSet out, int end, boolean lowMemory) throws IOException {

        int index = 0;
        while (index < end) {
            //og.i("index:"+index,"end:"+end);
            String chunkName = Reader.getRiffName(in);
            if (chunkName.equals("LIST")) {
                int length = Reader.getRiffLength(in);
                //while (ATTRIBUTE_index < ATTRIBUTE_length) {
                chunkName = Reader.getRiffName(in);
                index += length+8;
                //og.i("chunk name","LIST"+chunkName+", :"+index+"<"+end);
                switch (chunkName) {
                    case "INFO":
                        //og.i("read info","ATTRIBUTE_length:"+length);
                        readRIFF_SFBK_LIST_INFO_LIST(in, out, length - 4);
                        break;
                    case "sdta":
                        //og.i("read sdta","ATTRIBUTE_length:"+length);
                        readRIFF_SFBK_sdta(in, out, length - 4, lowMemory);
                        break;
                    case "pdta":
                        //og.i("read pdta","ATTRIBUTE_length:"+length);
                        SampleMetas sm = new SampleMetas();
                        readRIFF_SFBK_pdta(in, sm, length - 4,lowMemory);
                        out.setSampleMetas(sm);
                        break;
                    default:
                        throw new IOException("expected chunk Name of:[INFO,stda,pdta], but instead found " + chunkName);
                }
            } else {
                throw new IOException("expected chunk Name of:LIST, but instead found '"
                        + chunkName
                        + "' ,\n remaining elements:"
                        + (end - index)
                        + ". next section:"
                        + Reader.getRiffName(in)
                );
            }
        }
        if (index != end) {
            throw new IOException("error in reading bytes:" + end + " != " + index);
        }
        return index;


    }

    /**
     * @param in
     * @param out
     * @param end ATTRIBUTE_length of the chunk in bytes
     * @return
     * @throws IOException
     */
    private static void readRIFF_SFBK_LIST_INFO_LIST(BufferedInputStream in, SoundFontMidiTimbreSet out, int end) throws IOException {
        MetaData metas = new MetaData();
        int start = 0;
        int repitition = 0;
        while (start < end) {
            String param = Reader.getRiffName(in);
            start += 4;
            int length = Reader.getRiffLength(in);
            start += 4;
            start += length;
            switch (param) {
                case "ifil":
                    if (length != 4) {
                        throw new IOException("unable to read ifil param:"+length +" != 4");
                    }
                    metas.version = (double) in.getWord() + (0.01 * in.getWord());
                    break;
                case "INAM":
                    metas.soundEngine = Reader.getString(in, length);
                    break;
                case "ISFT":
                    metas.creatorApplication = Reader.getString(in, length);
                    break;
                case "isng":
                    metas.targetSoundEngine = Reader.getString(in, length);
                    break;
                case "IPRD":
                    metas.intendedProduct = Reader.getString(in, length);
                    break;
                case "IENG":
                    metas.engineersOfFile = Reader.getString(in, length);
                    break;
                case "ICRD":
                    metas.creationDate = Reader.getString(in, length);
                    break;
                case "ICMT":
                    metas.comments = Reader.getString(in, length);
                    break;
                case "ICOP":
                    metas.copyright = Reader.getString(in, length);
                    break;
                default:


                    throw new IOException("unsupported header ATTRIBUTE_type: " + param + "\n" + metas.toString()+"\n repitition:"+repitition);

            }
            repitition++;
        }
        out.metaData = metas;
        return;
    }

    private static void readRIFF_SFBK_sdta(BufferedInputStream in, SoundFontMidiTimbreSet out, int end, boolean lowMemory) throws IOException {
        int start = 0;
        while (start < end) {
            String name = Reader.getRiffName(in);
            int length = Reader.getRiffLength(in);
            start += 8; //for the 4 bytes read for the ATTRIBUTE_name and ATTRIBUTE_length
            start += length;
            switch (name) {
                case "smpl":
                    //og.i("read smpl length", ""+length);
                    read_stdasmpl(in, out, length, lowMemory);
                    break;
                default:
                    throw new IOException("unsupported sdta sub chunk: " + name + ", " + start + "!=" + end);
            }
        }
        if (start != end) {
            throw new IOException("failed to read whole chunk:" + start + " != " + end);
        }

        //return -1;
    }

    private static void read_stdasmpl(BufferedInputStream in, SoundFontMidiTimbreSet out, int end, boolean lowMemory) throws IOException {

        if(lowMemory){// once this is settled we can implement it
            short[] samples = new short[end/4];
            short[] buffer = new short[2];
            for(int i = 0; i<samples.length; i++){
                in.read(buffer);
                samples[i] = (short)((buffer[0] + buffer[1])/2);
            }
            out.rawSamples = samples;
        }
        else {
            short[] samples = new short[(end / 2)];
            long time = System.currentTimeMillis();
            in.read(samples);
            Log.e("read sample", "lasted:" + ((System.currentTimeMillis() - time) / 1000f) + " seconds");
            out.rawSamples = samples;
        }


    }


    private static void readRIFF_SFBK_pdta(BufferedInputStream in, SampleMetas sampleMetas, int end, boolean lowMemory) throws IOException {

        int index = 0;
        while (index < end) {
            String name = Reader.getRiffName(in);
            int length = Reader.getRiffLength(in);
            index += length+8;
            switch (name) {
                case "phdr":
                    readRIFF_SFBK_ptda_phdr(in, sampleMetas, length);break;

                case "pbag":
                    readRIFF_SFBK_ptda_pbag(in, sampleMetas, length);break;

                case "pmod":
                    readRIFF_SFBK_ptda_pmod(in, sampleMetas, length);break;

                case "pgen":
                    readRIFF_SFBK_ptda_pgen(in, sampleMetas, length);break;

                case "inst":
                    readRIFF_SFBK_ptda_inst(in, sampleMetas, length);break;

                case "ibag":
                    readRIFF_SFBK_ptda_ibag(in, sampleMetas, length);break;

                case "imod":
                    readRIFF_SFBK_ptda_imod(in, sampleMetas, length);break;

                case "igen":
                    readRIFF_SFBK_ptda_igen(in, sampleMetas, length);break;

                case "shdr":
                    readRIFF_SFBK_ptda_shdr(in, sampleMetas, length, lowMemory);break;

                default:
                    throw new IOException("unknown sub chunk: " + name +"at:"+index + "!="+end);
            }
        }
        if (index != end) {
            throw new IOException("index!=end:"+index+"!="+end);
        }

    }
/*
contains midi numbers, bank, zones index, genre, morphology
 */
    private static void readRIFF_SFBK_ptda_phdr(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        int start = 0;
        ArrayList<PresetHeader> presetList = new ArrayList<PresetHeader>();
        while (start < end) {
            PresetHeader preset = new PresetHeader();
            preset.name = Reader.getString(in, 20).trim();
            start += 20;
            preset.MIDI_number = in.getWord();
            start += 2;
            preset.bank = in.getWord();
            start += 2;
            preset.presetZones_index = in.getWord();
            start += 2;
            preset.library = Reader.getRiffLength(in);
            start += 4;
            preset.genre = Reader.getRiffLength(in);
            start += 4;
            preset.morphology = Reader.getRiffLength(in);
            start += 4;
            presetList.add(preset);
        }
        sampleMetas.presets = presetList;
        if (start != end) {
            throw new IOException(start+"!="+end);
        }
    }

    private static void readRIFF_SFBK_ptda_pbag(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        ArrayList<PresetZoneIndex> barList = new ArrayList<>();
        int start = 0;
        while (start < end) {
            PresetZoneIndex zone = new PresetZoneIndex();
            zone.generatorIndex = in.getWord();
            start += 2;
            zone.modulatorIndex = in.getWord();
            start += 2;
            barList.add(zone);
        }
        sampleMetas.presetZones = barList;
        if (start != end) {
            throw new IOException("exception:" + end + "-" + start + "!=" + end);
        }
    }

    private static void readRIFF_SFBK_ptda_pmod(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        ArrayList<PresetZoneModulator> modulatorList = new ArrayList<PresetZoneModulator>();
        int start = 0;
        while (start < end) {
            PresetZoneModulator modulator = new PresetZoneModulator();
            modulator.sourceOperator = in.getWord();
            start += 2;
            modulator.destOperator = in.getWord();
            start += 2;
            modulator.modAmount = in.getWord();
            start += 2;
            modulator.amountSourceOperator = in.getWord();
            start += 2;
            modulator.amountTransformOperator = in.getWord();
            start += 2;
            modulatorList.add(modulator);
        }
        sampleMetas.zoneModulators = modulatorList;
        if (start != end) {
            throw new IOException(start+"!="+end);
        }
    }

    private static void readRIFF_SFBK_ptda_pgen(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        ArrayList<PresetZoneGenerator> zoneGenerators = new ArrayList<PresetZoneGenerator>();
        int start = 0;
        while (start < end) {
            PresetZoneGenerator zoneGen = new PresetZoneGenerator();
            zoneGen.genraterOperator = in.getWord();
            start += 2;
            zoneGen.range_low = (byte) in.read();
            start++;
            zoneGen.range_high = (byte) in.read();
            start++;
            zoneGenerators.add(zoneGen);
        }
        sampleMetas.zoneGenerators = zoneGenerators;
        if (start != end) {
            throw new IOException("exception");
        }
    }

    private static void readRIFF_SFBK_ptda_inst(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        ArrayList<String> instruments = new ArrayList<String>();
        ArrayList<Integer> zoneIndices = new ArrayList<>();
        int start = 0;
        while (start < end) {
            instruments.add(Reader.getString(in, 20).trim());
            start += 20;
            zoneIndices.add(in.getWord());
            start += 2;
        }

        sampleMetas.instrumentNames = instruments;
        sampleMetas.instrumentZonIndices = zoneIndices;
        if (start != end) {
            throw new IOException("exception");
        }
    }

    private static void readRIFF_SFBK_ptda_ibag(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        ArrayList<InstrumentGenModIndcies> instruments = new ArrayList<InstrumentGenModIndcies>();
        int start = 0;
        while (start < end) {
            InstrumentGenModIndcies zone = new InstrumentGenModIndcies();
            zone.instGenNdx = in.getWord();
            start += 2;
            zone.instModNdx = in.getWord();
            start += 2;
            instruments.add(zone);
        }
        sampleMetas.allInstrumentZones = instruments;
        if (start != end) {
            throw new IOException("exception");
        }
    }

    private static void readRIFF_SFBK_ptda_imod(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        int start = 0;
        ArrayList<InstrumentModulator> instruments = new ArrayList<InstrumentModulator>();
        while (start < end) {
            InstrumentModulator zone = new InstrumentModulator();
            zone.modAmount = in.getWord();
            start += 2;
            zone.instModNdx = in.getWord();
            start += 2;
            zone.sfModDestOper = in.getWord();
            start += 2;
            zone.sfModAmtSrcOper = in.getWord();
            start += 2;
            zone.sfModTransOper = in.getWord();
            start += 2;
            instruments.add(zone);
        }
        sampleMetas.allInstrumentZoneModulators = instruments;
        if (start != end) {
            throw new IOException( start + "!=" + end);
        }
    }
private static String generatorOperatorToString(BufferedInputStream in) throws IOException {
    int oper = in.getWord();
    switch(oper){
        case 0: return "startAddrsOffset";       case 1: return "endAddrsOffset";
        case 2: return "startloopAddrsOffset";   case 3: return "endloopAddrsOffset";
        case 4: return "startAddrsCoarseOffset"; case 5: return "modLfoToPitch";
        case 6: return "vibLfoToPitch";          case 7: return "modEnvToPitch";
        case 8: return "initialFilterFc";        case 9: return "initialFilterQ";
        case 10: return "modLfoToFilterFc";      case 11: return "modEnvToFilterFc";
        case 12: return "endAddrsCoarseOffset";  case 13: return "modLfoToVolume";
        case 14: return "unused1";               case 15: return "chorusEffectsSend";
        case 16: return "reverbEffectsSend";     case 17: return "pan";
        case 18: return "unused2";               case 19: return "unused3";
        case 20: return "unused4";               case 21: return "delayModLFO";
        case 22: return "freqModLFO";            case 23: return "delayVibLFO";
        case 24: return "freqVibLFO";            case 25: return "delayModEnv";
        case 26: return "attackModEnv";          case 27: return "holdModEnv";
        case 28: return "decayModEnv";           case 29: return "sustainModEnv";
        case 30: return "releaseModEnv";         case 31: return "keynumToModEnvHold";
        case 32: return "keynumToModEnvDecay";   case 33: return "delayVolEnv";
        case 34: return "attackVolEnv";          case 35: return "holdVolEnv";
        case 36: return "decayVolEnv";           case 37: return "sustainVolEnv";
        case 38: return "releaseVolEnv";         case 39: return "keynumToVolEnvHold";
        case 40: return "keynumToVolEnvDecay";   case 41: return "instrument";
        case 42: return "reserved1";             case 43: return "keyRange";
        case 44: return "velRange";              case 45: return "startloopAddrsCoarseOffset";
        case 46: return "keynum";                case 47: return "velocity";
        case 48: return "initialAttenuation";    case 49: return "reserved2";
        case 50: return "endloopAddrsCoarseOffset";case 51: return "coarseTune";
        case 52: return "fineTune";              case 53: return "sampleID";
        case 54: return "sampleModes";           case 55: return "reserved3";
        case 56: return "scaleTuning";           case 57: return "exclusiveClass";
        case 58: return "overridingRootKey";     case 59: return "unused5";
        case 60: return "endOper";               default: return "unknown:"+oper;
    }
}
    private static void readRIFF_SFBK_ptda_igen(BufferedInputStream in, SampleMetas sampleMetas, int end) throws IOException {
        int start = 0;
        ArrayList<InstrumentGenerator> instruments = new ArrayList<InstrumentGenerator>();
        while (start < end) {
            InstrumentGenerator zone = new InstrumentGenerator();
            zone.sfGenOper = in.getWord();
            start += 2;
            zone.genAmount_byLO = in.read();
            zone.genAmount_byHI = in.read();//get_WORD(array, start);
            start += 2;
            instruments.add(zone);
        }
        sampleMetas.zoneGeneratorsPerZone = instruments;
        if (start != end) {
            throw new IOException("exception");
        }
    }

    private static void readRIFF_SFBK_ptda_shdr(BufferedInputStream in, SampleMetas sampleMetas, int end,boolean lowMemory) throws IOException {
        if(end%46 != 0){ throw new IOException("the chunk is not a multiple of 46 bytes:"+end); }
        ArrayList<SampleHeaders> list = new ArrayList<SampleHeaders>();
        int index = 0;
        while(index<end){
            String hSampleName = Reader.getString(in, 20).trim();index += 20;
            int hstart = Reader.getRiffLength(in);index += 4;
            int hend = Reader.getRiffLength(in);index += 4;
            int hstartLoop = Reader.getRiffLength(in);index += 4;
            int hendLoop = Reader.getRiffLength(in);index += 4;
            int hsampleRate = Reader.getRiffLength(in);index += 4;
            int horiginalPitch = in.read();index++;
            int hpitchCorrection = in.read();index++;
            int hsampleLink = in.getWord(); index += 2;

            String sampleTYPE = "";
            int sampleType = in.read(); index++;
            int rom = in.read(); index++;
            if (rom == 0x80) {
                sampleTYPE = "rom ";
            }

            SampleHeaders obj = new SampleHeaders(
                    hSampleName,
                    hstart,
                    hend,
                    hstartLoop,
                    hendLoop,
                    hsampleRate,
                    horiginalPitch,
                    hpitchCorrection,
                    hsampleLink,
                    sampleTYPE
            );
            Log.i("new sample header",hSampleName);
            if(lowMemory){ //once this is settled we can implement it
                obj.setStart(obj.getStart()/2);
                obj.setEnd(obj.getEnd()/2);
                obj.setStartLoop(obj.getStartLoop()/2);
                obj.setEndLoop(obj.getEndLoop()/2);
                obj.setSampleRate (obj.getSampleRate()/2);
            }




            obj.fSampleType = sampleTYPE;
            list.add(obj);
        }
        sampleMetas.sampleHeadersList = list;
        if (index != end) {
            throw new IOException(index +"!="+end);
        }
    }
}
