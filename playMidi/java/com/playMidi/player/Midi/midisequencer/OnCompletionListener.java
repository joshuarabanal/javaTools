package com.playMidi.player.Midi.midisequencer;


import com.playMidi.player.AudioPlayerIterface;

/**
 * Created by ra on 12/20/2016.
 * Interface definition for a callback to be invoked when playback of
 * a media source has completed.
 */
public interface OnCompletionListener
{
    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the #MidiSequencer MidiPlayer that reached the end of the file
     */
    void onCompletion(AudioPlayerIterface mp);
}
