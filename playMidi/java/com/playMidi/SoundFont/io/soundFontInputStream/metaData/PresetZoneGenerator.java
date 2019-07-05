package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 */

public class PresetZoneGenerator {
    public int genraterOperator;
    public byte range_low;
    public byte range_high;

    @Override
    public String toString() {
        return "\n{"
                +"genraterOperator:"+ModulatorEnumerator.getTypeAsString(genraterOperator)
                +",range_low:"+range_low
                +",range_high:"+range_high
                +"}";
    }
}
