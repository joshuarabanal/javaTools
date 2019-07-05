package com.playMidi.player.Midi.midisequencer.outputStreams;

import com.playMidi.player.Midi.midisequencer.OnCompletionListener;

/**
 * Created by Joshua on 19/01/2018.
 */

public interface AudioOutputStream {
    public int write(short[] frames, int start, int length) throws Exception;
    public void close()throws Exception;
    public int getSampleRate();
    public void setOnCompletionListener(OnCompletionListener ocl);

}
