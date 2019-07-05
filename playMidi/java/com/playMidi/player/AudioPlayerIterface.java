package com.playMidi.player;

import com.playMidi.player.Midi.midisequencer.OnCompletionListener;
import com.playMidi.player.Midi.midisequencer.OnPreparedListener;

/**
 * Created by Joshua on 19/12/2017.
 */

public interface AudioPlayerIterface {
    public boolean isRunning();
    public boolean isPlaying();
    public boolean isLoading();
    public boolean isPrepared();
    public void prepare();
    public void setOnCompletionListener(OnCompletionListener ocl);
    public void setOnPreparedListener(OnPreparedListener opl);
    public void stop();
    public void release();
    public void pause();
    public void play();
    public int getCurrentPosition();
    public int getDuration();
    public void seekTo(int position);
    public void start();
    public void reset();
}
