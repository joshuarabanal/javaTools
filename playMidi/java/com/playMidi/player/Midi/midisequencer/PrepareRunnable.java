package com.playMidi.player.Midi.midisequencer;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.RawRes;
import android.util.Log;

import Analytics.CrashReporter;
import com.playMidi.player.Midi.MidiEvent;
import com.playMidi.player.Midi.MidiSequencer;
import com.playMidi.player.soundEvent.MidiTimbreSet;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by ra on 12/21/2016.
 */

public class PrepareRunnable extends AsyncTask<Void,Void,Boolean> {
    private MidiSequencer midi;
    private OnPreparedListener onPreparedListener;
    private boolean running;
    private Uri file;
    private Activity context;

    //things to return
    private int ticksPerNote = -1;
    private ArrayList<ArrayList<MidiEvent>> tracks;
    private int[] positions = null;
    private MidiTimbreSet instrument = null;
    private int defaultRes;
    private int backupRes;

    /**
     *
     * @param mi
     * @param f
     * @param c
     * @param defaultResource link to xml soundfont resource
     * @param backupRes link to the raw.tight_piano_sf resid
     */
    public PrepareRunnable(MidiSequencer mi, Uri f, Activity c, @RawRes int defaultResource, @RawRes int backupRes){
        defaultRes = defaultResource;
        this.backupRes = backupRes;
        midi = mi;
        file = f;
        context = c;
        if(c == null){
            throw new NullPointerException("context is null");
        }
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        running = true;
        try {

            beginPreparing(file);
            return true;
        } catch (Exception e) {
            InputStream fis = null;
            try {
                fis = context.getContentResolver().openInputStream(file);
                byte[] b = new byte[1024];
                int howmany = -1;
                StringBuilder log = new StringBuilder("file");
                while((howmany = fis.read())>=0){
                    log.append( ":"+howmany);
                }
                //og.("exception",log.toString());
            } catch (IOException e1) {
                CrashReporter.sendDefaultErrorReport(e1);
            }
            CrashReporter.sendDefaultErrorReport(e);
            return false;
        }

    }
    @Override
    protected void onPostExecute(Boolean success) {
        if(!running){
            tracks.clear();
            ticksPerNote = -1;
            instrument = null;
            return;
        }
        running = false;
        //og.("loading complete","loading complete");
        if(onPreparedListener != null && success){
            onPreparedListener.onPrepared(midi);
        }
    }
    public boolean isRunning(){
        return running;
    }
    public void stop(){
        running = false;
    }


    //getters
    public MidiTimbreSet getMidiInstrument(){
        return instrument;
    }
    public ArrayList<ArrayList<MidiEvent>> getTracks(){
        return tracks;
    }
    public int getTicksPerNote(){
        return ticksPerNote;
    }



    //actual loading functions
    public void setOnPreparedListener(OnPreparedListener opl){
        onPreparedListener = opl;
    }
    public void beginPreparing(Uri f)throws IOException, Exception{

        initializeMidiInstrument(defaultRes,backupRes);
        //og.i("MidiSequencer", f.toString());
        InputStream fis = context.getContentResolver().openInputStream(file);//new FileInputStream(f);
        if(fis.read() != 77 || fis.read() != 84 || fis.read() != 104 || fis.read() != 100){//
            throw new IOException("file format not midi");
        }
        int headerLength = readBigEndian((byte)fis.read(),(byte)fis.read(),(byte)fis.read(),(byte)fis.read());
        //og.i("header ATTRIBUTE_length",""+headerLength);
        int fileFormat = readBigEndian((byte)fis.read(),(byte)fis.read());
        int numberOfTracks = readBigEndian((byte)fis.read(),(byte)fis.read());
        //og.i("number of tracks",""+numberOfTracks);
        tracks = new ArrayList<ArrayList<MidiEvent>>(numberOfTracks);
        ticksPerNote = readBigEndian((byte)fis.read(),(byte)fis.read());
        //og.i("ticksPerNote", ""+ticksPerNote);
        switch(fileFormat){
            case 0:
                break;
            case 1:
                readMultiTrackFormat(fis,numberOfTracks, ticksPerNote);
                break;
            case 2:
                break;
        }

    }
    private void readMultiTrackFormat(InputStream fis, int numberOfTracks, int ticksPerNote) throws Exception {
        for(int i = 0; i<numberOfTracks; i++){
            if(fis.read() != 77 || fis.read() != 84 || fis.read() != 114 || fis.read() != 107){ //track header MTrk
                throw new Exception("midi track error: MTrk");
            }
            int length = readBigEndian((byte) fis.read(), (byte)fis.read(), (byte)fis.read(), (byte)fis.read());
            byte[] b = new byte[length];
            //og.i("track ATTRIBUTE_length",""+ATTRIBUTE_length);
            fis.read(b);
            ArrayList<MidiEvent> track = new ArrayList<MidiEvent>();
            int index = 0;

            int previousIndex= 0;
            int startTime = 0;
            while( index<b.length && running){
                int startingIndex = index;

                int deltaTime = 0;
                while( (b[index] &0x80) != 0 ){
                    deltaTime = deltaTime<<7;
                    deltaTime+=(b[index]&0x7f);
                    index++;
                }
                deltaTime = deltaTime<<7;
                deltaTime+=b[index];
                index++;
                startTime +=deltaTime;


                if( (b[index]&0xff) == 0xff ){//meta events
                    switch(b[index+1]){
                        case 0x00:  //sequence number
                        case 0x01:	//Text event
                        case 0x02:	//Copyright notice
                        case 0x54:	//SMPTE offset
                        case 0x05:	//Lyric text
                        case 0x06:	//Marker text
                        case 0x7F:	//Sequencer specific event
                        case 0x07:	//Cue point
                            throw new Exception("this event is not supported");
                        
                        case 0x51://TEMPO
                            if(b[index+2] != 0x03){ throw new UnsupportedEncodingException("ff,51"+String.format("%02X",b[index+2])); }
                            int tempo = readBigEndian(b[index+3], b[index+4], b[index+5]);
                            //og.("tempo event",""+tempo);
                            track.add(MidiEvent.new_tempoEvent(startTime, tempo));
                            index+=6;
                            break;

                        case 0x59://key change
                            if(b[index+2] != 0x02){ throw new UnsupportedEncodingException("ff,59"+String.format("%02X",b[index+2])); }
                            int keyNumber = b[index+3];
                            boolean major = (b[index+4] == 0);
                            //og.("key change", keyNumber+","+major);
                            index+= 5;
                            break;

                        case 0x03://track ATTRIBUTE_name
                            int stringlen = b[index+2];
                            String trackName = new String(b,index+3,stringlen);
                            //og.("track ATTRIBUTE_name",trackName);
                            index+= 3+stringlen;
                            break;

                        case 0x04://instrument ATTRIBUTE_name
                            int stringlength = b[index+2];
                            String instName = new String(b,index+3,stringlength);
                            //og.("instrument name",instName);
                            index+= 3+stringlength;
                            break;

                        case 0x58://time signature
                            if(b[index+2] != 0x04){ throw new UnsupportedEncodingException("ff,58"+String.format("%02X",b[index+2])); }
                            /**og.i(
                                    "time signatureb "+index,
                                    b[index+3]+"/"+b[index+4] +
                                            ", midi clocks per metronome tick"+b[index+5]+
                                            ", midi 32nd notes/24(quarter note) = "+b[index+6]+
                                            ", next byte:"+String.format("%02X",b[index+7])
                            );
                             **/
                            index+= 7;
                            break;

                        case 0x20://MIDI channel prefix assignment
                            if(b[index+2] != 0x01){ throw new UnsupportedEncodingException("ff,20"+String.format("%02X",b[index+2])); }
                            //og.("channel change", ""+b[index+3]);
                            index+=4;
                            break;

                        case 0x2f://end of track
                            if(b[index+2] != 0x00){ throw new UnsupportedEncodingException("ff,2f"+String.format("%02X",b[index+2])); }
                            if(index+3< b.length){
                                throw new IndexOutOfBoundsException("track ended but byte array not terminated");
                            }
                            Log.i("end of track", b[index]+"");
                            index = b.length;
                            break;

                        default:
                            Log.i("meta ewvent", "not found");
                            Log.i("previous index", previousIndex+"");
                            Log.i(
                                    "item["+index+"]",
                                    String.format("%02X,",b[index]) +
                                            String.format("%02X,",b[index+1])+
                                            String.format("%02X",b[index+2])
                            );
                            throw new IOException("unhandled meta event ATTRIBUTE_type: see documentation(http://www.onicos.com/staff/iz/formats/midi-event.html, http://www.cs.cmu.edu/~music/cmsip/readings/Standard-MIDI-file-format-updated.pdf)");
                    }
                }
                else{
                    switch(b[index]&0xf0){
                        case MidiEvent.noteOff://0x80
                            track.add(MidiEvent.new_noteOff(startTime, (byte) (b[index]&0x0F), b[index+1], b[index+2]));
                            Log.i("note off", b[index]+"");
                            index+=3;
                            break;

                        case MidiEvent.noteOn://0x90
                            track.add(MidiEvent.new_noteOn(startTime, (byte) (b[index]&0x0F), b[index+1], b[index+2]));
                            Log.i("note on", b[index]+"");
                            index+=3;
                            break;

                        case MidiEvent.afterTouch://0xa0 => 1010****
                            track.add(MidiEvent.new_Accent(startTime, (byte) (b[index]&0x0F), b[index+1], b[index+2]));
                            Log.i("after touch", b[index]+"");
                            index+=3;
                            break;

                        case MidiEvent.controlChange://0xb0 => 1011**** control mode change
                            int controllerNumber = b[index+1];
                            int newValue = b[index+2];
                            Log.i("control change", controllerNumber+"=>"+newValue);
                            index+=3;
                            break;

                        case MidiEvent.changeInstrument://0xc0 => 1100**** program change
                            track.add(MidiEvent.new_changeInstrument(startTime,(byte) (b[index]&0x0F), b[index+1]));
                            Log.i("change instrument", b[index]+"");
                            index+= 2;
                            break;
                            
                        case 0xD0://channel afterTouch
                        case 0xE0://pitch wheel change
                        case 0xF0://system exclusive
                            throw new Exception("failed to process unsupported event");

                        default:
                            Log.i("regular event","not found:"+(b[index]&0xf0));
                            Log.i("previous index", previousIndex+"");
                            Log.i(
                                    "item["+index+"]",
                                    String.format("%02X,",b[index]) +
                                            String.format("%02X,",b[index+1])+
                                            String.format("%02X",b[index+2])
                            );
                            StringBuilder sb = new StringBuilder();
                            for(int y = previousIndex; y<index+4 && y<b.length; y++) {sb.append(String.format("%02X,",b[y]));}
                            Log.i("previous-current event", sb.toString());

                            sb = new StringBuilder();
                            for(int y = 0; y<b.length; y++) {sb.append(String.format("%02X,",b[y]));}
                            Log.i("track["+b.length+"]", sb.toString());

                            throw new IOException("unhandled meta event ATTRIBUTE_type: see documentation(http://www.onicos.com/staff/iz/formats/midi-event.html, http://www.cs.cmu.edu/~music/cmsip/readings/Standard-MIDI-file-format-updated.pdf)");

                    }
                }
                previousIndex = startingIndex;
            }
            tracks.add(track);
        }
        reset();
    }
    private int readBigEndian(byte... data){
        //String string = ""; for(byte b : data){ string += String.format("%02X ", b); }Log.i("readBigEndian",string);
        int retu = 0;
        for(int i = 0; i<data.length; i++){
            if(i>0){
                retu = retu<<8;
            }
            retu+=(data[i]& 0xff);

        }
        //og.i("readBigEndian",""+retu);
        return retu;
    }
    private void initializeMidiInstrument(@RawRes int resId, @RawRes int defaultRes) throws Exception{
        instrument = XmlMidiTimbreSet.retrieveCachedSoundFont(context,resId, defaultRes);
    }
    public void reset(){
        if(positions == null){
            positions = new int[tracks.size()];
        }
        for(int i = 0; i<positions.length; i++){
            positions[i] = 0;
        }
    }
}
