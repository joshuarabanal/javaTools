package com.playMidi.player.Midi;


import com.playMidi.player.SoundEvent;

/**
 * <table>
 *     <tr>
 *         <td>Type</td><td>Event</td>
 *         <td>Type</td><td>Event</td>
 *
 *     </tr>
 *     <tr>
 *         <td>0x00</td><td>Sequence number</td> <td>0x20</td><td>MIDI channel prefix assignment</td>
 *
 *     </tr>
 *     <tr>
 *         <td>0x01</td><td>Text event</td><td>0x2F</td><td>End of track</td>
 *     </tr>
 *     <tr>
 *         <td>0x02</td><td>Copyright notice</td>
 *         <td>0x51</td><td>Tempo setting</td>
 *     </tr>
 *     <tr>
 *         <td>x03</td><td>Sequence or track name</td>
 *         <td>0x54</td><td>SMPTE offset</td>
 *     </tr>
 *     <tr>
 *         <td>0x04</td><td>Instrument name</td>
 *         <td>0x58</td><td>Time signature</td>
 *     </tr>
 *     <tr>
 *         <td>0x05</td><td>Lyric text</td>
 *         <td>0x59</td><td>Key signature</td>
 *     </tr>
 *     <tr>
 *         <td>0x06</td><td>Marker text</td>
 *         <td>0x7F</td><td>Sequencer specific event</td>
 *     </tr>
 *     <tr>
 *         <td>0x07</td><td>Cue point</td>
 *     </tr>
 *
 *
 *
 * </table>
 <li></li>







 *
 * Created by ra on 11/26/2016.
 */

public class MidiEvent {
    private final byte event;
        /** the event prefix for note on events**/public static final int noteOn = 0x90;
    /** the event prefix for note off events**/public static final int noteOff = 0x80;
    /** the event command for tempo events**/public static final int setTempo = 0x51;
    /** the event prefix for instrument change events**/public static final int changeInstrument = 0xC0;
    /** aftertouch:
     * Polyphonic Key Pressure (Aftertouch). This message is most often sent by pressing down on the key after it "bottoms out". (kkkkkkk) is the key (note) number. (vvvvvvv) is the pressure value
     */public static final int afterTouch = 0xA0;
    /** **/public static final int changeBank = 0x00;
    /** **/public static final int controlChange = 0xb0;

        
    private final int noteNumber;
    private final byte volume;
    private final int startTime;


    /**
     *
     * @param event = note On | noteOff
     * @param noteNumber
     * @param noteVolume
     * @param startTime
     */
    private MidiEvent( int startTime, byte event, int noteNumber, byte noteVolume){
    this.event = event;
    this.noteNumber = noteNumber;
    this.volume = noteVolume;
    this.startTime = startTime;
}
    public static MidiEvent new_noteOn(int startTime, byte track, byte noteNumber, byte noteVolume) throws Exception {
        if(noteNumber>256){ throw new Exception("note number invalid"); }
        return new MidiEvent( startTime, (byte)(noteOn +track),  noteNumber,  noteVolume);
    }
    public static MidiEvent new_noteOff(int startTime, byte track, byte noteNumber, byte noteVolume) throws Exception {
        if(noteNumber>256){ throw new Exception("note number invalid"); }
        return new MidiEvent( startTime, (byte)(noteOff +track),  noteNumber,  noteVolume);
    }
    public static MidiEvent new_Accent(int startTime,byte track, byte noteNumber, byte noteVolume){
        return new MidiEvent(startTime, (byte)(afterTouch+track), noteNumber, noteVolume);
    }

    /**
     *
     * @param startTime
     * @param microSecondsPerTic
     * 1 micro second = 10<sup>-6</sup> seconds
     * @return
     * @throws Exception
     */
    public static MidiEvent new_tempoEvent(int startTime, int microSecondsPerTic) throws Exception {
        int tempo = 60000000/microSecondsPerTic;

        if(tempo>256){ throw new Exception("tempo is invalid"); }
        return new MidiEvent( startTime, (byte)setTempo,  tempo,  (byte)0);
    }
    public static MidiEvent new_changeInstrument(int startTime, byte track, byte instrument){
        return new MidiEvent( startTime, (byte)(changeInstrument+track),  instrument,  (byte)0);
    }
    public static MidiEvent new_changeBank(int startTime, int new_bank){
        return new MidiEvent(startTime, (byte)changeBank, new_bank, (byte)0);
    }
    //getters
    public int getEventType(){
    return event & 0xF0;
}
    public int getEventTrack(){ return event & 0x0F; }
    public int getStartTime(){ return startTime; }
    public int getNoteNumber() throws Exception {
        if((isNoteOff() || isNoteOn()) && noteNumber<256){
            return noteNumber;
        }
        else{
            throw new Exception("note number not valid");
        }
    }
    public int getTempo() throws Exception{
        if(!isSetTempoEvent()){ throw new Exception("this is not a tempo event"); }
        else{ return this.noteNumber; }
    }

    //event types
    public boolean isNoteOff(){ return getEventType() == noteOff; }
    public boolean isNoteOn(){ return getEventType() == noteOn; }
    public boolean isSetTempoEvent(){ return event == setTempo; }
    public boolean isChangeInstrumentEvent(){ return getEventType() == changeInstrument; }

    public boolean equals(SoundEvent se) throws Exception {
        if(this.getEventTrack() == se.getTrack() && this.getNoteNumber() == se.getNoteNumber()){ return true;}
        else{return false;}
    }

}