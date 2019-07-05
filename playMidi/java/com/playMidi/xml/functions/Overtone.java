package com.playMidi.xml.functions;



import com.playMidi.xml2.XmlMidiTimbreSet;

import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public class Overtone implements Function{
    private double index;
    private Function delta = null;
    private Function amp = null;
    public Overtone(NameValuePairList attrs){
        index = 0;
        delta = new PowerSeries(new double[]{0});
        for(int i = 0; i<attrs.size(); i++){
            NameValuePair at  = attrs.get(i);
            if(at.getNameIndex() == XmlMidiTimbreSet.attribute_index){
                index = Double.parseDouble(at.getValue());
            }
            else if(at.getNameIndex() == XmlMidiTimbreSet.attribute_delta){
                delta =new PowerSeries(new double[]{Double.parseDouble(at.getValue())});
            }
        }
    }
    private double getOvertoneMultiplier(){ return index; }
    public double getDelta(int x){ return delta.valueAt(x); }
    public Overtone(int index, double delta){
        this.index = index;
        if(delta<0){
            delta = (2*Math.PI)-delta;
        }
        this.delta = new PowerSeries(new double[]{delta});
    }
    public String toString(){
        String retu = "";
            retu += amp.toString();
        return retu;
    }
    @Override
    public double valueAt(int x) {
        return amp.valueAt(x);
    }

    @Override
    public void addChild(XMLelement f) {
        if(f.getClass().equals(Delta.class)){
            delta = ((Delta)f).getFunction();
        }
        else if(f.getClass().equals(Amplitude.class)){
            amp = ((Amplitude)f).getFunction();
        }
    }

    @Override
    public void closeElement() throws Exception{
        if(amp == null || delta == null){
            throw new Exception("attribute_Overtone closed without chiuldren");
        }
    }

    /**
     * generates single wave filled to the buffer
     * @param wavenumber
     * @param baseWave
     * @param data
     * @param wavelength
     */
    public void makeWaveAt(int wavenumber, double[] baseWave, short[] data, int wavelength){

        double amp = this.valueAt(wavenumber);
        if(amp ==0){ return; }
        double multiplier = this.getOvertoneMultiplier();

        //initialize the starting position
        int wavePosition = (int)((delta.valueAt(wavenumber)*wavelength)/(2*Math.PI));
        wavePosition += (wavelength*multiplier*wavenumber);
        wavePosition -=  wavelength*((int)(wavePosition/wavelength));
        if(wavePosition <0){
            wavePosition += wavelength;
        }
        //while (wavePosition>=wavelength){ wavePosition-=wavelength; }

        for(int i = 0; i<=wavelength; i++){
            data[i]+= baseWave[(int)wavePosition]*amp;
            wavePosition += multiplier;
            if(wavePosition>=wavelength){ wavePosition-= wavelength; }
        }
    }

    private int fillSingleWave(int wavelength, int position, int i, short[] baseWave, short[] buffer, float amp){
        for(int index = 0; index<=wavelength; index++){
            if(position>=wavelength){ position -= wavelength; }
            buffer[i]+= baseWave[position]*amp;
            position += this.index; i++;
            if(i>=buffer.length){ return buffer.length; }

        }
        return i;
    }

    /**
     *
     * @param wavenumber number of wavelengths that have passed since the start of this sound
     * @param baseWave short array representing a single sine wave in this wavelength
     * @param buffer the buffer to fill
     * @param wavelength wavelength of this sound
     * @param position the offset position(delta) to start along the basewave
     */
    public void fillBuffer(int wavenumber, short[] baseWave, short[] buffer, int wavelength, int position){
        int multiplier = (int) getOvertoneMultiplier();
        position *= multiplier;
        position = position % wavelength;

        int lastDelta = 0;
        float inv_period = (float)(wavelength/(2*Math.PI));
        float amp = (float) (this.amp.valueAt(wavenumber) / Short.MAX_VALUE);;
        float deltaChange;
        int count = 0;
        for(int i = 0; i<buffer.length; i++){

            if(position>=wavelength) {
                position-=wavelength;
                count ++;

                if(count>=multiplier) {
                    count = 0;
                    amp = (float) (this.amp.valueAt(wavenumber) / Short.MAX_VALUE);
                    deltaChange = (int) ((delta.valueAt(wavenumber) * inv_period) - lastDelta);
                    if (deltaChange > 1 || deltaChange < -1) {
                        position += deltaChange;
                        lastDelta += ((int) deltaChange);

                        if(position>=wavelength){
                            position = position%wavelength;
                        }
                        else if(position<0){
                            position = (position%wavelength) + wavelength;
                        }
                    }
                    wavenumber++;
                }
            }

            buffer[i]+= baseWave[position]*amp;
            position += multiplier;
        }
    }



}
