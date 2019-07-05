package com.playMidi.player.notate.player;

import android.app.Activity;
import android.app.ActivityManager;
import android.media.MediaPlayer;

import Analytics.CrashReporter;
import com.musicxml.R;
import com.musicxml.dialogs.editInstrumentDialog.PickSoundFontDialog;
import com.musicxml.noteDataTypes.DrawingDataBox;
import com.musicxml.noteDataTypes.RepeatsTimeTempoAndKeyChanges;
import com.musicxml.noteDataTypes.Track;
import com.musicxml.noteDataTypes.note.Note;
import com.musicxml.noteDataTypes.note.RegularNote;
import com.musicxml.noteDataTypes.note.RestNote;
import com.musicxml.noteDataTypes.note.regularNote.NoteLengths;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;
import com.playMidi.player.AudioPlayerIterface;
import com.playMidi.player.Midi.midisequencer.OnCompletionListener;
import com.playMidi.player.Midi.midisequencer.outputStreams.AudioOutputStream;
import com.playMidi.player.Midi.midisequencer.outputStreams.AudioPlayer;
import com.playMidi.player.notate.Player;
import com.playMidi.player.notate.player.playbackRunnable.TrackCrawler;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Joshua on 19/12/2017.
 */

public class PlaybackRunnable implements Runnable{
    private Thread t;
    private boolean running = false;
    private float position = 0;
    private MidiTimbreSet instrumento;
    private ArrayList<Track> tracks;
    private DrawingDataBox ddb;
    private int frameRate;
    private Activity context;
    private AudioPlayer out = null;
    private RepeatsTimeTempoAndKeyChanges measures;
    private Player player;
    private int tempo;

    public PlaybackRunnable(Activity c, DrawingDataBox ddb, Player player){
        //Log.e("memory allocated", "new playback runnable created");
       // new IndexOutOfBoundsException().printStackTrace();
        this.player = player;
        context = c;
        tracks  = ddb.tracks;
        measures = ddb.measureMetaData;
        tempo = ddb.key.tempo;
        this.ddb = ddb;
    }
    public void pause(){
        //Log.i("playback runnable", "pause");
        float pos = position;
        stop();
        position = pos;
    }
    public void stop(){
        //Log.i("playback runnable", "stop");
        if(out != null){
            out.stop();
        }
        running = false;
        position = 0;
    }
    public void play(){
        //Log.i("playback runnable", "play");
        t = new Thread(this);
        running = true;
        t.start();
    }
    private boolean availableMemory(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.lowMemory;
    }

    @Override
    public void run() {
        // File temp;
        try {

            running = true;
            // temp = new File(context.getExternalCacheDir(), "audio.wav");
            out = new AudioPlayer(context);//context
            out.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(AudioPlayerIterface mp) {
                    stop();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.e("on completion", "on completion:"+isRunning());
                            player.onCompletion();
                        }
                    });
                }
            });
            if(instrumento == null) {
                if (tracks.get(0).synth !=null && tracks.get(0).synth.exists()) {
                    instrumento = new SoundFontMidiTimbreSet(tracks.get(0).synth, availableMemory());
                }
                else {
                    instrumento = new SoundFontMidiTimbreSet(context, R.raw.grand_piano, availableMemory());
                }
            }
            instrumento.setFrameRate(out.getSampleRate());
            //og.i("instrumento listo", instrumento.toString());

            int howManyFinished = 0;

            long time = System.currentTimeMillis();

            short[] buffer = new short[bufferLength()];
            int bufferPosicion = buffer.length;
            ArrayList<TrackCrawler> tracks  = new ArrayList<TrackCrawler>();
            for(int i = 0; i<ddb.tracksSize(); i++){
                tracks.add(new TrackCrawler(ddb,i, instrumento));
            }

            while(running && tracks.size()>0){
                if(bufferPosicion<buffer.length){
                    bufferPosicion+=out.write(buffer, bufferPosicion, buffer.length-bufferPosicion);
                    continue;
                }


                for(int i = 0;i<buffer.length; i++){
                    buffer[i] = 0;
                }
                for(int i = 0; i<tracks.size(); i++){
                    if(tracks.get(i).fillBuffer(buffer)){
                        tracks.remove(i);
                        i--;
                    }
                }
                bufferPosicion = out.write(buffer,0,buffer.length);
                position++;
            }
            running  = false;
            out.close();
            t = null;
            //Log.i("audio finished writing", (System.currentTimeMillis()-time)+"");
            // testPlayAudioFile(temp);
        } catch (Exception e) {
            running = false;
            try{out.close();} catch(Exception e2){}
            t = null;
            CrashReporter.sendDefaultErrorReport(e);
        }





    }

    @Deprecated
    public void run_old_version() {
        // File temp;
        try {

            running = true;
            // temp = new File(context.getExternalCacheDir(), "audio.wav");
            out = new AudioPlayer(context);//context
            out.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(AudioPlayerIterface mp) {
                    stop();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.e("on completion", "on completion:"+isRunning());
                            player.onCompletion();
                        }
                    });
                }
            });
            if(tracks.get(0).synth.exists()){
                instrumento = new SoundFontMidiTimbreSet(tracks.get(0).synth, availableMemory());
            }
            else{
                instrumento = new SoundFontMidiTimbreSet(context, R.raw.grand_piano,availableMemory());
            }
            instrumento.setFrameRate(out.getSampleRate());
            //og.i("instrumento listo", instrumento.toString());

            int howManyFinished = 0;

            long time = System.currentTimeMillis();

            short[] buffer = new short[bufferLength()];
            int bufferPosicion = buffer.length;

            while(running && howManyFinished<tracks.size()){
                if(bufferPosicion<buffer.length){
                    bufferPosicion+=out.write(buffer, bufferPosicion, buffer.length-bufferPosicion);
                    continue;
                }


                for(int i = 0;i<buffer.length; i++){
                    buffer[i] = 0;
                }
                howManyFinished = 0;
                for(int i = 0; i<tracks.size(); i++){
                    howManyFinished += (trackFillBuffer(i, buffer)? 1: 0);
                }
                bufferPosicion = out.write(buffer,0,buffer.length);
                //out.play();
                position++;
            }
            running  = false;
            out.close();
            t = null;
            //Log.i("audio finished writing", (System.currentTimeMillis()-time)+"");
            // testPlayAudioFile(temp);
        } catch (Exception e) {
            running = false;
            try{out.close();} catch(Exception e2){}
            t = null;
            CrashReporter.sendDefaultErrorReport(e);
        }





    }
    private MediaPlayer mp_test;

    private int bufferLength(){
        //og.i("buffer length","tempo:"+tempo+", smplrate:"+out.getSampleRate()+", quarter:"+NoteLengths.quarter);
        return (int) (
                out.getSampleRate()*(
                        60.0f/(tempo* NoteLengths.quarter*3)//the three is for triplets
                )
        );
    }

    /**
     *
     * @param trackIndex
     * @param buffer
     * @return cierto: si este track esta completado,<br> falso: si hay mas notas para reproducir
     */
    private boolean trackFillBuffer(int trackIndex, short[] buffer){
        Track t = tracks.get(trackIndex);
        int count = 0;
        Note n;
        for(int i = 0; i<t.notes.size(); i++){
            n = t.notes.get(i);
            count+= n.getLength();

            if(count>= position ){
                if(n.getType() == RegularNote.TYPE){
                    notaLlennarMatriz((RegularNote) n,buffer,n.getLength()-(count-position), tracks.size());
                    return false;
                }
                else if(n.getType() == RestNote.TYPE){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * llena el matriz con el sonido del nota
     * @param nota el nota para reproducir
     * @param matriz el matriz para llenar
     * @param posicion el posicion entre el nota (0=la empiezsa)
     */
    private void notaLlennarMatriz(RegularNote nota, short[] matriz, float posicion,int numberOfTracks){
        float volumeMultiplier = numberOfTracks+(nota.chord !=null? nota.chord.length: 0);
        volumeMultiplier = 0.5f/volumeMultiplier;
       // Log.i("midi note number",(nota.name +(12*(nota.octave+1)) -1)+"");
        instrumento.fillBuffer(
                nota.name +(12*(nota.octave+1)) -1,
                posicion*matriz.length,
                nota.length*matriz.length,
                matriz,
                volumeMultiplier,
                nota.articulation
        );
        if(nota.chord != null){
            for(int i =0; i<nota.chord.length; i++){
                instrumento.fillBuffer(
                        nota.chord[i][0] +(12*(nota.chord[i][1]+1)) -1,
                        posicion*matriz.length,
                        nota.length*matriz.length,
                        matriz,
                        volumeMultiplier,
                        nota.articulation
                );
            }
        }
    }
    public boolean isRunning() {
        return running ;
    }
    public boolean isPlaying(){
        //Log.i("playbackrunnable=playing", "out == null:"+(out == null)+", is playing:"+(out == null? "false":out.isPlaying())+", is running:"+isRunning());
        return (out!=null && out.isPlaying()) || isRunning();
    }

}
