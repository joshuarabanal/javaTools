package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 */

public class InstrumentModulator {
    public int modAmount;
    public int instModNdx;
    public int sfModDestOper;
    public int sfModAmtSrcOper;
    public int sfModTransOper;

    @Override
    public String toString() {
        return "\n{"
                +"modAmount:"+modAmount
                +"instModNdx:"+instModNdx
                +"sfModDestOper:"+sfModDestOper
                +"sfModAmtSrcOper:"+sfModAmtSrcOper
                +"sfModTransOper:"+sfModTransOper
                +"}";
    }
}
