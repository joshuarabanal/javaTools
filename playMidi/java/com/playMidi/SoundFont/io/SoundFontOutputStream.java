package com.playMidi.SoundFont.io;

import android.util.Log;

import Analytics.CrashReporter;
import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.SoundFont.SoundFontMidiTimbreSet;
import com.playMidi.SoundFont.io.soundFontInputStream.MetaData;
import com.playMidi.SoundFont.io.soundFontInputStream.SampleMetas;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenModIndcies;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.InstrumentModulator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetHeader;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneGenerator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneIndex;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.PresetZoneModulator;
import com.playMidi.SoundFont.io.soundFontInputStream.metaData.SampleHeaders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import waveFormat.PcmHelpers;

/**
 * Created by ra on 24/05/2017.
 */

public class SoundFontOutputStream implements Runnable{
    private SoundFontMidiTimbreSet sf;
    private boolean background;
    File f;
    public SoundFontOutputStream(SoundFontMidiTimbreSet sf, File f, boolean background) throws FileNotFoundException {
        this.background = background;
        this.f = f;
        this.sf = sf;
    }

    public void write() {
        if(background){
            new Thread(this).start();
        }
        else{
            run();
        }
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        Log.i("starting file save", f.toString());
        try {
            writeRIFF_Sfbk();
            time = (System.currentTimeMillis()-time)/1000;
            Log.i("file save finished", "in "+time+" seconds");
        } catch (IOException e) {
            CrashReporter.sendDefaultErrorReport(e);
            Log.i("file save failed", f.toString());
            f.delete();
        }

        try {
            SoundFontMidiTimbreSet sf = new SoundFontMidiTimbreSet(new FileInputStream(f), false);
        } catch (IOException e) {
            CrashReporter.sendDefaultErrorReport(e);
        }
    }

    public void close() throws IOException {
    }

    private void writeRIFF_Sfbk() throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        File info = write_INFO_listBody();
        File sdta = write_sdta_listBody();
        File pdta = write_pdta_listBody();

        int length = (int)(info.length()+ sdta.length()+pdta.length());
        Log.i("write riff","file ATTRIBUTE_length:"+length);
        Writer.writeRiffName(out,"RIFF", (int)length+4+(12*3));
        Writer.writeRiffName(out, "sfbk");

        writeListFromFile(info,out,"INFO");
        writeListFromFile(sdta,out,"sdta");
        writeListFromFile(pdta,out,"pdta");
        out.close();
    }
    private void writeListFromFile(File f, OutputStream out, String riffName) throws IOException {
        Writer.writeRiffName(out, "LIST", (int)f.length()+4);
        Writer.writeRiffName(out, riffName);
        InputStream in = new FileInputStream(f);
        byte[] b = new byte[2048];
        int howmany;
        while((howmany = in.read(b))>0){
            out.write(b,0,howmany);
        }
        in.close();
        f.delete();
    }
    private void writeTempFile(File f, OutputStream out, String riffName, boolean includeLength) throws IOException {
        if(includeLength){
            Writer.writeRiffName(out, riffName, (int)f.length());
        }
        else{
            Writer.writeRiffName(out, riffName);
        }

        InputStream in = new FileInputStream(f);
        byte[] b = new byte[2048];
        int howmany;
        while((howmany = in.read(b))>0){
            out.write(b,0,howmany);
        }
        in.close();
        f.delete();

    }
    private File write_pdta_listBody() throws IOException {
        File f = File.createTempFile("pdta","list");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));


        SampleMetas sm = sf.getSampleMetas();

        write_pdta_pdhr(out, sm.presets);
        write_pdta_pbag(out,sm.presetZones );
        write_pdta_pmod(out, sm.zoneModulators);
        write_pdta_pgen(out, sm.zoneGenerators);
        write_pdta_inst(out,sm.instrumentNames, sm.instrumentZonIndices);
        write_pdta_ibag(out, sm.allInstrumentZones);
        write_pdta_imod(out,sm.allInstrumentZoneModulators);
        write_pdta_igen(out, sm.zoneGeneratorsPerZone);
        write_pdta_shdr(out, sm.sampleHeadersList);
        out.close();
        return f;
    }
    private static void write_pdta_shdr(OutputStream in, ArrayList<SampleHeaders> inst) throws IOException {
        Writer.writeRiffName(in, "shdr",inst.size()*46);
        for(int i = 0; i<inst.size(); i++){
            SampleHeaders head = inst.get(i);
            Writer.write_StringOfLength(in, head.hSampleName, 20);
            Writer.writeRiffLength(in, head.getStart());
            Writer.writeRiffLength(in, head.getEnd());
            Writer.writeRiffLength(in, head.getStartLoop());
            Writer.writeRiffLength(in, head.getEndLoop());
            Writer.writeRiffLength(in, head.getSampleRate());
            in.write(head.originalPitch);
            in.write(head.pitchCorrection);
            Writer.writeWord(in,head.sampleLink);
            in.write(head.sapleTypeToInt());
            in.write(head.sampleRomtoInt());
        }
    }
    private static void write_pdta_igen(OutputStream out, ArrayList<InstrumentGenerator> instruments) throws IOException {
        Writer.writeRiffName(out, "igen",instruments.size()*4);
        for(int i = 0; i<instruments.size(); i++){
            InstrumentGenerator zone =instruments.get(i);
            Writer.writeWord(out,zone.sfGenOper);
            out.write(zone.genAmount_byLO);
            out.write(zone.genAmount_byHI);//get_WORD(array, start);
        }
    }
    private static void write_pdta_imod(OutputStream out, ArrayList<InstrumentModulator> instruments) throws IOException {
        Writer.writeRiffName(out, "imod",instruments.size()*10);
        for(int i = 0; i<instruments.size(); i++) {
            InstrumentModulator zone = instruments.get(i);
            Writer.writeWord(out,zone.modAmount);
            Writer.writeWord(out,zone.instModNdx);
            Writer.writeWord(out,zone.sfModDestOper);
            Writer.writeWord(out,zone.sfModAmtSrcOper);
            Writer.writeWord(out,zone.sfModTransOper);
        }
    }
    private static void write_pdta_ibag(OutputStream out, ArrayList<InstrumentGenModIndcies> zones) throws IOException {
        Writer.writeRiffName(out, "ibag",zones.size()*4);
        int start = 0;
        for (int i = 0; i < zones.size(); i++) {
            InstrumentGenModIndcies zone = zones.get(i);
            Writer.writeWord(out, zone.instGenNdx);
            Writer.writeWord(out, zone.instModNdx);
        }
    }
    private static void write_pdta_pgen(OutputStream out, ArrayList<PresetZoneGenerator> zoneGenerators) throws IOException {
        Writer.writeRiffName(out, "pgen",zoneGenerators.size()*4);
        for(int i = 0; i<zoneGenerators.size(); i++) {
            PresetZoneGenerator zoneGen = zoneGenerators.get(i);
            Writer.writeWord(out,zoneGen.genraterOperator);
            out.write(zoneGen.range_low);
            out.write(zoneGen.range_high);
        }
    }

    public static void write_pdta_inst(OutputStream out, ArrayList<String> instruments, ArrayList<Integer> zoneIndices) throws IOException {
        Writer.writeRiffName(out, "inst",instruments.size()*22);
        for(int i = 0; i<instruments.size(); i++){
            Writer.write_StringOfLength(out, instruments.get(i),20);
            Writer.writeWord(out, zoneIndices.get(i));
        }
    }
    private void write_pdta_pmod(OutputStream out, ArrayList<PresetZoneModulator> modulators) throws IOException {
        Writer.writeRiffName(out, "pmod",modulators.size()*10);
        for(int i = 0; i<modulators.size(); i++){
            PresetZoneModulator mod = modulators.get(i);
            Writer.writeWord(out, mod.sourceOperator);
            Writer.writeWord(out, mod.destOperator);
            Writer.writeWord(out, mod.modAmount);
            Writer.writeWord(out, mod.amountSourceOperator);
            Writer.writeWord(out, mod.amountTransformOperator);
        }
    }
    private void write_pdta_pdhr(OutputStream out, ArrayList<PresetHeader> presetList ) throws IOException {
        Writer.writeRiffName(out, "phdr", presetList.size()*38);
        for (int i = 0; i < presetList.size(); i++) {
            PresetHeader preset = presetList.get(i);
            Writer.write_StringOfLength(out, preset.name, 20);
            Writer.writeWord(out, preset.MIDI_number );
            Writer.writeWord(out, preset.bank );
            Writer.writeWord(out, preset.presetZones_index);
            Writer.writeRiffLength(out,preset.library);
            Writer.writeRiffLength(out, preset.genre);
            Writer.writeRiffLength(out, preset.morphology);
        }
    }
    private void write_pdta_pbag(OutputStream out, ArrayList<PresetZoneIndex> barList) throws IOException {
        Writer.writeRiffName(out, "pbag",barList.size()*4);
        for(int i = 0; i<barList.size(); i++){
            PresetZoneIndex pzi = barList.get(i);
            Writer.writeWord(out, pzi.generatorIndex);
            Writer.writeWord(out, pzi.modulatorIndex);
        }

    }
    private File write_sdta_listBody() throws IOException {
        File f = File.createTempFile("sdta","list");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        Writer.writeRiffName(out,"smpl",sf.rawSamples.length*2);
        PcmHelpers.Short.write(sf.rawSamples,out);
        out.close();
        return f;
    }
    private String toEvenString(String input){
        if(input.length()%2 != 0){ return input+"\0"; }
        return input;
    }
    private File write_INFO_listBody() throws IOException {
        File f = File.createTempFile("Info","LIST");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));


        MetaData metas = sf.metaData;


        Writer.writeRiffName(out, "ifil", 4);
            Writer.writeWord(out,2);
            Writer.writeWord(out,0);

            String string = toEvenString(metas.targetSoundEngine);
        Writer.writeRiffName(out, "isng", string.length());
            Writer.writeString(out, string);

            string = toEvenString(metas.soundEngine);
        Writer.writeRiffName(out, "INAM", string.length());
            Writer.writeString(out,string);

            string = toEvenString(metas.creatorApplication);
        Writer.writeRiffName(out, "ISFT", string.length());
        Writer.writeString(out,string);

            string = toEvenString(metas.intendedProduct);
        Writer.writeRiffName(out, "IPRD", string.length());
        Writer.writeString(out,string);

            string = toEvenString(metas.engineersOfFile);
        Writer.writeRiffName(out, "IENG", string.length());
        Writer.writeString(out,string);

            string = toEvenString(metas.creationDate);
        Writer.writeRiffName(out, "ICRD", string.length());
        Writer.writeString(out,string);

            string = toEvenString(metas.comments);
        Writer.writeRiffName(out, "ICMT", string.length());
        Writer.writeString(out,string);

            string = toEvenString(metas.copyright);
        Writer.writeRiffName(out, "ICOP", string.length());
        Writer.writeString(out,string);

        out.close();
        return f;
    }
}
