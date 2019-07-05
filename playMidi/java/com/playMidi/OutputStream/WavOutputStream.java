package com.playMidi.OutputStream;

import com.musicxml.R;
import com.musicxml.noteDataTypes.Track;
import com.musicxml.noteDataTypes.note.Note;
import com.musicxml.noteDataTypes.note.RegularNote;
import com.musicxml.noteDataTypes.note.RestNote;
import com.musicxml.noteDataTypes.note.regularNote.NoteLengths;
import com.musicxml.views.NoteScript;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import waveFormat.WaveOutputStream;

/**
 * Created by Joshua on 21/12/2017.
 */

public class WavOutputStream {

    private WaveOutputStream out;
    private ArrayList<Track> tracks;
    private int position;
    private SoundFontMidiTimbreSet instrumento;
    private int tempo;
    private int frameRate;
    private float volumeMultiplier;


    public WavOutputStream(File out, NoteScript paper) throws IOException {
        this.out = new WaveOutputStream(out, 44100);
        tracks = paper.getDrawingData().tracks;
        instrumento = new SoundFontMidiTimbreSet(paper.getContext(), R.raw.grand_piano, false);
        tempo = paper.getDrawingData().key.tempo;
        frameRate = 44100;
        volumeMultiplier = 1.0f/tracks.size();
    }

    public void write() throws IOException {

            int howManyFinished = 0;

            long time = System.currentTimeMillis();

            short[] buffer = new short[bufferLength()];
            position = 0;

            while( howManyFinished<tracks.size()){


                for(int i = 0;i<buffer.length; i++){
                    buffer[i] = 0;
                }
                howManyFinished = 0;
                for(int i = 0; i<tracks.size(); i++){
                    howManyFinished += (trackFillBuffer(i, buffer)? 1: 0);
                }
                out.write(buffer,0,buffer.length);
                position++;
            }

    }
    public void close() throws IOException {
        out.close();
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
                    notaLlennarMatriz((RegularNote) n,buffer,n.getLength()-(count-position));
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
    private void notaLlennarMatriz(RegularNote nota, short[] matriz, int posicion){
        instrumento.setFrameRate(frameRate);
        float volumeMultiplier = this.volumeMultiplier;
        if(nota.chord != null){ volumeMultiplier/= (nota.chord.length+1); }

        instrumento.fillBuffer(
                nota.name +(12*(nota.octave+1)) -1,
                posicion*matriz.length,
                nota.length*matriz.length,
                matriz,
                volumeMultiplier,
                0
        );
        if(nota.chord != null){
            for(int i =0; i<nota.chord.length; i++){
                instrumento.fillBuffer(
                        nota.chord[i][0] +(12*(nota.chord[i][1]+1)) -1,
                        posicion*matriz.length,
                        nota.length*matriz.length,
                        matriz,
                        volumeMultiplier,
                        0
                );
            }
        }
    }

    private int bufferLength(){
        return (int) (
                44100*(
                        60.0f/(
                                (float)(
                                        tempo* NoteLengths.quarter
                                )
                        )
                )
        );
    }
}
