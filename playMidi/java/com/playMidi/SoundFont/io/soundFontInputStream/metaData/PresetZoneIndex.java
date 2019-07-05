package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 *
 * <h1>7.3 The PBAG Sub-chunk</h1>

 The PBAG sub-chunk is a required sub-chunk listing all preset zones within the SoundFont compatible file. It is always a
 multiple of four bytes in length, and contains one record for each preset zone plus one record for a terminal zone according
 to the structure:
 struct sfPresetBag
 <br>
 {
 <br>
 WORD wGenNdx;
 <br>
 WORD wModNdx;
 <br>
 };
 <br>
 The first zone in a given preset is located at that preset’s
 {@link com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas#presets SampleMetas.presets}. The number of zones in the preset is determined
 by the difference between the next preset’s
 {@link com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas#presets SampleMetas.presets}
 and the current
 {@link com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas#presets SampleMetas.presets}
 .
 */

public class PresetZoneIndex {
    /**
     * The WORD wGenNdx is an ATTRIBUTE_index to the preset’s zone list of generators in the PGEN sub-chunk
     * ( {@link com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas#zoneGenerators SampleMetas.zoneGenerators}PresetZoneModulator
     * , and the wModNdx is an
     ATTRIBUTE_index to its list of modulators in the PMOD sub-chunk
     * ( {@link com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas#zoneModulators SampleMetas.zoneModulators})
     . Because both the generator and modulator lists are in the same
     order as the preset header and zone lists, these indices will be monotonically increasing with increasing preset zones. The
     size of the PMOD sub-chunk in bytes will be equal to ten times the terminal preset’s wModNdx plus ten and the size of the
     PGEN sub-chunk in bytes will be equal to four times the terminal preset’s wGenNdx plus four. If the generator or
     modulator indices are non-monotonic or do not match the size of the respective PGEN or PMOD sub-chunks, the file is
     structurally defective and should be rejected at load time.
     If a preset has more than one zone, the first zone may be a global zone. A global zone is determined by the fact that the last
     generator in the list is not an Instrument generator. All generator lists must contain at least one generator with one
     exception - if a global zone exists for which there are no generators but only modulators. The modulator lists can contain
     zero or more modulators.
     If a zone other than the first zone lacks an Instrument generator as its last generator, that zone should be ignored. A global
     zone with no modulators and no generators should also be ignored.
     If the PBAG sub-chunk is missing, or its size is not a multiple of four bytes, the file should be rejected as structurally
     unsound.
     */
    public int generatorIndex;
    /**
     * see {@link #generatorIndex}
     */
    public int modulatorIndex;

    @Override
    public String toString() {
        return "\n{generatorIndex:"+generatorIndex+", modulatorIndex:"+modulatorIndex+"}";
    }
}
