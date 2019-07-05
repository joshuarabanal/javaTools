package com.playMidi.player.notate.player.playbackRunnable;

import android.util.Log;

import com.musicxml.noteDataTypes.DrawingDataBox;
import com.musicxml.noteDataTypes.note.DynamicNote;
import com.musicxml.noteDataTypes.note.GraceNote;
import com.musicxml.noteDataTypes.note.Note;
import com.musicxml.noteDataTypes.note.OctavaNote;
import com.musicxml.noteDataTypes.note.RegularNote;
import com.musicxml.noteDataTypes.note.RestNote;
import com.musicxml.noteDataTypes.note.TripletNote;
import com.playMidi.player.soundEvent.MidiTimbreSet;

import java.util.ArrayList;

import static com.musicxml.noteDataTypes.note.DynamicNote.value_dynamic_fortississimo;

public class TrackCrawler {
    private DrawingDataBox ddb;
    private final int track;
    private int index = 0;
    private MidiTimbreSet instrumento;

    private int position = 0;

    ArrayList<NoteEvent> notes = new ArrayList<NoteEvent>();
    private class NoteEvent{
        int time = 0;
        int endPadding = 0;
        int mididNumber = 0;
        int [] chords;
        int length = 0;
        private int articulation = RegularNote.Articulation_null;
        
        public void set(int name, int octave, int length, int articulation){
          this.mididNumber = name +(12*(octave+1+octaveAdjust)) -1;
          this.length = length;
          this.articulation = articulation;
          this.time = 0;
          switch(articulation){
                case RegularNote.Articulation_staccato:
                case RegularNote.Articulation_rooftop:
                  endPadding = length/3;
                  this.length =(length*2)/3;
                  break;
              case RegularNote.Articulation_legato:
                  this.length+= 12;
                  time = 6;
                  this.endPadding-=6;
                  break;
              case RegularNote.Articulation_fermatta:
                  this.length*=3;
                  break;

          }
           // Log.i("new note created", "midi number:"+mididNumber+", time:"+time+", end padding:"+endPadding+", length:"+this.length+", orig length:"+length);
          
        }

        /**
         *
         * @param buffer
         * @return true if note has been completed
         */
        public boolean play(short[] buffer){
            boolean finished;
           // Log.e("playing note event", "note event:"+this.mididNumber);
            if(mididNumber>0 && time<=length) {
                float noteVolume = volume/notes.size();
                if(chords !=null){
                    noteVolume /=chords.length;
                }
                finished =instrumento.fillBuffer(
                    mididNumber,
                    time * buffer.length,
                        (length) * buffer.length,
                    buffer,
                    noteVolume,
                    articulation
                );

                if(chords != null){//play chord tones if they exist
                    for(int i = 0; i<chords.length; i++){
                        instrumento.fillBuffer(
                                chords[i],
                                time * buffer.length,
                                (length) * buffer.length,
                                buffer,
                                volume/notes.size(),
                                articulation
                        );
                    }
                }
            }
            else{
                finished = time>(length+endPadding);
            }
                if(time==length+endPadding){
                    moveToNextNote();
                }
            //Log.i("new time increment", "midi:"+mididNumber+", time:"+time+", end:"+(length+endPadding));

                time++;

                return finished && time>=length+endPadding;
        }

        public void addChords(RegularNote note) {
            if(note.chord == null || note.chord.length == 0){
                return;
            }
            chords = new int[note.chord.length];
            for(int i = 0; i<note.chord.length; i++){
                chords[i] = note.chord[i][0] +(12*(note.chord[i][1]+1+octaveAdjust)) -1;
            }
        }
    }

    public TrackCrawler(DrawingDataBox ddb, int track, MidiTimbreSet inst){
        this.ddb = ddb;
        this.track = track;
        this.instrumento = inst;
        moveToNextNote();
    }
    private void moveToNextNote(){
        NoteEvent event = new NoteEvent();
        setNoteValues(event);
    }



    private float volume = 0.5f;
    private int octaveAdjust = 0;
    private int borrowedLength = 0;
    private int tripletItoration = 0;
    private void setNoteValues(NoteEvent event){
        if(ddb.trackNotesSize(track)<=index){
            index++;
            return;//reached end of song
        }
        Note n = ddb.getNote(track,index);
        index++;
        Log.i("new index incremented", "index:"+index);
        setNoteValues(event,n, (n.getLength()*3)+borrowedLength);
    }
    private void setNoteValues(NoteEvent event, Note n, int overridingLength){
        switch(n.getType()){
            case RegularNote.TYPE:
                RegularNote note = (RegularNote)n;
                event.set(note.name,note.octave,overridingLength, note.articulation);
                event.addChords(note);
                borrowedLength = 0;
                notes.add(event);
                return;
            case GraceNote.TYPE:
                borrowedLength = -3;
                GraceNote grace = (GraceNote)n;
                event.set(grace.name,grace.octave,3,grace.articulation);
                notes.add(event);
                return;
            case RestNote.TYPE:
                event.mididNumber = -1;
                event.time = 0;
                event.articulation = RegularNote.Articulation_null;
                event.length = overridingLength;
                borrowedLength = 0;
                notes.add(event);
                return;
            case TripletNote.TYPE:
                TripletNote tn= (TripletNote)n;
                n = tn.notes[tripletItoration];
                tripletItoration++;
                if(tripletItoration<tn.notes.length){index--; }
                else{ tripletItoration = 0; }
                int thisLength = ((n.getLength()*2));
                setNoteValues(event,n,thisLength);
                return;
            case DynamicNote.TYPE:
                volume = ((DynamicNote)n).volume/((float)value_dynamic_fortississimo);
                break;
            case OctavaNote.TYPE_OPEN:
                OctavaNote on = (OctavaNote)n;
                octaveAdjust = on.up? 1:-1;
                break;
            case OctavaNote.TYPE_CLOSE:
                octaveAdjust = 0;
                break;
        }

        setNoteValues(event);
    }

    public boolean fillBuffer(short[] buffer){
        for(int i = 0; i<notes.size(); i++){
            if(notes.get(i).play(buffer)){
                    notes.remove(i);
                    i--;
            }
        }
        if(notes.size() == 0 && index > ddb.trackNotesSize(track)){
            return true;
        }
        return false;
    }
}
