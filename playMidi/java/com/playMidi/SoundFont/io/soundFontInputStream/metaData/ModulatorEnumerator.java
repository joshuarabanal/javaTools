package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

import androidx.annotation.NonNull;

/**
 * Created by ra on 5/5/2017.
 */

public class ModulatorEnumerator {
    private static int getType(int source){
        return source>>10;
    }
    private static boolean getPolarity(int source){
        return (source & 0x200) != 0;
    }
    private static boolean getDirection(int source){
        return (source & 0x100) != 0;
    }
    private static boolean getContinuousControllerFlag(int source){
        return (source & 0x80) != 0;
    }
    private static int getIndex(int source){
        return source & 0x7F;
    }
    @NonNull
    public static String getTypeAsString(int type){
        switch (type){
            case 0: return "startAddrsOffset";
            case 1: return "endAddrsOffset";
            case 2: return "startloopAddrsOffset";
            case 3: return "endloopAddrsOffset";
            case 4: return "startAddrsCoarseOffset";
            case 5: return "modLfoToPitch";
            case 6: return "vibLfoToPitch";
            case 7: return "modEnvToPitch";
            case 8: return "initialFilterFc";
            case 9: return "initialFilterQ";
            case 10: return "modLfoToFilterFc";
            case 11: return "modEnvToFilterFc";
            case 12: return "endAddrsCoarseOffset";
            case 13: return "modLfoToVolume";
            case 14: return "unused1";
            case 15: return "chorusEffectsSend";
            case 16: return "reverbEffectsSend";
            case 17: return "pan";
            case 18: return "unused2";
            case 19: return "unused3";
            case 20: return "unused4";
            case 21: return "delayModLFO";
            case 22: return "freqModLFO";
            case 23: return "delayVibLFO";
            case 24: return "freqVibLFO";
            case 25: return "delayModEnv";
            case 26: return "attackModEnv";
            case 27: return "holdModEnv";
            case 28: return "decayModEnv";
            case 29: return "sustainModEnv";
            case 30: return "releaseModEnv";
            case 31: return "keynumToModEnvHold";
            case 32: return "keynumToModEnvDecay";
            case 33: return "delayVolEnv";
            case 34: return "attackVolEnv";
            case 35: return "holdVolEnv";
            case 36: return "decayVolEnv";
            case 37: return "sustainVolEnv";
            case 38: return "releaseVolEnv";
            case 39: return "keynumToVolEnvHold";
            case 40: return "keynumToVolEnvDecay";
            case 41: return "instrument";
            case 42: return "reserved1";
            case 43: return "keyRange";
            case 44: return "velRange";
            case 45: return "startloopAddrsCoarseOffset";
            case 46: return "keynum";
            case 47: return "velocity";
            case 48: return "initialAttenuation";
            case 49: return "reserved2";
            case 50: return "endloopAddrsCoarseOffset";
            case 51: return "coarseTune";
            case 52: return "fineTune";
            case 53: return "sampleID";
            case 54: return "sampleModes";
            case 55: return "reserved3";
            case 56: return "scaleTuning";
            case 57: return "exclusiveClass";
            case 58: return "overridingRootKey";
            case 59: return "unused5";
            case 60: return "endOper";
            default : throw new IndexOutOfBoundsException("invalid ATTRIBUTE_type:"+type);
        }
    }
    public static String toString(int source){
        return "(Modulator){"
                +"ATTRIBUTE_type:"+getTypeAsString(getType(source))
                +",polarity:"+getPolarity(source)
                +",direction:"+getDirection(source)
                +",CC:"+getContinuousControllerFlag(source)
                +",ATTRIBUTE_index:"+getIndex(source)+
                "}";
    }
}
