package com.playMidi.xml2.holders;

import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;
import xml.unoptimized.NameValuePair;

/**
 * Created by ra on 05/06/2017.
 */

public class Instrument implements XMLelement {
    int bank;
    int midiNumber;
    String name;
    ArrayList<Zone> zones = new ArrayList<Zone>();
    public Instrument(NameValuePairList attributes) {
        for(int i = 0; i<attributes.size(); i++){
            NameValuePair attr = attributes.get(i);
            switch (attr.getNameIndex()){
                case XmlMidiTimbreSet.attribute_name:
                    this.name = attr.getValue();
                    break;
                case XmlMidiTimbreSet.attribute_midiNumber:
                    this.midiNumber = Integer.parseInt(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_bank:
                    this.bank = Integer.parseInt(attr.getValue());
                    break;
            }
        }
    }
    //----------------------------------------------------------

    /**
     * velovity range
     * instrument
     * @return
     */
    int getPGENcount(){
        return zones.size()*2;
    }
    void writePGEN(OutputStream out) throws IOException {
        for(int i = 0; i<zones.size(); i++){
            Zone z = zones.get(i);
/**
            //write velocity range
            Writer.writeWord(out,44); out.write(z.volumeMin); out.write(z.volumeMax);
**/
            //write keyRange
            Writer.writeWord(out,43);out.write(0);out.write(127);
            //write instrument
            Writer.writeWord(out,41); Writer.writeWord(out, i);
        }
    }
    int getIGENcount(){
        int retu = 0;
        for(int i = 0; i<zones.size(); i++){
            retu += zones.get(i).instruments.getIGENcount();
        }
        return retu;
    }
    void writeIGEN(OutputStream out, SamplesList s) throws IOException {
        for(int i = 0; i<zones.size(); i++){
            zones.get(i).instruments.writeIGEN(out, s);
        }
    }
    static void writeDefaultModulator(OutputStream out) throws IOException {
        Writer.writeWord(out, 0);//sourceOperator
        Writer.writeWord(out, 0);//destOperator
        Writer.writeWord(out, 0);//modAmount
        Writer.writeWord(out, 0);//amountSourceOperator
        Writer.writeWord(out, 0);//amountTransformOperator
    }
    static void writeDefaultGenerator(OutputStream out) throws IOException {
        Writer.writeWord(out,60);
        Writer.writeWord(out,0);
    }

    int getIMODcount(){
        return 0;
    }
    void writeIMOD(OutputStream out){
        return;
    }

    int getPMODcount(){
        return 0;
    }
    void writePMOD(OutputStream out){
        return;
    }
    int getPresetZonesSize(){
        return this.zones.size();
    }
    public int getInstrumentZonesCount() {
        int retu = 0;
        for(int i = 0; i<zones.size(); i++){
            retu +=zones.get(i).instruments.instruments.size();
        }
        return retu;
    }

     int getIBAGcount() {
         int retu = 0;
         for(int i = 0; i<zones.size(); i++){
             retu += zones.get(i).instruments.instruments.size();
         }
        return retu;
    }
    /**

    int getModulatorCount(){
        int count = 0;
        for(int i = 0; i<zones.size(); i++){
            count += zones.get(i).modulatorCount();
        }
        return count;
    }
     int getInstrumentGeneratorCount(){
        int count = 0;
        for(int i = 0; i<zones.size(); i++){
            count += 2;
        }
        return count;
    }
    int getinstrumentZonesCount(){
        return zones.size();
    }
    
    void writeModulators(OutputStream out){
        for(int i = 0; i<zones.size(); i++){
            zones.get(i).writeModulators(out);
        }
    }



    int instrumentModulatorsCount(){
        int count = 0;
        for(int i = 0; i<zones.size(); i++){
            count += zones.get(i).instrumentModulatorCount();
        }
        return count;
    }

    void writeInstrumentModulators(OutputStream out){
        for(int i = 0; i<zones.size(); i++){
            zones.get(i).writeInstrumentModulator(out);
        }
    }
    int getInstrumentModulatorsCount(){
        int retu = 0;
        for(int i = 0; i<zones.size(); i++){
            retu += zones.get(i).getInstrumentModulatorsCount();
        }
        return retu;
    }





    int getGeneratorCount(){
        int retu = 0;
        for(int i = 0; i<zones.size(); i++){ retu += zones.get(i).instruments.getGeneratorsCount(); }
        return retu;
    }


    
    **/

    //---------------------------------------------------------

    @Override
    public void addChild(XMLelement f) {
        zones.add((Zone)f);
    }

    @Override
    public void closeElement() throws Exception {
        if(zones.size() == 0){ throw new IndexOutOfBoundsException("Zones list is empty"); }
    }



}

