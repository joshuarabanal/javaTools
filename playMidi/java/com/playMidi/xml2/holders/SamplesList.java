package com.playMidi.xml2.holders;

import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.xml.functions.Function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import xml.NameValuePairList;
import xml.unoptimized.NameValuePair;
import xml.XMLelement;

/**
 * Created by ra on 05/06/2017.
 */

public class SamplesList implements Function {
    private ArrayList<Sample> samples;
    public SamplesList(NameValuePairList attributes) {
        if(attributes != null && attributes.size()>0){
            String log = "[";
            for(int i = 0; i<attributes.size(); i++){
                NameValuePair attr = attributes.get(i);
                log+= "("+attr.getName()+","+attr.getValue()+"),";
            }
            log+="]";
            throw new IndexOutOfBoundsException("this element takes no attributes:"+log);
        }
    }

    @Override
    public double valueAt(int x) {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {
        if(samples == null){ samples = new ArrayList<>(); }
        samples.add((Sample) f);
    }

    @Override
    public void closeElement() throws Exception {
        if(samples == null){ throw new NullPointerException("samples empty"); }
    }
    public File writeSDTA() throws IOException {
        File f = File.createTempFile("SDTA","TMP");
        FileOutputStream out = new FileOutputStream(f);

        int length = 0;
        for(int i = 0; i<samples.size();i++){
            length+= (samples.get(i).getSampleLength()*2);
        }

        Writer.writeRiffName(out,"LIST", length+12);
        Writer.writeRiffName(out,"sdta");
        Writer.writeRiffName(out,"smpl", length);
        for(int i = 0; i<samples.size();i++){
            samples.get(i).writeSample(out);
        }
        out.close();
        return f;
    }

    int getShdrLength(){
        return (1+samples.size())*46;
    }
    public void writeSHDR(OutputStream out) throws IOException {
        Writer.writeRiffName(out,"shdr", getShdrLength());
        int start = 0;
        for(int i = 0; i<samples.size(); i++){
            samples.get(i).writeSHDR(out, start);
            start += samples.get(i).getSampleLength();
        }
        Sample.writeDefaultSHDR(out);
    }

    public int getSampleId(String name) {
        for(int i = 0; i<samples.size(); i++){
            if(samples.get(i).name.equals(name)){
                return 0;
            }
        }
        throw new IndexOutOfBoundsException("failed to find the index of element");
    }
}
