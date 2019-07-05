package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 * <h1>7.4 The PMOD Sub-chunk</h1>
 The PMOD sub-chunk is a required sub-chunk listing all preset zone modulators within the SoundFont compatible file. It is
 always a multiple of ten bytes in length, and contains zero or more modulators plus a terminal record according to the
 structure:
 struct sfModList
 <br>
 {
 <br>
 SFModulator sfModSrcOper;
 <br>
 SFGenerator sfModDestOper;
 <br>
 SHORT modAmount;
 <br>
 SFModulator sfModAmtSrcOper;
 <br>
 SFTransform sfModTransOper;
 <br>
 };
 <br>
 The preset zone’s wModNdx points to the first modulator for that preset zone, and the number of modulators present for a
 preset zone is determined by the difference between the next higher preset zone’s wModNdx and the current preset’s
 wModNdx. A difference of zero indicates there are no modulators in this preset zone.
 */

public class PresetZoneModulator {
    public int sourceOperator;
    public int destOperator;
    public int modAmount;
    public int amountSourceOperator;
    public int amountTransformOperator;

    @Override
    public String toString() {
        return
                "\n{"
                +"sourceOperator:"+ModulatorEnumerator.toString(sourceOperator)+","
                +"destOperator:"+destOperator+","
                +"modAmount:"+modAmount+","
                +"amountSourceOperator:"+amountSourceOperator+","
                +"amountTransformOperator:"+amountTransformOperator
                +"}";
    }
}
