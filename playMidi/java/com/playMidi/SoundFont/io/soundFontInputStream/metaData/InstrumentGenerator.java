package com.playMidi.SoundFont.io.soundFontInputStream.metaData;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by ra on 5/5/2017.
 */

public class InstrumentGenerator {
    public static final int
        startAddrsOffset = 0,endAddrsOffset = 1
            ,startloopAddrsOffset = 2,endloopAddrsOffset = 3
            ,startAddrsCoarseOffset = 4,modLfoToPitch = 5
            ,vibLfoToPitch = 6,modEnvToPitch = 7
            ,initialFilterFc = 8,initialFilterQ = 9
            ,modLfoToFilterFc = 10,modEnvToFilterFc = 11
            ,endAddrsCoarseOffset = 12,modLfoToVolume = 13
            ,unused1 = 14,chorusEffectsSend = 15
            ,reverbEffectsSend = 16,pan = 17
            ,unused2 = 18,unused3 = 19
            ,unused4 = 20,delayModLFO = 21
            ,freqModLFO = 22,delayVibLFO = 23
            ,freqVibLFO = 24,delayModEnv = 25
            ,attackModEnv = 26,holdModEnv = 27
            ,decayModEnv = 28,sustainModEnv = 29
            ,releaseModEnv = 30,keynumToModEnvHold = 31
            ,keynumToModEnvDecay = 32,delayVolEnv = 33
            ,attackVolEnv = 34,holdVolEnv = 35
            ,decayVolEnv = 36,sustainVolEnv = 37
            ,releaseVolEnv = 38,keynumToVolEnvHold = 39
            ,keynumToVolEnvDecay = 40,instrument = 41
            ,reserved1 = 42,keyRange = 43
            ,velRange = 44,startloopAddrsCoarseOffset = 45
            ,keynum = 46,velocity = 47
            ,initialAttenuation = 48,reserved2 = 49
            ,endloopAddrsCoarseOffset = 50,coarseTune = 51
            ,fineTune = 52,sampleID = 53
            ,sampleModes = 54,reserved3 = 55
            ,scaleTuning = 56,exclusiveClass = 57
            ,overridingRootKey = 58,unused5 = 59
            ,endOper = 60;
    //public int sfGenOper_type;
    //public boolean sfGenOper_polarity;
    //public boolean sfGenOper_direction;
    //public int sfGenOper_midiContinuousController;
    //public int sfGenOper_index;

    public int genAmount_byLO;
    public int genAmount_byHI;
    public int sfGenOper;

    @Override
    public String toString() {
            return "\n{"
                    +",sfGenOper:"+generatorOperatorToString(this.sfGenOper)
                    +",genAmount_byLO:"+genAmount_byLO
                    +",genAmount_byHI:"+genAmount_byHI
                    +"}";

    }
    public void setGeneratorOperator(String sfGenOper) throws UnsupportedEncodingException {
        switch(sfGenOper){
            case "startAddrsOffset": this.sfGenOper = 0;       break; case "endAddrsOffset": this.sfGenOper = 1;
            break; case "startloopAddrsOffset": this.sfGenOper = 2;   break; case "endloopAddrsOffset": this.sfGenOper = 3;
            break; case "startAddrsCoarseOffset": this.sfGenOper = 4; break; case "modLfoToPitch": this.sfGenOper = 5;
            break; case "vibLfoToPitch": this.sfGenOper = 6;          break; case "modEnvToPitch": this.sfGenOper = 7;
            break; case "initialFilterFc": this.sfGenOper = 8;        break; case "initialFilterQ": this.sfGenOper = 9;
            break; case "modLfoToFilterFc": this.sfGenOper = 10;      break; case "modEnvToFilterFc": this.sfGenOper = 11;
            break; case "endAddrsCoarseOffset": this.sfGenOper = 12;  break; case "modLfoToVolume": this.sfGenOper = 13;
            break; case "unused1": this.sfGenOper = 14;               break; case "chorusEffectsSend": this.sfGenOper = 15;
            break; case "reverbEffectsSend": this.sfGenOper = 16;     break; case "pan": this.sfGenOper = 17;
            break; case "unused2": this.sfGenOper = 18;               break; case "unused3": this.sfGenOper = 19;
            break; case "unused4": this.sfGenOper = 20;               break; case "delayModLFO": this.sfGenOper = 21;
            break; case "freqModLFO": this.sfGenOper = 22;            break; case "delayVibLFO": this.sfGenOper = 23;
            break; case "freqVibLFO": this.sfGenOper = 24;            break; case "delayModEnv": this.sfGenOper = 25;
            break; case "attackModEnv": this.sfGenOper = 26;          break; case "holdModEnv": this.sfGenOper = 27;
            break; case "decayModEnv": this.sfGenOper = 28;           break; case "sustainModEnv": this.sfGenOper = 29;
            break; case "releaseModEnv": this.sfGenOper = 30;         break; case "keynumToModEnvHold": this.sfGenOper = 31;
            break; case "keynumToModEnvDecay": this.sfGenOper = 32;   break; case "delayVolEnv": this.sfGenOper = 33;
            break; case "attackVolEnv": this.sfGenOper = 34;          break; case "holdVolEnv": this.sfGenOper = 35;
            break; case "decayVolEnv": this.sfGenOper = 36;           break; case "sustainVolEnv": this.sfGenOper = 37;
            break; case "releaseVolEnv": this.sfGenOper = 38;         break; case "keynumToVolEnvHold": this.sfGenOper = 39;
            break; case "keynumToVolEnvDecay": this.sfGenOper = 40;   break; case "instrument": this.sfGenOper = 41;
            break; case "reserved1": this.sfGenOper = 42;             break; case "keyRange": this.sfGenOper = 43;
            break; case "velRange": this.sfGenOper = 44;              break; case "startloopAddrsCoarseOffset": this.sfGenOper = 45;
            break; case "keynum": this.sfGenOper = 46;                break; case "velocity": this.sfGenOper = 47;
            break; case "initialAttenuation": this.sfGenOper = 48;    break; case "reserved2": this.sfGenOper = 49;
            break; case "endloopAddrsCoarseOffset": this.sfGenOper = 50;break; case "coarseTune": this.sfGenOper = 51;
            break; case "fineTune": this.sfGenOper = 52;              break; case "sampleID": this.sfGenOper = 53;
            break; case "sampleModes": this.sfGenOper = 54;           break; case "reserved3": this.sfGenOper = 55;
            break; case "scaleTuning": this.sfGenOper = 56;           break; case "exclusiveClass": this.sfGenOper = 57;
            break; case "overridingRootKey": this.sfGenOper = 58;     break; case "unused5": this.sfGenOper = 59;
            break; case "endOper": this.sfGenOper = 60;   break;            default : throw new UnsupportedEncodingException("cannot parse:"+sfGenOper);
        }
    }
    /**
    public int generatorOperatorToInt() throws UnsupportedEncodingException {
        switch(sfGenOper){
            case "startAddrsOffset":return 0;       case "endAddrsOffset":return 1;
            case "startloopAddrsOffset":return 2;   case "endloopAddrsOffset":return 3;
            case "startAddrsCoarseOffset":return 4; case "modLfoToPitch":return 5;
            case "vibLfoToPitch":return 6;          case "modEnvToPitch":return 7;
            case "initialFilterFc":return 8;        case "initialFilterQ":return 9;
            case "modLfoToFilterFc":return 10;      case "modEnvToFilterFc":return 11;
            case "endAddrsCoarseOffset":return 12;  case "modLfoToVolume":return 13;
            case "unused1":return 14;               case "chorusEffectsSend":return 15;
            case "reverbEffectsSend":return 16;     case "pan":return 17;
            case "unused2":return 18;               case "unused3":return 19;
            case "unused4":return 20;               case "delayModLFO":return 21;
            case "freqModLFO":return 22;            case "delayVibLFO":return 23;
            case "freqVibLFO":return 24;            case "delayModEnv":return 25;
            case "attackModEnv":return 26;          case "holdModEnv":return 27;
            case "decayModEnv":return 28;           case "sustainModEnv":return 29;
            case "releaseModEnv":return 30;         case "keynumToModEnvHold":return 31;
            case "keynumToModEnvDecay":return 32;   case "delayVolEnv":return 33;
            case "attackVolEnv":return 34;          case "holdVolEnv":return 35;
            case "decayVolEnv":return 36;           case "sustainVolEnv":return 37;
            case "releaseVolEnv":return 38;         case "keynumToVolEnvHold":return 39;
            case "keynumToVolEnvDecay":return 40;   case "instrument":return 41;
            case "reserved1":return 42;             case "keyRange":return 43;
            case "velRange":return 44;              case "startloopAddrsCoarseOffset":return 45;
            case "keynum":return 46;                case "velocity":return 47;
            case "initialAttenuation":return 48;    case "reserved2":return 49;
            case "endloopAddrsCoarseOffset":return 50;case "coarseTune":return 51;
            case "fineTune":return 52;              case "sampleID":return 53;
            case "sampleModes":return 54;           case "reserved3":return 55;
            case "scaleTuning":return 56;           case "exclusiveClass":return 57;
            case "overridingRootKey":return 58;     case "unused5":return 59;
            case "endOper":return 60;               default : throw new UnsupportedEncodingException("cannot parse:"+sfGenOper);
        }
    }
    **/
    private static String generatorOperatorToString(int oper) {
        switch(oper){
            case 0: return "startAddrsOffset";       case 1: return "endAddrsOffset" ;
            case 2: return "startloopAddrsOffset" ;   case 3: return "endloopAddrsOffset" ;
            case 4: return "startAddrsCoarseOffset" ; case 5: return "modLfoToPitch" ;
            case 6: return "vibLfoToPitch" ;          case 7: return "modEnvToPitch" ;
            case 8: return "initialFilterFc" ;        case 9: return "initialFilterQ";
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
}
