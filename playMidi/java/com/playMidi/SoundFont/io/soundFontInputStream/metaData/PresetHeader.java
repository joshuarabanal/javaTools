package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/4/2017.
 * The PHDR sub-chunk is a required sub-chunk listing all presets within the SoundFont compatible file. It is always a
 multiple of thirty-eight bytes in ATTRIBUTE_length, and contains a minimum of two records, one record for each preset and one for a
 terminal record according to the structure:
 struct sfPresetHeader
 <br>
 {
 <br>
 CHAR achPresetName[20];
 <br>
 WORD wPreset;
 <br>WORD wBank;
 <br>
 WORD wPresetBagNdx;
 <br>
 DWORD dwLibrary;
 <br>
 DWORD dwGenre;
 <br>
 DWORD dwMorphology;
 <br>
 };
 <br>
 If the PHDR sub-chunk is missing, or contains fewer than two records, or its size is not a multiple of 38 bytes, the file
 should be rejected as structurally unsound.
 */

public class PresetHeader {
    /**
     * The ASCII character field achPresetName contains the ATTRIBUTE_name of the preset expressed in ASCII, with unused terminal
     characters filled with zero valued bytes. Preset names are case sensitive. A unique ATTRIBUTE_name should always be assigned to each
     preset in the SoundFont compatible bank to enable identification. However, if a bank is read containing the erroneous state
     of presets with identical names, the presets should not be discarded. They should either be preserved as read or preferably
     uniquely renamed.

     */
    public String name;
    /**
     * The WORD wPreset contains the MIDI Preset Number and the WORD wBank contains the MIDI Bank Number which
     apply to this preset. Note that the presets are not ordered within the SoundFont compatible bank. Presets should have a
     unique set of wPreset and wBank numbers. However, if two presets have identical values of both wPreset and wBank, the
     first occurring preset in the PHDR chunk is the active preset, but any others with the same wBank and wPreset values
     should be maintained so that they can be renumbered and used at a later time. The special case of a General MIDI
     SoundFont 2.01 Technical Specification - Page 22 - Printed 12/10/1996 5:57 PM
     percussion bank is handled conventionally by a wBank value of 128. If the value in either field is not a valid MIDI value of
     zero through 127, or 128 for wBank, the preset cannot be played but should be maintained.
     */
    public int MIDI_number;
    /**
     * see {@link #name}
     */
    public int bank;
    /**
     The WORD wPresetBagNdx is an ATTRIBUTE_index to the preset’s zone list in the PBAG sub-chunk. Because the preset zone list is in
     the same order as the preset header list, the preset bag indices will be monotonically increasing with increasing preset
     headers. The size of the PBAG sub-chunk in bytes will be equal to four times the terminal preset’s wPresetBagNdx plus
     four. If the preset bag indices are non-monotonic or if the terminal preset’s wPresetBagNdx does not match the PBAG subchunk
     size, the file is structurally defective and should be rejected at load time. All presets except the terminal preset must
     have at least one zone; any preset with no zones should be ignored.
     */
    public int presetZones_index;
    /**
     * The DWORDs dwLibrary, dwGenre and dwMorphology are reserved for future implementation in a preset library
     management function and should be preserved as read, and created as zero.
     */
    public int library;
    /**
     * see {@link #library}
     */
    public int genre;
    /**
     * see {@link #library}
     */
    public int morphology;

    @Override
    public String toString() {
        return "\n{"
                +"name:"+name+","
                +"MIDI_number:"+MIDI_number+", "
                +" bank:"+bank+", "
                +" presetZones_index:"+presetZones_index+", "
                +" library:"+library+", "
                +" genre:"+genre+", "
                +" morphology:"+morphology
                +"}";
    }
}
