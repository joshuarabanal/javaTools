package com.playMidi.xml2.holders;

import com.playMidi.xml2.XmlMidiTimbreSet;

import java.io.OutputStream;
import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;
import xml.unoptimized.NameValuePair;

/**
 * Created by ra on 05/06/2017.
 */

public class Zone implements XMLelement {


     int volumeMin = 0;
     int volumeMax = 127;
     String name;
     TimbreRanges instruments = null;

    public Zone(NameValuePairList attributes) {
        for(int i = 0; i<attributes.size(); i++){
            NameValuePair attr = attributes.get(i);
            switch (attr.getNameIndex()){
                case XmlMidiTimbreSet.attribute_name:
                    this.name = attr.getValue();
                    break;
                case XmlMidiTimbreSet.attribute_volumeMax:
                    this.volumeMax = Integer.parseInt(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_volumeMin:
                    this.volumeMin = Integer.parseInt(attr.getValue());
                    break;
            }
        }
    }

    /**
     * @deprecated
     * @return
     */
    public int instrumentModulatorCount() {
        return 0;
    }

    /**
     * @deprecated
     * @param out
     */
    public void writeInstrumentModulator(OutputStream out) {
        return;
    }

    int getInstrumentModulatorsCount() {
        return 0;
    }
    /**
     * @deprecated
     * @param out
     */
    public void writeModulators(OutputStream out) {
        return;
    }

    /**
     *
     * @deprecated
     * @return
     */
    public int modulatorCount() {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {
        if(instruments == null){
            instruments = (TimbreRanges) f;
        }
        else{
            throw new IndexOutOfBoundsException("child node already set");
        }
    }

    @Override
    public void closeElement() throws Exception {
        if(instruments == null){
            throw new IndexOutOfBoundsException("null instruments");
        }
    }



}
