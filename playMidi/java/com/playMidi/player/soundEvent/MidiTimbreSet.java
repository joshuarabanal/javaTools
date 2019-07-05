package com.playMidi.player.soundEvent;

/**
 * Created by ra on 4/28/2017.
 */

public interface MidiTimbreSet {
    /**
     * make a singe wave with correct amplitudes at this point
     * @param midiNoteNumber = the sound number ascociated with this note
     *  @param position = the amount this sound has progressed through its ATTRIBUTE_length (measured in output frames)
     *  @param noteLength = the ATTRIBUTE_length of this note(measured in output frames)
     *      @param data buffer to fill
     *  @param volumeMultiplier decimal to multiply by the current volume 0>multiplier<1
     *                          @param styleMarkings defaults top 0
     *
     * @return true if note finished, false if note not finished
     */
    public boolean fillBuffer(int midiNoteNumber, float position, float noteLength, short[] data, float volumeMultiplier, int styleMarkings);
    public void setFrameRate(int rate);
}
