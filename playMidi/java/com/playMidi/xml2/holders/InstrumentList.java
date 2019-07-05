package com.playMidi.xml2.holders;

import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import xml.XMLelement;

/**
 * Created by ra on 06/06/2017.
 */

public class InstrumentList implements com.playMidi.xml.functions.Function {
    private ArrayList<Instrument> instruments;
    @Override public double valueAt(int x) {
        return 0;
    }

    @Override public void addChild(XMLelement f) {
        if(instruments == null){ instruments = new ArrayList<>(); }
        instruments.add((Instrument)f);
    }

    @Override public void closeElement() throws Exception {
        if(instruments == null){ throw new NullPointerException("this element has no child nodes"); }
    }
    
    
    public File writePDTA(SamplesList samplesList) throws IOException {

        //chink sizes
        int PHDR_size = 38*(1+instruments.size());
        int PBAG_size = 4;
        for(int i = 0; i<instruments.size(); i++){
            PBAG_size+=2*(instruments.get(i).getPMODcount() + instruments.get(i).getPGENcount());
        }


        int  PGEN_size = 0;
        int PMOD_size = 0;
        int IGEN_size = 0;
        int IMOD_size = 0;
        int IBAG_size = 0;
        for(int i = 0; i<instruments.size(); i++){
            PGEN_size += instruments.get(i).getPGENcount();
            PMOD_size += instruments.get(i).getPMODcount();
            IMOD_size +=instruments.get(i).getIMODcount();
            IGEN_size +=instruments.get(i).getIGENcount();
            IBAG_size+=instruments.get(i).getIBAGcount();
        }
        PMOD_size = (PMOD_size+1)*10;
        PGEN_size = (PGEN_size+1)*4;
        IMOD_size = (IMOD_size+1)*10;
        IGEN_size = (IGEN_size+1)*4;
        IBAG_size =  (IBAG_size+1)*4;
        int INST_size = (instruments.size()+1)*22;



        int totalSize = 4+//PDTA
                PHDR_size+8+
                PBAG_size+8+
                PMOD_size+8+
                PGEN_size+8+
                INST_size+8+
                IBAG_size+8+
                IMOD_size+8+
                IGEN_size+8+
                samplesList.getShdrLength()+8;

        File f = File.createTempFile("INFO_tmp","TMP");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));

        Writer.writeRiffName(out,"pdta");


        //PHDR
        Writer.writeRiffName(out,"phdr", PHDR_size);
                    int bagIndex = 0;
                    for(int i = 0; i<instruments.size(); i++){
                        Instrument inst = instruments.get(i);
                        Writer.write_StringOfLength(out, inst.name,20);
                        Writer.writeWord(out,inst.midiNumber);
                        Writer.writeWord(out,inst.bank);
                        Writer.writeWord(out, bagIndex);bagIndex+=inst.getPresetZonesSize();
                        Writer.writeRiffLength(out,0);//library
                        Writer.writeRiffLength(out,0);//genre
                        Writer.writeRiffLength(out,0);//morphology
                    }
                    Writer.write_StringOfLength(out, "EOP",20);
                    Writer.writeWord(out,255);
                    Writer.writeWord(out,0);
                    Writer.writeWord(out, bagIndex);
                    Writer.writeRiffLength(out,0);
                    Writer.writeRiffLength(out,0);
                    Writer.writeRiffLength(out,0);



        //PBAG

        Writer.writeRiffName(out,"pbag", PBAG_size);
        //pgen and pmod links
        int  generatorIndex = 0;
        int modulatorIndex = 0;
        for(int i = 0; i<instruments.size(); i++){
            Writer.writeWord(out, generatorIndex);
            Writer.writeWord(out, modulatorIndex);
            generatorIndex += instruments.get(i).getPGENcount();
            modulatorIndex += instruments.get(i).getPMODcount();

        }
        //terminal element
        Writer.writeWord(out, generatorIndex);
        Writer.writeWord(out, modulatorIndex);



        //PMOD
        Writer.writeRiffName(out,"pmod", PMOD_size);
        for(int i = 0; i<instruments.size(); i++){
            instruments.get(i).writePMOD(out);
        }
        Instrument.writeDefaultModulator(out);


        Writer.writeRiffName(out,"pgen",PGEN_size);
        for(int i = 0; i<instruments.size(); i++){
            instruments.get(i).writePGEN(out);
        }
        Instrument.writeDefaultGenerator(out);



        //INST
        Writer.writeRiffName(out,"inst", INST_size);
        int instZoneIndex = 0;
        for(int i = 0; i<instruments.size(); i++){
            Writer.write_StringOfLength(out,instruments.get(i).name,20);
            Writer.writeWord(out, instZoneIndex);
            instZoneIndex += instruments.get(i).getInstrumentZonesCount();
        }
        //terminal record
        Writer.write_StringOfLength(out,"EOI",20);
        Writer.writeWord(out,instZoneIndex);



        //IBAG

        Writer.writeRiffName(out,"ibag", IBAG_size);
        int InstrumentModulatorsCount = 0;
        int InstrumentGeneratorsCount = 0;
        for(int i = 0; i<instruments.size(); i++){//for each instrument
            TimbreRanges inst = instruments.get(i).zones.get(i).instruments;
            for(int y = 0; y<inst.instruments.size(); y++){//for each TimbreRange
                Writer.writeWord(out,InstrumentGeneratorsCount);
                Writer.writeWord(out,InstrumentModulatorsCount);
                InstrumentGeneratorsCount += TimbreRange.getIGENcount();
                InstrumentModulatorsCount += TimbreRange.getIMODcount();
            }


        }
        //terminal element
        Writer.writeWord(out,InstrumentGeneratorsCount);
        Writer.writeWord(out,InstrumentModulatorsCount);

        Writer.writeRiffName(out,"imod", IMOD_size);
        for(int i = 0; i<instruments.size(); i++){
            instruments.get(i).writeIMOD(out);
        }
        Instrument.writeDefaultModulator(out);

        Writer.writeRiffName(out,"igen", IGEN_size);
        for(int i = 0; i<instruments.size(); i++){
            instruments.get(i).writeIGEN(out, samplesList);
        }
        Instrument.writeDefaultGenerator(out);

        //SHDR
        samplesList.writeSHDR(out);

        out.close();

        if(f.length() != totalSize){
            throw new IndexOutOfBoundsException("file length and header length do not match:"+f.length()+"!="+totalSize);
        }
        File retu = File.createTempFile("INFO","TMP");
        out = new BufferedOutputStream(new FileOutputStream(retu));
        Writer.writeRiffName(out,"LIST", ((int) f.length()) );
        XmlMidiTimbreSet.writeFile(out, f);
        out.close();
        return retu;
    }
}
