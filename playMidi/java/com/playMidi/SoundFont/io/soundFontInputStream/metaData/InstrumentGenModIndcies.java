package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 */

public class InstrumentGenModIndcies {
    public int instGenNdx;
    public int instModNdx;

    @Override
    public String toString() {
        return "\n{"
                +"instGenndx:"+instGenNdx
                +"instModndx:"+instModNdx
                +"}";
    }
}
