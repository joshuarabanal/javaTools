package com.playMidi.player.Midi.midisequencer.outputStreams;

import com.playMidi.player.Midi.midisequencer.OnCompletionListener;

import java.io.File;
import java.io.IOException;

import waveFormat.WaveOutputStream;

/**
 * Created by Joshua on 19/01/2018.
 */

public class AudioFileWriter implements AudioOutputStream {

    private final int sampleRate;
    private final WaveOutputStream out;

    public AudioFileWriter(File f, int sampleRate) throws IOException {
        this.sampleRate = sampleRate;
        out = new WaveOutputStream(f, sampleRate);

    }
    public int write(short[] buffer, int start, int length) throws Exception {
            out.write(buffer,start, length);
            return length;
    }
    public void close()throws Exception{
                out.close();
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener ocl) {

    }

}
