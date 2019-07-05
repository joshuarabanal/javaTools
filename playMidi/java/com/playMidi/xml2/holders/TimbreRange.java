package com.playMidi.xml2.holders;

import com.playMidi.xml.functions.Function;
import com.playMidi.xml2.XmlMidiTimbreSet;

import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

/**
 * Created by ra on 05/06/2017.
 */

public class TimbreRange implements Function {
     int keyMin;
     int rootKey;
     int keyMax;
     String name;

    public TimbreRange(NameValuePairList attributes) {
        for(int i = 0; i<attributes.size(); i++){
            NameValuePair attr = attributes.get(i);
            switch (attr.getNameIndex()){
                case XmlMidiTimbreSet.attribute_name:
                    this.name = attr.getValue();
                    break;
                case XmlMidiTimbreSet.attribute_keyMax:
                    this.keyMax = Integer.parseInt(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_keyMin:
                    this.keyMin = Integer.parseInt(attr.getValue());
                    break;
                case XmlMidiTimbreSet.attribute_rootKey:
                    this.rootKey = Integer.parseInt(attr.getValue());
                    break;
            }
        }
    }

    static int getIGENcount() {
        return 3;
    }
    static int getIMODcount() {
        return 0;
    }

    @Override
    public double valueAt(int x) {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {

    }

    @Override
    public void closeElement() throws Exception {

    }


}
