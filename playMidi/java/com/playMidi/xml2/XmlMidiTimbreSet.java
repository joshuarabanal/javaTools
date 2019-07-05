package com.playMidi.xml2;

import android.content.Context;
import androidx.annotation.RawRes;

import Analytics.CrashReporter;
import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;
import com.playMidi.player.soundEvent.MidiTimbreSet;
import com.playMidi.xml.functions.Addition;
import com.playMidi.xml.functions.Amplitude;
import com.playMidi.xml.functions.Decay;
import com.playMidi.xml.functions.Delta;
import com.playMidi.xml.functions.Multiplier;
import com.playMidi.xml.functions.Overtone;
import com.playMidi.xml.functions.Periodic;
import com.playMidi.xml.functions.PowerSeries;
import com.playMidi.xml2.holders.Instrument;
import com.playMidi.xml2.holders.InstrumentList;
import com.playMidi.xml2.holders.TimbreRanges;
import com.playMidi.xml2.holders.Sample;
import com.playMidi.xml2.holders.SamplesList;
import com.playMidi.xml2.holders.TimbreRange;
import com.playMidi.xml2.holders.Zone;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import xml.NameValuePairList;
import xml.Optimized.OptimizedParser;
import xml.Optimized.OptimizedXmlCursor;
import xml.XMLelement;

/**
 * Created by ra on 05/06/2017.
 */

public class XmlMidiTimbreSet implements OptimizedXmlCursor, MidiTimbreSet, Runnable {
    private static final String[] nodesNattributes = {
            "ZoneSet", "Instrument", "Zone", "TimbreRanges", "Instrument",//0-4
            "TimbreRanges", "TimbreRange", "SamplesList", "Sample", "name",//5-9
            "midiNumber","TimbreSet","length","loop","minKey",//10-14
            "maxKey", "sampleRate","rootKey", "Overtone", "index", //15-19
            "type", "amp","Addition", "Decay", "PowerSeries",//20-24
            "amplitudes","Multiplier","Sine","delta", "bank",//25-29
            "volumeMax","volumeMin","keyMin", "keyMax","loopPoint",//30-34
            "InstrumentList"
};
    private static final int attribute_zoneSet = 0;
        private static final int attribute_Instrument = 1;
        private static final int attribute_zone = 2;
        private static final int attribute_TimbreRanges = 3;
        private static final int attribute_instrument = 4;
    private static final int attribute_timbreRanges = 5;
        private static final int attribute_timbreRange = 6;
        private static final int attribute_samplesList = 7;
        private static final int attribute_sample = 8;
        public static final int attribute_name = 9;
    public static final int attribute_midiNumber = 10;
        private static final int attribute_TimbreSet = 11;
        public static final int attribute_length = 12;
        public static final int attribute_loop = 13;
        public static final int attribute_minKey = 14;
    public static final int attribute_maxKey = 15;
        public static final int attribute_sampleRate = 16;
        public static final int attribute_rootKey = 17;
        private static final int attribute_Overtone = 18;
        public static final int attribute_index = 19;
    private static final int attribute_type = 20;
        private static final int attribute_amp = 21;
        private static final int attribute_Addition= 22;
        private static final int attribute_Decay = 23;
        private static final int attribute_PowerSeries = 24;
    public static final int attribute_amplitudes = 25;
        private static final int attribute_Multiplier = 26;
        private static final int attribute_Sine = 27;
        public static final int attribute_delta = 28;
        public static final int attribute_bank = 29;
    public static final int attribute_volumeMax = 30;
        public static final int attribute_volumeMin = 31;
        public static final int attribute_keyMin = 32;
        public static final int attribute_keyMax = 33;
        public static final int attribute_loopPoint = 34;
    private static final int attribute_InstrumentList = 35;


    //---------------------------------------------------------------------------------------------

    private InstrumentList instrumentList;
    private SamplesList samplesList;
    private File output = null;


    private ArrayList<XMLelement>  stack = new ArrayList<XMLelement>();

    private XmlMidiTimbreSet(Context c, int resource) throws Exception {
        InputStream is = c.getResources().openRawResource(resource);
        OptimizedParser p =new OptimizedParser(is, this,nodesNattributes);
        p.read();
        if(stack.size()>0){
            throw new IndexOutOfBoundsException("stack size is too great");
        }
        stack = null;
    }
    public static SoundFontMidiTimbreSet retrieveCachedSoundFont(Context c, @RawRes int resId, @RawRes int defaultRes) throws Exception {
        File f = new File(c.getExternalCacheDir(),resId+".tmp");

        if(f.exists() && false){//cache exists
                return new SoundFontMidiTimbreSet(new FileInputStream(f),false);
        }
                new XmlMidiTimbreSet(c,resId).convert(f);
        return new SoundFontMidiTimbreSet(c, defaultRes, false);
    }

    //----------------------------------------------------------------------------------------

    public void convert(File f){
        this.output = f;
        Thread t  = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
    public void run(){
        try {
            File INFO = writeINFO();
            File SDTA = samplesList.writeSDTA();//TODO finish implementation
            File PDTA = instrumentList.writePDTA(samplesList);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.output));
            Writer.writeRiffName(out, "RIFF", 4+(int)(INFO.length()+SDTA.length()+PDTA.length()));
            Writer.writeRiffName(out, "sfbk");
            writeFile(out, INFO);
            writeFile(out, SDTA);
            writeFile(out, PDTA);
            out.close();

        } catch (IOException e) {
            CrashReporter.sendDefaultErrorReport(e);
        }

    }
    public static void writeFile(OutputStream out, File f) throws IOException {
        FileInputStream in  = new FileInputStream(f);
        byte[] buffer = new byte[1024];
        int howMany = 0;
        while( (howMany = in.read(buffer)) >0 ){
            out.write(buffer,0, howMany);
        }
        in.close();
        f.delete();

    }
    private File writeINFO() throws IOException {
        File f = File.createTempFile("INFO","TMP");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));



        String soundEngine = "Notate\0\0";
        String copyright = "JoshuaRabanal@Gmail.com\0";
        int size = 4+//INFO
                8+4+//ifil
                8+soundEngine.length()+//isng
                8+soundEngine.length()+//INAM
                8+copyright.length();//ICOP

        Writer.writeRiffName(out,"LIST", size);
        Writer.writeRiffName(out,"INFO");

        Writer.writeRiffName(out, "ifil", 4);
                Writer.writeWord(out, 2);
                Writer.writeWord(out, 0);

        Writer.writeRiffName(out, "isng", soundEngine.length());
            Writer.writeString(out,soundEngine);

        Writer.writeRiffName(out, "INAM", soundEngine.length());
            Writer.writeString(out,soundEngine);

        //Writer.writeRiffName(out,"irom");
        //Writer.writeRiffName(out,"iver");
        //Writer.writeRiffName(out,"icrd");
        //Writer.writeRiffName(out,"IENG", name.length());
        //Writer.writeRiffName(out, "IPRD", soundEngine.length());
        //Writer.writeString(out,soundEngine);

        Writer.writeRiffName(out,"ICOP",copyright.length());
            Writer.writeString(out,copyright);

        //Writer.writeRiffName(out,"ICMT");

        //Writer.writeRiffName(out, "ISFT", soundEngine.length()+2);
            //Writer.write_StringOfLength(out,soundEngine, soundEngine.length()+2);

    out.close();


        return f;
    }




//--------------------------------------------------------------------------------------------------
    @Override
    public void newElement(float name, NameValuePairList attributes, boolean autoClose) throws Exception {
        switch((int)name){
            case attribute_zoneSet:
                break;
            case attribute_InstrumentList:
                stack.add(new InstrumentList());
                break;

            case attribute_Instrument://once was Zone
                stack.add(new Instrument(attributes));
                break;
            case attribute_zone:
                stack.add(new Zone(attributes));
                break;
            case attribute_TimbreRanges://once was TimbreRanges
                stack.add(new TimbreRanges(attributes));
                break;
            case attribute_timbreRange:
                stack.add(new TimbreRange(attributes));
                break;
            case attribute_samplesList:
                stack.add(new SamplesList(attributes));
                break;
            case attribute_sample:
                stack.add(new Sample(attributes));
                break;


            //timbreFunctions
            case attribute_Multiplier:
                stack.add(new Multiplier());
                break;

            case attribute_Decay:
                stack.add(new Decay());
                break;

            case attribute_Sine:
                stack.add(new Periodic());
                break;

            case attribute_PowerSeries:
                stack.add(new PowerSeries(attributes));
                break;

            case attribute_Addition:
                stack.add(new Addition());
                break;

            case attribute_Overtone:
                stack.add(new Overtone(attributes));
                break;
            case attribute_amp:
                stack.add(new Amplitude());
                break;

            case attribute_delta:
                stack.add(new Delta());
                break;

            default: throw new UnsupportedEncodingException("cannot parse:"+name+" = "+nodesNattributes[(int)name]);
        }
        if(autoClose){
            closeElement(name);
        }
    }

    @Override
    public void closeElement(float name) throws Exception {

        if(stack.size()>0){//place element inside of parent on the stack
            XMLelement f = stack.remove(stack.size()-1);
            f.closeElement();
            if(stack.size()>0){//add this child to parent
                stack.get(stack.size()-1).addChild(f);
            }
            else{//add this child to the timbreSets
                if(name == attribute_InstrumentList){
                    this.instrumentList = (InstrumentList)f;
                }
                else if(name == attribute_samplesList){
                    this.samplesList = (SamplesList)f;
                }
                else{
                    throw new RuntimeException("incorect stack parents:"+name);
                }
            }
        }
        else{
            if(name == attribute_zoneSet){
                return;
            }
            throw new Exception("overtone null cant close element:"+nodesNattributes[(int) name]);
        }
    }

    @Override
    public void textElement(String text) {

    }

    @Override
    public boolean fillBuffer(int midiNoteNumber, float position, float noteLength, short[] data, float volumeMultiplier, int articulation) {
        return true;
    }
    public void setFrameRate(int rate){}


}
