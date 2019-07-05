package com.playMidi.player.Midi.midisequencer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import Analytics.CrashReporter;
import com.playMidi.player.Midi.MidiEvent;
import com.playMidi.player.Midi.MidiSequencer;
import com.playMidi.player.Midi.midisequencer.outputStreams.AudioFileWriter;
import com.playMidi.player.Midi.midisequencer.outputStreams.AudioOutputStream;
import com.playMidi.player.Midi.midisequencer.outputStreams.AudioPlayer;
import com.playMidi.player.SoundEventRecycler;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ra on 12/21/2016.
 */
public class PlaybackRunnable extends AsyncTask<Void,MidiSequencer, Integer>{
    private MidiSequencer midi;
    private Context context;
    private int currentTime = 0;
    private int BeatLength = 100;
    AudioOutputStream t = null;
    private int sampleRate;
    private short[] buffer = null;
    private int ticksPerNote;
    private OnCompletionListener onCompletionListener;
    private ArrayList<ArrayList<MidiEvent>> tracks = null;
    private MidiTimbreSet instrument;
    private boolean running = false;
    private File output;


    public PlaybackRunnable(MidiSequencer m, Context c, int ticksPerNote, ArrayList<ArrayList<MidiEvent>> t){
        midi = m;
        if(c == null){
            throw new NullPointerException("context is null");
        }
        context = c;
        this.ticksPerNote = ticksPerNote;
        setNoteEvents(t);
    }
    public void setOutputFile(File f){
        this.output = f;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Thread.currentThread().setName("Playback Runnable");
    if(instrument == null){
        throw new NullPointerException("midi instrumewnt is null");
    }
        running = true;
        try {
            beginPlayback();
        } catch (Exception e) {
            CrashReporter.sendDefaultErrorReport(e);
        }
        return 0;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        Log.i("on post execute","integer:"+integer);
        if(!running){
            if(t!= null){
                try {
                    t.close();
                } catch (Exception e) {
                    CrashReporter.sendDefaultErrorReport(e);
                }
            }
            return ;
        }
        running = false;
        if(integer == 0){
            if(onCompletionListener != null){
                onCompletionListener.onCompletion(midi);
            }
        }
    }
    public boolean isRunning(){
        return running;
    }
    public void stop(){
        running = false;
    }


    public void setInstruments(MidiTimbreSet inst){
        instrument = inst;
    }
    public void setNoteEvents(ArrayList<ArrayList<MidiEvent>> t){
        if(tracks == null){
            tracks = new ArrayList<ArrayList<MidiEvent>>();
            tracks.clear();
        }
        for(int i = 0;  i<t.size(); i++){
            ArrayList<MidiEvent> track = t.get(i);
            ArrayList<MidiEvent> newTrack = new ArrayList<MidiEvent>();

            for(int y = 0; y<track.size(); y++){
                newTrack.add((track.get(y)));
            }
            tracks.add(newTrack);
        }
    }

    public void setOnCompletionListener(OnCompletionListener ocl){
        onCompletionListener = ocl;
    }
    private void beginPlayback() throws Exception {

            initializeAudioTrack(context);
            currentTime = 0;
            ArrayList<SoundEventRecycler> currentEvents = new ArrayList<SoundEventRecycler>();
            for(int i = 0; i<tracks.size(); i++){ currentEvents.add(new SoundEventRecycler()); }
            int howManyWriten =Integer.MAX_VALUE;
            while(currentEvents.size()>0 && tracks.size()>0 && running){
                if(buffer != null && howManyWriten <buffer.length){//entrar lo que no has entrado de la ultima vez
                    t.write(buffer, howManyWriten, buffer.length-howManyWriten);
                    howManyWriten = buffer.length;
                }
                else {
                    removeEventsAtTime(currentTime, currentEvents);
                    PlaySingleUnitOfEvents(currentTime, currentEvents);
                    currentTime++;
                    howManyWriten = t.write(buffer, 0, buffer.length);
                    //t.play();
                }
            }
            Log.i("finished playback","finished playback");
            t.close();
            running = false;

    }
    private void initializeAudioTrack(Context c) throws IOException {
        if(output == null){
            t = new AudioPlayer(c);
        }
        else{
            t = new AudioFileWriter(output, 44100);
        }
    }

    /**<ul>
     * <li>removes all of the note on events that are next to play and adds then to the</li>
     * <li>removes SouundEvents for any not off events</li>
     * <li>changes the time signature for time signature events</li>
     * </ul>
     * @param time
     * @param output
     */
    private void removeEventsAtTime(int time, ArrayList<SoundEventRecycler> output)throws Exception{
        //og.i("remove events", ""+time);
        for(int t = 0; t<tracks.size(); t++){ // for each track
            ArrayList<MidiEvent> track = tracks.get(t);
            SoundEventRecycler trackEvents = output.get(t);

            for(int event = 0; event<track.size(); event++){ //for each note in that track
                if(track.get(event).getStartTime() <= time){//if the event is next to happen
                    MidiEvent me = track.remove(event); event --;

                    if(me.isNoteOff()){//note off event
                        boolean eventCanceled = false;
                        for(int i = 0; i<trackEvents.size(); i++){
                            if(me.equals(trackEvents.get(i))){
                                eventCanceled = true;
                                trackEvents.remove(i);
                            }
                        }
                        if(!eventCanceled){
                            throw new Exception("midi event not canceled"+me.getNoteNumber());
                        }
                    }

                    else if(me.isSetTempoEvent()){//tempo event
                        this.BeatLength = (60*sampleRate)/me.getTempo();
                        buffer = new short[BeatLength/(ticksPerNote)];
                    }
                    else if(me.isChangeInstrumentEvent()){
                        //TODO add change instrument for each track
                    }
                    else if(me.isNoteOn()){
                        //SoundEvent se = SoundEvent.new_SoundEvent(me, ATTRIBUTE_sampleRate);
                        trackEvents.add(me,sampleRate);
                    }
                    else{
                        //og.i("command", String.format("# %02X", me.getEventType()));
                        throw new Exception("unknown midi command found");
                    }
                }
            }

            if(track.size() == 0) {
                tracks.remove(t); t--;
            }

        }
    }
    private void PlaySingleUnitOfEvents(int position, ArrayList<SoundEventRecycler> currentEvents){
        //og.i("play unit of events", ""+position);//TODO remove this
        for(int i = 0; i<buffer.length; i++){ buffer[i] = 0; }
        int multipiler = 0;
        for(int i = 0; i<currentEvents.size(); i++){
            multipiler += currentEvents.get(i).size();
        }

        if(buffer.length == 0){ return; }
        for(int i = 0; i<currentEvents.size(); i++){
            SoundEventRecycler track = currentEvents.get(i);
            for(int sound =0; sound < track.size(); sound ++){
                //og.i("fill buffer", "buffer ATTRIBUTE_length:"+buffer.ATTRIBUTE_length);
                track.get(sound).fillBuffer(buffer,getMidiInstrument(i),multipiler);
            }
        }
    }
    private MidiTimbreSet getMidiInstrument(int type){
        return instrument;
    }
}
