package com.playMidi.xml.functions;

import android.util.Xml;

import java.util.ArrayList;

import xml.XMLelement;

/**
 * Created by ra on 1/3/2017.
 */

public class Addition implements Function {
    private ArrayList<Function> tempChildren = null;
    private Function[] children;
    @Override
    public double valueAt(int x) {
        double retu = 0;
        for(Function child :children){
            retu+= child.valueAt(x);
        }
        return retu;
    }

    @Override
    public void addChild(XMLelement f) {
        if(tempChildren == null){
            tempChildren = new ArrayList<Function>();
        }
        tempChildren.add((Function) f);
    }

    @Override
    public void closeElement() throws Exception {
        if(tempChildren == null){ throw new Exception("ATTRIBUTE_Addition has no children");}
        children = new Function[tempChildren.size()];
        for(int i = 0; i<children.length; i++){
            children[i] = tempChildren.get(i);
        }
    }
}
