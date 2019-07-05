package com.playMidi.xml.functions;


import com.playMidi.xml2.XmlMidiTimbreSet;

import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public class PowerSeries implements Function {
    private double[] amplitudes;
    public PowerSeries(double[] amp){
        amplitudes = amp;
    }
    public PowerSeries(NameValuePairList attrs){
       for(int y = 0; y<attrs.size(); y++){
           NameValuePair attr = attrs.get(y);
           if(attr.getNameIndex() == XmlMidiTimbreSet.attribute_amplitudes ){
               String[] amps = attr.getValue().split(",");
               amplitudes = new double[amps.length];
               for(int i = 0; i<amplitudes.length; i++){
                   amplitudes[i] = Double.parseDouble(amps[i]);
               }
           }
       }
    }
    @Override
    public double valueAt(int x) {
        double retu = 0;
        int t = 1;
        for(double amplitude :amplitudes){
            retu += amplitude*t;
            t = t*x;
        }
        return retu;
    }
    public double valueAt_depreciated(int x) {
        double retu = 0;
        int t = 1;
        for(int i = 0; i<this.amplitudes.length; i++){
            retu += amplitudes[i]*t;
            t = t*x;
        }
        return retu;
    }

    @Override
    public void addChild(XMLelement f) {

    }

    @Override
    public void closeElement() throws Exception {

    }
    public String toString(){
        String retu = "";
        for(int i = 0; i<amplitudes.length; i++){
            if(i>0){ retu+="+"; }

            if(i == 0){ retu += amplitudes[i]; }
            else if(i == 1){ retu += amplitudes[i]+"x"; }
            else{
                retu += amplitudes[i]+"x^"+i;
            }

        }
        return retu;
    }


}
