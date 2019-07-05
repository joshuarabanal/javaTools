/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.media;

import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Joshua
 */
public class AudioTrack extends OutputStream {
    private SourceDataLine line = null;
    private byte[] b = new byte[1];
    
    /**
     * Class constructor.
     * @param streamType the type of the audio stream. See
     *   {@link AudioManager#STREAM_VOICE_CALL}, {@link AudioManager#STREAM_SYSTEM},
     *   {@link AudioManager#STREAM_RING}, {@link AudioManager#STREAM_MUSIC},
     *   {@link AudioManager#STREAM_ALARM}, and {@link AudioManager#STREAM_NOTIFICATION}.
     * @param sampleRateInHz the initial source sample rate expressed in Hz.
     *   {@link AudioFormat#SAMPLE_RATE_UNSPECIFIED} means to use a route-dependent value
     *   which is usually the sample rate of the sink.
     *   {@link #getSampleRate()} can be used to retrieve the actual sample rate chosen.
     * @param channelConfig describes the configuration of the audio channels.
     *   See {@link AudioFormat#CHANNEL_OUT_MONO} and
     *   {@link AudioFormat#CHANNEL_OUT_STEREO}
     * @param audioFormat the format in which the audio data is represented.
     *   See {@link AudioFormat#ENCODING_PCM_16BIT},
     *   {@link AudioFormat#ENCODING_PCM_8BIT},
     *   and {@link AudioFormat#ENCODING_PCM_FLOAT}.
     * @param bufferSizeInBytes the total size (in bytes) of the internal buffer where audio data is
     *   read from for playback. This should be a nonzero multiple of the frame size in bytes.
     *   <p> If the track's creation mode is {@link #MODE_STATIC},
     *   this is the maximum length sample, or audio clip, that can be played by this instance.
     *   <p> If the track's creation mode is {@link #MODE_STREAM},
     *   this should be the desired buffer size
     *   for the <code>AudioTrack</code> to satisfy the application's
     *   latency requirements.
     *   If <code>bufferSizeInBytes</code> is less than the
     *   minimum buffer size for the output sink, it is increased to the minimum
     *   buffer size.
     *   The method {@link #getBufferSizeInFrames()} returns the
     *   actual size in frames of the buffer created, which
     *   determines the minimum frequency to write
     *   to the streaming <code>AudioTrack</code> to avoid underrun.
     *   See {@link #getMinBufferSize(int, int, int)} to determine the estimated minimum buffer size
     *   for an AudioTrack instance in streaming mode.
     * @param mode streaming or static buffer. See {@link #MODE_STATIC} and {@link #MODE_STREAM}
     * @throws java.lang.IllegalArgumentException
     * @deprecated use {@link Builder} or
     *   {@link #AudioTrack(AudioAttributes, AudioFormat, int, int, int)} to specify the
     *   {@link AudioAttributes} instead of the stream type which is only for volume control.
     */
    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes, int mode)
    throws IllegalArgumentException {
        AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            try{
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open();
                line.start();
            }catch(Exception e){
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage());
            }
            
    }

    public void write(byte[] b, int start, int length){
        line.write(b,start,length);
    }
    @Override
    public void write(int i) throws IOException {
        b[0] = (byte) i;
        line.write(b,0,1);
    }
    public void close(){
        release();
    }
    public void release(){
        line.close();
    }
    
}
