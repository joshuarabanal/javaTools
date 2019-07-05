package com.playMidi.player.Midi.midisequencer;


import com.playMidi.player.AudioPlayerIterface;

/**
 * Created by ra on 12/20/2016.
 * Interface definition for a callback to be invoked when the media
 * source is ready for playback.
 */

public interface OnPreparedListener
{
    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MidiPlayer that is ready for playback
     */
    void onPrepared(AudioPlayerIterface mp);
}
