package com.playMidi.xml2.holders;

import com.playMidi.SoundFont.io.RiffFormat.Writer;
import com.playMidi.xml.functions.Function;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import xml.NameValuePairList;
import xml.XMLelement;

/**
 * Created by ra on 05/06/2017.
 */

public class TimbreRanges implements Function {
    ArrayList<TimbreRange> instruments = null;

    public TimbreRanges(NameValuePairList attributes) {

    }


    
    

    @Override
    public double valueAt(int x) {
        return 0;
    }

    @Override
    public void addChild(XMLelement f) {
        if(instruments == null){ instruments = new ArrayList<>(); }
        instruments.add((TimbreRange) f);
    }

    @Override
    public void closeElement() throws Exception {
        if(instruments == null){
            throw new IndexOutOfBoundsException("null instruments");
        }
    }

    public int getIGENcount() {
        return TimbreRange.getIGENcount()*instruments.size();
    }
    public void writeIGEN(OutputStream out, SamplesList s) throws IOException {
        for(int i = 0; i<instruments.size(); i++){
            TimbreRange tr = instruments.get(i);
            //Instrument name(sampleID)
            Writer.writeWord(out, 53); Writer.writeWord(out, s.getSampleId(tr.name));
            //keyRange
            Writer.writeWord(out, 43);out.write(tr.keyMin);out.write(tr.keyMax);
            //overridingRootKey
            Writer.writeWord(out, 58); Writer.writeWord(out, tr.rootKey);


        }
    }


}
